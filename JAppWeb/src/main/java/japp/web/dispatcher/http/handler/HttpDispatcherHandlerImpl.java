package japp.web.dispatcher.http.handler;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import japp.util.ByteHelper;
import japp.util.DateHelper;
import japp.util.ReflectionHelper;
import japp.util.Setable;
import japp.util.SingletonFactory;
import japp.util.Singletonable;
import japp.util.StringHelper;
import japp.web.WebApp;
import japp.web.controller.http.HttpController;
import japp.web.controller.http.annotation.RequestBody;
import japp.web.controller.http.annotation.RequestParameter;
import japp.web.controller.http.annotation.UriVariable;
import japp.web.dispatcher.http.HttpDispatcherHelper;
import japp.web.dispatcher.http.HttpDispatcherUriCompilation;
import japp.web.dispatcher.http.parser.HttpDispatcherParserManager;
import japp.web.dispatcher.http.parser.HttpDispatcherParserManagerImpl;
import japp.web.dispatcher.http.request.RequestMapping;
import japp.web.exception.HttpException;
import japp.web.view.View;

public class HttpDispatcherHandlerImpl implements Singletonable, HttpDispatcherHandler {
	
	public static String DATE_TIME_FORMAT_PATTERN = DateHelper.DATE_TIME_FORMAT_PATTERN;
	
	protected HttpDispatcherParserManager httpDispatcherParserManager;
	
	public static synchronized HttpDispatcherHandlerImpl getInstance() {
		return SingletonFactory.getInstance(HttpDispatcherHandlerImpl.class);
	}
	
	public static synchronized HttpDispatcherHandlerImpl getInstance(final HttpDispatcherParserManager httpDispatcherParserManager) {
		final HttpDispatcherHandlerImpl instance = getInstance();
		instance.httpDispatcherParserManager = httpDispatcherParserManager;
		
		return instance;
	}
	
	protected HttpDispatcherHandlerImpl() {
		this.httpDispatcherParserManager = HttpDispatcherParserManagerImpl.getInstance();
	}
	
	@Override
	public void handle(final HttpDispatcherUriCompilation httpDispatcherUriCompilation, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
		final RequestMapping requestMapping = httpDispatcherUriCompilation.getRequestMapping();
		final HttpController httpController = requestMapping.getHttpController();
		final Method method = requestMapping.getMethod();
		final Map<String, Object> parameters;
		final Object methodReturnObject;
		
		try {
			parameters = getParameters(httpDispatcherUriCompilation, httpServletRequest, httpServletResponse);
			methodReturnObject = parameters.values().isEmpty() ? method.invoke(httpController) : method.invoke(httpController, parameters.values().toArray());
		} catch (final IllegalArgumentException exception) {
			throw new HttpException(404);
		} catch (final IllegalAccessException | InvocationTargetException | IOException | ServletException exception) {
			throw new HttpException(500, exception);
		}
		
		if (methodReturnObject instanceof View) {
			final View view = (View) methodReturnObject;
			final String resolverPrefix = view.getLayoutName() == null ? WebApp.getWebAppConfiguration().getViewResolverPrefix() : WebApp.getWebAppConfiguration().getLayoutResolverPrefix();
			final String resolverSuffix = view.getLayoutName() == null ? WebApp.getWebAppConfiguration().getViewResolverSuffix() : WebApp.getWebAppConfiguration().getLayoutResolverSuffix();
			final String viewName;
			
			if (view.getLayoutName() == null) {
				viewName = view.getName();
			} else {
				viewName = view.getLayoutName();
				
				httpServletRequest.setAttribute("__view__", WebApp.getWebAppConfiguration().getViewResolverPrefix() + view.getName() + WebApp.getWebAppConfiguration().getViewResolverSuffix());
				httpServletRequest.setAttribute("__viewName__", view.getName());
			}
			
			httpServletRequest.setAttribute("__appName__", WebApp.getWebAppConfiguration().getAppName());
			httpServletRequest.setAttribute("__appVersion__", WebApp.getWebAppConfiguration().getAppVersion());
			
			try {
				httpServletRequest.getRequestDispatcher(resolverPrefix + viewName + resolverSuffix).forward(httpServletRequest, httpServletResponse);
			} catch (final ServletException | IOException exception) {
				throw new HttpException(500, exception);
			}
		} else if (!Void.TYPE.equals(method.getReturnType())) {
			final String acceptContentType = httpServletRequest.getHeader("Accept");
			final boolean useAcceptContentType = acceptContentType != null && !acceptContentType.equals("*/*");
			final Setable<String> contentType = new Setable<>(httpServletResponse.getContentType() == null ? useAcceptContentType ? acceptContentType : WebApp.getWebAppConfiguration().getNonViewDefaultContentType() : httpServletResponse.getContentType());
			final byte[] content = httpDispatcherParserManager.parseOutgoing(contentType, useAcceptContentType, methodReturnObject);
			
			HttpDispatcherHelper.httpServletResponseWrite(httpServletResponse, 200, contentType.getValue(), content);
		}
	}
	
	protected Map<String, Object> getParameters(final HttpDispatcherUriCompilation httpDispatcherUriCompilation, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws IOException, ServletException {
		final Map<String, Object> parameters = new LinkedHashMap<>();
		
		for (final Parameter parameter : httpDispatcherUriCompilation.getRequestMapping().getMethod().getParameters()) {
			final UriVariable uriVariable = parameter.isAnnotationPresent(UriVariable.class) ? parameter.getAnnotation(UriVariable.class) : null;
			final RequestParameter requestParameter = parameter.isAnnotationPresent(RequestParameter.class) ? parameter.getAnnotation(RequestParameter.class) : null;
			final RequestBody requestBody = parameter.isAnnotationPresent(RequestBody.class) ? parameter.getAnnotation(RequestBody.class) : null;
			final Class<?> type = parameter.getType();
			
			String name = parameter.getName();
			Object value = null;
			
			if (uriVariable != null) {
				if (!uriVariable.value().isEmpty()) {
					name = uriVariable.value();
				}
				
				value = generateBasicValue(httpDispatcherUriCompilation.getVariables().get(name), type);
			} else if (requestParameter != null) {
				if (!requestParameter.value().isEmpty()) {
					name = requestParameter.value();
				} else if (!parameter.isNamePresent()) {
					throw new HttpException(500, "-parameters compiler argument must be setted");
				}
				
				if (type.isArray()) {
					if (type.isAssignableFrom(Part.class)) {
						final String finalName = name;
						final List<Part> fileParts = httpServletRequest.getParts().stream().filter(part -> finalName.equals(part.getName())).collect(Collectors.toList());
						
						value = fileParts.toArray();
					} else {
						final String[] parameterValues = httpServletRequest.getParameterValues(name);
						
						if (parameterValues != null) {
							final Object values = Array.newInstance(type.getComponentType(), parameterValues.length);
							
							for (int i = 0; i < parameterValues.length; i++) {
								Array.set(values, i, generateBasicValue(parameterValues[i], type.getComponentType()));
							}
							
							value = values;
						}
					}
				} else {
					if (type.isAssignableFrom(Part.class)) {
						value = httpServletRequest.getPart(name);
					} else {
						value = generateBasicValue(httpServletRequest.getParameter(name), type);
					}
				}
				
				if (value == null && !requestParameter.defaultValue().isEmpty()) {
					value = ReflectionHelper.generateBasicValue(requestParameter.defaultValue(), type);
				} else if (value == null && !requestParameter.required() && type.isPrimitive()) {
					value = ReflectionHelper.generateDefaultValue(type);
				} else if (value == null && requestParameter.required()) {
					throw new HttpException(404);
				}
			} else if (requestBody != null) {
				Object requestBodyObject = null;
				
				if (Byte.class.isAssignableFrom(type)) {
					final byte[] bytes = getRequestBodyAsBytes(httpServletRequest);
					
					requestBodyObject = type.isArray() ? bytes : bytes.length > 0 ? bytes[0] : null;
				} else {
					final byte[] requestBodyBytes = getRequestBodyAsBytes(httpServletRequest);
					
					requestBodyObject = generateBasicValue(StringHelper.toString(requestBodyBytes), type);
					
					if (requestBodyObject == null) {
						final Setable<String> contentType = new Setable<>(httpServletRequest.getContentType() != null ? httpServletRequest.getContentType() : WebApp.getWebAppConfiguration().getNonViewDefaultContentType());
						
						requestBodyObject = httpDispatcherParserManager.parseIncoming(contentType, requestBodyBytes, type);
					}
				}
				
				value = requestBodyObject;
			} else {
				if (type.isAssignableFrom(HttpServletRequest.class)) {
					value = httpServletRequest;
				} else if (type.isAssignableFrom(HttpServletResponse.class)) {
					value = httpServletResponse;
				}
			}
			
			parameters.put(name, value);
		}
		
		return parameters;
	}
	
	protected String getRequestBodyAsString(final HttpServletRequest httpServletRequest) throws IOException {
		return StringHelper.toString(getRequestBodyAsBytes(httpServletRequest));
	}
	
	protected byte[] getRequestBodyAsBytes(final HttpServletRequest httpServletRequest) throws IOException {
		return ByteHelper.toBytes(httpServletRequest.getInputStream());
	}
	
	protected Object generateBasicValue(final String value, final Class<?> type) {
		if (type.isAssignableFrom(Date.class)) {
			try {
				return new SimpleDateFormat(DATE_TIME_FORMAT_PATTERN).parse(value);
			} catch (final ParseException exception) {
				return null;
			}
		} else {
			return ReflectionHelper.generateBasicValue(value, type);
		}
	}
}

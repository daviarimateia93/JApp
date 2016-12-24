package japp.web.dispatcher.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import japp.model.ModelApp;
import japp.util.ByteHelper;
import japp.util.JAppRuntimeException;
import japp.util.Setable;
import japp.util.SingletonFactory;
import japp.util.Singletonable;
import japp.util.StringHelper;
import japp.web.WebApp;
import japp.web.controller.http.HttpController;
import japp.web.controller.http.HttpControllerFactory;
import japp.web.controller.http.HttpControllerFactoryImpl;
import japp.web.controller.http.annotation.Requestable;
import japp.web.dispatcher.http.handler.HttpDispatcherHandler;
import japp.web.dispatcher.http.handler.HttpDispatcherHandlerImpl;
import japp.web.dispatcher.http.request.RequestMapping;
import japp.web.dispatcher.http.request.RequestMethod;
import japp.web.exception.HttpException;
import japp.web.uri.UriCompilation;
import japp.web.uri.UriCompiler;
import japp.web.uri.UriCompilerImpl;

public class HttpDispatcherImpl implements Singletonable, HttpDispatcher {
	
	protected final Map<String, RequestMapping> requestMappings;
	protected UriCompiler uriCompiler;
	protected HttpControllerFactory httpControllerFactory;
	protected HttpDispatcherHandler httpDispatcherHandler;
	
	public static synchronized HttpDispatcherImpl getInstance() {
		return SingletonFactory.getInstance(HttpDispatcherImpl.class);
	}
	
	protected HttpDispatcherImpl() {
		this.requestMappings = new HashMap<>();
		this.uriCompiler = UriCompilerImpl.getInstance();
		this.httpControllerFactory = HttpControllerFactoryImpl.getInstance();
		this.httpDispatcherHandler = HttpDispatcherHandlerImpl.getInstance();
	}
	
	public void setUriCompiler(final UriCompiler uriCompiler) {
		this.uriCompiler = uriCompiler;
	}
	
	public void setHttpControllerFactory(final HttpControllerFactory httpControllerFactory) {
		this.httpControllerFactory = httpControllerFactory;
	}
	
	public void setHttpDispatcherHandler(final HttpDispatcherHandler httpDispatcherHandler) {
		this.httpDispatcherHandler = httpDispatcherHandler;
	}
	
	@Override
	public void dispatch(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
		final boolean isOpenSessionView = WebApp.getWebAppConfiguration().isOpenSessionView();
		final Setable<EntityManager> entityManager = new Setable<>();
		final Setable<Exception> threadException = new Setable<>();
		
		try {
			final HttpDispatcherUriCompilation httpDispatcherUriCompilation = getHttpDispatcherUriCompilation(httpServletRequest);
			final Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					httpDispatcherHandler.handle(httpDispatcherUriCompilation, httpServletRequest, httpServletResponse);
				}
			};
			
			if (isOpenSessionView) {
				
				final Thread thread = new Thread() {
					public void run() {
						try {
							entityManager.setValue(ModelApp.getModelAppConfiguration().getRepositoryFactory().getEntityManager(WebApp.getWebAppConfiguration().getPersistenceUnitName(httpServletRequest), WebApp.getWebAppConfiguration().getPersistenceProperties(httpServletRequest)));
							
							ModelApp.getModelAppConfiguration().getRepositoryFactory().executeInNewTransaction(entityManager.getValue(), runnable);
							
							if (entityManager.getValue().isOpen()) {
								entityManager.getValue().close();
							}
						} catch (final Exception exception) {
							threadException.setValue(exception);
						}
					}
				};
				
				thread.start();
				thread.join();
				
				if (threadException.getValue() != null) {
					throw threadException.getValue();
				}
			} else {
				runnable.run();
			}
		} catch (final Exception exception) {
			handleUncaughtException(exception, httpServletRequest, httpServletResponse);
			
			if (isOpenSessionView && entityManager.getValue() != null && entityManager.getValue().isOpen()) {
				entityManager.getValue().close();
			}
		}
	}
	
	@Override
	public void handleUncaughtException(final Exception uncaughtException, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) {
		final int httpStatusCode = uncaughtException instanceof HttpException ? ((HttpException) uncaughtException).getHttpStatusCode() : 500;
		final String uncaughtExceptionMessage = uncaughtException instanceof HttpException ? uncaughtException.getCause() != null ? uncaughtException.getCause().getMessage() : uncaughtException.getMessage() : uncaughtException.getMessage();
		final StringBuilder contentStringBuilder = new StringBuilder();
		contentStringBuilder.append(StringHelper.isNullOrEmpty(uncaughtExceptionMessage) ? "" : uncaughtExceptionMessage);
		
		if (httpStatusCode == 500) {
			final StringWriter stringWriter = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(stringWriter);
			
			uncaughtException.printStackTrace(printWriter);
			uncaughtException.printStackTrace();
			
			contentStringBuilder.append("\r\n" + stringWriter.toString());
		}
		
		HttpDispatcherHelper.httpServletResponseWrite(httpServletResponse, httpStatusCode, "text/plain", ByteHelper.toBytes(contentStringBuilder.toString()));
	}
	
	@Override
	public void clearRequestMappings() {
		requestMappings.clear();
	}
	
	@Override
	public <T extends HttpController> void register(final Class<T> httpControllerClass) {
		final String servletContextConfiguration = HttpDispatcherServlet.getConfiguration("servlet-context");
		final Requestable rootRequestable = httpControllerClass.isAnnotationPresent(Requestable.class) ? httpControllerClass.getAnnotation(Requestable.class) : null;
		
		for (final Method method : httpControllerClass.getDeclaredMethods()) {
			if (!Modifier.isPublic(method.getModifiers())) {
				throw new JAppRuntimeException(String.format("%s must be public", method.getName()));
			}
			
			if (method.isAnnotationPresent(Requestable.class)) {
				final Requestable requestable = method.getAnnotation(Requestable.class);
				final RequestMethod[] requestMethods = requestable.method().length > 0 ? requestable.method() : rootRequestable.method();
				
				if (requestMethods.length == 0) {
					throw new JAppRuntimeException(String.format("%s and %s has no request method", httpControllerClass.getName(), method.getName()));
				}
				
				for (final RequestMethod requestMethod : requestMethods) {
					final String[] produces = requestable.produces() != null && requestable.produces().length > 0 ? requestable.produces() : rootRequestable.produces();
					final String[] consumes = requestable.consumes() != null && requestable.consumes().length > 0 ? requestable.consumes() : rootRequestable.consumes();
					
					String uriPattern = (!StringHelper.isBlank(requestable.value()) ? rootRequestable.value() + requestable.value() : rootRequestable.value()).trim();
					
					if (servletContextConfiguration != null && servletContextConfiguration.endsWith("/") && uriPattern.startsWith("/")) {
						uriPattern = uriPattern.substring(1);
					}
					
					uriPattern = (servletContextConfiguration == null ? "" : servletContextConfiguration) + uriPattern;
					
					if (uriPattern.isEmpty()) {
						uriPattern = "/";
					} else {
						if (uriPattern.startsWith("//")) {
							uriPattern = uriPattern.substring(1);
						} else if (!uriPattern.startsWith("/")) {
							uriPattern = "/" + uriPattern;
						}
						
						if (uriPattern.endsWith("//")) {
							uriPattern = uriPattern.substring(0, uriPattern.length() - 1);
						}
					}
					
					addRequestMapping(new RequestMapping(httpControllerFactory.getHttpController(httpControllerClass), method, requestMethod, uriPattern, produces, consumes));
				}
			}
		}
	}
	
	protected String getRequestMappingKey(final RequestMapping requestMapping) {
		return requestMapping.getRequestMethod() + ": " + requestMapping.getUriPattern();
	}
	
	protected void addRequestMapping(final RequestMapping requestMapping) {
		final String key = getRequestMappingKey(requestMapping);
		
		if (requestMappings.containsKey(key)) {
			throw new JAppRuntimeException(String.format("%s already mapped", key));
		}
		
		requestMappings.put(key, requestMapping);
	}
	
	protected HttpDispatcherUriCompilation getHigherScoreHttpDispatcherUriCompilation(final List<HttpDispatcherUriCompilation> httpDispatcherUriCompilations) {
		HttpDispatcherUriCompilation higherScoreHttpDispatcherUriCompilation = null;
		
		for (final HttpDispatcherUriCompilation httpDispatcherUriCompilation : httpDispatcherUriCompilations) {
			if (higherScoreHttpDispatcherUriCompilation == null || higherScoreHttpDispatcherUriCompilation.getScore() < httpDispatcherUriCompilation.getScore()) {
				higherScoreHttpDispatcherUriCompilation = httpDispatcherUriCompilation;
			}
		}
		
		return higherScoreHttpDispatcherUriCompilation;
	}
	
	protected HttpDispatcherUriCompilation getHttpDispatcherUriCompilation(final HttpServletRequest httpServletRequest) {
		final String uriWithoutContextPath = HttpDispatcherHelper.getUriWithoutContextPath(httpServletRequest);
		final List<HttpDispatcherUriCompilation> httpDispatcherUriCompilations = new ArrayList<>();
		
		for (final Map.Entry<String, RequestMapping> entry : requestMappings.entrySet()) {
			final RequestMapping requestMapping = entry.getValue();
			final UriCompilation uriCompilation = uriCompiler.compile(requestMapping.getUriPattern(), requestMapping.getUriPattern().endsWith("/") && !uriWithoutContextPath.endsWith("/") ? uriWithoutContextPath + "/" : uriWithoutContextPath);
			final String acceptContentType = httpServletRequest.getHeader("Accept");
			final String contentType = httpServletRequest.getContentType();
			final boolean requestMethodIsValid = requestMapping.getRequestMethod() == RequestMethod.valueOf(httpServletRequest.getMethod().toUpperCase());
			final boolean consumesIsValid = requestMapping.getConsumes() != null && requestMapping.getConsumes().length > 0 ? HttpDispatcherHelper.containsContentType(requestMapping.getConsumes(), contentType) : true;
			final boolean producesIsValid = requestMapping.getProduces() != null && requestMapping.getProduces().length > 0 && acceptContentType != null ? HttpDispatcherHelper.containsContentType(requestMapping.getProduces(), acceptContentType.split("\\,")) : true;
			
			if (uriCompilation.isValid() && requestMethodIsValid && consumesIsValid && producesIsValid) {
				httpDispatcherUriCompilations.add(new HttpDispatcherUriCompilation(requestMapping, uriCompilation));
			}
		}
		
		final HttpDispatcherUriCompilation httpDispatcherUriCompilation = getHigherScoreHttpDispatcherUriCompilation(httpDispatcherUriCompilations);
		
		if (httpDispatcherUriCompilation == null) {
			throw new HttpException(404, String.format("No request mapping found for %s", uriWithoutContextPath));
		} else {
			return httpDispatcherUriCompilation;
		}
	}
}

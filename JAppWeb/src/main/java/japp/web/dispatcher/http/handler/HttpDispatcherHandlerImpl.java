package japp.web.dispatcher.http.handler;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Date;
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
import japp.util.Reference;
import japp.util.ReflectionHelper;
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

public class HttpDispatcherHandlerImpl implements HttpDispatcherHandler {

    private static String dateTimeFormatPattern = DateHelper.getDateTimeFormatPattern();
    private static String dateFormatPattern = DateHelper.getDateFormatPattern();
    private static String timeFormatPattern = DateHelper.getTimeFormatPattern();

    private final HttpDispatcherParserManager httpDispatcherParserManager;

    public HttpDispatcherHandlerImpl() {
        this(HttpDispatcherParserManagerImpl.getInstance());
    }

    public HttpDispatcherHandlerImpl(final HttpDispatcherParserManager httpDispatcherParserManager) {
        this.httpDispatcherParserManager = httpDispatcherParserManager;
    }

    public static String getDateTimeFormatPattern() {
        return dateTimeFormatPattern;
    }

    public static void setDateTimeFormatPattern(final String dateTimeFormatPattern) {
        HttpDispatcherHandlerImpl.dateTimeFormatPattern = dateTimeFormatPattern;
    }

    public static String getDateFormatPattern() {
        return dateFormatPattern;
    }

    public static void setDateFormatPattern(final String dateFormatPattern) {
        HttpDispatcherHandlerImpl.dateFormatPattern = dateFormatPattern;
    }

    public static String getTimeFormatPattern() {
        return timeFormatPattern;
    }

    public static void setTimeFormatPattern(final String timeFormatPattern) {
        HttpDispatcherHandlerImpl.timeFormatPattern = timeFormatPattern;
    }

    @Override
    public void handle(
            final HttpDispatcherUriCompilation httpDispatcherUriCompilation,
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse) {

        final RequestMapping requestMapping = httpDispatcherUriCompilation.getRequestMapping();
        final HttpController httpController = requestMapping.getHttpController();
        final Method method = requestMapping.getMethod();
        final Map<String, Object> parameters;
        final Object methodReturnObject;

        try {
            parameters = getParameters(httpDispatcherUriCompilation, httpServletRequest, httpServletResponse);

            methodReturnObject = parameters.values().isEmpty()
                    ? method.invoke(httpController)
                    : method.invoke(httpController, parameters.values().toArray());

        } catch (final IllegalArgumentException exception) {
            throw new HttpException(404);
        } catch (final IllegalAccessException | InvocationTargetException | IOException | ServletException exception) {
            throw new HttpException(500, exception);
        }

        if (methodReturnObject instanceof View) {
            handleView(methodReturnObject, httpServletRequest, httpServletResponse);
        } else if (!Void.TYPE.equals(method.getReturnType())) {
            handleNonVoid(methodReturnObject, requestMapping, httpServletRequest, httpServletResponse);
        }
    }

    protected void handleView(
            final Object methodReturnObject,
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse) {

        final View view = (View) methodReturnObject;
        final String viewResolverPrefix = WebApp.getWebAppConfiguration().getViewResolverPrefix();
        final String viewResolverSuffix = WebApp.getWebAppConfiguration().getViewResolverSuffix();

        final String resolverPrefix = view.getLayoutName() == null
                ? viewResolverPrefix
                : WebApp.getWebAppConfiguration().getLayoutResolverPrefix();

        final String resolverSuffix = view.getLayoutName() == null
                ? viewResolverSuffix
                : WebApp.getWebAppConfiguration().getLayoutResolverSuffix();

        final String viewName;

        if (view.getLayoutName() == null) {
            viewName = view.getName();
        } else {
            viewName = view.getLayoutName();

            httpServletRequest.setAttribute("__view__", viewResolverPrefix + view.getName() + viewResolverSuffix);
            httpServletRequest.setAttribute("__viewName__", view.getName());
        }

        httpServletRequest.setAttribute("__appName__", WebApp.getWebAppConfiguration().getAppName());
        httpServletRequest.setAttribute("__appVersion__", WebApp.getWebAppConfiguration().getAppVersion());

        try {
            httpServletRequest
                    .getRequestDispatcher(resolverPrefix + viewName + resolverSuffix)
                    .forward(httpServletRequest, httpServletResponse);
        } catch (final ServletException | IOException exception) {
            throw new HttpException(500, exception);
        }
    }

    protected void handleNonVoid(
            final Object methodReturnObject,
            final RequestMapping requestMapping,
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse) {
        
        final String acceptContentType = httpServletRequest.getHeader("Accept");
        final boolean useAcceptContentType = acceptContentType != null && !acceptContentType.equals("*/*");
        final String contentType;

        if (httpServletResponse.getContentType() == null) {
            if (requestMapping.getProduces().length > 0) {
                contentType = requestMapping.getProduces()[0];
            } else if (useAcceptContentType) {
                contentType = acceptContentType;
            } else {
                contentType = WebApp.getWebAppConfiguration().getNonViewDefaultContentType();
            }
        } else {
            contentType = httpServletResponse.getContentType();
        }

        final byte[] content = httpDispatcherParserManager
                .parseOutgoing(contentType, useAcceptContentType, methodReturnObject);

        HttpDispatcherHelper.httpServletResponseWrite(httpServletResponse, 200, contentType, content);
    }

    protected Map<String, Object> getParameters(
            final HttpDispatcherUriCompilation httpDispatcherUriCompilation,
            final HttpServletRequest httpServletRequest,
            final HttpServletResponse httpServletResponse)
            throws IOException, ServletException {

        final Map<String, Object> parameters = new LinkedHashMap<>();

        for (final Parameter parameter : httpDispatcherUriCompilation.getRequestMapping().getMethod().getParameters()) {
            final Class<?> type = parameter.getType();
            final Reference<String> name = new Reference<>(parameter.getName());
            final Reference<Object> value = new Reference<>();

            final UriVariable uriVariable = parameter.isAnnotationPresent(UriVariable.class)
                    ? parameter.getAnnotation(UriVariable.class)
                    : null;

            final RequestParameter requestParameter = parameter.isAnnotationPresent(RequestParameter.class)
                    ? parameter.getAnnotation(RequestParameter.class)
                    : null;

            final RequestBody requestBody = parameter.isAnnotationPresent(RequestBody.class)
                    ? parameter.getAnnotation(RequestBody.class)
                    : null;

            if (uriVariable != null) {
                setNameAndValueFromUriVariable(uriVariable, httpDispatcherUriCompilation, type, name, value);
            } else if (requestParameter != null) {
                setNameAndValueFromRequestParameter(requestParameter, parameter, type, httpServletRequest, name, value);
            } else if (requestBody != null) {
                setValueFromRequestBody(requestBody, httpServletRequest, type, value);
            } else {
                if (type.isAssignableFrom(HttpServletRequest.class)) {
                    value.set(httpServletRequest);
                } else if (type.isAssignableFrom(HttpServletResponse.class)) {
                    value.set(httpServletResponse);
                }
            }

            parameters.put(name.get(), value.get());
        }

        return parameters;
    }

    protected void setNameAndValueFromUriVariable(
            final UriVariable uriVariable,
            final HttpDispatcherUriCompilation httpDispatcherUriCompilation,
            final Class<?> type,
            final Reference<String> name,
            final Reference<Object> value) {

        if (!uriVariable.value().isEmpty()) {
            name.set(uriVariable.value());
        }

        value.set(generateBasicValue(httpDispatcherUriCompilation.getVariables().get(name.get()), type));
    }

    protected void setNameAndValueFromRequestParameter(
            final RequestParameter requestParameter,
            final Parameter parameter,
            final Class<?> type,
            final HttpServletRequest httpServletRequest,
            final Reference<String> name,
            final Reference<Object> value) throws IOException, ServletException {

        if (!requestParameter.value().isEmpty()) {
            name.set(requestParameter.value());
        } else if (!parameter.isNamePresent()) {
            throw new HttpException(500, "-parameters compiler argument must be setted");
        }

        if (type.isArray()) {
            if (type.isAssignableFrom(Part.class)) {
                final List<Part> fileParts = httpServletRequest
                        .getParts()
                        .stream()
                        .filter(part -> name.get().equals(part.getName()))
                        .collect(Collectors.toList());

                value.set(fileParts.toArray());
            } else {
                final String[] parameterValues = httpServletRequest.getParameterValues(name.get());

                if (parameterValues != null) {
                    final Object values = Array.newInstance(type.getComponentType(), parameterValues.length);

                    for (int i = 0; i < parameterValues.length; i++) {
                        Array.set(values, i, generateBasicValue(parameterValues[i], type.getComponentType()));
                    }

                    value.set(values);
                }
            }
        } else {
            if (type.isAssignableFrom(Part.class)) {
                value.set(httpServletRequest.getPart(name.get()));
            } else {
                value.set(generateBasicValue(httpServletRequest.getParameter(name.get()), type));
            }
        }

        if (value.get() == null && !requestParameter.defaultValue().isEmpty()) {
            value.set(ReflectionHelper.generateBasicValue(requestParameter.defaultValue(), type));
        } else if (value.get() == null && !requestParameter.required() && type.isPrimitive()) {
            value.set(ReflectionHelper.generateDefaultValue(type));
        } else if (value.get() == null && requestParameter.required()) {
            throw new HttpException(404);
        }
    }

    protected void setValueFromRequestBody(
            final RequestBody requestBody,
            final HttpServletRequest httpServletRequest,
            final Class<?> type,
            final Reference<Object> value) throws IOException {

        final byte[] requestBodyBytes = getRequestBodyAsBytes(httpServletRequest);

        Object requestBodyObject = null;

        if (Byte.class.isAssignableFrom(type)) {
            requestBodyObject = type.isArray()
                    ? requestBodyBytes
                    : requestBodyBytes.length > 0 ? requestBodyBytes[0] : null;
        } else {
            requestBodyObject = generateBasicValue(StringHelper.toString(requestBodyBytes), type);

            if (requestBodyObject == null) {
                final String contentType = httpServletRequest.getContentType() != null
                        ? httpServletRequest.getContentType()
                        : WebApp.getWebAppConfiguration().getNonViewDefaultContentType();

                requestBodyObject = httpDispatcherParserManager
                        .parseIncoming(contentType, requestBodyBytes, type);
            }
        }

        value.set(requestBodyObject);
    }

    private byte[] getRequestBodyAsBytes(final HttpServletRequest httpServletRequest) throws IOException {
        return ByteHelper.toBytes(httpServletRequest.getInputStream());
    }

    protected Object generateBasicValue(final String value, final Class<?> type) {
        if (type.isAssignableFrom(Date.class) && value != null) {
            return DateHelper.parseDate(value, dateTimeFormatPattern, dateFormatPattern, timeFormatPattern);
        } else {
            return ReflectionHelper.generateBasicValue(value, type);
        }
    }
}

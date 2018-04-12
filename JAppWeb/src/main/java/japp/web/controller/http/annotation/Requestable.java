package japp.web.controller.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import japp.util.ProxyMethodInterceptable;
import japp.web.dispatcher.http.request.RequestMethod;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
@ProxyMethodInterceptable
public @interface Requestable {
    String value() default "";

    RequestMethod[] method() default { RequestMethod.GET };

    String[] produces() default {};

    String[] consumes() default {};
}

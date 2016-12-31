package japp.model.service.authorization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import japp.util.ProxyMethodInterceptable;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ProxyMethodInterceptable
public @interface Role {
	String[] value() default {};
}

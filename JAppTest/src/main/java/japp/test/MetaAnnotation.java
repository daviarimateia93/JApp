package japp.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import japp.util.ProxyMethodInterceptable;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ProxyMethodInterceptable
public @interface MetaAnnotation {

}

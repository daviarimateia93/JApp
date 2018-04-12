package japp.model.service.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import japp.util.ProxyMethodInterceptable;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ProxyMethodInterceptable
public @interface Transactionable {

    public static enum Mode {
        CURRENT, CURRENT_OR_NEW, NEW
    }

    Mode value() default Mode.CURRENT_OR_NEW;
}

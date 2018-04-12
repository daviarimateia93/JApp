package japp.util;

import java.util.List;

import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;

public class ProxyEnhancer extends Enhancer {

    @SuppressWarnings("rawtypes")
    @Override
    protected void filterConstructors(Class sc, List constructors) {
        CollectionUtils.filter(constructors, new Predicate() {

            @Override
            public boolean evaluate(final Object arg) {
                return true;
            }
        });
    }
}

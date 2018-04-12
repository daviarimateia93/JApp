package japp.util;

import java.util.Base64;

public abstract class Base64Helper {

    protected Base64Helper() {

    }

    public static byte[] encode(final byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    public static byte[] decode(final byte[] bytes) {
        return Base64.getDecoder().decode(bytes);
    }
}

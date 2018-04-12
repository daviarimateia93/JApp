package japp.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public abstract class RsaHelper {

    public static final String ALGORITHM_SHA_512 = "SHA-512";
    public static final String ALGORITHM_RSA = "RSA";

    protected RsaHelper() {

    }

    public static byte[] encrypt(final byte[] bytes, final PrivateKey privateKey)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);

            return cipher.doFinal(bytes);
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException exception) {
            return null;
        }
    }

    public static byte[] decrypt(final byte[] bytes, final PublicKey publicKey)
            throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);

            return cipher.doFinal(bytes);
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException exception) {
            return null;
        }
    }
}

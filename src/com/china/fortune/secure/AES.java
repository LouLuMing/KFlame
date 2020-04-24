package com.china.fortune.secure;

import com.china.fortune.global.Log;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    public static byte[] decrypt(byte[] byteMi, byte[] aesKey, byte[] iv, String mode) {
        try {
            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            Cipher cipher = Cipher.getInstance(mode);
            if (iv != null) {
                IvParameterSpec zeroIv = new IvParameterSpec(iv);
                cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key);
            }
            return cipher.doFinal(byteMi);
        } catch (Exception e) {
            Log.logException(e);
        }
        return null;
    }

    public static byte[] encrypt(byte[] byteMi, byte[] aesKey, byte[] iv, String mode) {
        try {
            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            Cipher cipher = Cipher.getInstance(mode);
            if (iv != null) {
                IvParameterSpec zeroIv = new IvParameterSpec(iv);
                cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
            return cipher.doFinal(byteMi);
        } catch (Exception e) {
            Log.logException(e);
        }
        return null;
    }

    public static byte[] decrypt(byte[] byteMi, byte[] aesKey) {
        return decrypt(byteMi, aesKey, null, "AES/ECB/PKCS5Padding");
    }

    public static byte[] encrypt(byte[] byteMi, byte[] aesKey) {
        return encrypt(byteMi, aesKey, null, "AES/ECB/PKCS5Padding");
    }

	public static String decryptCBCBase64(String encrypted, byte[] aesKey, byte[] iv) {
        try {
            byte[] byteMi = Base64.getDecoder().decode(encrypted);
            byte[] decryptedData = decrypt(byteMi, aesKey, iv, "AES/CBC/PKCS5Padding");
            if (decryptedData != null) {
                return new String(decryptedData, "UTF-8");
            }
        } catch (Exception e) {
            Log.logException(e);
        }
        return null;
	}

    public static String encryptCBCBase64(String encrypted, byte[] aesKey, byte[] iv) {
        try {
            byte[] byteMi = encrypted.getBytes();
            byte[] decryptedData = encrypt(byteMi, aesKey, iv, "AES/CBC/PKCS5Padding");
            if (decryptedData != null) {
                return Base64.getEncoder().encodeToString(decryptedData);
            }
        } catch (Exception e) {
            Log.logException(e);
        }
        return null;
    }
}

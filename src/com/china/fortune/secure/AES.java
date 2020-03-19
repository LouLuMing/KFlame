package com.china.fortune.secure;

import com.china.fortune.global.Log;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	public static String decryptCBC(String encrypted, byte[] aesKey, byte[] iv) {  
        try {  
            byte[] byteMi = Base64.getDecoder().decode(encrypted);  
            IvParameterSpec zeroIv = new IvParameterSpec(iv);  
            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte[] decryptedData = cipher.doFinal(byteMi);  
            return new String(decryptedData, "UTF-8");
        } catch (Exception e) {
            Log.logException(e);
        }  
        return null;
	}

    public static String encryptCBC(String encrypted, byte[] aesKey, byte[] iv) {
        try {
            byte[] byteMi = encrypted.getBytes();
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] decryptedData = cipher.doFinal(byteMi);
            return Base64.getEncoder().encodeToString(decryptedData);
        } catch (Exception e) {
            Log.logException(e);
        }
        return null;
    }
}

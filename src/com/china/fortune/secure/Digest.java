package com.china.fortune.secure;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.china.fortune.common.ByteAction;

public class Digest {
	public static String getMD5(byte[] bData) {
		String sMd5 = "";
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(bData);
			byte[] m = md5.digest();
			sMd5 = ByteAction.toHexStringLower(m);
		} catch (NoSuchAlgorithmException e)  {
		}
		return sMd5;
	}  
	
	public static String getMD5Base64(String sSource) {
		return getMD5Base64(sSource.getBytes());
	} 
	
	public static String getMD5Base64(byte[] bData) {
		String sMd5 = "";
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(bData);
			byte[] m = md5.digest();
			sMd5 = Base64.getEncoder().encodeToString(m);
		} catch (NoSuchAlgorithmException e)  {
		}
		return sMd5;
	}  
	
	public static String getMD5(String sSource) {
		return getMD5(sSource.getBytes());
	}

	public static byte[] toSHA(String sData) {
		return toSHA(sData.getBytes());
	}

	public static byte[] toSHA(byte[] bData) {
		byte[] m = null;
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("SHA");
			md5.update(bData);
			m = md5.digest();
		} catch (NoSuchAlgorithmException e)  {
		}
		return m;
	}

	public static String getSHA(byte[] bData) {
		String sSHA = "";
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("SHA");
			md5.update(bData);
			byte[] m = md5.digest();
			sSHA = ByteAction.toHexStringLower(m);
		} catch (NoSuchAlgorithmException e)  {
		}
		return sSHA;
	} 
	
	public static String getSHA(String sSource) {
		return getSHA(sSource.getBytes());
	}  
	
	public static String HMACSHA256(byte[] bData, byte[] bKey) {
		try {
			SecretKeySpec signingKey = new SecretKeySpec(bKey, "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);
			return ByteAction.toHexStringLower(mac.doFinal(bData));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String HMACSHA256(String sSource, String sKey) {
		return HMACSHA256(sSource.getBytes(), sKey.getBytes());
	}  

}

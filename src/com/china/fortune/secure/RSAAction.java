package com.china.fortune.secure;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import com.china.fortune.global.Log;

public class RSAAction {
	static public PublicKey getPublicKeyFromX509(String algorithm, String bysKey)
			throws NoSuchAlgorithmException, Exception {
		byte[] decodedKey = Base64.getDecoder().decode(bysKey);
		X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);

		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
		return keyFactory.generatePublic(x509);
	}
	
	static public PrivateKey getPrivateKeyFromPKCS8(String algorithm, String privateKey)
			throws NoSuchAlgorithmException, Exception {
		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
		KeyFactory keyf = KeyFactory.getInstance(algorithm);
		return keyf.generatePrivate(priPKCS8);
//		
//		byte[] decodedKey = Base64.getDecoder().decode(bysKey);
//		X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);
//
//		KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
//		return keyFactory.generatePrivate(x509);
	}

	public static String getKeyString(Key key) throws Exception {
		byte[] keyBytes = key.getEncoded();
		return Base64.getEncoder().encodeToString(keyBytes);
	}

	static public PublicKey getPublicKeyFromX509(String sFile) {
		PublicKey keyStore = null;
		File file = new File(sFile);
		if (file.exists() && file.isFile()) {
			InputStream ins = null;
			try {
				ins = new FileInputStream(file);
				CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
				Certificate cer = cerFactory.generateCertificate(ins);
				keyStore = cer.getPublicKey();
			} catch (Exception e) {
				Log.logException(e);
			} finally {
				if (ins != null) {
					try {
						ins.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return keyStore;
	}

	static public KeyStore loadKeyStore(String sFile) {
		KeyStore keyStore = null;
		File file = new File(sFile);
		if (file.exists() && file.isFile()) {
			InputStream ins = null;
			try {
				ins = new FileInputStream(file);
				Certificate cer = CertificateFactory.getInstance("X.509").generateCertificate(ins);
				keyStore = KeyStore.getInstance("PKCS12", "BC"); // 问2
				keyStore.load(null, null);
				keyStore.setCertificateEntry("trust", cer);
			} catch (Exception e) {
				Log.logException(e);
			} finally {
				if (ins != null) {
					try {
						ins.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return keyStore;
	}

	static public String encrypt(String sContent, Key key) {
		String sEncode = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] otherText = cipher.doFinal(sContent.getBytes("utf-8"));
			sEncode = Base64.getEncoder().encodeToString(otherText);
		} catch (Exception e) {
			Log.logException(e);
		}
		return sEncode;
	}

	static public String encryptByPublicKey(String sContent, String sKey) {
		String sEncode = null;
		try {
			sEncode = encrypt(sContent, getPublicKeyFromX509("RSA", sKey));
		} catch (Exception e) {
			Log.logException(e);
		}
		return sEncode;
	}

	static public String decrypt(String sContent, Key key) {
		String sEncode = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] bData = Base64.getDecoder().decode(sContent);
			byte[] otherText = cipher.doFinal(bData);
			sEncode = new String(otherText, "utf-8");
		} catch (Exception e) {
			Log.logException(e);
		}
		return sEncode;
	}

	static public String decryptByPrivateKey(String sContent, String privateKey) {
		String sEncode = null;
		try {
			sEncode = decrypt(sContent, getPrivateKeyFromPKCS8("RSA", privateKey));
		} catch (Exception e) {
			Log.logException(e);
		}
		return sEncode;
	}

//	static public String decrypt(String sContent, String privateKey) {
//		String sEncode = null;
//		try {
//			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
//			KeyFactory keyf = KeyFactory.getInstance("RSA");
//			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
//			
//			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//			cipher.init(Cipher.DECRYPT_MODE, priKey);
//			byte[] otherText = cipher.doFinal(sContent.getBytes("utf-8"));
//			sEncode = Base64.getEncoder().encodeToString(otherText);
//		} catch (Exception e) {
//			Log.logException(e);
//		}
//		return sEncode;
//	}
	
	public static String signature(String content, String privateKey, String algorithms) {
		String sData = null;
		try {
//			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
//			KeyFactory keyf = KeyFactory.getInstance("RSA");
//			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
			PrivateKey priKey = getPrivateKeyFromPKCS8("RSA", privateKey);
			Signature signature = Signature.getInstance(algorithms);

			signature.initSign(priKey);
			signature.update(content.getBytes("utf-8"));

			byte[] signed = signature.sign();
			sData = Base64.getEncoder().encodeToString(signed);
		} catch (Exception e) {
			Log.logException(e);
		}
		return sData;
	}

	public static boolean checkSignature(String sSrc, String sSign, String publicKey, String algorithms) {
		try {
//			X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
//			KeyFactory keyf = KeyFactory.getInstance("RSA");
//			PublicKey pubKey = keyf.generatePublic(bobPubKeySpec);
			PublicKey pubKey = getPublicKeyFromX509("RSA", publicKey);
			Signature signature = Signature.getInstance(algorithms);
			byte[] signed = Base64.getDecoder().decode(sSign);
			signature.initVerify(pubKey);
			signature.update(sSrc.getBytes("utf-8"));
            return signature.verify(signed);
		} catch (Exception e) {
			Log.logException(e);
		}
		return false;
	}
	
	public static void main(String[] args) {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			KeyPair key = keyGen.generateKeyPair();
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			// 把第二个参数改为 key.getPrivate()
			cipher.init(Cipher.ENCRYPT_MODE, key.getPublic());
			byte[] cipherText = cipher.doFinal("Message".getBytes("utf-8"));
			System.out.println(Base64.getEncoder().encodeToString(cipherText));
			// 把第二个参数改为key.getPublic()
			cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
			byte[] newPlainText = cipher.doFinal(cipherText);
			System.out.println(new String(newPlainText, "utf-8"));

			System.out.println("RSA");
			System.out.println(getKeyString(key.getPublic()));
			System.out.println(getKeyString(key.getPrivate()));
			
			String sSrc = "hello world";
			String sSgin = signature(sSrc, getKeyString(key.getPrivate()), "MD5withRSA");
			Log.log(sSgin);
			Log.log("" + checkSignature(sSrc, sSgin, getKeyString(key.getPublic()), "MD5withRSA"));

			sSgin = encrypt(sSrc, key.getPrivate());
			Log.log(sSgin);
			Log.log("" + decrypt(sSgin, key.getPublic()));

			// String PUBLIC_KEY =
			// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCqgYhBKyHRNM9YjHcHHZ9iJA5hGrupm5RlIDYrgQIrWAvV51UhbuXaaS+ZWOb7LHA2vvcOWuJu66WstU8gZjjNEQo6VuUyavif4RFPdG5CGCPugWHuPY0GuKUqi+r9X9oWDynI8EfQIcIvEzux/cXf6Ocvvixjj5B4XMsPvJoCBQIDAQAB";
			// cipher.init(Cipher.ENCRYPT_MODE,
			// getPublicKeyFromX509("RSA", PUBLIC_KEY));
			// byte[] otherText = cipher.doFinal("Message".getBytes("utf-8"));
			// System.out.println(Base64.getEncoder().encodeToString(otherText));
			//
			// cipher.init(Cipher.ENCRYPT_MODE,
			// getPublicKeyFromX509("z:\\tcpsrsa_310001047654.crt"));
			// otherText = cipher.doFinal("Message".getBytes("utf-8"));
			// System.out.println(Base64.getEncoder().encodeToString(otherText));
		} catch (Exception e) {
		}
	}

}

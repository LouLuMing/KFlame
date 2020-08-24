package com.china.fortune.secure;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import com.china.fortune.global.Log;

public class KeyStoreUtils {
// KeyStore Type: JKS, PKCS12
	static public KeyManagerFactory readKeyManager(String sKeyStoreType, String sFile, String sPassword) {
		KeyManagerFactory kmf = null;
		try {
			kmf = KeyManagerFactory.getInstance("SunX509");
			KeyStore ks = KeyStore.getInstance(sKeyStoreType);
			FileInputStream fis = new FileInputStream(sFile);
			ks.load(fis, sPassword.toCharArray());
			kmf.init(ks, sPassword.toCharArray());
			fis.close();
		} catch (Exception e) {
			Log.logException(e);
		}
		return kmf;
	}

	static public TrustManagerFactory readTrustManager(String sKeyStoreType, String sFile, String sPassword) {
		TrustManagerFactory tmf = null;
		try {
			tmf = TrustManagerFactory.getInstance("X509");
			KeyStore tks = KeyStore.getInstance(sKeyStoreType);
			FileInputStream fis = new FileInputStream(sFile);
			tks.load(fis, sPassword.toCharArray());
			tmf.init(tks);
			fis.close();
		} catch (Exception e) {
			Log.logException(e);
		}
		return tmf;
	}
}

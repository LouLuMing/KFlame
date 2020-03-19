package com.china.fortune.socket;

import java.io.InputStream;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.*;

import com.china.fortune.global.Log;
import com.china.fortune.http.client.TrustAllCertsManager;

public class SecretSocketFactory {
	public static SocketFactory createSocketFactory(InputStream keyStore, String password, 
			String keystoreType, String algorithm, String protocol) {
		SocketFactory sf = null;
		try {
			char[] pwdChars = password.toCharArray();
			KeyStore ks = KeyStore.getInstance(keystoreType);
			ks.load(keyStore, pwdChars);
			KeyManagerFactory kf = KeyManagerFactory.getInstance(algorithm);
			kf.init(ks, pwdChars);
			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
	        tmf.init((KeyStore)null);
	        SSLContext context = SSLContext.getInstance(protocol);
			context.init(kf.getKeyManagers(), tmf.getTrustManagers(), null);
			
			sf = context.getSocketFactory();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return sf;
	}

	public static SocketFactory createSocketFactory(KeyManagerFactory kmf, TrustManagerFactory tmf) {
		SocketFactory sf = null;
		try {
			try {
				SSLContext sc = SSLContext.getInstance("SSL");
				KeyManager[] km = null;
				if (kmf != null) {
					km = kmf.getKeyManagers();
				}
				TrustManager[] tm = null;
				if (tmf != null) {
					tm = tmf.getTrustManagers();
				} else {
					tm = new TrustManager[] { new TrustAllCertsManager() };
				}
				sc.init(km, tm, null);
				sf = sc.getSocketFactory();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return sf;
	}
}

package com.china.fortune.socket;

import com.china.fortune.global.Log;
import com.china.fortune.http.client.TrustAllCertsManager;

import javax.net.ssl.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketUtils {
    private int iTimeout = 5 * 1000;
    private SSLSocketFactory sSLSocketFactory = null;

    public void setTimeOut(int iConnect, int iRecv) {
        iTimeout = iConnect;
        iTimeout = iRecv;
    }

    public boolean setSocketFactory(KeyManagerFactory kmf, TrustManagerFactory tmf) {
        sSLSocketFactory = getSocketFactory(kmf, tmf);
        return sSLSocketFactory != null;
    }

    public Socket createSocket(String sServerIP, int iPort, boolean bHttp) {
        if (bHttp) {
            return createSocket(sServerIP, iPort);
        } else {
            if (sSLSocketFactory == null) {
                sSLSocketFactory = getSocketFactory(null, null);
            }
            return createSSLSocket(sSLSocketFactory, sServerIP, iPort);
        }
    }

    public Socket createSocket(String sServerIP, int iPort) {
        InetSocketAddress socketAddress = new InetSocketAddress(sServerIP, iPort);
        Socket socket = new Socket();
        try {
            socket.connect(socketAddress, iTimeout);
            socket.setSoTimeout(iTimeout);
        } catch (Exception e) {
            socket = null;
            Log.logClassError(e.getMessage());
        }
        return socket;
    }

    public SSLSocketFactory getSocketFactory(KeyManagerFactory kmf, TrustManagerFactory tmf) {
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
            return sc.getSocketFactory();
        } catch (Exception e) {
            Log.logClassError(e.getMessage());
        }
        return null;
    }

    public Socket createSSLSocket(SSLSocketFactory sSLSocketFactory, String sServerIP, int iPort) {
        Socket sslSocket = null;
        if (sSLSocketFactory != null) {
            try {
                sslSocket = sSLSocketFactory.createSocket(sServerIP, iPort);
                sslSocket.setSoTimeout(iTimeout);
            } catch (Exception e) {
                Log.logClassError(e.getMessage());
            }
        }
        return sslSocket;
    }

    public Socket createSSLSocket(KeyManagerFactory kmf, TrustManagerFactory tmf, String sServerIP, int iPort) {
        Socket sslSocket = null;
        SSLSocketFactory sSLSocketFactory = getSocketFactory(kmf, tmf);
        if (sSLSocketFactory != null) {
            try {
                sslSocket = sSLSocketFactory.createSocket(sServerIP, iPort);
                sslSocket.setSoTimeout(iTimeout);
            } catch (Exception e) {
                Log.logClassError(e.getMessage());
            }
        }
        return sslSocket;
    }
}

package com.china.fortune.socket.pointToPoint;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.china.fortune.global.Log;

public abstract class P2PServer {
	private int iRecvTimeout = 1000;
	private boolean bRunning = true;
	private Thread tAcceptAndRead = null;
	private int iListenPort = 0;

	protected abstract boolean onRead(SocketChannel sc);

	protected abstract boolean onAccept(SocketChannel sc);

	public boolean start(int iPort) {
		Log.logClass(String.valueOf(iPort));
		iListenPort = iPort;
		bRunning = true;
		tAcceptAndRead = new Thread() {
			@Override
			public void run() {
				accept();
			}
		};
		tAcceptAndRead.start();
		return true;
	}

	public void stop() {
		bRunning = false;
		if (tAcceptAndRead != null) {
			try {
				tAcceptAndRead.join();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
			tAcceptAndRead = null;
		}
	}

	private void closeServerSocketChannel(ServerSocketChannel ssc) {
		if (ssc != null) {
			try {
				ssc.close();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
	}

	private void closeSocketChannel(SocketChannel sc) {
		if (sc != null) {
			try {
				sc.close();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
	}

	private ServerSocketChannel createServerSocketChannel() {
		ServerSocketChannel ssc = null;
		try {
			ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(iListenPort));
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return ssc;
	}

	private void accept() {
		ServerSocketChannel ssc = null;
		while (bRunning) {
			if (ssc == null) {
				ssc = createServerSocketChannel();
			}
			if (ssc != null) {
				SocketChannel sc = null;
				try {
					sc = ssc.accept();
				} catch (Exception e) {
					Log.logClass(e.getMessage());
					closeServerSocketChannel(ssc);
					ssc = null;
					sc = null;
				}

				if (sc != null) {
					if (onAccept(sc)) {
						closeServerSocketChannel(ssc);
						ssc = null;
						try {
							sc.socket().setSoLinger(true, 0);
							sc.socket().setSoTimeout(iRecvTimeout);
						} catch (Exception e) {
							Log.logClass(e.getMessage());
						}
						while (bRunning && onRead(sc));
					}
					closeSocketChannel(sc);
				}
			}
		}
		closeServerSocketChannel(ssc);
	}

}

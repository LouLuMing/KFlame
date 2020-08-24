package com.china.fortune.socket;

import com.china.fortune.common.ByteAction;
import com.china.fortune.global.Log;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

public class IPUtils {
	static public int ciLoopIP = Ip2Int("127.0.0.1");
	
	public static byte[] Ip2Bytes(String strIp) {
		byte[] bytes = null;
		String[] ss = strIp.split("\\.");
		if (ss.length == 4) {
			bytes = new byte[ss.length];
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = (byte) Integer.parseInt(ss[i]);
			}
		}
		return bytes;
	}

	public static int Ip2Int(String strIp) {
		byte[] bytes = Ip2Bytes(strIp);
		if (bytes != null) {
			return ByteAction.byteToIntBE(bytes);
		}
		return 0;
	}

	public static byte[] int2Byte(int ip) {
		return ByteAction.intToByteBE(ip);
	}
	
	public static String int2String(int ip) {
		byte[] bData = ByteAction.intToByteBE(ip);
		return bytes2String(bData);
	}
	
	public static int bytes2Int(byte[] bAddr) {
		if (bAddr != null) {
			return ByteAction.byteToIntBE(bAddr);
		} else {
			return 0;
		}
	}

	public static String bytes2String(byte[] bAddr) {
		StringBuilder sb = new StringBuilder();
		sb.append((int) (bAddr[0] & 0x00ff));
		sb.append('.');
		sb.append((int) (bAddr[1] & 0x00ff));
		sb.append('.');
		sb.append((int) (bAddr[2] & 0x00ff));
		sb.append('.');
		sb.append((int) (bAddr[3] & 0x00ff));
		return sb.toString();
	}

	public static boolean startWith(int iIp, int start) {
		return (((iIp >> 24) & 0xff) == start);
	}

	public static String getRemoteSocketAddress(Socket st) {
		if (st != null) {
			InetSocketAddress isa = (InetSocketAddress) st.getRemoteSocketAddress();
			if (isa != null) {
				return bytes2String(isa.getAddress().getAddress());
			}
		}
		return null;
	}

	public static String getRemoteSocketAddress(SocketChannel sc) {
		if (sc != null) {
			return getRemoteSocketAddress(sc.socket());
		} else {
			return null;
		}
	}

	public static void main(String[] args) {
		Log.log(ciLoopIP + "");
	}
}

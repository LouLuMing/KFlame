package com.china.fortune.target.readServer;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.china.fortune.global.Log;
import com.china.fortune.myant.TargetInterface;
import com.china.fortune.processflow.ProcessAction;
import com.china.fortune.socket.selectorManager.NioReadNoAttach;
import com.china.fortune.string.StringAction;
import com.china.fortune.xml.XmlNode;

public class ReadServer extends NioReadNoAttach implements TargetInterface {
	@Override
	protected boolean onRead(SocketChannel sc) {
		boolean rs = false;

		ByteBuffer bb = ByteBuffer.allocate(64 * 1024);
		try {
			bb.clear();
			int len = sc.read(bb);
			Log.logClass("len:" + len);
			if (len > 0) {
				bb.flip();
				do {
					sc.write(bb);
				} while (bb.remaining() > 0);
				rs = true;
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return rs;
	}

	@Override
	public boolean doAction(XmlNode cfg, ProcessAction self) {
		int iLocalPort = StringAction.toInteger(cfg.getChildNodeText("localport"));
		if (openAndStart(iLocalPort)) {
			Log.log("ReadServerTarget init");
			join();
			stop();
			Log.log("ReadServerTarget deinit");
		}
		return true;
	}

	public static void main(String[] args) {
		ReadServer rs = new ReadServer();
		rs.startAndBlock(9900);
	}
}

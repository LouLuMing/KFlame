package com.china.fortune.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.china.fortune.global.Log;

public class PairSocket {
	private Selector mSelector = null;
	private boolean bRunning = true;
	private SocketChannel mFromSC = null;
	private SocketChannel mToSC = null;
	private Thread mThread = null;
	private ByteBuffer bb = ByteBuffer.allocate(64 * 1024);
	
	public PairSocket() {
		try {
			mSelector = Selector.open();
		} catch (IOException e) {
			Log.logClass(e.getMessage());
		}
	}
	
	public boolean start(SocketChannel from, SocketChannel to) {
		boolean rs = false;
		if (from != null && to != null) {
			mFromSC = from;
			mToSC = to;
			rs = true;
			try {
				mFromSC.register(mSelector, SelectionKey.OP_READ); 
				mToSC.register(mSelector, SelectionKey.OP_READ); 
			} catch (IOException e) {
				Log.logClass(e.getMessage());
				rs = false;
			}
			
			if (rs) {
				bRunning = true;
				mThread = new Thread() {
					@Override
		            public void run() {
		            	select();
		            }
		        };
		        mThread.start();
			}
		}

        return rs;
	}
	
	public void stop() {
		bRunning = false;
		if (mThread != null) {
			mSelector.wakeup();
			try {
				if (mThread != null && mThread.isAlive()) {
					mThread.join();
				}
				mThread = null;
			}
			catch (Exception e) {
				Log.logClass(e.getMessage());
			}
		}
	}
	
	private void closeAllSocket() {
		freeKeyAndSocket(mFromSC);
		freeKeyAndSocket(mToSC);
	}
	
	private void freeKeyAndSocket(SocketChannel sc) {
		try {
			if (sc != null) {
				SelectionKey key = sc.keyFor(mSelector);
				if (key != null) {
					key.cancel();
				}
				sc.close();
			}
		}
		catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}
	
	private boolean readAndSend(SelectionKey key) {
		boolean rs = false;
		
		SocketChannel from = (SocketChannel)key.channel();
		SocketChannel to = mToSC;
		if (from != mFromSC) {
			to = mFromSC;
		}
		try {
			bb.clear();
			int iRead = from.read(bb);
			if (iRead > 0) {
				bb.flip();
				if (to.write(bb) == iRead) {
					rs = true;
				}
			}
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return rs;
	}
	
	
	private void select() {
		while (bRunning) {
			int iSel = 0;
			try {
				iSel = mSelector.select();
			} catch (Exception e) {
				Log.logClass(e.getMessage());
				iSel = 0;
			}
			if (bRunning && iSel > 0) {
				Set<SelectionKey> selectedKeys = mSelector.selectedKeys();
				if (selectedKeys != null) {
					Iterator<SelectionKey> it = selectedKeys.iterator();
					while (bRunning && it.hasNext()) {
						SelectionKey key = (SelectionKey)it.next();
						it.remove();
						if (!key.isValid()) {
							key.cancel();
							bRunning = false;
						} else if (key.isReadable()) {
							bRunning = readAndSend(key);
						}
					}
				}
			}
		}
		closeAllSocket();
	} 
}

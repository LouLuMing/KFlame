package com.china.fortune.myant;

import com.china.fortune.file.ReadLinesInteface;
import com.china.fortune.global.Log;
import com.china.fortune.string.StringUtils;

public class GetCpuAndMem {
	private ReadLinesInteface rli = new ReadLinesInteface() {
		private StringBuilder sb = new StringBuilder();
		@Override
		public boolean onRead(String s) {
			if (s.contains("Cpu")) {
				sb.setLength(0);
			}
			sb.append(s);
			if (s.contains("KiB Swap")) {
				String sLine = sb.toString();
				String sCpu = StringUtils.findBetween(sLine, "Cpu(s)", "KiB Mem");
				String sCpuUS = StringUtils.findBetween(sCpu, ":", "us");
				String KiBMem = StringUtils.findBetween(sLine, "KiB Mem", "KiB Swap");
				String sTotal = StringUtils.findBetween(KiBMem, ":", "total");
				String sFree = StringUtils.findBetween(KiBMem, "total", "free");
				int iTotal = StringUtils.toInteger(sTotal);
				int iFree = StringUtils.toInteger(sFree);
				int iMemPercent = (iTotal - iFree) * 100 / iTotal; 
				int iCpu = StringUtils.toInteger(sCpuUS);
				Log.log("Cpu:" + iCpu + "% Mem:" + iMemPercent + "%");
				
				return false;
			}
			return true;
		}
	};
	
	private void getCpuAndMem() {
		MyAntCommand mac = new MyAntCommand("121.40.112.2", 9000, rli);
//		mac.sendCommandAndRecvMore("run ./taillog.sh");
		mac.sendCommand("run top -b -n 1");
		mac.sendCommand("exit");
		mac.close();
	}
	
	public static void main(String[] args) {
		GetCpuAndMem tt = new GetCpuAndMem();
		tt.getCpuAndMem();
	}
}

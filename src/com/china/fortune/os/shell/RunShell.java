package com.china.fortune.os.shell;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

import com.china.fortune.file.ReadEnterBuffer;
import com.china.fortune.file.ReadLinesInteface;
import com.china.fortune.global.Log;
import com.china.fortune.os.common.OsDepend;

//java -cp myAnt.jar com.china.fortune.os.shell.RunShell
public class RunShell {
	static public void winRun(String sProgram) {
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec(sProgram);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	static public ArrayList<String> winRunAndOutput(String sProgram, String sPath) {
		ArrayList<String> lsOutput = null;
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(sProgram, null, new File(sPath));
			lsOutput = ReadEnterBuffer.readLines(proc.getInputStream(), "utf-8");
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return lsOutput;
	}

	static public ArrayList<String> winRunAndOutput(String sProgram) {
		ArrayList<String> lsOutput = null;
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(sProgram);
			lsOutput = ReadEnterBuffer.readLines(proc.getInputStream(), "utf-8");
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return lsOutput;
	}

	static public void winRun(String sProgram, ReadLinesInteface rli) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(sProgram);
			ReadEnterBuffer.readLines(proc.getInputStream(), "utf-8", rli);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	static public void winRun(String sProgram, String sPath, ReadLinesInteface rli) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(sProgram, null, new File(sPath));
			ReadEnterBuffer.readLines(proc.getInputStream(), "utf-8", rli);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	static public ArrayList<String> linuxShell(String sShell) {
		ArrayList<String> lsOutput = null;
		try {
			Process proc = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", sShell }, null, null);
			lsOutput = ReadEnterBuffer.readLines(proc.getInputStream(), "utf-8");
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return lsOutput;
	}

	static public void linuxShell(String sShell, ReadLinesInteface rli) {
		try {
			Process proc = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", sShell }, null, null);
			ReadEnterBuffer.readLines(proc.getInputStream(), "utf-8", rli);
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
	}

	static public ArrayList<String> linuxShellWait(String sShell) {
		ArrayList<String> lsOutput = new ArrayList<String>();
		try {
			Process proc = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", sShell }, null, null);

			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			LineNumberReader lnr = new LineNumberReader(is);
			proc.waitFor();

			do {
				String sLine = lnr.readLine();
				if (sLine != null) {
					lsOutput.add(sLine);
				} else {
					break;
				}
			} while (true);

			is.close();
			lnr.close();
		} catch (Exception e) {
			Log.logClass(e.getMessage());
		}
		return lsOutput;
	}
	
	static public void runShellAndLog(String sCmd) {
		ReadLinesInteface rli = new ReadLinesInteface() {
			@Override
			public boolean onRead(String sLine) {
				Log.log(sLine);
				return true;
			}
		};
		if (OsDepend.isWin()) {
			winRun(sCmd, rli);
		} else {
			linuxShell(sCmd, rli);
		}
	}

	public static void main(String[] args) {
		// ArrayList<String> lsOutput = RunShell.linuxShell("netstat -aon | grep :9600");
		// for (String s : lsOutput) {
		// Log.log(s);
		// }

//		ArrayList<String> lsOutput = RunShell.winRunAndOutput("netstat -aon | findstr :22");
//		if (lsOutput != null) {
//			for (String s : lsOutput) {
//				Log.log(s);
//			}
//		}
		
		ReadLinesInteface rli = new ReadLinesInteface() {
			@Override
			public boolean onRead(String sLine) {
				Log.log(sLine);
				return true;
			}
		};
		if (OsDepend.isWin()) {
			RunShell.winRun("./taillog.sh", rli);
		} else {
			RunShell.linuxShell("./taillog.sh", rli);
		}
	}
}

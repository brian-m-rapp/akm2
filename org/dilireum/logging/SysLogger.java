package org.dilireum.logging;
import java.io.*;

/* SysLogger levels:
 * 		0 - no messages
 * 		1 - 
 * 		2 - 
 * 		3 - 
 * 		4 - 
 * 		5 - 
 * 		6 - 
 * 		7 - 
 * 		8 - 
 * 		9 - all debug messages are output
 */
public class SysLogger {
	
	static public final int		FILE	= 1;
	static public final int		SCREEN	= 2;
	static public final int		SERVICE	= 4;
	static public final int		ALLOUT = FILE | SCREEN | SERVICE;
	
	static private int 			outputTo = 0;
	static private int 			debugLevel = 0;
	static private String			outputPath = "/dev/null";
	static private PrintWriter		logWriter = null;
	
	static public void setLogOutput(int outTo) {
		outputTo = outTo & ALLOUT;
	}

	static public int getLogOutput() {
		return outputTo;
	}

	static public void setOutputPath(String path) {
		outputPath = path;
		openLogFile();
	}
	
	static public String getOutputPath() {
		return outputPath;
	}
	
	static public void closeLogFile() {
		if (logWriter != null) {
			logWriter.close();
			logWriter = null;
		}
	}

	static public void openLogFile() {
		closeLogFile();
		
		try {
			logWriter = new PrintWriter(new FileWriter(getOutputPath(), true));
		} catch (IOException e) {
			logWriter = null;
			System.err.println("Exception " + e.getLocalizedMessage() + " while opening log file.");
		}
	}
	
	static public void setLogLevel(int level) {
		if (level < 0) {
			level = 0;
		}

		if (level > 9) {
			level = 9;
		}

		debugLevel = level;
		logMsg(1, "SysLogger level set to " + getLogLevel());
	}
	
	static public int getLogLevel() {
		return debugLevel;
	}

	static public synchronized void logMsg(int level, String msg) {
		if (getLogLevel() >= level) {
			if ((getLogOutput() & SCREEN) == SCREEN) { 
				System.out.println(msg);
			}
			
			if ((getLogOutput() & FILE) == FILE) {
				if (logWriter != null) {
					logWriter.println(msg);
				}
			}

			if ((getLogOutput() & SERVICE) == SERVICE) { 
			}
		}
	}
}

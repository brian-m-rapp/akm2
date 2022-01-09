

public class showJVMversion {

	/**
	 * Finds the version of the JVM in use. An optional parameter can be given
	 * that will affect the exit status. If the JVM is less than the version
	 * given, the result will be 1. If it's greater than the version given, 0
	 * will be the result. If a version isn't specified, version 1.2 is assumed.
	 * <p>
	 * This program should be compiled as follows so it will run on any JVM 1.2
	 * or later:
	 * <pre>javac -source 1.2 -target 1.2 showJVMversion.java</pre>
	 * @param args only parameter is the minimum Java version that is acceptable.
	 */
	public static void main(String[] args) {
		
		String version = System.getProperty("java.version").substring(0, 3);
		String minVersion = "1.2";

		if (args.length > 0) {
			minVersion = args[0];
		}

		System.out.println(version);
		
		System.exit((version.compareTo(minVersion) < 0) ? 1 : 0);
	}

}

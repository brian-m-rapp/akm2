
import java.util.Locale;


public class GetSystemProperties {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String			osName			= System.getProperty("os.name");
		String			arch			= System.getProperty("os.arch");
		String			osVersion		= System.getProperty("os.version");
		String			userName		= System.getProperty("user.name");
		String			userHome		= System.getProperty("user.home");
		String			userDir			= System.getProperty("user.dir");
		Locale			locale			= Locale.getDefault();
		
		System.out.println("OS Name: " + osName + ", version: " + osVersion);
		System.out.println("Hardware Architecture: " + arch);
		System.out.println("User Name: " + userName);
		System.out.println("Home Directory: " + userHome);
		System.out.println("Current Directory: " + userDir);
		System.out.println("Default locale: " + locale.getLanguage());
		//java.util.Properties properties = System.getProperties();
	    //properties.list(System.out);
	}
}

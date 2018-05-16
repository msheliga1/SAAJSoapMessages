package utility;

/** 
 * 
 * @author Mike Sheliga 5.3.18
 * Contains version accessory routines.
 *
 */
public class VersionUtility {
	private static final String NEW_LINE = System.lineSeparator();
	
	/** 
	 * Prints the package name and version info for a given object.
	 * This prints the JRE version for most system defined objects, but 
	 * prints null for most user defined objects.
	 */
	public static void printVersionInfo(Object obj) {
		if (obj == null ) {
			System.out.println("Can not print version info because object is null.");
			return;
		}
		Package objPackage = obj.getClass().getPackage();
		//examine the package object 
		System.out.println("SOAPMessage Package name: " + objPackage.getSpecificationTitle());
		System.out.println("Package specification version: " + objPackage.getImplementationVersion());
		System.out.println("Package version: " + objPackage.getSpecificationVersion() + NEW_LINE);
	} // end printVersionInfo

} // end class VersionUtility

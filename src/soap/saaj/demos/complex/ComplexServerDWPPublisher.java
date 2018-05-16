package soap.saaj.demos.complex;

import java.net.BindException;
import javax.xml.ws.Endpoint;

import com.sun.xml.internal.ws.server.ServerRtException;

/** 
 * Simple publisher class used for testing soap messages with complex parameters.
 */
public class ComplexServerDWPPublisher {
	
public static void main(String args[]) {
	
	// 5.13.18 - Was able to get this to
	// deploy and show WSDL at http://localhost:9876/complex?wsdl with Postman.
		
	final String WS_ADDR = "http://localhost:9876/complex";
	System.out.println("Starting Main ComplexServerDWPPublisher at " + WS_ADDR + "...");
	System.out.println("    WebService Method addIntList(List<Integer> intList) ...");
	System.out.println("    WebService Method getPersonAge(Person p1) ...");
	System.out.println("    WebService Method addChildAges(Family family) ...");
	System.out.println("    WebService Method getOldestFamily(List<Family> familiyList) ...");

	try {
		Endpoint.publish(WS_ADDR, new ComplexServerImpl());
	} catch (ServerRtException ex) {
		System.out.println("A ServerRtException has been found.  ");
		Throwable suppressed = ex.getCause();
			System.out.println("Caused by Exception of type " + suppressed.getClass());
			if (suppressed instanceof BindException) {
				System.out.println("Message: " + suppressed.getMessage());
				System.out.println("The server has very likely already been started.");
				System.out.println("You should currently be able to run clients sucessfully.");
		}
	}
} // end main

} // end ComplexServerDWPPublisher

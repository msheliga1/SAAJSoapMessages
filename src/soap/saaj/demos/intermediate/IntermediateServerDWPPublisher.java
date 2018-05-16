package soap.saaj.demos.intermediate;

import javax.xml.ws.Endpoint;

/**
* Simple publisher class used for testing soap messages with primitive parameters.
 */

public class IntermediateServerDWPPublisher {
	
public static void main(String args[]) {
	
	// 5.13.18 - Was able to get this to
	// deploy and show WSDL at http://localhost:9876/ts?wsdl with Postman.
		
	final String WS_ADDR = "http://localhost:9876/intermediate";
	// URI uri = new URI(addr);
	// Endpoint eif = null;
	System.out.println("Starting Main IntermediateServerDWPPublisher at " + WS_ADDR + "...");
	System.out.println("    WebService Method returnHelloName(String name) ...");
	System.out.println("    WebService Method add2Ints(int int1, int int2) ...");
	System.out.println("    WebService Method addBSILD(byte byte1, short short1, int int1, long long1, double double1) ...");
	Endpoint.publish(WS_ADDR, new IntermediateServerImpl());
} // end main

}

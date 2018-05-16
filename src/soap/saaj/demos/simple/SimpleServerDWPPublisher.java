package soap.saaj.demos.simple;

import java.net.URI;

import javax.xml.ws.Endpoint;

public class SimpleServerDWPPublisher {
	
public static void main(String args[]) {
	
	// 5.10.18 - Was able to get this to
	// deploy and show WSDL at http://localhost:9876/ts?wsdl with Postman.
	// Next tried POSTing soap msg .. after much trying following worked!
	// <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:pm="http://ts.ch01.kalin/">
	//  <soapenv:Header></soapenv:Header>
	//  <soapenv:Body>
	// <pm:returnHello7>
	//  </pm:returnHello7>
	//  </soapenv:Body>
	// </soapenv:Envelope>
	
	// Could not get wsdl to change unless eclipse was restarted.
	// -----------------------------------------------------------
	// ... no matter how server was stopped or deleted or shutdown.bat, or 
	// if new ComplexServer was edited and run .....
	// no matter if Chrome/Firefox browser history was cleared or Postman restarted.
	// Tried a DWP Dynamic Web Service Project ... this updated the webservice in 
	// both Chrome and Postman once old project 
	// was closed, server shutdown.bat and deleted.  Note that the DWP was still run 
	// as a Java project.  Note that the new DWP wsdl shows even though there is no 
	// active server in the Servers tab in eclipse.
	// Solution seems to be to stop the endpoint.publisher in the console or top menu bar.  
	// Server does not seem to have any affect.
	// 
	// Set postman http method to POST and address as below.
	// Got a 415 Unsupported Media Type error.  
	// Next set header content-type to text/xml and got a 500 Internal Server Error.
	// 
		
	final String WS_ADDR = "http://localhost:9876/simple";
	// URI uri = new URI(addr);
	// Endpoint eif = null;
	System.out.println("Starting Main SimpleServerDWPPublisher at " + WS_ADDR + "...");
	System.out.println("    WebService Method returnHello2 ...");
	System.out.println("    WebService Method returnHello5 ...");
	Endpoint.publish(WS_ADDR, new SimpleServerImpl());
	// eif.publish();
} // end main

}

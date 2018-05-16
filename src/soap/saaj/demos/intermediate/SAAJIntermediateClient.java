package soap.saaj.demos.intermediate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import soap.saaj.demos.intermediate.MultiParameterSOAPMessage;
import soap.saaj.soapmessage.SOAPMessageUtility;
import utility.VersionUtility;

/** 
 * This client tests SAAJ routines that use only primitive parameters.
 * The SOAPConnection class is used along with accessory SAAJ routines 
 * to create SOAP messages, print their content, execute exposed web service 
 * methods, get the response SOAPMessage and print it out. Only routines 
 * with primitive input parameters are used in this "intermediate" client.
 * This class was mainly implemented to learn and work with SAAJ.  Both 
 * SOAPMessage SOAPElements and DOM Nodes are used for learning purposes.
 * This shows that DOM Nodes are much more difficult to work with.
 * @author Mike Sheliga 5.7.18
 */
public class SAAJIntermediateClient {
	public static final String NEW_LINE = System.lineSeparator();
	// Note that this is reversed compared to the package name!!
	public static final String URI_QNAME_STRING = "http://intermediate.demos.saaj.soap/";
	public static final String WS_STRING = "http://localhost:9876/intermediate";
	
	public static void main(String args[]) {
		
		System.out.println("Starting SAAJIntermediateClient .... " + NEW_LINE + 
			"This client uses SAAJ and DOM to manipulate several intermediate complexity SOAP messages. "
					+ NEW_LINE +
			"It creates, modifies, prints, and executes web service calls and prints the results." + NEW_LINE + 
			"Each request contains a single method with parameters of various types." + NEW_LINE + 
			"It uses both DOM Nodes and SAAJ Iterators to modify these messages and prints them ");
		System.out.println("out using a pretty-print message, as well as an all on one line SAAJ method. ");
		System.out.println(NEW_LINE + "It also uses these request messages to execute SOAP web services and ");
		System.out.println("print the SOAP response messages. " + NEW_LINE + 
			"=====================================================================");

		MultiParameterSOAPMessage sm = new MultiParameterSOAPMessage();
		// must have non-null, JRE class obj to print package/version information
		VersionUtility.printVersionInfo(sm.getSoapMessage());
		
		SOAPConnection sconn = null;
		try {
			sm.initializeSOAPRequest("returnHelloName", "JimBob");
			SOAPMessageUtility.multiprintSOAPMessage(sm.getSoapMessage());

			System.out.print("Connecting ....");
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			sconn = scf.createConnection(); // not auto-closeable
			URL wsUrl = new URL(WS_STRING);
			SOAPMessage response = sconn.call(sm.getSoapMessage(), wsUrl);
			System.out.println("   .... Connection result sucessfully returned.");
			SOAPMessageUtility.multiprintSOAPMessage(response);
				
			// Change the method name
			String newText = "add2Ints";
			String oldText = sm.updateSOAPMethod(newText);
			System.out.println("Updated SOAP Request's Method: " + oldText + " -> " + 
					newText + NEW_LINE + SOAPMessageUtility.soapMessageToString(sm.getSoapMessage()));

			// Replace text the new way using iterators and javax.xml.soap methods
			List<String> newParams = Arrays.asList("3", "4");
			sm.updateSOAPParamsUsingIterators(newParams);
			System.out.println("Modified SOAP Request Using Iterators: " + newParams + " -> " + 
				(newText.length()==0 ? "(blank)" : newText) + 
				NEW_LINE + SOAPMessageUtility.soapMessageToString(sm.getSoapMessage()));
			
			System.out.print("Connecting ....");
			response = sconn.call(sm.getSoapMessage(), wsUrl);
			System.out.println("   .... Connection result sucessfully returned.");
			SOAPMessageUtility.multiprintSOAPMessage(response);

			newText = "addBSILD";
			oldText = sm.updateSOAPMethod(newText);
			newParams = Arrays.asList("5", "22", "-27", "4", "24.2");
			sm.updateSOAPParamsUsingIterators(newParams);
			System.out.println("Modified SOAP Request Using Iterators: " + newText + ": " + newParams +  
				NEW_LINE + SOAPMessageUtility.soapMessageToString(sm.getSoapMessage()));
			
			System.out.print("Connecting ....");
			response = sconn.call(sm.getSoapMessage(), wsUrl);
			System.out.println("   .... Connection result sucessfully returned.");
			SOAPMessageUtility.multiprintSOAPMessage(response);
			
			// Replace text the old way using DOM Nodes from w3c.dom
			newParams = Arrays.asList("add2Ints", "9", "77");
			sm.updateSOAPUsingDOMNodes(newParams);			
			System.out.println("Modified SOAP Request Using DOM Nodes: " + newParams); 
			SOAPMessageUtility.multiprintSOAPMessage(sm.getSoapMessage());
			
			System.out.print("Connecting ....");
			response = sconn.call(sm.getSoapMessage(), wsUrl);
			System.out.println("   .... Connection result sucessfully returned.");
			SOAPMessageUtility.multiprintSOAPMessage(response);
			
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (SOAPException e) {
			System.out.print("SOAPException in SAAJIntermediateClient main");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Error in SAAJIntermediateClient main");
			e.printStackTrace();
		} finally {  // much much better if SOAPConnections were auto-closeable ...
			if (sconn != null ) {
				try { 
					sconn.close();
				} catch (Exception ex) {
					System.out.println("Exception closing soap connection.");
					ex.printStackTrace();
				}
			}
		} // end try-catch-finally
		
		System.out.println("Done SAAJIntermediateClient Main");				
	} // end main

} // end SAAJIntermediateClient

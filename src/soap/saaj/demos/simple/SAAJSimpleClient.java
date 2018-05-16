package soap.saaj.demos.simple;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import soap.saaj.soapmessage.SOAPMessageUtility;
import utility.VersionUtility;

/** 
 * This client tests simple no parameter SAAJ routines.
 * The SOAPConnection class is used along with accessory SAAJ routines 
 * to create SOAP messages, print their content, execute exposed web service 
 * methods, get the response SOAPMessage and print it out. 
 * This class was mainly implemented to learn and work with SAAJ. Only routines 
 * with no input parameters are used in this "simple" client.
 * A few dummy parameters with dummy attributes are inserted for testing and 
 * learning purposes.  They do not affect the web service methods.
 * @author Mike Sheliga 5.1.18
 */
public class SAAJSimpleClient {
	public static final String NEW_LINE = System.lineSeparator();
	// Note that this is reversed compared to the package name!!
	public static final String URI_QNAME_STRING = "http://simple.demos.saaj.soap/";
	public static final String WS_STRING = "http://localhost:9876/simple";
	
	public static void main(String args[]) {	
		System.out.println("Starting SAAJComplexClient .... " + NEW_LINE + 
			"This client uses SAAJ to create several simple SOAP request messages, " + NEW_LINE +
			"modify them, print them out, execute web service calls and print the results." + NEW_LINE + 
			"Each request contains a single method with one argument (which may be blank)." + NEW_LINE + 
			"It uses both Nodes and Iterators to modify these messages and prints them ");
		System.out.println("out using a pretty-print message, as well as an all on one line SAAJ method. ");
		System.out.println(NEW_LINE + "It also uses these request messages to execute SOAP web services and ");
		System.out.println("print the SOAP response messages. " + NEW_LINE + 
			"=====================================================================");

		NoParameterSOAPMessage sm = new NoParameterSOAPMessage();
		// must have non-null, JRE class obj to print package/version information
		VersionUtility.printVersionInfo(sm.getSoapMessage());
		
		sm.initializeSOAPRequest("returnHello2");
		SOAPMessageUtility.multiprintSOAPMessage(sm.getSoapMessage());

		System.out.print("Connecting ....");
		
		SOAPConnection sconn = null;
		try {
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			sconn = scf.createConnection(); // not auto-closeable
			URL wsUrl = new URL(WS_STRING);
			SOAPMessage response = sconn.call(sm.getSoapMessage(), wsUrl);
			System.out.println("   .... Connection result sucessfully returned.");
			SOAPMessageUtility.multiprintSOAPMessage(response);
				
			// Replace text the old way using Nodes from w3c.dom
			String newText = "NewDummyText";
			String oldText = sm.updateSOAPTextUsingNodes(newText);
			System.out.println("Modified SOAP Request Using Nodes: " + oldText + " -> " + newText + 
				NEW_LINE + SOAPMessageUtility.soapMessageToString(sm.getSoapMessage()));

			// Replace text the new way using iterators and javax.xml.soap methods
			newText = "";
			oldText = sm.updateSOAPTextUsingIterators(newText);
			System.out.println("Modified SOAP Request Using Iterators: " + oldText + " -> " + 
				(newText.length()==0 ? "(blank)" : newText) + 
				NEW_LINE + SOAPMessageUtility.soapMessageToString(sm.getSoapMessage()));
			
			// Change the method name to returnHello5
			newText = "returnHello5";
			oldText = sm.updateSOAPMethod(newText);
			System.out.println("Updated SOAP Request's Method: " + oldText + " -> " + 
					newText + NEW_LINE + SOAPMessageUtility.soapMessageToString(sm.getSoapMessage()));
			
			System.out.print("Connecting ....");
			response = sconn.call(sm.getSoapMessage(), wsUrl);
			System.out.println("   .... Connection result sucessfully returned.");
			SOAPMessageUtility.multiprintSOAPMessage(response);
			
		} catch (UnsupportedOperationException e) {
			System.out.print("UnsupportedOperationException in SAAJSimpleClient main");
			e.printStackTrace();
		} catch (SOAPException e) {
			System.out.print("SOAPException in SAAJSimpleClient main");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.print("MalformedURLException in SAAJSimpleClient main");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException in SAAJSimpleClient main");
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
		
		System.out.println("Done SAAJSimpleClient Main");				
	} // end main

} // end SAAJSimpleClient

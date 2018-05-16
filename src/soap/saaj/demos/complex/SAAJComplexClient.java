package soap.saaj.demos.complex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import beans.Family;
import beans.Person;
import soap.saaj.soapmessage.SOAPMessageUtility;
import utility.VersionUtility;

/** 
 * This client tests SAAJ routines that use complex parameters.
 * The SOAPConnection class is used along with accessory SAAJ routines 
 * to create SOAP messages, print their content, execute exposed web service 
 * methods, get the response SOAPMessage and print it out. Routines 
 * use complex parameters such as Collections (lists) and nested POJOs.
 * This class was mainly implemented to learn and work with SAAJ.  
 * @author Mike Sheliga 5.14.18
 */
public class SAAJComplexClient {
	public static final String NEW_LINE = System.lineSeparator();
	// Note that this is reversed compared to the package name!!
	public static final String URI_QNAME_STRING = "http://complex.demos.saaj.soap/";
	public static final String WS_STRING = "http://localhost:9876/complex";
	
	public static void main(String args[]) {
		
		System.out.println("Starting SAAJComplexClient .... " + NEW_LINE + 
			"This client uses SAAJ to create several complex SOAP request messages, " + NEW_LINE +
			"modify them, print them out, execute web service calls and print the results." + NEW_LINE + 
			"Each request contains a single method with collection and/or POJO parameters of various types." + NEW_LINE + 
			"It uses Iterators to modify these messages and prints them ");
		System.out.println("out using a pretty-print message, as well as an all on one line SAAJ method. ");
		System.out.println(NEW_LINE + "It also uses these request messages to execute SOAP web services and ");
		System.out.println("print the SOAP response messages. " + NEW_LINE + 
			"=====================================================================");
		
		Person grandpa = new Person("Mike", 102);
		Person grandma = new Person("Anna", 100);
		List<Person> parents = Arrays.asList(grandpa, grandma);
		Person mom = new Person("Loretta", 82);
		Person tom = new Person("Tom", 76);
		Person dolores = new Person("Dolores", 80);
		List<Person> kids = Arrays.asList(mom, tom, dolores);		
		Family marchevkas = new Family("Marchevka", parents, kids);	
		
		grandpa = new Person("George", 102);
		grandma = new Person("Helen", 100);
		parents = Arrays.asList(grandpa, grandma);
		Person cyril = new Person("Cryil", 62);
		Person margaret = new Person("Margaret", 61);
		Person martha = new Person("Martha", 59);
		Person steve = new Person("Steve", 48);
		kids = Arrays.asList(cyril, margaret, martha, steve);		
		Family wargos = new Family("Wargo", parents, kids);
		
		List<Family> clans = Arrays.asList(marchevkas, wargos);
		
		CollectionSOAPMessage sm = new CollectionSOAPMessage();
		// must have non-null, JRE class obj to print package/version information
		VersionUtility.printVersionInfo(sm.getSoapMessage());
		
		// Create method, print, call web service, print result, ... repeat
		SOAPConnection sconn = null;
		try {
			List<Integer> intList = Arrays.asList(3, 4, 5, 6);
			String newMethod = "addIntList";
			sm.initializeSOAPRequest(newMethod, intList);
			System.out.println("Created SOAP Request including List parameters: " + newMethod); 
					SOAPMessageUtility.multiprintSOAPMessage(sm.getSoapMessage());

			System.out.print("Connecting ....");		
			SOAPConnectionFactory scf = SOAPConnectionFactory.newInstance();
			sconn = scf.createConnection(); // not auto-closeable
			URL wsUrl = new URL(WS_STRING);
			SOAPMessage response = sconn.call(sm.getSoapMessage(), wsUrl);
			System.out.println("   .... Connection result sucessfully returned.");
			SOAPMessageUtility.multiprintSOAPMessage(response);
			
			// Change the method to getPersonAge with a POJO
			newMethod = "getPersonAge";
			sm.updateSOAPPerson(newMethod, mom);
			System.out.println("Updated SOAP Request including POJO parameter: " + 
					newMethod + NEW_LINE + SOAPMessageUtility.soapMessageToString(sm.getSoapMessage()));
			System.out.print("Connecting ....");
			response = sconn.call(sm.getSoapMessage(), wsUrl);
			System.out.println("   .... Connection result sucessfully returned." + NEW_LINE +
					SOAPMessageUtility.soapMessageToString(response));
			
			// Change the method to addChildAges with a POJO containing a list.
			newMethod = "addChildAges";
			sm.updateSOAPFamily(newMethod, marchevkas);
			System.out.println("Updated SOAP Request including POJO parameter with Lists: " + 
					newMethod + NEW_LINE + SOAPMessageUtility.soapMessageToString(sm.getSoapMessage()));
			System.out.print("Connecting ....");
			response = sconn.call(sm.getSoapMessage(), wsUrl);
			System.out.println("   .... Connection result sucessfully returned." + NEW_LINE +
					SOAPMessageUtility.soapMessageToString(response));

			// Change the method to getOldestFamily with a list containing POJOs that 
			// contains lists consisting of other POJOs.
			newMethod = "getOldestFamily";
			sm.updateSOAPFamilies(newMethod, clans);
			System.out.println("Updated SOAP Request including list with POJOs with lists: " + 
					newMethod + NEW_LINE + SOAPMessageUtility.soapMessageToString(sm.getSoapMessage()));
			System.out.print("Connecting ....");
			response = sconn.call(sm.getSoapMessage(), wsUrl);
			System.out.println("   .... Connection result sucessfully returned." + NEW_LINE +
					SOAPMessageUtility.soapMessageToString(response));
			
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (SOAPException e) {
			System.out.print("SOAPException in SAAJComplexClient main");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Error in SAAJComplexClient main");
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
		
		System.out.println("Done SAAJComplexClient Main");				
	} // end main

} // end SAAJComplexClient

package soap.saaj.demos.complex;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import beans.Family;
import beans.Person;
import soap.saaj.soapmessage.SOAPMessageUtility;

/** 
 * @Author: Mike Sheliga 5.12.15
 * Use SAAJ to build and manipulate SOAP Messages including methods with complex parameters
 * such as collections (lists) and Objects with multiple instance fields as well as other objects.
 * It was written to learn about and demonstrate the SAAJ API.
 * 
 *   The methods are hard wired for specific parameter types (such as Family 
 *   and List<Integer>. The "next step" would be to write a generalized method for 
 *   all objects using either reflection or the WSDL.
 */
public class CollectionSOAPMessage {
	
	private SOAPMessage soapMessage;  
	
	public CollectionSOAPMessage( ) {
		soapMessage = SOAPMessageUtility.createEmptySOAPMessage();
	}
	
	public SOAPMessage getSoapMessage() {return soapMessage;}
	public void setSoapMessage(SOAPMessage soapMessage) {this.soapMessage = soapMessage;}
	
	/** Updates a SOAP request message's method and returns the old method.
	* Precondition: SOAP request message has a body
	*/
	public String updateSOAPMethod(String wsMethod) {
		String result = null;
		SOAPMessage sm = soapMessage; // for convenience only
		try {			
			SOAPBody sb = sm.getSOAPBody();
			// Use SAAJ iterators (DOM nodes much harder).
			Iterator<SOAPBodyElement> iter = sb.getChildElements();
			if (iter == null || (!iter.hasNext())) {
				throw new SOAPException("Could not get first child from the soap body.");
			}
			if (iter.hasNext()) {
				SOAPBodyElement sbe = iter.next();
				result = sbe.getLocalName();
				// namespaceURI, localName, prefix
				QName qname = new QName(sbe.getNamespaceURI(), wsMethod, sbe.getPrefix());
				sbe.setElementQName(qname);
			}
		} catch (SOAPException e) {
			System.out.println("SOAP Error in updateSOAPMethod: " + e.getMessage());
			e.printStackTrace();
		} // end try-catch
		return result;
	} // end updateSOAPMethod

	/**
	 *  Initializes a SOAP request message whose parameters are a list of integers.
	 */
	public SOAPMessage initializeSOAPRequest(String wsMethod, List<Integer> intList) {
		SOAPMessage sm = soapMessage; // for convenience with length
		try {
			sm.setContentDescription("Mike Sheliga Soap SAAJ simple demo class.");
			// add xml version and utf info ...
			// sm.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
			
			SOAPBody sb = sm.getSOAPBody();  // alternate more concise method
			// Qualified XML name - URI, local part, prefix
			QName qName = new QName(SAAJComplexClient.URI_QNAME_STRING, wsMethod, "tsns");
			SOAPBodyElement be = sb.addBodyElement(qName);  // no need to add anything else to the body element	
			
			// Add arg0 soap elements ... first with some attributes
			SOAPElement sel = null;
			QName qArg0 = new QName("arg0");
			QName qComment = new QName("Note");
			boolean first = true;
			for (Integer intValue: intList) {
				sel = be.addChildElement(qArg0);
				if (first) sel.addAttribute(qComment, "Lists All Need To Be Arg0!");
				sel.setTextContent("" + intValue);
				first = false;
			} // end for each intValue
		
			// printing sm just prints classname@hashCode
			System.out.println("Sucessfully created complex " + wsMethod + " soap request msg ... ");
		} catch (SOAPException e) {
			System.out.println("SOAP Error in initializeSOAPRequest: "+ e.getMessage());
			e.printStackTrace();
		} // end try-catch
		return sm;
	} // end initializeSOAPRequest
	

	/** Updates a SOAP request's parameters using javax.xml.soap methods and iterators.
	* The soap body's child element (aka method) is fetched and all child elements (parameters).
	* are removed. Then the sub-elements (aka parameters) are updated. Only parameters represented 
	* by Strings are permitted.
	*/
	public void updateSOAPParamsUsingStrings(List<String> params) {
		SOAPMessage sm = soapMessage;
		try {			
			SOAPBody sb = sm.getSOAPBody();
			Iterator<SOAPBodyElement> iter = sb.getChildElements();
			if (iter == null) throw new SOAPException("SoapBody getChildElements returns null");
			if (!iter.hasNext()) throw new SOAPException("SoapBody getChildElements returns none.");
			SOAPBodyElement sbe = iter.next();
			if (sbe == null) throw new SOAPException("SoapBody SoapBodyElement is null.");
			sbe.removeContents();
			int i = 0;
			for (String param: params) {
				String paramName = "arg" + i++;
				sbe.addChildElement(paramName);
				sbe.getLastChild().setTextContent(param);
			}
		} catch (SOAPException e) {
			System.out.println("SOAP Error in end updateSOAPParamsUsingIterators: " + e.getMessage());
			e.printStackTrace();
		} // end try-catch
	} // end updateSOAPParamsUsingIterators
	
	/** Updates a complex SOAP request's method name and its Person parameters.
	* This uses javax.xml.soap methods and iterators.
	*/
	public void updateSOAPPerson(String wsMethod, Person p1) {
		SOAPMessage sm = soapMessage;
		this.updateSOAPMethod(wsMethod);
		try {
			// Get the first SOAPBodyElement which must be the method name
			SOAPBody sb = sm.getSOAPBody();
			Iterator<SOAPBodyElement> iter = sb.getChildElements();
			if (iter == null) throw new SOAPException("SoapBody getChildElements returns null");
			if (!iter.hasNext()) throw new SOAPException("SoapBody getChildElements returns none.");
			SOAPBodyElement sbe = iter.next();
			if (sbe == null) throw new SOAPException("SoapBody SoapBodyElement is null.");
			// Remove all old parameters
			sbe.removeContents();
			// Add new parameters
			int i = 0;
			String paramName = "arg" + i++;
			SOAPElement arg = sbe.addChildElement(paramName);
			// Note that their is no <ObjectName> tag such as <Person>
			// SOAPElement person = arg.addChildElement("Person");
			SOAPElement id = arg.addChildElement("firstName");
			id.setTextContent(p1.getFirstName());
			SOAPElement age = arg.addChildElement("age");
			age.setTextContent("" + p1.getAge());
			// adding a text node gets rid of other elements
			// SOAPElement xx = person.addTextNode("JoeBob");
			// xx.setTextContent("Doublexx");
		} catch (SOAPException e) {
			System.out.println("SOAP Error in end updateSOAPPerson: " + e.getMessage());
			e.printStackTrace();
		} // end try-catch
	} // end updateSOAPPerson
	
	/** Updates a SOAP request message's method name and its Family parameters.
	 * This uses javax.xml.soap methods and iterators.
	 */
	public void updateSOAPFamily(String wsMethod, Family fam) {
		SOAPMessage sm = soapMessage;
		this.updateSOAPMethod(wsMethod);
		try {
			// Get the first SOAPBodyElement which must be the method name
			SOAPBody sb = sm.getSOAPBody();
			Iterator<SOAPBodyElement> iter = sb.getChildElements();
			if (iter == null) throw new SOAPException("SoapBody getChildElements returns null");
			if (!iter.hasNext()) throw new SOAPException("SoapBody getChildElements returns none.");
			SOAPBodyElement sbe = iter.next();
			if (sbe == null) throw new SOAPException("SoapBody SoapBodyElement is null.");
			// Remove all old parameters
			sbe.removeContents();
			// Add new parameters
			int i = 0;
			String paramName = "arg" + i++;
			SOAPElement arg = sbe.addChildElement(paramName);
			// Note that their is no <ObjectName> tag such as <Family>
			SOAPElement surName = arg.addChildElement("surName");
			surName.setTextContent(fam.getSurName());
			List<Person> parents = fam.getParents();
			for (Person parent: parents) {
				SOAPElement se = arg.addChildElement("parents");
				SOAPElement firstName = se.addChildElement("firstName");
				firstName.addTextNode(parent.getFirstName());
				SOAPElement age = se.addChildElement("age");
				age.addTextNode("" + parent.getAge());
			}
			List<Person> kids = fam.getChildren();
			for (Person kid: kids) {
				SOAPElement se = arg.addChildElement("children");
				SOAPElement firstName = se.addChildElement("firstName");
				firstName.addTextNode(kid.getFirstName());
				SOAPElement age = se.addChildElement("age");
				age.addTextNode("" + kid.getAge());
			}
		} catch (SOAPException e) {
			System.out.println("SOAP Error in end updateSOAPFamily: " + e.getMessage());
			e.printStackTrace();
		} // end try-catch
	} // end updateSOAPFamily
	
	/** Updates a complex SOAP request's method name and its List<Family> parameters.
 	 * This uses javax.xml.soap methods and iterators.
 	 */
	public void updateSOAPFamilies(String wsMethod, List<Family> clans) {
		SOAPMessage sm = soapMessage;
		this.updateSOAPMethod(wsMethod);
		try {
			// Get the first SOAPBodyElement which must be the method name
			SOAPBody sb = sm.getSOAPBody();
			Iterator<SOAPBodyElement> iter = sb.getChildElements();
			if (iter == null) throw new SOAPException("SoapBody getChildElements returns null");
			if (!iter.hasNext()) throw new SOAPException("SoapBody getChildElements returns none.");
			SOAPBodyElement sbe = iter.next();
			if (sbe == null) throw new SOAPException("SoapBody SoapBodyElement is null.");
			// Remove all old parameters
			sbe.removeContents();
			// Add new parameters
			for (Family fam: clans) {
				String paramName = "arg0";
				SOAPElement arg = sbe.addChildElement(paramName);
				// Note that their is no <ObjectName> tag such as <Family>
				SOAPElement surName = arg.addChildElement("surName");
				surName.setTextContent(fam.getSurName());
				List<Person> parents = fam.getParents();
				for (Person parent: parents) {
					SOAPElement se = arg.addChildElement("parents");
					SOAPElement firstName = se.addChildElement("firstName");
					firstName.addTextNode(parent.getFirstName());
					SOAPElement age = se.addChildElement("age");
					age.addTextNode("" + parent.getAge());
				}
				List<Person> kids = fam.getChildren();
				for (Person kid: kids) {
					SOAPElement se = arg.addChildElement("children");
					SOAPElement firstName = se.addChildElement("firstName");
					firstName.addTextNode(kid.getFirstName());
					SOAPElement age = se.addChildElement("age");
					age.addTextNode("" + kid.getAge());
				}				
			} // end for all families
		} catch (SOAPException e) {
			System.out.println("SOAP Error in end updateSOAPFamilies: " + e.getMessage());
			e.printStackTrace();
		} // end try-catch
	} // end updateSOAPFamilies
	
	/** Updates a SOAP request message's method and parameters.
	 * Not implemented as of 5.18.
	 * This routine could use either reflection or the wsdl.
	 * If using reflection all getter/setter methods would be used to 
	 * generate the appropriate xml code with default tag names such as arg0.
	 * If using the wsdl, more appropriate tag names from the wsdl could be
	 * used.  A limited number of instance variables could also be included 
	 * per the wsdl.
	*/
	public void updateSOAPMessageBody(String wsMethod, List<Object> params) {
		System.out.println("upateSOAPMessageBody not yet implemented.");
	} // end updateSOAPMessageBody
	
} // end class CollectionSOAPMessage

package soap.saaj.demos.simple;

import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.w3c.dom.Node;

import soap.saaj.soapmessage.SOAPMessageUtility;

/** Creates, manipulates and tests SOAP Messages with no parameters.
 * This class was implemented for learning and testing the SAAJ API.  While it mainly 
 * uses SAAJ SOAPBodyElements and iterators it also uses DOM Nodes which tend to be much harder
 * to work with. 
 * @Author Mike Sheliga 5.2.18
 */
public class NoParameterSOAPMessage {
	
	/** A NoParameterSOAPMessage has a return type but no parameters. */
	private SOAPMessage soapMessage;  
	
	public NoParameterSOAPMessage( ) {
		soapMessage = SOAPMessageUtility.createEmptySOAPMessage();
	}
	
	public SOAPMessage getSoapMessage() {return soapMessage;}
	public void setSoapMessage(SOAPMessage soapMessage) {this.soapMessage = soapMessage;}
	
	/** Initializes a simple no parameter SOAP request message.  
	 * In addition to setting the method name this inserts a dummy element with 
	 * a few dummy attributes for demonstration purposes.
	 */
	public SOAPMessage initializeSOAPRequest(String wsMethod) {
		SOAPMessage sm = soapMessage; // for convenience with length
		try {
			sm.setContentDescription("Mike Sheliga Soap SAAJ simple demo class.");
			// add xml version and utf info ...
			// sm.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
			
			SOAPPart sp = sm.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			SOAPBody sb = se.getBody();
			// SOAPBody sb2 = sm.getSOAPBody();  // alternate more concise method
			// Qualified XML name - URI, local part, prefix
			QName qName = new QName(SAAJSimpleClient.URI_QNAME_STRING, wsMethod, "tsns");
			SOAPBodyElement be = sb.addBodyElement(qName);  // no need to add anything else to the body element	
			
			// Add unused Dummy element with some attributes
			QName qDummy = new QName("Dummy");
			QName qAge = new QName("Age");
			QName qStreet = new QName("Street"); // AttrImpl - returned by getAllAttributes
			QName qColon = new QName("xmlns:colon");  // AttrNSImpl - not returned by getAllAttributes
			SOAPElement sel = be.addChildElement(qDummy);
			sel.addAttribute(qAge, "34");
			sel.addAttribute(qStreet,"LilacWay");
			sel.addAttribute(qColon, "MyColon");
			sel.addTextNode("DummyText");
			
			// printing sm just prints classname@hashCode
			System.out.println("Sucessfully created simple soap request msg ... ");
		} catch (SOAPException e) {
			System.out.println("SOAP Error in initializeSOAPRequest: "+ e.getMessage());
			e.printStackTrace();
		} // end try-catch
		return sm;
	} // end initializeSOAPRequest
	
	/** Updates a one parameter SOAP message request's method. 
	 */
	public String updateSOAPMethod(String wsMethod) {
		String result = null;
		SOAPMessage sm = soapMessage; // for convenience only
		try {			
			SOAPBody sb = sm.getSOAPBody();
			// changing a nodes name not easy, so use iterators instead
			// Node node = sb.getFirstChild();
			// System.out.println("The body's first child node is: " + node.getLocalName());
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
			System.out.println("SOAP Error in updateOneParameterSOAPMethod: " + e.getMessage());
			e.printStackTrace();
		} // end try-catch
		return result;
	} // end updateSOAPMethod
	
	/** Updates a one parameter SOAP request's text using DOM Nodes.
	* The soap body's first element (the method) first sub-element's (parameters) 
	* text will be updated. DOM Nodes are used instead of iterators and soapElements.
	* Using DOM Nodes was more difficult but was good for learning purposes.
	*/
	public String updateSOAPTextUsingNodes(String newText) {
		String result = null;
		SOAPMessage sm = soapMessage;
		try {			
			SOAPBody sb = sm.getSOAPBody();
			if (sb == null) throw new SOAPException("soapBody is null!");
			Node methodNode = sb.getFirstChild();  // this is the method.
			if (methodNode == null) throw new SOAPException("soapBody has no children!");
			Node paramNode = methodNode.getFirstChild();  // this is the param node
			if (paramNode == null) throw new SOAPException("soapMethod has no parameters!");
			result = paramNode.getTextContent();
			paramNode.setTextContent(newText);
		} catch (SOAPException e) {
			System.out.println("SOAP Error in updateSOAPTextUsingNodes: " + e.getMessage());
			e.printStackTrace();
		} // end try-catch
		return result;
	} // end updateSOAPTextUsingNodes
	
	/** Updates a simple SOAP request's text using javax.xml.soap methods and iterators.
	* The soap body's child element (aka method) containing helloRequest is fetched.
	* Then the first sub-element's (aka parameter) containing "dummy" will be updated.
	* While this method is not very object oriented it helped to learn SAAJ.
	*/
	public String updateSOAPTextUsingIterators(String newText) {
		String result = null;
		SOAPMessage sm = soapMessage;
		try {			
			SOAPBody sb = sm.getSOAPBody();
			Iterator<SOAPBodyElement> iter = sb.getChildElements();
			if (iter == null) throw new SOAPException("SoapBody getChildElements returns null");
			if (!iter.hasNext()) throw new SOAPException("SoapBody getChildElements returns none.");
			SOAPBodyElement sbe = null;
			while (iter.hasNext()) {
				sbe = iter.next();
				if (sbe.getNodeName().toLowerCase().contains("returnhello")) break;
			}
			if (sbe == null) throw new SOAPException("SoapBody SoapBodyElement is null.");
			if (!(sbe.getNodeName().toLowerCase().contains("returnhello"))) {
				throw new SOAPException("Could not find returnhello name in body.");
			}
			
			Iterator<SOAPElement> iter2 = sbe.getChildElements();
			if (iter2 == null) throw new SOAPException("SoapBodyElement getChildElements returns null");
			if (!iter2.hasNext()) throw new SOAPException("SoapBodyElement getChildElements returns none.");
			SOAPElement se = null;
			while (iter2.hasNext()) {
				se = iter2.next();
				if (se.getNodeName().toLowerCase().contains("dummy")) break;
			}
			if (se == null) throw new SOAPException("SoapBodyElement SoapElement is null");
			if (!(se.getNodeName().toLowerCase().contains("dummy"))) {
				throw new SOAPException("Could not find argument containing dummy in returnHello element.");
			}
			result = se.getTextContent();
			se.setTextContent(newText);  // make it blank, to see how tag displays				
		} catch (SOAPException e) {
			System.out.println("SOAP Error in end updateSOAPTextUsingIterators: " + e.getMessage());
			e.printStackTrace();
		} // end try-catch
		return result;
	} // end updateSOAPTextUsingIterators

} // end class NoParameterSOAPMessage

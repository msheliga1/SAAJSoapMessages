package soap.saaj.demos.intermediate;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import soap.saaj.soapmessage.SOAPMessageUtility;

/** 
 * Contains methods used to create SOAPMessages with the SAAJ API.
 * While the methods may have multiple input parameters all parameters are 
 * primitive types.
 * This was written to learn about and demonstrate the SAAJ API.
 * @Author Mike Sheliga 5.6.18
 */
public class MultiParameterSOAPMessage {
	
	private SOAPMessage soapMessage;  // has one method and primitive parameters.
	
	/** Creates a new MulitParameterSOAPMessage with an empty soapMessage.
	 */
	public MultiParameterSOAPMessage( ) {
		soapMessage = SOAPMessageUtility.createEmptySOAPMessage();
	}
	
	public SOAPMessage getSoapMessage() {return soapMessage;}
	public void setSoapMessage(SOAPMessage soapMessage) {this.soapMessage = soapMessage;}
	
	/** 
	 *  Initializes a SOAP request message with the given method and parameter name.
	 */
	public SOAPMessage initializeSOAPRequest(String wsMethod, String param) {
		SOAPMessage sm = soapMessage; // for convenience with length
		try {
			sm.setContentDescription("Mike Sheliga Soap SAAJ simple demo class.");
			// add xml version and utf info ...
			// sm.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
			
			SOAPBody sb = sm.getSOAPBody();  // alternate more concise method
			// Qualified XML name - URI, local part, prefix
			QName qName = new QName(SAAJIntermediateClient.URI_QNAME_STRING, wsMethod, "tsns");
			SOAPBodyElement be = sb.addBodyElement(qName);  // no need to add anything else to the body element	
			
			// Add unused Dummy element with some attributes
			QName qArg0 = new QName("arg0");
			QName qAge = new QName("Age");
			QName qStreet = new QName("Street"); // AttrImpl - returned by getAllAttributes
			QName qColon = new QName("xmlns:colon");  // AttrNSImpl - not returned by getAllAttributes
			SOAPElement sel = be.addChildElement(qArg0);
			sel.addAttribute(qAge, "34");
			sel.addAttribute(qStreet,"Intermediate");
			sel.addAttribute(qColon, "MyColon");
			sel.addTextNode(param);
			
			// printing sm just prints classname@hashCode
			System.out.println("Sucessfully created simple soap request msg ... ");
		} catch (SOAPException e) {
			System.out.println("SOAP Error in initializeSOAPRequest: "+ e.getMessage());
			e.printStackTrace();
		} // end try-catch
		return sm;
	} // end initializeSOAPRequest
	
	/**
	 *  Updates the SOAP request message with the given method name.
	 */
	// 
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
			System.out.println("SOAP Error in updateSOAPMethod: " + e.getMessage());
			e.printStackTrace();
		} // end try-catch
		return result;
	} // end updateSOAPMethod
	
	/** Updates a SOAP request's method name and parameters using DOM Nodes.
	 * This is much more complex than using SOAPMessage and SOAPElement routines, 
	 * but was implemented for learning purposes.  It demonstrates how much more 
	 * difficult and lengthy using only DOM functions is.
	* It uses DOM Document instead of iterators. One basic difficulty 
	* is that there is no simple way to update a tags localPart.
	* The soap body's first element (the method) and it's sub-element's (parameters) 
	* text are updated.  
	 */
	public void updateSOAPUsingDOMNodes(List<String> methodInfo) {
		SOAPMessage sm = soapMessage;  // for convenience
		try {
			// Since we cant directly update a Nodes local value (although we can retrieve it!)
			// we must create a DOM Document and use Document.renameNode. We can 
			// change prefix, insert child nodes and text, but not the local name.
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			Source src = sm.getSOAPPart().getContent();
			DOMResult domResult = new DOMResult();
			// convert the src XML to a DOM result document
			transformer.transform(src, domResult); 

			// Get the soap part of the overall domResult
			Document doc = (Document) domResult.getNode();
			
			// We now have the soap part, extract envelope
			NodeList nodes = doc.getChildNodes();
			Node envelope = null;
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getLocalName().toLowerCase().contains("envelope")) {
					envelope = node;
					break;
				}
			}
			if (envelope == null) throw new SOAPException("Envelope node not found in DOM.");
			
			// We have the envelope, get the Body
			nodes = envelope.getChildNodes();
			Node body = null;
			for (int i=0; i<nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (node.getLocalName().toLowerCase().contains("body")) {
					body = node;
					break;
				}				
			}
			if (body == null) throw new SOAPException("Body node not found in DOM envelope.");

			// We have the body update the method name
			Node method = body.getFirstChild();
			if (method == null) throw new SOAPException("Method node not found in DOM Body.");
			String newMethodQName = method.getPrefix() + ":" + methodInfo.get(0);
			// must use a NamespaceURI if newMethod includes a prefix.
			doc.renameNode(method, method.getNamespaceURI(), newMethodQName);
			// System.out.println("MethodNode renamed: " + method);

			// We have the method, now delete all old params ... very tricky ...
			// Must delete from back to front as oldParams length changes as we delete nodes
			NodeList oldParams = method.getChildNodes();
			for (int i=oldParams.getLength()-1; i>=0; i--) {
				Node param = oldParams.item(i);
				method.removeChild(param);
			}
			// ... and add new parameters
			// begin at one size since first value is method name 
			for (int i=1; i<methodInfo.size(); i++) {
				// There is no new Node() constructor ... no wonder folks dont 
				// do xml conversion with DOM ..
				String tagName = "arg" + (i-1);
				Element param = doc.createElement(tagName);
				param.setTextContent(methodInfo.get(i));
				method.appendChild(param);
			}			
			soapMessage  = SOAPMessageUtility.documentToSOAPMessage(doc);
		} catch (SOAPException e) {
			System.out.println("SOAP Error in updateSOAPUsingDOMNodes: " + e.getMessage());
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			System.out.println("TransformerConfig Error in updateSOAPUsingDOMNodes: " + e.getMessage());
			e.printStackTrace();
		} catch (TransformerException e) {
			System.out.println("Transformer Error in updateSOAPUsingDOMNodes: " + e.getMessage());
			e.printStackTrace();
		} // end try-catch
	} // end updateSOAPUsingDOMNodes
	
	/**
	* Updates a SOAP request parameter's text using javax.xml.soap methods and iterators.
	* The soap body's child element (aka method) is fetched and current parameters removed.
	* Then new sub-element's (aka parameters) are updated. Note that all input parameters 
	* have already been converted to Strings.
	*/
	public void updateSOAPParamsUsingIterators(List<String> params) {
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

} // end class MultiParmeterSOAPMessage

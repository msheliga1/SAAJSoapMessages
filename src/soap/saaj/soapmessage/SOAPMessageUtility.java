package soap.saaj.soapmessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.Text;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * 
 * @author Mike Sheliga 5.1.18
 * Contains accessory routines for SOAPMessages and its components such as SOAPBody.
 *
 * Most methods assist with converting a soapMessage to XML.  
 *  There are other ways to do this including soapMsg.writeTo(System.out) which displays 
 *  XML but no line breaks and using transformers which have line breaks.  
 *  This implementation was a good way to learn about SoapMessages and SAAJ methods.
 *  Less processing methods likely could have been needed if inheritance was used, 
 *  but this implementation was better for learning purposes.
 */

public class SOAPMessageUtility {

	public static final String LEFT_XML = "<";
	public static final String RIGHT_XML = ">";
	public static final String END_XML = "/";
	public static String indenter = "   ";  // 3 spaces for each indentation for now
	public static boolean prettyPrint = true; // include line breaks
	public static final String NEW_LINE = System.lineSeparator();
	public static final String BREAK;
	static {
		if (prettyPrint) {
			BREAK = System.lineSeparator();
		} else {
			BREAK = "";
		}
	}
	

	/* 
	 *  Creates an empty SOAP request message 
	 */
	public static SOAPMessage createEmptySOAPMessage( ) {
		MessageFactory mf = null;
		SOAPMessage sm = null;
		try {
			mf = MessageFactory.newInstance();
			sm = mf.createMessage();
		} catch (SOAPException e) {
			System.out.println("SOAP Error in SAAJComplexClient createSOAPMessage");
			e.printStackTrace();
		} // end try-catch
		return sm;
	} // end createEmptySOAPMessage
	
	
	/* 
	 * Prints a SOAP message in both one line format followed by multi-line formats.
	 */
	public static void multiprintSOAPMessage(SOAPMessage sm) {
		try {
			// printing sm just prints classname@hashCode
			sm.writeTo(System.out);  // Displays XML but no line breaks
			// prettyPrint
			System.out.println(NEW_LINE + SOAPMessageUtility.soapMessageToString(sm)); 
		} catch (SOAPException e) {
			System.out.println("SOAP Error in SAAJComplexClient main");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Error in SAAJComplexClient main");
			e.printStackTrace();
		} // end try-catch
	} // end multiprintSOAPMessage
	
	public static String soapMessageToString(SOAPMessage sm) {
		
		String result = "";
		String indent = ""; 
		
		// System.out.println("Displaying SOAP Message ....");
		if (sm==null) {
			System.out.println("soap message is null!!");
			return result;
		}
		String cd = sm.getContentDescription();
		if (cd != null) result += "Content: " + cd + BREAK;

		try {
			Object obj = sm.getProperty(SOAPMessage.WRITE_XML_DECLARATION);
			if (obj != null) {
				result += "XMLDeclaration: " + obj.toString() + BREAK;
			}
			obj = sm.getProperty(SOAPMessage.CHARACTER_SET_ENCODING);
			if (obj != null) result += obj.toString() + BREAK;
			SOAPPart soapPart = sm.getSOAPPart();
			if (soapPart == null) {
				System.out.println("soapPart is null!!");
				return result;
			}
			SOAPEnvelope soapEnv = soapPart.getEnvelope();  // throws SOAPException
			if (soapEnv == null) {
				System.out.println("soap Envelope is null!!");
				return result;
			}
			result += soapEnvelopeToString(soapEnv, indent);
			// soap attachments ToDo
		} catch (SOAPException e) {
			System.out.println("soap Exception in soapMessageToString");
			e.printStackTrace();
		} // end try-catch	
		return result;
	} // end soapMessageToString
	
	/* 
	 * Converts a SOAPEnvelope to a string including line breaks and indentation.
	 */
	public static String soapEnvelopeToString(SOAPEnvelope soapEnv, String indent) { 		
		String result = "";
		if (soapEnv == null) {
			System.out.println("soap Envelope is null!!");
			return result;
		}
		try {
			String tagBody = soapEnv.getNodeName();
			// SoapEnv.toString, getBaseURI, getValue and getNodeValue will be null
			// must use getNodeName (ie SOAP-ENV:Envelope, not getLocalName which is only Envelope)
			// and getPrefix (ie SOAP-ENV)
			// and getNamespaceURI (ie http://schemas...)
			// dont use getNodeName, getPrefix, getNamespaceURI to build NS attribute, get 
			// it directly, but note that getAllAttributes and getAllAttributesAsQNames 
			// wont get NSattr, so must use NamedNodeMap = getAttributes
			// String tagBody = soapEnv.getNodeName() + " xmlns:" + soapEnv.getPrefix() + "=\"" + 
			// 	             soapEnv.getNamespaceURI() + "\"";
			if (soapEnv.hasAttributes()) {
				NamedNodeMap nnm = soapEnv.getAttributes();  // this includes attrNSImpl namespaces.
				if (nnm != null) {
					for (int i = 0; i < nnm.getLength(); i++) {
						tagBody += " " + nnm.item(i);
						// System.out.println("nnm(" + i + ") = " + nnm.item(i));
					}
				}
			}
			result += indent + toXmlStartTag(tagBody) + BREAK;		
			result += soapHeaderToString(soapEnv.getHeader(), indent + indenter);
			result += soapBodyToString(soapEnv.getBody(), indent + indenter);
			result += indent + toXmlEndTag(soapEnv.getNodeName()) + BREAK;
		} catch (SOAPException e) {
				e.printStackTrace();
		} // end try-catch
			
		// System.out.println("Done SOAPEnvelopeToString");		
		return result;
		
	} // end SOAPEnvelopeToString
	
	/* 
	 * Converts a SOAPHeader to a string including line breaks and indentation.
	 */
	public static String soapHeaderToString(SOAPHeader sh, String indent) {
		String result = "";
		if (sh==null) {
			// System.out.println(indent + "SoapHeader is null!!");
			return result; // SOAPMessages may have null headers
		}
				
		// note that we pass a soapHeader which is a type of soapElement
		boolean isEmpty = soapElementIsEmpty(sh);
		
		try {
			// Set up the start tag body, including the attributes, then creat startTag
			// sb.toString, sb.getValue, sb.getNodeValue print some form of null 
			String tagBody = sh.getNodeName();
			if (sh.hasAttributes()) {
				NamedNodeMap nnm = sh.getAttributes(); // getAllAttributes wont get NS attribs
				if (nnm != null) { 
					for (int j=0; j < nnm.getLength(); j++) {
						tagBody += nnm.item(j).toString();
					}					
				}
			}
			// if tag is empty return empty tag of form <car color=red/>
			if (soapElementIsEmpty(sh)) {
				result += indent + toXmlEmptyTag(tagBody) + BREAK;
				return result;
			}
			result += indent + toXmlStartTag(tagBody) + BREAK;
			
			// Fetch and process all child elements
			Iterator<SOAPHeaderElement> shi = sh.getChildElements();
			while (shi.hasNext()) {
				SOAPHeaderElement she = shi.next();
				result += soapHeaderElementToString(she, indent + indenter);
			}
			// Finally build the xml end tag
			result += indent + toXmlEndTag(sh.getNodeName()) + BREAK;
		} catch (Exception ex) {
			System.out.println("Exception in displaySOAPHeader");
			ex.printStackTrace();
		}		
		return result;
	} // end soapHeaderToString

	/* 
	 * Converts a SOAPHeaderElement to a string including line breaks and indentation.
	 */
	public static String soapHeaderElementToString(SOAPHeaderElement she, String indent) {
		String result = "";
		if (she==null) {
			return result;
		}

		// First build the start tag body including all attributes
		// sb.toString, getNodeValue and sb.getValue print some form of null
		// Couldnt get Iterator<Name>, Iterator<Object> or Iterator to include NSattribs
		// for getAllAttributes or getAllAttributesAsQNames. 5.11.18
		String tagHeader = she.getNodeName();
		if (she.hasAttributes()) {
			NamedNodeMap nnm = she.getAttributes();  // this includes attrNSImpl namespaces.
			if (nnm != null) {
				for (int j = 0; j < nnm.getLength(); j++) {
					tagHeader += " " + nnm.item(j);
				}
			}
		}
		result += indent + toXmlStartTag(tagHeader) + BREAK;
		
		// Next process all children
		Iterator<SOAPElement> iter = she.getChildElements();
		while (iter.hasNext()) {
			result += soapElementToString(iter.next(), indent + indenter);
		}
		
		// Finally create the end tag
		result += indent + toXmlEndTag(she.getNodeName()) + BREAK;
	
		return result;
	} // end soapHeaderElementToString	
	
	
	// ------------ SOAP Body Routines ------------
	/* 
	 * Converts a SOABody to a string including line breaks and indentation.
	 */
	public static String soapBodyToString(SOAPBody sb, String indent) {
		String result = "";
		if (sb==null) {
			System.out.println(indent + "SoapBody is null!!");
			return result;
		}
		
		try {
			// result += indent + "The SOAP Body is:" + BREAK;
			// sb.toString and sb.getValue print some form of null
			String tagBody = sb.getNodeName();
			result += indent + toXmlStartTag(tagBody) + BREAK;
			Iterator<SOAPBodyElement> sbi = sb.getChildElements();
			while (sbi.hasNext()) {
				SOAPBodyElement sbe = sbi.next();
				result += soapBodyElementToString(sbe, indent + indenter);
			}
			result += indent + toXmlEndTag(sb.getNodeName()) + BREAK;
		} catch (Exception ex) {
			System.out.println("Exception in sOAPBodyToString");
			ex.printStackTrace();
		}		
		return result;
	} // end soapBodyToString

	
	/* 
	 * Converts a SOAPHeaderElement to a string including line breaks and indentation.
	 */
	public static String soapBodyElementToString(SOAPBodyElement sbe, String indent) {
		String result = "";
		if (sbe==null) {
			return result;
		}

		// First build the start tag body including all attributes
		// sb.toString, getNodeValue and sb.getValue print some form of null
		// Couldnt get Iterator<Name>, Iterator<Object> or Iterator to include NSattribs
		// for getAllAttributes or getAllAttributesAsQNames. 5.11.18
		String tagBody = sbe.getNodeName();
		if (sbe.hasAttributes()) {
			NamedNodeMap nnm = sbe.getAttributes();  // this includes attrNSImpl namespaces.
			if (nnm != null) {
				for (int j = 0; j < nnm.getLength(); j++) {
					tagBody += " " + nnm.item(j);
				}
			}
		}
		result += indent + toXmlStartTag(tagBody) + BREAK;
		
		// Next process all children
		Iterator<Object> iter = sbe.getChildElements();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj instanceof Text) {
				Text txt = (Text) obj;
				result += indent + txt.getData() + BREAK;
				continue;
			} else if (obj instanceof SOAPElement) {
				SOAPElement se = (SOAPElement) obj;
				result += soapElementToString(se, indent + indenter);					
			} else {
				System.out.println("Unknown type in soapBodyElementToString of " + 
					obj.getClass());
			}
		}
		
		// Finally create the end tag
		result += indent + toXmlEndTag(sbe.getNodeName()) + BREAK;
	
		return result;
	} // end soapBodyElementToString
	
	
	/* 
	 * Converts a SOAPElement to a string including line breaks and indentation.
	 */
	public static String soapElementToString(SOAPElement se, String indent) {
		String result = "";
		if (se==null) {
			return result;
		}
		// stick start-text-end on one line
		boolean oneLine = soapElementOnlyHasTextChildren(se);
		String conditionalNewLine = BREAK;  // typical new line
		if (oneLine) conditionalNewLine = "";
		
		try {
			// create the start tag including all attributes
			// se.toString, getNodeValue and getValue print some form of null
			String tagBody = se.getNodeName();
			if (se.hasAttributes()) {
				NamedNodeMap nnm = se.getAttributes();  // this includes attrNSImpl namespaces.
				if (nnm != null) {
					for (int j = 0; j < nnm.getLength(); j++) {
						tagBody += " " + nnm.item(j);
					}
				}
			}
			// if tag is empty return empty tag of form <car attr1=name1 attr2=name2/>
			if (soapElementIsEmpty(se)) {
				result += indent + toXmlEmptyTag(tagBody) + BREAK;
				return result;
			}
			result += indent + toXmlStartTag(tagBody) + conditionalNewLine;
			
			// process all child elements
			Iterator<Object> iter = se.getChildElements();
			while (iter.hasNext()) {
				Object obj = iter.next();
				if (obj instanceof Text) {
					Text txt = (Text) obj;
					if (!oneLine) result += indent;
					result += txt.getData() + conditionalNewLine;
					continue;
				} else if (obj instanceof SOAPElement) {
					SOAPElement so = (SOAPElement) obj;
					result += soapElementToString(so, indent + indenter);					
				} else {
					System.out.println("Unknown type in soapElementToString of " + obj.getClass());
				}
			}
			// generate endTag including newLine even if this is only a text element
			if (!oneLine) result += indent;
			result += toXmlEndTag(se.getNodeName()) + BREAK;  
		} catch (Exception ex) {
			System.out.println("Exception in soapElementToString");
			ex.printStackTrace();
		}		
		return result;
	} // end soapElementToString
	
	// ============ Non toString accessory routines ============
	/* 
	 * Checks if a SOAPElement is a text element. 
	 * It may include attributes and still be a text element.
	 */
	public static boolean soapElementOnlyHasTextChildren(SOAPElement se) {
		if (se==null) {
			return true;
		}
		// process all child elements
		Iterator<Object> iter = se.getChildElements();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj instanceof Text) {
				continue;			
			} else {
				return false;
			}
		}					
		return true;
	} // end soapElementOnlyHasTextChildren
	
	/* 
	 * Checks if a SOAPElement is empty.
	 * Note that empty elements may have attributes such as <car color=red />
	 */
	public static boolean soapElementIsEmpty(SOAPElement se) {
		if (se==null) {
			return true;
		}
		// check if there are any child elements
		Iterator<Object> iter = se.getChildElements();
		if (iter.hasNext()) return false;
		return true;
	} // end soapElementIsEmpty
	
	
	/* Generates an XML start tag given the tagBody text such as <Header>.
	 * Adds start and end brackets around the tagBody text.
	 */
	public static String toXmlStartTag(String body) {	
		if (body==null) {
			return LEFT_XML + RIGHT_XML;
		}
		return LEFT_XML + body + RIGHT_XML;
	} // end toXmlStartTag

	/* Generates an XML end tag given the tagBody text such as </car>.
	 * Adds start and end brackets and end slash around the tagBody text.
	 */
	public static String toXmlEndTag(String body) {	
		if (body==null) {
			return LEFT_XML + END_XML + RIGHT_XML;
		}
		return LEFT_XML + END_XML + body + RIGHT_XML;
	} // end toXmlEndTag
	
	/* 
	 * Create an XML empty tag such as <car attrib=name/> 
	*/
	public static String toXmlEmptyTag(String body) {	
		if (body==null) {
			return LEFT_XML + END_XML + RIGHT_XML;
		}
		return LEFT_XML + body + END_XML + RIGHT_XML;
	} // end toXmlEmptyTag
	
	/*
	 *   Converts a DOM Document to a SOAPMessage 
	 *   Precondition: Document must actually be xml corresponding to SOAP.
	*/
	public static SOAPMessage documentToSOAPMessage(Document doc) {
		// Could use JDOM, but want to stick to basic java for now.
		// org.jdom.output.XMLOutputter is an open source non-JDK class.
		// String xml = new XMLOutputter().outputString();
		SOAPMessage sm = null;  // this is the result
		String docString = null;
		try {
			// first convert the Document to a string
		    DOMSource domSource = new DOMSource(doc);
		    StringWriter writer = new StringWriter();
		    StreamResult result = new StreamResult(writer);
		    TransformerFactory tf = TransformerFactory.newInstance();
		    Transformer transformer;
			transformer = tf.newTransformer();
		    transformer.transform(domSource, result);
		    docString = writer.toString();	
		    // System.out.println("The Document is: " + docString);
		    
		    // Now convert the xmlString into a SOAPMessage
		    InputStream inputStream = new ByteArrayInputStream(docString.getBytes());
		    // This is a SOAP message factory
		    MessageFactory mf = MessageFactory.newInstance();
		    sm = mf.createMessage(null, inputStream);
		} catch (TransformerConfigurationException e) {
			System.out.println("Transformer Config error in documentToSOAPMessage.");
			e.printStackTrace();
		} catch (TransformerException e) {
			System.out.println("Transformer error in documentToSOAPMessage.");
			e.printStackTrace();
		} catch (SOAPException e) {
			System.out.println("SOAP error in documentToSOAPMessage.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO error in documentToSOAPMessage.");
			e.printStackTrace();
		}	
		return sm;
	} // end DocumentToSOAPMessage
	
} // end SOAPMessageUtilityClass

package org.menacheri.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.transform.stream.StreamResult;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Some conversion utilities which will take care of converting from JDom to
 * String and back.
 * 
 * @author Abraham Menacherry.
 * 
 */
public class XMLUtils {

	public static String convertStreamToString(StreamResult result) throws IOException
	{
		String xml = null;
		OutputStream stream = result.getOutputStream();
		if(null != stream){
			xml = stream.toString();
			stream.close();
		}
		return xml;
	}
	
	public static String convertElementToString(Element element)
	{
		XMLOutputter putter = new XMLOutputter();
		return putter.outputString(element);
	}
	
	/**
	 * Converts incoming xml data into a XML PCDATA string by removing special
	 * characters like greater than, less than symbol etc. This method is
	 * extremely useful when you are dealing with web services which accept a
	 * xsd:String input, which in turn is supposed to an xml message.
	 * 
	 * @param xmlString
	 *            The PCDATA string which needs to be converted to XML String.
	 * @return The XML string representation of the message.
	 */
	public static String getXMLEscapedText(String xmlString)
	{
		Element element = new Element("testsuite");
		element.setText(xmlString);
		String xml = convertElementToString(element);
		String replaced = xml.replaceFirst("<testsuite>", "");
		String result = replaced.replaceFirst("</testsuite>", "");
		return result;
	}
	
	public static Element convertStringToJdomElement(String xml)
			throws JDOMException, IOException
	{
		Document result = convertStringToJdomDoc(xml);
		return result.detachRootElement();
	}
	
	public static Document convertStringToJdomDoc(String xml)
			throws JDOMException, IOException 
	{
		SAXBuilder builder = new SAXBuilder();
		Document result = null;
		result = builder.build(new StringReader(xml));
		return result;

	}
}

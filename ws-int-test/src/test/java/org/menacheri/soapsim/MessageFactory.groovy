package org.menacheri.soapsim;

import org.jdom.Element
import org.menacheri.invmmsg.DefaultMessage;
import org.menacheri.invmmsg.IMessage
import org.menacheri.invmmsg.MessageIdentity
import org.menacheri.invmmsg.MessageIdentityImpl
import org.menacheri.util.XMLUtils
import org.menacheri.util.XMLValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.xml.sax.SAXException

/**
 * This helper class will take the incoming xml and create a IMessage instance and 
 * return it back. In order to create the message instance, it also creates a MessageIdentity 
 * instance. This identity is created by parsing the xml and getting the relevant information 
 * and using it as the identifier. Note that the identity object so created has to match the  
 * identity object created by the test case while subscribing on the invm channel. Only then  
 * will the message get delivered to the right test case.
 * 
 * NOTE: There is another spring 'messageFactory' bean in the app context already hence a 
 * different name is chosen for this component.
 * @author Abraham Menacherry.
 *
 */
@Component(value='messageFactoryHoliday')
class MessageFactory 
{
	@Autowired
	XMLValidator xmlValidator;
	
	public IMessage createHolidayMessage(Element holidayRequest)
	{
		String xml = XMLUtils.convertElementToString(holidayRequest);
		Exception exception = validateXML(xml,xmlValidator);
		def request = new XmlSlurper().parseText(xml).declareNamespace(ns:HumanResourceEndpoint.getNamespaceUri());
		String empId = request?.Employee?.Number?.text();
		MessageIdentity identity = new MessageIdentityImpl<String>(empId);
		IMessage message = new DefaultMessage(identity);
		message.setPayload(holidayRequest);
		message.setException(exception);
		return message;
	}
	
	public IMessage createHolidayResponseMsg(Element holidayResponse)
	{
		String xml = XMLUtils.convertElementToString(holidayResponse);
		Exception exception = validateXML(xml,xmlValidator);
		def response = new XmlSlurper().parseText(xml).declareNamespace(ns:HumanResourceEndpoint.getNamespaceUri());
		String empId = response?.Employee?.Number?.text();
		MessageIdentity identity = new MessageIdentityImpl<String>(empId);
		IMessage message = new DefaultMessage(identity);
		message.setPayload(xml);
		message.setException(exception);
		return message;
	}
	
	public Exception validateXML(String messageBody,XMLValidator xmlValidator)
	{
		Exception exception = null;
		try {
			xmlValidator.validate(messageBody);
		} catch (SAXException e) {
			exception = e;
		} catch (IOException e) {
			exception = e;
		}
		return exception;
	}
}

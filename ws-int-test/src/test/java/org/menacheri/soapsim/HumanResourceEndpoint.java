package org.menacheri.soapsim;

import org.jdom.Element;
import org.jdom.Namespace;
import org.menacheri.invmmsg.InVMChannel;
import org.menacheri.invmmsg.IMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * This class is a spring web service {@link Endpoint}. When a web service call
 * is made by a client with the proper name space and local part the
 * corresponding method is invoked by spring-ws on this class.
 * 
 * @author Abraham Menacherry.
 * 
 */
@Endpoint("hrEndpoint")
public class HumanResourceEndpoint 
{
	public static final String NAMESPACE_URI ="http://menacheri.org/hr/schemas";
	public static final Namespace NPC_NAMESPACE = Namespace.getNamespace("hr",NAMESPACE_URI);
	private InVMChannel inVMChannel;
	private MessageFactory messageFactory;
	
	@PayloadRoot(namespace=NAMESPACE_URI, localPart="HolidayRequest")
	@ResponsePayload
	public Element processHolidayRequest(@RequestPayload Element holidayRequest)
	{
		System.out.println("In processHolidayRequest, thread: " + Thread.currentThread().getName());
		Element response = publish(holidayRequest);
		return response;
	}
	
	public Element publish(Element holidayRequest)
	{
		if(null == holidayRequest) return null;
		Element response = null;
		IMessage message = messageFactory.createHolidayMessage(holidayRequest);
		IMessage reply = inVMChannel.publish(message);
		if(null!=reply){
			response = (Element)reply.getPayload();
		}
		return response;
	}
	
	/**
	 * This end point should <b>not</b> be on the server. This is put here only
	 * for showing async test case example. Actually this is the response that
	 * is sent to client by the server.
	 * 
	 * @param holidayResponse
	 */
	@PayloadRoot(namespace=NAMESPACE_URI, localPart="HolidayResponse")
	public void processHolidayResponse(@RequestPayload Element holidayResponse)
	{
		System.out.println("In processHolidayResponse, thread: " + Thread.currentThread().getName());
		IMessage message = messageFactory.createHolidayResponseMsg(holidayResponse);
		inVMChannel.publish(message);// Fire and forget.
	}
	
	public InVMChannel getInVMChannel() 
	{
		return inVMChannel;
	}
	
	@Autowired
	public void setInVMChannel(InVMChannel inVMChannel) 
	{
		this.inVMChannel = inVMChannel;
	}

	public static String getNamespaceUri() {
		return NAMESPACE_URI;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

	@Autowired
	@Qualifier("messageFactoryHoliday")
	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
}

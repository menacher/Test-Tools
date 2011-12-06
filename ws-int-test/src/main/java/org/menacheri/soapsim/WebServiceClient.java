package org.menacheri.soapsim;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.menacheri.util.XMLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;


/**
 * A helper class which wraps the spring {@link WebServiceTemplate} class. It is
 * mainly used to send and receive plain xml strings to remote web services.
 * 
 * @author Abraham Menacherry.
 * 
 */
@Component
public class WebServiceClient {

	private final WebServiceTemplate webServiceTemplate;
	
	@Autowired
	public WebServiceClient(WebServiceTemplate webServiceTemplate)
	{
		this.webServiceTemplate = webServiceTemplate;
	}
	
	/**
	 * A utility method which can take an xml as a string, an endpoint address
	 * and take care of the sending part. Useful when sending xml templates
	 * created from Groovy.
	 * 
	 * @param request
	 *            An xml string to be sent to the remote web service.
	 * @param address
	 *            The remote address at which the web service is running.
	 * @return Returns the reply from the web service in String format or null
	 *         if it is a void return.
	 * @throws IOException
	 * @throws JDOMException
	 */
	public String getStringReqReply(String request, String address) throws IOException,JDOMException 
	{
		String xml = null;
		StreamResult result = sendStringAndReceiveToResult(request, address);
		if(null != result){
			xml = XMLUtils.convertStreamToString(result);
		}
		return xml;
	}
	
	public synchronized StreamResult customSendAndReceive(String message, String address) 
	{
        StreamSource source = new StreamSource(new StringReader(message));
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
        StreamResult result = new StreamResult(bos);
        
        if(webServiceTemplate.sendSourceAndReceiveToResult(address,
                source, result)){
        	return result;
        }else{
        	return null;
        }
    }
	
	/**
	 * Sends an xml message as string to a remote address. It returns back a
	 * stream source containing the remote servers reply or null if there is no
	 * reply.
	 * 
	 * @param xmlStringPayload
	 * @param address
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public synchronized StreamResult sendStringAndReceiveToResult(String xmlStringPayload,
			String address) throws JDOMException, IOException
	{
		Document jdomDocument = XMLUtils.convertStringToJdomDoc(xmlStringPayload);
		org.jdom.output.DOMOutputter outputter = new org.jdom.output.DOMOutputter();
		org.w3c.dom.Document domDocument = null;
		domDocument = outputter.output(jdomDocument);
		javax.xml.transform.Source xmlSource = new javax.xml.transform.dom.DOMSource(
				domDocument);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(bos);

		if (webServiceTemplate.sendSourceAndReceiveToResult(address, xmlSource,
				result)) {
			return result;
		} else {
			return null;
		}
	}

	public WebServiceTemplate getWebServiceTemplate() {
		return webServiceTemplate;
	}
}

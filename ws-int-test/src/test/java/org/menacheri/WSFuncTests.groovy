package org.menacheri

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull
import static org.testng.Assert.fail

import java.util.concurrent.TimeUnit

import org.jdom.Element
import org.menacheri.invmmsg.IMessageCallback;
import org.menacheri.invmmsg.InVMChannel
import org.menacheri.invmmsg.IMessage
import org.menacheri.invmmsg.MessageCallback
import org.menacheri.invmmsg.MessageIdentity
import org.menacheri.invmmsg.MessageIdentityImpl
import org.menacheri.soapsim.WebServiceClient
import org.menacheri.templates.HolidayXmlTemplates
import org.menacheri.util.XMLUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

/**
 * The "test suite" which can run TestNG test cases written in Groovy. When writing test cases, 
 * <b>extending AbstractTestNGSpringContextTests is absolutely important</b> otherwise it may 
 * result in some weird exceptions. The ContextConfiguration is necessary to load the correct 
 * spring beans definition file. Using different such classes and files the same eclipse project 
 * can be used to test a wide variety of product releases.  
 * @author Abraham Menacherry.
 *
 */
@ContextConfiguration( locations=("classpath:ws-int-test-beans.xml") )
class WSFuncTests  extends AbstractTestNGSpringContextTests
{
	@Autowired
	private InVMChannel inVMChannel;
	
	@Autowired
	private WebServiceClient client;
	
	@Value('${holidayRequest.url}')
	private String holidayUrl;
	
	@Value('${holidayNamespace}')
	private String schemaNamespace;
	
	/**
	 * Synchronous test case which will subscribe synchronously to the memory channel. 
	 * The callback contains the code for parsing request and sending the response. 
	 * Callback will be executed on receipt of the request. 
	 */
	@Test(groups="schema-test")
	public void sendHolidayRequest()
	{
		HolidayXmlTemplates template = new HolidayXmlTemplates();
		template.empId = "100";
		// The subscription is the code that is actually sending back the response to this test case.
		subscribeSyncToHolidayRequest("SUCCESS",template.empId);
		String holidayRequest = template.getHolidayRequest();// Get the request from the template.
		// Send the request to server. The "server" in this example is actually in the
		// same machine and same jvm listening on port 8080. On receipt of the request
		// it will execute the callback and provide the response.
		String reply = client.getStringReqReply(holidayRequest,holidayUrl);
		assertNotNull(reply);
		def response = new XmlSlurper().parseText(reply).declareNamespace(ws:schemaNamespace);
		def status = response.Status.text();
		assertEquals(status,"SUCCESS");
	}
	
	/**
	 * This is the async scenario. This is rather contrived test, first a request is sent to a remote  
	 * web service, Test case waits for this request to reach the callback. Then the response 
	 * is sent again to the server(rather than client). Test case now waits for this response to reach 
	 * the response callback.
	 */
	@Test(groups="schema-test")
	public void asyncTest()
	{
		HolidayXmlTemplates template = new HolidayXmlTemplates();
		template.empId = "110";
		
		// Do the subscription first and only then send the message to server in order to prevent race conditions
		MessageCallback reqCallback = subscribeToHolidayRequest("SUCCESS",template); // Async subscription.
		String holidayRequest = template.getHolidayRequest();
		client.getStringReqReply(holidayRequest,holidayUrl); // Since response is sent async the reply is of no use here.
		if(!reqCallback.await(10, TimeUnit.SECONDS)){
			reqCallback.done();// Required since the callback was never executed.
			fail ("Message was not recieved in stipulated time. #fail")
		}
		
		// At this point the request callback has executed. Subscribe now for the response.
		// Using the template(which got updated in req callback) send response xml to the server.
		MessageCallback resCallback = subscribeToHolidayResponse(template.empId);
		// Get the xml response from the updated template
		String response = template.getHolidayResponse();
		client.getStringReqReply(response,holidayUrl);// Send response to server again!
		
		// Wait on the response callback to check if response has reached the server.
		if(!resCallback.await(10, TimeUnit.SECONDS)){
			resCallback.done();// Required since the callback was never executed.
			fail ("Message was not recieved in stipulated time. #fail")
		}
	}
	
	/**
	 * A test to show the working of identity object. The xml request sent to the server has id as 120, 
	 * but the callback is waiting using an id "wrong id" hence it will never get the request even 
	 * though the request reaches the server properly.
	 */
	@Test(groups="schema-test")
	public void wrongIdentityTest()
	{
		HolidayXmlTemplates template = new HolidayXmlTemplates();
		template.empId = "120";
		String holidayRequest = template.getHolidayRequest();
		template.empId = "wrong id"; // Set a wrong id on the template so that identities between xml sent and callback do not match.
		MessageCallback reqCallback = subscribeToHolidayRequest("SUCCESS",template);
		client.getStringReqReply(holidayRequest,holidayUrl); // Since response is sent async the reply is of no use here.
		// If await returns true then callback has executed!
		if(reqCallback.await(5, TimeUnit.SECONDS)){
			fail ("Callback was executed even though id was wrong. #fail")
		}else{
			reqCallback.done();// Required since the callback was never executed.
			// But callback may not have got executed even if the xml was invalid 
			// rather than identity problem so check if exception object is null or not.
			IMessage message = reqCallback.getMessage();
			if(message){
				assertNull(message.getException());
			}
		}
	}
	
	/**
	 * Method which will subscribe synchronously to the in-vm channel. The reply to the web service 
	 * request will hence be provided in the same thread as the web service request was invoked by 
	 * the spring container.
	 * @param status
	 * @param empId
	 */
	public IMessageCallback subscribeSyncToHolidayRequest(String status, String empId)
	{
		MessageIdentity identity = new MessageIdentityImpl<String>(empId);
		
		MessageCallback callback = new MessageCallback(){
			@Override
			public void onCallback(IMessage message){
				println "Callback thread:  ${Thread.currentThread().name}"
				// Create template and put all the values from request on the template.
				HolidayXmlTemplates template = new HolidayXmlTemplates(status:status,empId:empId);
				setValuesOnTemplateFromRequest(message.getPayload(),template);
				// Get the xml response from the template and set it as pay load on the message.
				String xmlResponse = template.getHolidayResponse();
				Element response = XMLUtils.convertStringToJdomElement(xmlResponse);
				// Set the response as the new message pay load. 
				// This will be used by the end point to reply back to client
				message.setPayload(response);
			}
		}
		// Subscribe the code on the channel. Note the "sync"
		inVMChannel.subscribeSync(callback,identity);
		return callback;
	}
	
	/**
	 * Method does asynchronous subscription and returns a callback on which the test case can 
	 * wait on to see if it got executed.
	 * @param status
	 * @param empId
	 * @return
	 */
	public IMessageCallback subscribeToHolidayRequest(String status, def template)
	{
		MessageIdentity identity = new MessageIdentityImpl<String>(template.empId);
		
		MessageCallback callback = new MessageCallback(){
			@Override
			public void onCallback(IMessage message){
				println "Async Request Callback thread:  ${Thread.currentThread().name}";
				setValuesOnTemplateFromRequest(message.getPayload(),template);
			}
		}
		// Subscribe the code on the channel. This is async subscription, 
		// meaning code will be run on a different thread relative to the 
		// message publishing thread.
		inVMChannel.subscribe(callback,identity);
		return callback;
	}
	
	public IMessageCallback subscribeToHolidayResponse(String empId)
	{
		MessageIdentity identity = new MessageIdentityImpl<String>(empId);
		MessageCallback callback = new MessageCallback(){
			@Override
			public void onCallback(IMessage message){
				println "Async Response Callback thread:  ${Thread.currentThread().name}"
			}
		}
		inVMChannel.subscribe(callback,identity);
		return callback;
	}
	
	/**
	 * Helper method to get values from xml request and set it on the template. 
	 * Shows the power and conciseness of groovy xml slurper.
	 * @param payload
	 * @param template
	 */
	private void setValuesOnTemplateFromRequest(Element xmlRequest, HolidayXmlTemplates template )
	{
		String payload = XMLUtils.convertElementToString(xmlRequest);
		def request = new XmlSlurper().parseText(payload).declareNamespace(ws:schemaNamespace);
		def employee = request?.Employee
		template.firstName = employee?.FirstName?.text();
		template.lastName = employee?.LastName?.text();
	}
}

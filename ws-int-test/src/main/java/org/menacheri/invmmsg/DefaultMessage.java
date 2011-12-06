package org.menacheri.invmmsg;



/**
 * This class is used to abstract an incoming or outgoing xml message to the
 * test bed. It contains an identity object which can be used by test cases to
 * filter and receive specific requests and a payload, which is the actual
 * incoming message. It is mainly used to publish and subscribe on the
 * {@link InVMChannel}.
 * 
 * @author Abraham Menacherry
 * 
 */
public class DefaultMessage implements IMessage {
	
	/**
	 * The message identity object which is used for filtering purposes. Only if
	 * the identity matches will a message get delivered to the
	 * {@link MessageCallback}.
	 */
	private final MessageIdentity messageIdentity;
	/**
	 * The actual incoming message, xml/JDOM etc is set on this variable.
	 */
	private Object payload;
	
	/**
	 * Used to capture any exception that occurred during
	 * communication/validation of xml etc. Using this, the callback gets to
	 * know that an exception has occurred and can fail the test if necessary in
	 * the test case thread. If the exception is thrown at the endpoint itself,
	 * then test case will be unaware of the actual error that occurred. If
	 * there are multiple test cases co-relating such exceptions which occurred
	 * in another thread and the actual test case could be hard.
	 */
	private Exception exception = null;
	
	public DefaultMessage(MessageIdentity messageIdentity)
	{
		this.messageIdentity = messageIdentity;
	}
	
	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessage#getMessageIdentity()
	 */
	@Override
	public MessageIdentity getMessageIdentity() {
		return messageIdentity;
	}
	
	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessage#getPayload()
	 */
	@Override
	public Object getPayload() {
		return payload;
	}
	
	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessage#setPayload(java.lang.Object)
	 */
	@Override
	public void setPayload(Object payload) {
		this.payload = payload;
	}

	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessage#getException()
	 */
	@Override
	public Exception getException() {
		return exception;
	}

	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessage#setException(java.lang.Exception)
	 */
	@Override
	public void setException(Exception exception) {
		this.exception = exception;
	}
}

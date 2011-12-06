package org.menacheri.invmmsg;


/**
 * An interface which describes a message soap/xml/jdom which is recieved or
 * sent from/to a remote interface.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IMessage {

	/**
	 * @return the identity object associated with this method. This object is
	 *         normally created by the message factory and set on the message
	 *         instance on receipt of the soap message.
	 */
	public abstract MessageIdentity getMessageIdentity();

	public abstract Object getPayload();

	public abstract void setPayload(Object payload);

	public abstract Exception getException();

	/**
	 * Set the exception for later analysis by the test case. This way, failure
	 * messages at the test case level will make more sense.
	 * 
	 * @param exception
	 */
	public abstract void setException(Exception exception);

}
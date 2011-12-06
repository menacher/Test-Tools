package org.menacheri.invmmsg;

import org.jetlang.core.Filter;


/**
 * This class is used to co-relate between an async request and reply test case.
 * A test case can send a message to a web service some message identification
 * say a transaction id. In order to listen for the reply to this message an
 * identity object is used while subscribing to the endpoint's
 * {@link InVMChannel}. The end point will receive an xml/soap from the web
 * service client, create the associated identity object and publish it to the
 * same {@link InVMChannel} instance. Since the test case's identity and this
 * newly created identity match(it should!), the appropriate
 * {@link MessageCallback} provided by the test case will be invoked.
 * Additionally, this class also implements the {@link Filter} interface of
 * Jetlang to enable Jetlang to invoke the correct callback.
 * 
 * @author Abraham Menacherry
 * 
 */
public class MessageIdentityImpl<T> implements MessageIdentity{
	
	@Override
	public boolean passes(IMessage iMessage) {
		if(null == iMessage){
			return false;
		}
		return equals(iMessage.getMessageIdentity());
	}
	
	private final T id;
	
	public MessageIdentityImpl(T id)
	{
		this.id = id;
	}

	public T getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		MessageIdentityImpl other = (MessageIdentityImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}

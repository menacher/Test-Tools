package org.menacheri.invmmsg;

import org.jetlang.core.Filter;

/**
 * This interface decides which {@link IMessageCallback} to be executed when a
 * message is published to the {@link InVMChannel}. It extends the Jetlang
 * {@link Filter} interface to filter out a message that is to be sent to a
 * callback.
 * 
 * This is a key interface which allows asynchronous request-replies of an
 * integration test to be co-related.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface MessageIdentity extends Filter<IMessage> 
{
	
}

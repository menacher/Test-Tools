package org.menacheri.invmmsg;

import org.jetlang.channels.ChannelSubscription;
import org.jetlang.channels.MemoryChannel;
import org.jetlang.core.Disposable;
import org.jetlang.core.DisposingExecutor;
import org.jetlang.fibers.Fiber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class implements in vm messaging using <a
 * href="http://code.google.com/p/jetlang/>Jetlang</a> library. It has a private
 * message publishing channel, to which incoming messages can be published, say
 * by the spring web service end points. Interested classes (usually test cases) can
 * listen on the channel by using one of the subscribe methods.
 * 
 * @author Abraham Menacherry
 * 
 */
@Component
public class InVMChannel {
	
	/**
	 * The <a href="http://code.google.com/p/jetlang/>Jetlang</a> memory channel
	 * which is the conduit between threads.
	 */
	private final MemoryChannel<IMessage> messageQueue = new MemoryChannel<IMessage>();

	private Fibers fibers;
	
	/**
	 * For async publish, this Method simply delegates to the publish method on
	 * the memory channel. Whenever a thread needs to publish a
	 * {@link DefaultMessage} to its subscribers this method should be used. For
	 * sync publish, the response is set as payload on the message instance
	 * itself.
	 * 
	 * @param message
	 *            The message to be published on the channel for interested
	 *            subscribers (mostly a test case)
	 * @return Returns a new message object containing reply in case of sync
	 *         messages or same message in case of async or error.
	 */
	public IMessage publish(IMessage message) {
		messageQueue.publish(message);
		return message;
	}

	/**
	 * Some webservice calls expect a synchronous reply back to the caller (some
	 * client). This method should be used for such scenario, the code in
	 * {@link MessageCallback} will be invoked on the webservice endpoint's
	 * thread if subscribed in this manner, allowing for synchronous, but
	 * customized replies to the caller. This method additionaly takes a
	 * {@link MessageIdentity}, so that it will only receive the messages
	 * filtered from the channel, which matches the identity and not all
	 * messages.
	 * 
	 * @param messageCallback
	 *            The callback containing the code to be executed when a
	 *            specific, filtered out message gets published to the channel
	 * @param identity
	 *            Based on the identity, only those messages published on the
	 *            channel with same identity will be filtered out and send to
	 *            the message callback's code for execution.
	 * 
	 */
	public void subscribeSync(IMessageCallback messageCallback,
			MessageIdentity identity) {
		DisposingExecutor sync = fibers.syncFiber();
		messageCallback.setFiber(sync);
		ChannelSubscription<IMessage> subscription = new ChannelSubscription<IMessage>(
				sync, messageCallback, identity);
		Disposable disposable = messageQueue.subscribe(subscription);
		messageCallback.setDisposable(disposable);
	}

	/**
	 * When the webservice endpoint gets the soap call, it will publish the
	 * incoming xml to the {@link #messageQueue}. This method allows test cases
	 * to subscribe to the channel and receive such xml messages asynchronously
	 * from the {@link #messageQueue}. Since the {@link MessageIdentity} is
	 * provided the call back will only be called when the incoming xml messages
	 * identity matches with this identity instance, other messages are ignored.
	 * 
	 * @param messageCallback
	 *            The onMessage method of this instance will be invoked when a
	 *            {@link IMessage} gets published to the queue by the enpoint,
	 *            such that identity.equals(message.getIdentity()) returns true.
	 * @param identity
	 *            The instance containing port id, tn number, message code etc
	 *            and used to filter out messages being published to this
	 *            channel.
	 * 
	 */
	public void subscribe(IMessageCallback messageCallback,
			MessageIdentity identity) {
		Fiber fiber = fibers.threadFiber();
		messageCallback.setFiber(fiber);
		ChannelSubscription<IMessage> subscription = new ChannelSubscription<IMessage>(
				fiber, messageCallback, identity);
		Disposable disposable = messageQueue.subscribe(subscription);
		messageCallback.setDisposable(disposable);
	}

	/**
	 * <b>Not very useful since there is no identity and filtering.</b> Some web
	 * service calls expect a synchronous reply back to the caller (some
	 * client). This method should be used for such scenario, the code in
	 * {@link MessageCallback} will be invoked on the webservice endpoint's
	 * thread if subscribed in this manner, allowing for synchronous, but
	 * customized replies to the caller.
	 * 
	 * @param messageCallback
	 *            The callback containing the code to be executed when a message
	 *            gets published to the channel
	 * 
	 */
	public void subscribeSync(IMessageCallback messageCallback) {
		DisposingExecutor sync = fibers.syncFiber();
		messageCallback.setFiber(sync);
		Disposable disposable = messageQueue.subscribe(sync, messageCallback);
		messageCallback.setDisposable(disposable);
	}
	
	/**
	 * <b>Not very useful since there is no identity and filtering.</b> When the
	 * web service end point gets the soap call, it will publish the incoming
	 * xml to the {@link #messageQueue}. This method allows test cases to
	 * subscribe to the channel and receive such xml messages asynchronously
	 * from the {@link #messageQueue}. Since there is no filtering involved, the
	 * onMessage of the {@link MessageCallback} will be called for all messages
	 * published to the queue.
	 * 
	 * @param messageCallback
	 *            The onMessage method of this instance will be invoked when any
	 *            {@link IMessage} gets published to the queue by the enpoint.
	 * 
	 */
	public void subscribe(IMessageCallback messageCallback) {
		Fiber fiber = fibers.threadFiber();
		messageCallback.setFiber(fiber);
		Disposable disposable = messageQueue.subscribe(
				messageCallback.getFiber(), messageCallback);
		messageCallback.setDisposable(disposable);
	}
	
	public Fibers getFibers() {
		return fibers;
	}

	@Autowired
	public void setFibers(Fibers fibers) {
		this.fibers = fibers;
	}

	public MemoryChannel<IMessage> getMessageQueue() {
		return messageQueue;
	}

}

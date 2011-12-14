package org.menacheri.invmmsg;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.core.DisposingExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract implementation class of the Jetlang {@link Callback} interface.
 * When a test case submits a request and is expecting a reply, this class can
 * be used to execute code on receipt of that reply. Hence it is one piece of a
 * request-reply integration test case infrastructure.
 * 
 * Jetlang has the concept of {@link Disposable}, which is basically used to
 * clean up after the receipt of a iMessage. This class automatically invoke
 * this cleanup method {@link #done()} to relase resources, However if onMessage
 * is never invoked then calling the "done" method manually is mandatory in
 * order to release Jetlang resources.
 * 
 * @author Abraham Menacherry
 * 
 */
public abstract class MessageCallback implements IMessageCallback {

	private static final Logger LOG = LoggerFactory.getLogger(MessageCallback.class);
	
	private final CountDownLatch latch = new CountDownLatch(1);
	private DisposingExecutor fiber;
	private IMessage iMessage;
	
	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessageCallback#onMessage(org.menacheri.invmmsg.IMessage)
	 */
	@Override
	public void onMessage(IMessage message)
	{
		try{
			this.iMessage = message;
			if(null != message.getException()){
				throw message.getException();
			}
			onCallback(message);
			done();
		}catch(Exception e){
			LOG.error("Error occurred during callback execution {}",e);
			if(null == message.getException()){
				message.setException(e);
			}
		}
	}
	
	@Override
	abstract public void onCallback(IMessage iMessage);
	
	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessageCallback#done()
	 */
	@Override
	public void done()
	{
		latch.countDown();
		if(null!=fiber)
		{
			// Disposing the fiber also disposes of all subscriptions.
			Disposable theDisposable = (Disposable)fiber;
			theDisposable.dispose();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessageCallback#await()
	 */
	@Override
	public void await() throws InterruptedException{
		latch.await();
	}
	
	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessageCallback#await(long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean await(long timeout, TimeUnit unit)
			throws InterruptedException {
		return latch.await(timeout, unit);
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessageCallback#getFiber()
	 */
	@Override
	public DisposingExecutor getFiber() {
		return fiber;
	}

	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessageCallback#setFiber(org.jetlang.core.DisposingExecutor)
	 */
	@Override
	public void setFiber(DisposingExecutor executor) {
		this.fiber = executor;
	}
	
	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessageCallback#getMessage()
	 */
	@Override
	public IMessage getMessage() {
		return iMessage;
	}

	/* (non-Javadoc)
	 * @see org.menacheri.invmmsg.IMessageCallback#setMessage(org.menacheri.invmmsg.IMessage)
	 */
	@Override
	public void setMessage(IMessage iMessage) {
		this.iMessage = iMessage;
	}
}

package org.menacheri.invmmsg;

import java.util.concurrent.TimeUnit;

import org.jetlang.core.Callback;
import org.jetlang.core.Disposable;
import org.jetlang.core.DisposingExecutor;

/**
 * When a test case submits a request and is expecting a reply, implementations
 * of this interface can be used to execute code on receipt of that reply. Hence
 * it is one piece of a request-reply integration test case infrastructure.
 * 
 * Jetlang has the concept of {@link Disposable}, which is basically used to
 * clean up after the receipt of a {@link IMessage}. This interface has the
 * method {@link #done()} which should be used for such cleanup.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface IMessageCallback extends Callback<IMessage> {

	abstract public void onCallback(IMessage iMessage);

	/**
	 * This method does all the cleanup activity necessary for Jetlang. It is
	 * invoked by the {@link #onMessage(DefaultMessage)} method automatically
	 * after the {@link #onCallback(DefaultMessage)} method is invoked, so if
	 * due to test case constraints onMessage does not get invoked then it is
	 * <b>responsibility of caller to call this method and release
	 * resources.</b>
	 */
	public abstract void done();

	/**
	 * Await for the callback to happen indefinitely. Note: Use with caution
	 * since it can block the thread/test case indefinitely.
	 * 
	 * @throws InterruptedException
	 */
	public abstract void await() throws InterruptedException;

	/**
	 * Await for the call back onMessage method to be invoked with a timed
	 * parameter. Useful in order to write test cases where a maximum execution
	 * time is expected.
	 * 
	 * @param timeout
	 *            Magnitude for the time out, 10,1000 etc.
	 * @param unit
	 *            unit like milliseconds, seconds etc.
	 * @return Return true if the onMessage and done were invoked within
	 *         expected time frame.
	 * @throws InterruptedException
	 */
	public abstract boolean await(long timeout, TimeUnit unit)
			throws InterruptedException;

	public abstract DisposingExecutor getFiber();

	public abstract void setFiber(DisposingExecutor executor);

	public abstract Disposable getDisposable();

	public abstract void setDisposable(Disposable disposable);

	public abstract IMessage getMessage();

	public abstract void setMessage(IMessage iMessage);

}
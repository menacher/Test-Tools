package org.menacheri.invmmsg;

import java.util.concurrent.ExecutorService;

import org.jetlang.core.DisposingExecutor;
import org.jetlang.core.SynchronousDisposingExecutor;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.PoolFiberFactory;
import org.jetlang.fibers.ThreadFiber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class acts as a factory for creating <a
 * href="http://code.google.com/p/jetlang/">jetlang</a> {@link Fiber}s.
 * 
 * @author Abraham Menacherry
 * 
 */
@Component
public class Fibers
{
	private ExecutorService service;
	private PoolFiberFactory fact;

	/**
	 * Creates and starts a fiber and returns the created instance.
	 * 
	 * @return The created {@link Fiber}.
	 */
	public Fiber pooledFiber()
	{
		Fiber fiber = fact.create();
		fiber.start();
		return fiber;
	}
	
	/**
	 * Creates a thread based fiber for in vm communication. Calls the start
	 * method on it, to activate it and returns the instance.
	 * 
	 * @return The started {@link Fiber} instance.
	 */
	public Fiber threadFiber()
	{
		Fiber fiber = new ThreadFiber();
		fiber.start();
		return fiber;
	}
	
	/**
	 * When using this fiber events will be executed immediately, rather than
	 * queued and executed on another thread. Useful for testing sync replies to
	 * web service calls.
	 * 
	 * @return A fiber which executes on the same thread as the incoming web
	 *         service call.
	 */
	public DisposingExecutor syncFiber() {
		return new SynchronousDisposingExecutor();
	}
	
	public ExecutorService getService() {
		return service;
	}

	@Autowired
	public void setService(ExecutorService service) {
		this.service = service;
	}

	public PoolFiberFactory getFact() {
		return fact;
	}

	@Autowired
	public void setFact(PoolFiberFactory fact) {
		this.fact = fact;
	}
}

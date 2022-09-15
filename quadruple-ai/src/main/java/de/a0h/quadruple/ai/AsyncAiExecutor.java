package de.a0h.quadruple.ai;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import de.a0h.quadruple.game.Game;

public class AsyncAiExecutor implements Runnable, AiExecutionListener {

	/**
	 * Duration of last move search in microseconds. Note it's microseconds (µs,
	 * 10^-6s), not milliseconds (ms, 10^-3s). This is in the middle between the ms
	 * unit used by System.currentTimeMillis() and the System.nanoTime(), which uses
	 * ns (nano seconds, 10^-9).
	 */
	public long lastSearchDuration = 0;

	public AiExecutionListener listener;

	protected String threadName;

	protected boolean isRunning;

	protected ReentrantLock lock = new ReentrantLock();

	protected Condition moveSearchEndCond = lock.newCondition();

	protected Thread runner;

	protected Game game;

	protected Ai ai;

	/**
	 * Ansychonously executed method.
	 */
	protected static enum Method {
		GET_MOVE_VALUES, //
		GET_BEST_MOVE
	}

	protected Method method;

	/**
	 * Values found for the different possible moves, using Float.NaN for impossible
	 * moves. If not initialized, this field is null.
	 */
	protected int[] moveValues = null;

	/**
	 * Best possible move found, -1 if no possible move found.
	 */
	protected int bestMove;

	public AsyncAiExecutor() {
		threadName = getClass().getSimpleName() + "Thread";
	}

	public void startGetBestMove(Game game, Ai ai) {
		prepare(game, ai);
		method = Method.GET_BEST_MOVE;
		runner.start();
	}

	public void startGetMoveValues(Game game, Ai ai) {
		prepare(game, ai);
		method = Method.GET_MOVE_VALUES;
		runner.start();
	}

	public int getBestMove() {
		lock.lock();
		try {
			if (isRunning) {
				throw new IllegalStateException("move search running");
			}

			if (method != Method.GET_BEST_MOVE) {
				throw new IllegalStateException("move search did not get best move; start move search differently.");
			}

			return bestMove;
		} finally {
			lock.unlock();
		}
	}

	public int[] getMoveValues() {
		lock.lock();
		try {
			if (isRunning) {
				throw new IllegalStateException("move search running");
			}

			if (method != Method.GET_MOVE_VALUES) {
				throw new IllegalStateException("move search did not get move values; start move search differently.");
			}

			return moveValues;
		} finally {
			lock.unlock();
		}
	}

	protected void prepare(Game game, Ai ai) {
		lock.lock();
		try {
			if (isRunning) {
				throw new IllegalStateException("move search already running");
			}
			isRunning = true;
		} finally {
			lock.unlock();
		}

		this.game = game;
		this.ai = ai;
		runner = new Thread(this, threadName);
		runner.setName(threadName + "_" + runner.getId());
	}

	@Override
	public void run() {
		lock.lock();
		try {
			if (Thread.currentThread() != runner) {
				throw new IllegalStateException("only the runner thread may invoke this method");
			}
		} finally {
			lock.unlock();
		}

		try {
			long startTime = System.nanoTime();

			if (listener != null) {
				listener.aiMoveSearchStarted(ai);
			}

			if (method == Method.GET_MOVE_VALUES) {
				moveValues = ai.getMoveValues(game, this);
			} else {
				bestMove = ai.getBestMove(game, this);
			}

			long endTime = System.nanoTime();

			lastSearchDuration = endTime - startTime;
			lastSearchDuration = (lastSearchDuration + 500L) / 1000L;

			lock.lock();
			try {
				isRunning = false;
				runner = null;
			} finally {
				lock.unlock();
			}

			if (listener != null) {
				listener.aiMoveSearchFinished(ai);
			}
		} finally {
			signal();
		}
	}

	/**
	 * Signal the end of an execution to all threads awaiting it.
	 */
	protected void signal() {
		lock.lock();
		try {
			// log("signal()", "doing");
			moveSearchEndCond.signalAll();
		} finally {
			lock.unlock();
		}
	}

	protected void awaitUninterruptibly() {
		// log("awaitUninterruptibly()", "start");

		lock.lock();
		try {
			moveSearchEndCond.awaitUninterruptibly();
		} finally {
			lock.unlock();
		}

		// log("awaitUninterruptibly()", "end");
	}

	public void log(String methodName, String msg) {
		String c = getClass().getSimpleName();
		String t = Thread.currentThread().getName();

		System.out.println(t + "\t" + c + '.' + methodName + ": " + msg);
	}

	/**
	 * Cancels the move search and blocks until the move search thread has
	 * exited.</br>
	 * If there is no running move search, this method returns immediately. If you
	 * want to run concurrent move searches, then you need to create one Ai instance
	 * per thread.</br>
	 * Note that this method waits until after the call to
	 * listerner.aiMoveSearchCancelled() returned, so be careful not to acquire
	 * locks inside of the listener method which could already be locked by the
	 * thread which calls the cancel() method.</br>
	 */
	public void cancel() {
		lock.lock();
		try {
			// log("cancel()", "" + isRunning);

			if (Thread.currentThread() == runner) {
				throw new IllegalStateException("don't call this method from the ai thread");
			}

			if (!isRunning) {
				// the move search might have finished meanwhile, so this is not a reason to
				// throw an exception
				return;
			}

			isRunning = false;

			if (runner != null) {
				runner.interrupt();
			}

			awaitUninterruptibly();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void aiMoveSearchStarted(Ai ai) {
		listener.aiMoveSearchStarted(ai);
	}

	@Override
	public void aiMoveSearchProgressed(Ai ai, int i, int j) {
		listener.aiMoveSearchProgressed(ai, i, j);
	}

	@Override
	public void aiMoveSearchCancelled(Ai ai) {
		listener.aiMoveSearchCancelled(ai);
	}

	@Override
	public void aiMoveSearchFinished(Ai ai) {
		listener.aiMoveSearchFinished(ai);
	}
}

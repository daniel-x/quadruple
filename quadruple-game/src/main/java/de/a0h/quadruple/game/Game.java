package de.a0h.quadruple.game;

import java.lang.reflect.Constructor;

/**
 * The variable names i is the abbreviation for row index and j is the
 * abbreviation for column index throughout this class. Subclasses are
 * encouraged to comply with this convention. When a loop iteration variable
 * does not represent a row or column index, then k, l, m, ... shall be used.
 * The first index is 0 for both, i and j.
 */
public abstract class Game {

	/**
	 * In an implementation which adheres to clean OOP standards, Player should be
	 * an enum. However, game AIs are typically computationally intensive, so
	 * primitive data types are used instead of enums to allow high performance
	 * algorithms.
	 */
	public static final int PLAYER_NONE = -1; // Player.NONE.idx;
	public static final int PLAYER_FRST = 0; // Player.FRST.idx;
	public static final int PLAYER_SCND = 1; // Player.SCND.idx;

	/**
	 * Height of the field, in number of stones.
	 */
	protected int mW;

	/**
	 * Width of the field, in number of stones.
	 */
	protected int mH;

	protected Game(int height, int width) {
		mH = height;
		mW = width;
	}

	public static Game create(int height, int width) {
		return new GameImplIntArray1D(height, width);
	}

	public int getH() {
		return mH;
	}

	public int getW() {
		return mW;
	}

	public int getStoneCount(int j) {
		ensureValidColumnIdx(j);

		return getStoneCount_nocheck(j);
	}

	public boolean isEmpty(int j) {
		ensureValidColumnIdx(j);

		return isEmpty_nocheck(j);
	}

	public boolean isFull(int j) {
		ensureValidColumnIdx(j);

		return isFull_nocheck(j);
	}

	public boolean isRunning() {
		boolean noWinner = (determineWinner() == PLAYER_NONE);
		boolean notFull = !isFull();

		return noWinner && notFull;
	}

	/**
	 * Returns the owner of the stone of the field at (i, j), which is one of
	 * PLAYER_NONE, PLAYER_FRST, or PLAYER_SCND. Throws an exception on out of
	 * bounds.
	 */
	public int getStone(int i, int j) {
		ensureValidRowIdx(i);
		ensureValidColumnIdx(j);

		return getStone_nocheck(i, j);
	}

	/**
	 * When there is a winner, PLAYER_FRST or PLAYER_SCND is returned. Otherwise
	 * PLAYER_NONE is returned, i.e. when the game is ongoing or when it's a draw.
	 */
	public int determineWinner() {
		FastWinnerDetector wd = new FastWinnerDetector(this);

		FieldIterator.INSTANCE.performFullIteration(wd, mH, mW);

		return wd.getResult();
	}

	/**
	 * When there is a winner who won with the location (i, j), PLAYER_FRST or
	 * PLAYER_SCND is returned. Otherwise PLAYER_NONE is returned, i.e. when the
	 * game is ongoing or when it's a draw.
	 */
	public int determineWinnerInNeighborhood(int i, int j) {
		FastWinnerDetector wd = new FastWinnerDetector(this);

		FieldIterator.INSTANCE.performNeighborhoodIteration(wd, mH, mW, i, j);

		return wd.getResult();
	}

	/**
	 * Places a stone of the current player onto the column with the specified index
	 * and switches the current player. Returned is the row index where the stone
	 * ended up.
	 */
	public int move(int j) {
		ensureRunning();
		ensureColumnNotFull(j);

		int i = move_nocheck(j);

		int winner = determineWinner();
		if (winner != PLAYER_NONE || isFull()) {
			gameFinished(winner);
		}

		return i;
	}

	/**
	 * Removes the highest stone in the specified column and switches the current
	 * player.
	 */
	public void unmove(int j) {
		ensureValidColumnIdx(j);
		ensureColumnNotEmpty(j);

		unmove_nocheck(j);
	}

	public void toggleCurrentPlayer() {
		ensureRunning();

		toggleCurrentPlayer_nocheck();
	}

	protected void ensureValidRowIdx(int i) {
		if (i < 0 || i >= mH) {
			throw new IllegalArgumentException( //
					"invalid row index for height of " + mH + ": " + i);
		}
	}

	protected void ensureValidColumnIdx(int j) {
		if (j < 0 || j >= mW) {
			throw new IllegalArgumentException( //
					"invalid column index for width of " + mW + ": " + j);
		}
	}

	protected void ensureColumnNotFull(int j) {
		if (isFull(j)) {
			throw new IllegalStateException("column " + j + " is full");
		}
	}

	protected void ensureColumnNotEmpty(int j) {
		if (isEmpty(j)) {
			throw new IllegalStateException("column " + j + " is empty");
		}
	}

	protected void ensureRunning() {
		if (!isRunning()) {
			throw new IllegalStateException("game is not running");
		}
	}

	@Override
	public String toString() {
		return Format.INSTANCE.toString(this);
	}

	/**
	 * Returns an instance of the same class as this class which represents the same
	 * game state.
	 */
	public Game copy() {
		Constructor<? extends Game> constr;
		try {
			constr = getClass().getConstructor(Integer.TYPE, Integer.TYPE);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("this should not happen. bug.");
		}

		Game copy;
		try {
			copy = constr.newInstance(mH, mW);
		} catch (Exception e) {
			throw new RuntimeException("this should not happen. bug.");
		}

		this.assignTo(copy);

		return copy;
	}

	public void assignTo(Game dst) {
		dst.setSize(mH, mW);
		dst.restart();

		for (int j = 0; j < mW; j++) {
			for (int i_ = 0; i_ < getStoneCount_nocheck(j); i_++) {
				int i = mH - 1 - i_;
				int value = getStone_nocheck(i, j);

				dst.setCurrentPlayer(value);

				dst.move_nocheck(j);
			}
		}

		dst.setCurrentPlayer(getCurrentPlayer());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Game)) {
			return false;
		}

		Game other = (Game) obj;
		if (other.getH() != getH()) {
			return false;
		}
		if (other.getW() != getW()) {
			return false;
		}

		if (other.getCurrentPlayer() != getCurrentPlayer()) {
			return false;
		}

		for (int i = 0; i < getH(); i++) {
			for (int j = 0; j < getW(); j++) {
				if (other.getStone_nocheck(i, j) != getStone_nocheck(i, j)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Called from move(int) when a player won or when there is a draw.
	 */
	protected final void gameFinished(int winner) {
		setCurrentPlayer(PLAYER_NONE);
	}

	public abstract void restart();

	public abstract int getCurrentPlayer();

	public abstract void setCurrentPlayer(int currPlayer);

	public abstract boolean isFull();

	public abstract boolean isFull_nocheck(int j);

	public abstract boolean isEmpty_nocheck(int j);

	public abstract int getStone_nocheck(int i, int j);

	public abstract int getStoneCount_nocheck(int j);

	public abstract int move_nocheck(int j);

	public abstract void unmove_nocheck(int j);

	public abstract void toggleCurrentPlayer_nocheck();

	protected abstract void setSize(int height, int width);

	/**
	 * Returns the row index i of the first non-occupied stone location in column j.
	 */
	public final int getNextVacancy_nocheck(int j) {
		int i = mH - 1 - getStoneCount_nocheck(j);
		return i;
	}

	public static int togglePlayer(int player) {
		switch (player) {
		case PLAYER_FRST:
			return PLAYER_SCND;
		case PLAYER_SCND:
			return PLAYER_FRST;
		default:
			throw new IllegalArgumentException("can't toggle player: " + player);
		}
	}
}

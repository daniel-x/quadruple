package de.a0h.quadruple.ai;

import de.a0h.quadruple.game.Game;

/**
 * A stateless, thus threadsafe ai.
 */
public abstract class Ai {

	/**
	 * Possible value for move indicating that there is no move.
	 */
	public static final int MOVE_NONE = -1;

	/**
	 * Used like Float.NaN. Can't use -1 here, so we use an int value which is
	 * rather unlikely to occur coincidentally.
	 */
	public static final int VALUE_INVALID = Integer.MIN_VALUE + 2;

	public static final int VALUE_VICTORY = 0x7fff << 16;

	public static final int VALUE_DEFEAT = -VALUE_VICTORY;

	/**
	 * Returns an array of move values, each element representing the value for the
	 * current player for placing the next stone into the respective column.
	 * Impossible moves get the value Float.NaN.
	 * 
	 * @param listener may be null
	 */
	public abstract int[] getMoveValues(Game game, AiExecutionListener listener);

	/**
	 * Returns the best move, i.e. what the ai believes to be the best column for
	 * the current player for placing the next stone in order to win. If there is no
	 * possible move, then MOVE_NONE is returned or an exception is thrown.
	 * 
	 * @param listener may be null
	 */
	public abstract int getBestMove(Game game, AiExecutionListener listener);

	/**
	 * Returns the index of the element with the maximum value, or MOVE_NONE if they
	 * are all NaN.
	 */
	public static int getBestMove(int[] moveValues) {
		int j = idxMax(moveValues);

		return j == -1 ? MOVE_NONE : j;
	}

	protected static int idxMax(int[] a) {
		float max = 0;
		int idx = -1;

		int j;
		for (j = 0; j < a.length; j++) {
			if (a[j] != VALUE_INVALID) {
				max = a[j];
				idx = j;
				break;
			}
		}
		j++;

		for (; j < a.length; j++) {
			if (a[j] > max) {
				max = a[j];
				idx = j;
			}
		}

		return idx;
	}

	protected static int idxMax(float[] a) {
		float max = 0;
		int idx = -1;

		int j;
		for (j = 0; j < a.length; j++) {
			if (!Float.isNaN(a[j])) {
				max = a[j];
				idx = j;
				break;
			}
		}
		j++;

		for (; j < a.length; j++) {
			if (a[j] > max) {
				max = a[j];
				idx = j;
			}
		}

		return idx;
	}
}

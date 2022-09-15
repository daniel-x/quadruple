package de.a0h.quadruple.ai.minimax;

import de.a0h.quadruple.ai.Ai;
import de.a0h.quadruple.ai.AiExecutionListener;
import de.a0h.quadruple.game.FastWinnerDetector;
import de.a0h.quadruple.game.FieldIterator;
import de.a0h.quadruple.game.Game;

public class MinimaxAi extends Ai {

	protected int lookAhead;

	public MinimaxAi() {
		this(3);
	}

	public MinimaxAi(int lookAhead) {
		this.lookAhead = lookAhead;
	}

	public int getLookAhead() {
		return lookAhead;
	}

	public void setLookAhead(int lookAhead) {
		this.lookAhead = lookAhead;
	}

	@Override
	public int[] getMoveValues(Game game, AiExecutionListener listener) {
		int depth = 0;
		int[] result = new int[game.getW()];

		FastWinnerDetector winDet = new FastWinnerDetector(game);
		PotentialEvaluater potEv = new PotentialEvaluater(game);

		for (int j = 0; j < game.getW(); j++) {
			if (game.isFull(j)) {
				result[j] = VALUE_INVALID;

			} else {
				int i = game.move_nocheck(j);

				result[j] = -getValue(game, winDet, potEv, i, j, depth);

				game.unmove_nocheck(j);
			}
		}

		return result;
	}

	@Override
	public int getBestMove(Game game, AiExecutionListener listener) {
		int depth = 0;
		int resultMove = MOVE_NONE;
		int resultValue = VALUE_INVALID;

		FastWinnerDetector winDet = new FastWinnerDetector(game);
		PotentialEvaluater potEv = new PotentialEvaluater(game);

		for (int j = 0; j < game.getW(); j++) {
			if (!game.isFull(j)) {
				int i = game.move_nocheck(j);

				int value = -getValue(game, winDet, potEv, i, j, depth);

				game.unmove_nocheck(j);

				if (value > resultValue || resultMove == MOVE_NONE) {
					resultValue = value;
					resultMove = j;
				}
			}
		}

		return resultMove;
	}

	/**
	 * Returns the ai's idea of the value of the specified game's state for the
	 * current player. The last stone was placed at (i, j). That's the stone of the
	 * opposite player. So if this stone won, VALUE_DEFEAT is returned because the
	 * opposite player won. This is somewhat similar to a winning probability mapped
	 * to [VALUE_DEFEAT,VALUE_VICTORY]. The borders of this interval represent
	 * certainty of victory or defeat.</br>
	 * This method uses the minimax algorithm: It tries out moves and opponent moves
	 * recursively until the specified depth is reached and then uses heuristics to
	 * determine the value of the situation.
	 */
	protected int getValue(Game game, FastWinnerDetector winDet, PotentialEvaluater potEv, int i, int j, int depth) {
		int result;

		FieldIterator.INSTANCE.performNeighborhoodIteration(winDet, game.getH(), game.getW(), i, j);
		int winner = winDet.getResult();

		if (winner != Game.PLAYER_NONE) {
			result = VALUE_DEFEAT;
			result += depth;

		} else if (depth == lookAhead) {
			result = getValueHeuristically(game, potEv);
			result += ((result < 0) ? +depth : -depth);

		} else {
			result = getValueRecursively(game, winDet, potEv, depth);
		}

		return result;
	}

	/**
	 * Submethod of getValue() which takes care of recursive calls.
	 */
	protected int getValueRecursively(Game game, FastWinnerDetector winDet, PotentialEvaluater potEv, int depth) {
		depth++;

		boolean notInitialized = true;
		int maxValue = 0;

		for (int j = 0; j < game.getW(); j++) {
			if (!game.isFull(j)) {
				int i = game.move_nocheck(j);

				int value = -getValue(game, winDet, potEv, i, j, depth);
				if (value > maxValue || notInitialized) {
					maxValue = value;
					notInitialized = false;
				}

				game.unmove_nocheck(j);
			}
		}

		return maxValue;
	}

	/**
	 * Submethod of getValue() which takes care of recursion end = heuristic
	 * evaluation of game state.
	 */
	protected int getValueHeuristically(Game game, PotentialEvaluater potEv) {
		FieldIterator.INSTANCE.performFullIteration(potEv, game.getH(), game.getW());

		int currPlayer = game.getCurrentPlayer();
		int result = potEv.getPotentialValue(currPlayer);

		return result;
	}
}

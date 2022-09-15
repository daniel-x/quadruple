package de.a0h.quadruple.ai.minimax;

import de.a0h.quadruple.ai.AiExecutionListener;
import de.a0h.quadruple.game.FastWinnerDetector;
import de.a0h.quadruple.game.FieldIterator;
import de.a0h.quadruple.game.Game;

public class AbPruningAi extends MinimaxAi {

	public AbPruningAi() {
		super();
	}

	public AbPruningAi(int lookAhead) {
		super(lookAhead);
	}

	@Override
	public int getBestMove(Game game, AiExecutionListener listener) {
		int depth = 0;
		int bestMove = MOVE_NONE;
		int maxValue = VALUE_INVALID;
		int beta = VALUE_VICTORY;

		FastWinnerDetector winDet = new FastWinnerDetector(game);
		PotentialEvaluater potEv = new PotentialEvaluater(game);

		for (int j = 0; j < game.getW(); j++) {
			if (!game.isFull(j)) {
				int i = game.move_nocheck(j);

				int value = -getValueAbPruning(game, winDet, potEv, i, j, depth, -beta, -maxValue);

				game.unmove_nocheck(j);

				if (value > maxValue || bestMove == MOVE_NONE) {
					maxValue = value;
					bestMove = j;
				}
			}
		}

		return bestMove;
	}

	@Override
	protected int getValue(Game game, FastWinnerDetector winDet, PotentialEvaluater potEv, int i, int j, int depth) {
		int alpha = VALUE_DEFEAT;
		int beta = VALUE_VICTORY;

		int result = getValueAbPruning(game, winDet, potEv, i, j, depth, alpha, beta);

		return result;
	}

	protected int getValueAbPruning(Game game, FastWinnerDetector winDet, PotentialEvaluater potEv, int i, int j,
			int depth, int alpha, int beta) {

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
			result = getValueRecursivelyAbPruning(game, winDet, potEv, depth, alpha, beta);
		}

		return result;
	}

	/**
	 * Submethod of getValueAbPruning() which takes care of recursive calls.
	 */
	protected int getValueRecursivelyAbPruning(Game game, FastWinnerDetector winDet, PotentialEvaluater potEv,
			int depth, int alpha, int beta) {
		depth++;

		boolean notInitialized = true;
		int maxValue = 0;

		for (int j = 0; j < game.getW(); j++) {
			if (!game.isFull(j)) {
				int i = game.move_nocheck(j);

				int value = -getValueAbPruning(game, winDet, potEv, i, j, depth, -beta, -alpha);

				game.unmove_nocheck(j);

				if (value >= beta) {
					maxValue = value;
					break;
				}

				if (value > maxValue || notInitialized) {
					maxValue = value;
					alpha = Math.max(alpha, maxValue);
					notInitialized = false;
				}
			}
		}

		return maxValue;
	}
}

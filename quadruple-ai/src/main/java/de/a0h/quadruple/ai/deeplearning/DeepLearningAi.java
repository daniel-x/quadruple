package de.a0h.quadruple.ai.deeplearning;

import java.util.Random;

import de.a0h.minideeplearn.operation.optimizer.GradientDescentOptimizer;
import de.a0h.minideeplearn.operation.optimizer.Optimizer;
import de.a0h.minideeplearn.predefined.Classifier;
import de.a0h.mininum.MnLinalg;
import de.a0h.quadruple.ai.Ai;
import de.a0h.quadruple.ai.AiExecutionListener;
import de.a0h.quadruple.game.Game;
import de.a0h.quadruple.game.GameLog;

public class DeepLearningAi extends Ai {

	private static final float ACTIVATION_PLAYER_NONE = 0.0f;

	private static final float ACTIVATION_PLAYER_CURR = 1.0f;

	private static final float ACTIVATION_PLAYER_OTHER = -1.0f;

	public Classifier net;

	public float[] inp;

	Optimizer optimizer = new GradientDescentOptimizer();

	// Operation compiledNet;

	public DeepLearningAi(int w, int h, int layerCount) {
		int inputSize = w * h;
		int outputSize = w;

		int[] shape = Classifier.createShape(inputSize, outputSize, layerCount);

		net = new Classifier(shape);
		inp = new float[net.getInputSize()];
	}

	public void init(Random rnd) {
		net.initParams(rnd);
	}

	public float[] getMoveProbabilities(Game game, AiExecutionListener listener) {
		if (listener != null) {
			listener.aiMoveSearchStarted(this);
		}

		transformToDlInput(game, inp);

		float[] out = net.calcOutput(inp);

		if (listener != null) {
			listener.aiMoveSearchFinished(this);
		}

		return out;
	}

	@Override
	public int[] getMoveValues(Game game, AiExecutionListener listener) {
		float[] prob = getMoveProbabilities(game, listener);

		int[] result = new int[game.getW()];

		transformToMoveValueOutput(game, prob, result);

		for (int j = 0; j < game.getW(); j++) {
			if (game.isFull(j)) {
				result[j] = Ai.VALUE_INVALID;
			}
		}

		return result;
	}

	protected static void transformToMoveValueOutput(Game game, float[] prob, int[] result) {
		float min = Float.MAX_VALUE;
		float max = Float.MIN_VALUE;
		float avg = 0.0f;

		for (int j = 0; j < prob.length; j++) {
			min = Math.min(min, prob[j]);
			max = Math.max(max, prob[j]);
			avg += prob[j];
		}
		avg /= prob.length;
		min = Math.abs(min - avg);
		max = Math.abs(max - avg);

		final int VALUE_INTERVAL_DIAM = 9999;
		for (int j = 0; j < game.getW(); j++) {
			float v = prob[j] - avg;

			v /= (v < 0) ? min : max;

			result[j] = (int) (v * VALUE_INTERVAL_DIAM);
		}
	}

	protected static void transformToDlInput(Game game, float[] inp) {
		int inpPtr = 0;
		int currPlayer = game.getCurrentPlayer();

		for (int i = game.getH() - 1; i >= 0; i--) {
			for (int j = 0; j < game.getW(); j++) {
				int stoneOwner = game.getStone(i, j);
				float inpVal;
				if (stoneOwner == Game.PLAYER_NONE) {
					inpVal = ACTIVATION_PLAYER_NONE;
				} else if (stoneOwner == currPlayer) {
					inpVal = ACTIVATION_PLAYER_CURR;
				} else {
					inpVal = ACTIVATION_PLAYER_OTHER;
				}
				inp[inpPtr] = inpVal;
				inpPtr++;
			}
		}
	}

	@Override
	public int getBestMove(Game game, AiExecutionListener listener) {
		int[] moveValues = getMoveValues(game, listener);

		for (int i = 0; i < moveValues.length; i++) {
			int move = idxMax(moveValues);

			if (game.isFull(move)) {
				moveValues[move] = Integer.MIN_VALUE;
				continue;
			}

			return move;
		}

		return Ai.MOVE_NONE;
	}

	/**
	 * Learn from the specified game log. The logged game needs to be decided (there
	 * must be a winner). Otherwise it's hard (thus not implemented) to tell how to
	 * interpret which player did the better moves.
	 * 
	 * @param game    game implementation which contains the end-state of the game
	 * @param gameLog move history of the game
	 */
	public void learnFrom(Game game, GameLog gameLog) {
		if (gameLog.winner == Game.PLAYER_NONE) {
			throw new UnsupportedOperationException("learning from undecided games is not supported");
		}

		float[][] moveOneHotList = new float[1][game.getW()];
		float[] moveOneHot = moveOneHotList[0];

		float[][] inpList = new float[][] { inp };

		float learningRateLastMove = 0.2f;
		float learningRateFirstMove = 0.01f;
		float learningRateDecayFactor = (float) Math.pow(((double) learningRateFirstMove) / learningRateLastMove,
				1.0D / (gameLog.moveCount - 1));

		float learningRate = learningRateLastMove;
		game.setCurrentPlayer(gameLog.winner);
		for (int numberOfMove = gameLog.moveCount - 1; numberOfMove >= 0; numberOfMove--) {
			int move = gameLog.moveList[numberOfMove];

			transformToDlInput(game, inp);

			boolean isWinnerMove = (game.getCurrentPlayer() == gameLog.winner);
			if (isWinnerMove) {
				moveOneHot[move] = 1.0f;
			} else {
				MnLinalg.assign(moveOneHot, 1.0f / (moveOneHot.length - 1));
				moveOneHot[move] = 0.0f;
			}

			if (isWinnerMove) {
				optimizer.run(net, inpList, moveOneHotList, -1, learningRate, null);
			}

			learningRate *= learningRateDecayFactor;

			MnLinalg.assign(moveOneHot, 0.0f);

			game.unmove(move);
		}
	}
}

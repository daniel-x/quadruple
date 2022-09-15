package de.a0h.quadruple.ai;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import de.a0h.minideeplearn.operation.Operation;
import de.a0h.mininum.MnFuncs;
import de.a0h.quadruple.ai.deeplearning.DeepLearningAi;
import de.a0h.quadruple.ai.minimax.AbPruningAi;
import de.a0h.quadruple.game.Format;
import de.a0h.quadruple.game.Game;
import de.a0h.quadruple.game.GameLog;

public class DeepLearningAiTest {

	@Test
	public void testGetMoveValues() {
		String problem = "" + //
				"current_player: x\n" + //
				"0 _______\n" + //
				"1 __x____\n" + //
				"2 __o_o__\n" + //
				"3 x_ooxx_\n" + //
				"4 oxooxo_\n" + //
				"5 oxxxox_\n" + //
				"  0123456\n" //
		;

		Game game = Format.INSTANCE.parse(problem);
		int layerCount = 4;
		DeepLearningAi dlAi = new DeepLearningAi(game.getW(), game.getH(), layerCount);
		Random rnd = new Random(0);
		dlAi.init(rnd);

		int[] actualMoveValues = dlAi.getMoveValues(game, null);

		int[] expectedMoveValues = { 1300, -8490, -9999, 4699, 670, 9999, -4909 };

		if (!Arrays.equals(expectedMoveValues, actualMoveValues)) {
			Assert.fail(String.format("" + //
					"expectedMoveValues (e) and actualMoveValues (a) differ:\n" + //
					"e:%s\n" + //
					"a:%s", //
					Arrays.toString(expectedMoveValues), Arrays.toString(actualMoveValues)));
		}
	}

	@Test
	public void testPlayGame() {
		String problem = "" + //
				"current_player: x\n" + //
				"0 _______\n" + //
				"1 __x____\n" + //
				"2 __o_o__\n" + //
				"3 x_ooxx_\n" + //
				"4 oxooxo_\n" + //
				"5 oxxxox_\n" + //
				"  0123456\n" //
		;

		Game game = Format.INSTANCE.parse(problem);
		int layerCount = 4;
		DeepLearningAi dlAi = new DeepLearningAi(game.getW(), game.getH(), layerCount);
		Random rnd = new Random(0);
		dlAi.init(rnd);

		GameLog actualGameLog = new GameLog(game.getH(), game.getW());
		actualGameLog.clear();
		while (game.isRunning()) {
			float[] prob = dlAi.getMoveProbabilities(game, null);

			int move;
			do {
				move = MnFuncs.draw(prob, rnd);
			} while (game.isFull(move));

			actualGameLog.log(move);
			game.move(move);
		}
		actualGameLog.winner = game.determineWinner();

		GameLog expectedGameLog = GameLog.parse("6*7;o;6: 2 1 4 5 3 5");

		if (!expectedGameLog.equals(actualGameLog)) {
			Assert.fail("" + //
					"expectedGameLog (e) and actualGameLog (a) differ:\n" + //
					"e: " + expectedGameLog + "\n" + //
					"a: " + actualGameLog + "\n" //
			);
		}
	}

	@Test
	public void testTrain() {
		Game game = Game.create(6, 7);
		int layerCount = 4;
		DeepLearningAi dlAi = new DeepLearningAi(game.getW(), game.getH(), layerCount);
		Random rnd = new Random(0);
		dlAi.init(rnd);
		GameLog gameLog = new GameLog(game.getH(), game.getW());

		learnFromOneGameAgainstItself(game, dlAi, rnd, gameLog);
	}

	protected void learnFromOneGameAgainstItself(Game game, DeepLearningAi dlAi, Random rnd, GameLog gameLog) {
		playAndLogDecidedGameAgainstItself(game, dlAi, gameLog, rnd);

		dlAi.learnFrom(game, gameLog);
	}

	private void playAndLogDecidedGameAgainstItself(Game game, DeepLearningAi dlAi, GameLog gameLog, Random rnd) {
		int i = 0;
		do {
			if (i >= 1000) {
				throw new IllegalStateException(
						"too many tries to find a random, decided game; probably someting is wrong: " + i);
			}
			i++;

			playAndLogGameAgainstItself(game, dlAi, gameLog, rnd);
		} while (gameLog.winner == Game.PLAYER_NONE);
	}

	private void playAndLogGameAgainstItself(Game game, DeepLearningAi dlAi, GameLog gameLog, Random rnd) {
		game.restart();
		gameLog.clear();

		do {
			float[] prob = dlAi.getMoveProbabilities(game, null);

			int move;
			do {
				move = MnFuncs.draw(prob, rnd);
			} while (game.isFull(move));

			if (game.isFull(move)) {
				continue;
			}

			gameLog.log(move);
			game.move(move);
		} while (game.isRunning());

		gameLog.winner = game.determineWinner();
	}

	@Test
	public void testDlAiAgainstMinimaxAi() {
		Game game = Game.create(6, 7);
		int layerCount = 12;
		DeepLearningAi dlAi = new DeepLearningAi(game.getW(), game.getH(), layerCount);
		Random rnd = new Random(0);
		dlAi.init(rnd);
		GameLog gameLog = new GameLog(game.getH(), game.getW());

		String homeDir = System.getProperty("user.home");

		for (int i = 0; i < 100000; i++) {
			// rnd.setSeed(1);

			learnFromOneGameAgainstItself(game, dlAi, rnd, gameLog);
//
//			if (i == 0) {
//				saveToFile(homeDir + "/ai_net_a.txt", dlAi.net);
//			}

			if (i % 1000 == 1) {
				System.out.print("quality: ");
				System.out.println(getDlAiQuality(dlAi, game, rnd));
//				saveToFile(homeDir + "/ai_net_b.txt", dlAi.net);
//				System.out.println("saved");
			}

//			if (!gameLog.equals(prevGameLog)) {
//				System.out.println("######################## different: " + i);
////				System.out.println(gameLog);
//				// System.out.println(game);
//
//				GameLog tmp = gameLog;
//				gameLog = prevGameLog;
//				prevGameLog = tmp;
//			} else {
////				System.out.println("######################## equal");
//			}
		}

		saveToFile(homeDir + "/ai_net_b.txt", dlAi.net);

		playAndLogDecidedGameAgainstItself(game, dlAi, gameLog, rnd);
		System.out.println(gameLog);
		System.out.println(game);

		if (Math.random() < 1) {
			return;
		}

		AbPruningAi abAi = new AbPruningAi(2);
		game.restart();
		int dlAiPlayer = Game.PLAYER_FRST;
		while (game.isRunning()) {
			boolean isDlMove = (game.getCurrentPlayer() == dlAiPlayer);
			Ai ai = isDlMove ? dlAi : abAi;

			int move = ai.getBestMove(game, null);
//			int[] moveValues = ai.getMoveValues(game, null);
//			int move = Ai.getBestMove(moveValues);
//
//			System.out.println("" + //
//					ai.getClass().getSimpleName() + " moveValues=" + Arrays.toString(moveValues) + //
//					" move=" + move);

			game.move(move);

			System.out.println(game);
		}

		int winner = game.determineWinner();
		if (winner != Game.PLAYER_NONE) {
			System.out.println("winner: " + Format.getSymbol(winner) + " " + //
					((winner == dlAiPlayer) ? "(DL ai)" : "(classic ai)"));
		} else {
			System.out.println("no winner");
		}
	}

	private int getDlAiQuality(DeepLearningAi dlAi, Game game, Random rnd) {
		AbPruningAi opponent = new AbPruningAi();

		int winCount = 0;
		int loseCount = 0;
		for (int lookAhead = 0; lookAhead < 3; lookAhead++) {
			opponent.setLookAhead(lookAhead);

			for (int i = 0; i < 20; i++) {
				boolean dlAiStarted = playRandomGame(dlAi, opponent, game, rnd);

				int winner = game.determineWinner();
				if (winner != Game.PLAYER_NONE) {
					if (false || //
							(dlAiStarted && winner == Game.PLAYER_FRST) || //
							(!dlAiStarted && winner == Game.PLAYER_SCND)) {
						winCount++;
					} else {
						loseCount++;
					}
				}
			}
		}

		return winCount - loseCount;
	}

	private boolean playRandomGame(DeepLearningAi dlAi, Ai opponent, Game game, Random rnd) {
		boolean dlAiIsFirstPlayer = rnd.nextFloat() < 0.5f;

		game.restart();

		do {
			int move;

			if (game.getCurrentPlayer() == Game.PLAYER_FRST && dlAiIsFirstPlayer) {
				float[] prob = dlAi.getMoveProbabilities(game, null);
				do {
					move = MnFuncs.draw(prob, rnd);
				} while (game.isFull(move));
			} else {
				move = opponent.getBestMove(game, null);
			}

			game.move(move);
		} while (game.isRunning());

		return dlAiIsFirstPlayer;
	}

	private void saveToFile(String dstFilename, Operation op) {
		File dstFile = new File(dstFilename);

		try {
			FileWriter fw = new FileWriter(dstFile, false);
			fw.write(op.toStringWithLayoutAndValues());
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException("couldn't write to file '" + dstFile.getAbsolutePath() + "'", e);
		}
	}
}
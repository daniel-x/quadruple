package de.a0h.quadruple.ai;

import java.util.ArrayList;

import org.junit.Test;

import de.a0h.quadruple.ai.minimax.AbPruningAi;
import de.a0h.quadruple.ai.minimax.MinimaxAi;
import de.a0h.quadruple.game.Game;
import de.a0h.quadruple.game.GameImpl1Long6x7;
import de.a0h.quadruple.game.GameImplEqualityTest;
import de.a0h.quadruple.game.GameImplIntArray1D;
import de.a0h.quadruple.game.GameImplLongMicroStacks;

public class GameImplWithAiEqualityTest {

	@Test
	public void testAiBehaveEquallyOnGameImpls() {
		ArrayList<Game> implList = new ArrayList<>();

		int h = 6;
		int w = 7;

		implList.add(new GameImpl1Long6x7(h, w));
		implList.add(new GameImplIntArray1D(h, w));
		implList.add(new GameImplLongMicroStacks(h, w));

		Ai aiFrst = new MinimaxAi(2);
		Ai aiScnd = new AbPruningAi(3);

		int repeatCount = 2;
		for (int gameIdx = 0; gameIdx < repeatCount; gameIdx++) {
			try {
				exerciseAiVsAiGame(implList, gameIdx, aiFrst, aiScnd);
			} catch (Exception e) {
				throw new RuntimeException("couldn't exercise game #" + gameIdx, e);
			} catch (AssertionError e) {
				throw new AssertionError("couldn't exercise game #" + gameIdx, e);
			}

			for (Game impl : implList) {
				impl.restart();
			}
		}
	}

	private void exerciseAiVsAiGame(ArrayList<Game> implList, int gameIdx, Ai aiFrst, Ai aiScnd) {
		try {
			GameImplEqualityTest.ensureConsistentStates(implList);
		} catch (Exception e) {
			throw new RuntimeException("problem detected before making a move", e);
		} catch (AssertionError e) {
			throw new AssertionError("problem detected before making a move", e);
		}

		int k = 0;
		while (implList.get(0).isRunning()) {
			try {
				makeOneAiMove(implList, aiFrst, aiScnd);
			} catch (Exception e) {
				throw new RuntimeException("couldn't exercise move #" + k, e);
			} catch (AssertionError e) {
				throw new AssertionError("couldn't exercise move #" + k, e);
			}

			try {
				GameImplEqualityTest.ensureConsistentStates(implList);
			} catch (Exception e) {
				throw new RuntimeException("problem detected after move #" + k, e);
			} catch (AssertionError e) {
				throw new AssertionError("problem detected after move #" + k, e);
			}

			k++;
		}
	}

	protected void makeOneAiMove(ArrayList<Game> gameImplList, Ai aiFrst, Ai aiScnd) {
		int currPlayer;
		Ai ai;

		ArrayList<Integer> moveList = new ArrayList<>();
		for (Game gameImpl : gameImplList) {
			try {
				currPlayer = gameImpl.getCurrentPlayer();

				ai = (currPlayer == Game.PLAYER_FRST) ? aiFrst : aiScnd;

				int j = ai.getBestMove(gameImpl, null);

				moveList.add(j);
			} catch (Exception e) {
				String implName = gameImpl.getClass().getSimpleName();
				throw new RuntimeException("finding a move with " + implName + ": " + gameImpl, e);
			}
		}

		if (moveList.get(0) == -1) {
			System.out.println(-1);
		}

		GameImplEqualityTest.ensureStateConsistent(gameImplList, moveList, "next ai move");

		GameImplEqualityTest.makeOneMove(gameImplList, moveList.get(0));
	}
}
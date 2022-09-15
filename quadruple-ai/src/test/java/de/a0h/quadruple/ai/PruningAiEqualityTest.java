package de.a0h.quadruple.ai;

import java.util.ArrayList;

import org.junit.Test;

import de.a0h.quadruple.ai.minimax.AbPruningAi;
import de.a0h.quadruple.ai.minimax.MinimaxAi;
import de.a0h.quadruple.game.Game;
import de.a0h.quadruple.game.GameImplEqualityTest;
import de.a0h.quadruple.game.GameImplIntArray1D;

public class PruningAiEqualityTest {

	@Test
	public void testGetBestMove() {
		for (int lookAhead = 0; lookAhead <= 3; lookAhead++) {
			exerciseGame(lookAhead, false);
		}
	}

	@Test
	public void testGetMoveValues() {
		for (int lookAhead = 0; lookAhead <= 3; lookAhead++) {
			exerciseGame(lookAhead, true);
		}
	}

	protected void exerciseGame(int lookAhead, boolean useMoveValues) throws AssertionError {
		int h = 6;
		int w = 7;

		Game game = new GameImplIntArray1D(h, w);
		game.restart();

		ArrayList<Ai> aiList = new ArrayList<>();
		aiList.add(new MinimaxAi(lookAhead));
		aiList.add(new AbPruningAi(lookAhead));

		ArrayList<Integer> moveList = new ArrayList<>(aiList.size());
		ArrayList<int[]> moveValuesList = new ArrayList<>(aiList.size());

		int k = 0;
		while (game.isRunning()) {
			moveList.clear();

			try {
				int j = 0;
				if (useMoveValues) {
					for (Ai ai : aiList) {
						int[] moveValues = ai.getMoveValues(game, null);
						j = Ai.getBestMove(moveValues);
						moveValuesList.add(moveValues);
					}
					GameImplEqualityTest.ensureStateConsistent(aiList, moveValuesList, "moveValues");

				} else {
					for (Ai ai : aiList) {
						j = ai.getBestMove(game, null);
						moveList.add(j);
					}
					GameImplEqualityTest.ensureStateConsistent(aiList, moveList, "move");
				}

				game.move(j);
			} catch (Exception e) {				
				throw new RuntimeException("" + //
						"couldn't exercise move #" + k + //
						" (lookahead:" + lookAhead + //
						" useMoveValues:" + useMoveValues + ")", e);
			} catch (AssertionError e) {
				System.out.println(game);

				throw new AssertionError("" + //
						"couldn't exercise move #" + k + //
						" (lookahead:" + lookAhead + //
						" useMoveValues:" + useMoveValues + ")", e);
			}

			k++;
		}
	}
}

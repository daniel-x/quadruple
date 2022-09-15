package de.a0h.quadruple.ai;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import de.a0h.quadruple.ai.minimax.AbPruningAi;
import de.a0h.quadruple.ai.minimax.MinimaxAi;
import de.a0h.quadruple.game.Format;
import de.a0h.quadruple.game.Game;
import de.a0h.quadruple.game.GameImplIntArray1D;

public class AiTest {

	@Test
	public void testAvoidDefeatIn4Moves() {
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
		AbPruningAi ai = new AbPruningAi(6);

		int[] moveValues = ai.getMoveValues(game, null);
		for (int i = 0; i < moveValues.length; i++) {
			moveValues[i] += 2147418109;
		}
		int j = ai.getBestMove(game, null);

		System.out.println(j);
		System.out.println(Arrays.toString(moveValues));
	}

	@Test
	public void aiAgainstOtherAiCompleteGame() {
		Game game = new GameImplIntArray1D(6, 7);

		Ai mmAi = new MinimaxAi(3);
		Ai abAi = new AbPruningAi(4);

		int k = 0;

		while (game.isRunning()) {
			try {
				boolean isFirstPlayer = game.getCurrentPlayer() == Game.PLAYER_FRST;
				Ai ai = isFirstPlayer ? abAi : mmAi;

				int j = ai.getBestMove(game, null);

				game.move(j);
			} catch (Exception e) {
				throw new RuntimeException("couldn't exercise move #" + k, e);
			} catch (AssertionError e) {
				throw new AssertionError("couldn't exercise move #" + k, e);
			}

			k++;
		}
	}

	@Test
	public void aiAgainstItselfCompleteGame() {
		int lookAhead = 4;

		Game game = new GameImplIntArray1D(6, 7);

		Ai ai = new AbPruningAi(lookAhead);

		while (game.isRunning()) {
			int[] moveValues = ai.getMoveValues(game, null);
			int j = Ai.getBestMove(moveValues);

			game.move(j);
		}
	}

	@Test
	public void testSimpleRunNoException() {
		int lookAhead = 4;

		Game game = new GameImplIntArray1D(6, 7);

		MinimaxAi mmAi = new MinimaxAi(lookAhead);

		mmAi.getBestMove(game, null);

		mmAi.getMoveValues(game, null);
	}

	@Test
	public void testDefend() {
		String problem = "" + //
				"current_player: ●\n" + //
				"0 xx____x\n" + //
				"1 oo____o\n" + //
				"2 xo____x\n" + //
				"3 ooo___o\n" + //
				"4 xxo__xx\n" + //
				"5 oo■_■■o\n" + //
				"  0123456\n" //
		;

		Game game = Format.INSTANCE.parse(problem);

		MinimaxAi ai = new MinimaxAi();

		int j = ai.getBestMove(game, null);

		assertEquals("defensive move is not chosen:", 3, j);
	}

	@Test
	public void testSeeWinningMoveSimple() {
		String problem = "" + //
				"current_player: x\n" + //
				"0 _______\n" + //
				"1 _______\n" + //
				"2 _______\n" + //
				"3 _______\n" + //
				"4 _ooo___\n" + //
				"5 xoxxx__\n" + //
				"  0123456\n" //
		;

		Game game = Format.INSTANCE.parse(problem);
		MinimaxAi ai = new MinimaxAi();

		int[] moveValues = ai.getMoveValues(game, null);
		int j = Ai.getBestMove(moveValues);

		assertEquals("winning move is not chosen", 5, j);
		assertEquals("move value should be Ai.VALUE_VICTORY:", Ai.VALUE_VICTORY, moveValues[j]);
	}

	@Test
	public void testSeeWinningMove() {
		String problem = "" + //
				"current_player: x\n" + //
				"0 xx____x\n" + //
				"1 oo____o\n" + //
				"2 xoo___x\n" + //
				"3 ooo___o\n" + //
				"4 xxo___x\n" + //
				"5 xo■■■_o\n" + //
				"  0123456\n" //
		;

		Game game = Format.INSTANCE.parse(problem);
		MinimaxAi ai = new MinimaxAi();

		int[] moveValues = ai.getMoveValues(game, null);
		int j = Ai.getBestMove(moveValues);

		assertEquals("winning move is not chosen", 5, j);
		assertEquals("move value should be Ai.VALUE_VICTORY:", Ai.VALUE_VICTORY, moveValues[j]);
	}

	@Test
	public void testSeeWinningMove2() {
		String problem = "" + //
				"current_player: x\n" + //
				"0 xx____x\n" + //
				"1 oo____o\n" + //
				"2 xoo___x\n" + //
				"3 ooo___o\n" + //
				"4 xxo__xx\n" + //
				"5 oo■_■■o\n" + //
				"  0123456\n" //
		;

		Game game = Format.INSTANCE.parse(problem);
		MinimaxAi ai = new MinimaxAi();
		int[] moveValues = ai.getMoveValues(game, null);
		int j = Ai.getBestMove(moveValues);

		assertEquals("winning move is not chosen:", 3, j);
		assertEquals("move value should be Ai.VALUE_VICTORY:", Ai.VALUE_VICTORY, moveValues[j]);
	}

	public static void assertEquals(String message, int expected, int actual) {
		if (actual != expected) {
			fail(message + " " + "expected:<" + expected + "> actual:<" + actual + ">");
		}
	}

	public static void assertEquals(String message, float expected, float actual) {
		if (actual != expected) {
			fail(message + " " + "expected:<" + expected + "> actual:<" + actual + ">");
		}
	}
}

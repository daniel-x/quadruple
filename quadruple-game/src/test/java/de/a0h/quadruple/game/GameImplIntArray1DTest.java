package de.a0h.quadruple.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GameImplIntArray1DTest {

	@Test
	public void testAssignTo() {
		String problem = "" + //
				"current_player: o\n" + //
				"0 xx____x\n" + //
				"1 oo____o\n" + //
				"2 xoo___x\n" + //
				"3 ooo___o\n" + //
				"4 xxo__xx\n" + //
				"5 oo■_■■o\n" + //
				"  0123456\n";

		Game gameA = Format.INSTANCE.parse(problem);
		Game gameB = new GameImplIntArray1D(5, 5);
		gameA.assignTo(gameB);

		String expected = Format.INSTANCE.toString(gameA);
		String actual = Format.INSTANCE.toString(gameB);

		assertEquals("move result incorrect:", expected, actual);
	}

	@Test
	public void testMoveUnmove() {
		String problem = "" + //
				"current_player: o\n" + //
				"0 _______\n" + //
				"1 xx____o\n" + //
				"2 xox___x\n" + //
				"3 ooo___o\n" + //
				"4 oxo__xx\n" + //
				"5 oox_xxo\n" + //
				"  0123456\n";

		Game gameA = Format.INSTANCE.parse(problem);
		GameImplIntArray1D gameB = new GameImplIntArray1D(4, 4);
		gameA.assignTo(gameB);

		for (int j = 0; j < gameB.getW(); j++) {
			gameB.move_nocheck(j);
			gameB.move_nocheck(3);
			gameB.move_nocheck(4);
			gameB.unmove_nocheck(4);
			gameB.unmove_nocheck(3);
			gameB.unmove_nocheck(j);

			String actual = Format.INSTANCE.toString(gameB);

//			if (!problem.equals(actual)) {
//				System.out.println("j=" + j);
//				System.out.println(problem);
//				System.out.println(actual);
//			}

			assertEquals("problem at j=" + j, problem, actual);
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testMoveOntoFullColumn() {
		String problem = "" + //
				"current_player: o\n" + //
				"0 xx____x\n" + //
				"1 oo____o\n" + //
				"2 xoo___x\n" + //
				"3 ooo___o\n" + //
				"4 xxo__xx\n" + //
				"5 oo■_■■o\n" + //
				"  0123456\n";

		Game game = Format.INSTANCE.parse(problem);

		game.move(1);
	}

	@Test
	public void testAssignToAndMove5() {
		String problem = "" + //
				"current_player: o\n" + //
				"0 xx____x\n" + //
				"1 oo____o\n" + //
				"2 xoo___x\n" + //
				"3 ooo___o\n" + //
				"4 xxo__xx\n" + //
				"5 oo■_■■o\n" + //
				"  0123456\n";

		Game gameA = Format.INSTANCE.parse(problem);
		Game gameB = new GameImplIntArray1D(5, 5);
		gameA.assignTo(gameB);

		gameB.move(5);

		String expected = "" + //
				"current_player: x\n" + //
				"0 xx____x\n" + //
				"1 oo____o\n" + //
				"2 xoo___x\n" + //
				"3 ooo__oo\n" + //
				"4 xxo__xx\n" + //
				"5 oox_xxo\n" + //
				"  0123456\n";

		String actual = Format.INSTANCE.toString(gameB);

		assertEquals("move result incorrect:", expected, actual);
	}

	@Test
	public void testMove3() {
		String problem = "" + //
				"current_player: o\n" + //
				"0 xx____x\n" + //
				"1 oo____o\n" + //
				"2 xoo___x\n" + //
				"3 ooo___o\n" + //
				"4 xxo__xx\n" + //
				"5 oo■_■■o\n" + //
				"  0123456\n";

		Game game = Format.INSTANCE.parse(problem);

		game.move(3);

		String expected = "" + //
				"current_player: x\n" + //
				"0 xx____x\n" + //
				"1 oo____o\n" + //
				"2 xoo___x\n" + //
				"3 ooo___o\n" + //
				"4 xxo__xx\n" + //
				"5 ooxoxxo\n" + //
				"  0123456\n";

		String actual = Format.INSTANCE.toString(game);

		assertEquals("move result incorrect:", expected, actual);
	}

	@Test
	public void testGetWinnerHorizontal() {
		String problem = "" + //
				"current_player: o\n" + //
				"0 xx____x\n" + //
				"1 oo____o\n" + //
				"2 xoo___x\n" + //
				"3 ooo___o\n" + //
				"4 xxo__xx\n" + //
				"5 oo■■■■o\n" + //
				"  0123456\n";

		Game game = Format.INSTANCE.parse(problem);
		int winner = game.determineWinner();

		assertEquals("result of game.getWinner() is incorrect:", Game.PLAYER_FRST, winner);
	}

	@Test
	public void testGetWinnerFirstDiagonal4() {
		String problem = "" + //
				"current_player: x\n" + //
				"0 x______\n" + //
				"1 x______\n" + //
				"2 xxx_●__\n" + //
				"3 oxo●o__\n" + //
				"4 oo●xx__\n" + //
				"5 o●oxx__\n" + //
				"  0123456\n";

		Game game = Format.INSTANCE.parse(problem);
		int winner = game.determineWinner();

		assertEquals("result of game.getWinner() is incorrect:", Game.PLAYER_SCND, winner);
	}

	@Test
	public void testGetWinnerSecondDiagonal4() {
		String problem = "" + //
				"current_player: x\n" + //
				"0 _______\n" + //
				"1 ●___x__\n" + //
				"2 o●__x__\n" + //
				"3 xx●_x__\n" + //
				"4 oxx●o__\n" + //
				"5 oo●xx__\n" + //
				"  0123456\n";

		Game game = Format.INSTANCE.parse(problem);
		int winner = game.determineWinner();

		assertEquals("result of game.getWinner() is incorrect:", Game.PLAYER_SCND, winner);
	}

	@Test
	public void testGetWinnerVertical() {
		String problem = "" + //
				"current_player: x\n" + //
				"0 _______\n" + //
				"1 o___■__\n" + //
				"2 ox__■__\n" + //
				"3 xoo_■__\n" + //
				"4 oxxo■__\n" + //
				"5 oooxx__\n" + //
				"  0123456\n";

		Game game = Format.INSTANCE.parse(problem);
		int winner = game.determineWinner();

		assertEquals("result of game.getWinner() is incorrect:", Game.PLAYER_FRST, winner);
	}

}

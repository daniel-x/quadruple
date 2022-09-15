package de.a0h.quadruple.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GameImpl1Long6x7Test {

	@Test
	public void testMove() {
		GameImpl1Long6x7 game = new GameImpl1Long6x7(6, 7);

		game.move_nocheck(4);
		game.move_nocheck(4);
		game.move_nocheck(3);
		game.move_nocheck(3);

		String actual = Format.INSTANCE.toString(game);

		String expected = "" + //
				"current_player: x\n" + //
				"0 _______\n" + //
				"1 _______\n" + //
				"2 _______\n" + //
				"3 _______\n" + //
				"4 ___oo__\n" + //
				"5 ___xx__\n" + //
				"  0123456\n" //
		;

		assertEquals("problem", expected, actual);
	}
}

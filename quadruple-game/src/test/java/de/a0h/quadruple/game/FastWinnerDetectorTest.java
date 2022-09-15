package de.a0h.quadruple.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FastWinnerDetectorTest {

	@Test
	public void test() {
		String problem = "" + //
				"current_player: x\n" + //
				"0 _______\n" + //
				"1 ___x___\n" + //
				"2 ___o___\n" + //
				"3 ___x___\n" + //
				"4 _ooo___\n" + //
				"5 xoxxx_o\n" + //
				"  0123456\n";

		Game game = Format.INSTANCE.parse(problem);

		FieldIterator fieldIter = FieldIterator.INSTANCE;
		FastWinnerDetector winDet = new FastWinnerDetector(game);

		fieldIter.performNeighborhoodIteration(winDet, game.getH(), game.getW(), 5, 6);

		assertEquals(winDet.getResult(), Game.PLAYER_NONE);
	}

}

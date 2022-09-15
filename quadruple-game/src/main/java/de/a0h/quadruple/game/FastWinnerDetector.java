package de.a0h.quadruple.game;

import de.a0h.quadruple.game.FieldIterator.IterationConsumer;

/**
 * Instances of this class are not thread-safe.
 */
public class FastWinnerDetector implements IterationConsumer {

	private int mResult;
	private int mCount;
	private Game mGame;

	public FastWinnerDetector(Game game) {
		setGame(game);
	}

	public void setGame(Game game) {
		mGame = game;
	}

	public int getResult() {
		return mResult;
	}

	@Override
	public void iterationStarted(int ignored1, int ignored2) {
	}

	@Override
	public void rowStarted() {
		mResult = Game.PLAYER_NONE;
	}

	@Override
	public boolean nextLocation(int i, int j) {
		int currColor;
//		try {
		currColor = mGame.getStone(i, j);
//		} catch (Exception e) {
//			throw new RuntimeException("(i, j) = " + "(" + i + ", " + j + ")", e);
//		}

		if (currColor == Game.PLAYER_NONE) {
			mResult = Game.PLAYER_NONE;

		} else if (currColor != mResult) {
			mResult = currColor;
			mCount = 1;

		} else {
			mCount++;

			if (mCount == 4) {
//				String s = "winner at (i, j) = " + "(" + i + ", " + j + "):";
//				System.out.println(s);
//				if ("winner at (i, j) = (2, 3):".equals(s)) {
//					System.out.println("foo");
//				}
//				System.out.println(mGame);
				return true;
			}
		}

		return false;
	}

	@Override
	public void iterationEnded() {
		if (mCount != 4) {
			mResult = Game.PLAYER_NONE;
		}
	}
}
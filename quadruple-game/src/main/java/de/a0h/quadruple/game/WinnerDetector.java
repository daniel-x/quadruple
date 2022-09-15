package de.a0h.quadruple.game;

import de.a0h.quadruple.game.FieldIterator.IterationConsumer;

public class WinnerDetector implements IterationConsumer {

	/**
	 * This instance is not thread-safe.
	 */
	public static final WinnerDetector INSTANCE = new WinnerDetector();

	protected Game mGame;
	public int mPlayer;
	public int mCount;

	public int mPrevI;
	public int mPrevJ;
	public int mCurrI;
	public int mCurrJ;

	public void setGame(Game game) {
		mGame = game;
	}

	@Override
	public void iterationStarted(int ignored1, int ignored2) {
	}

	@Override
	public void rowStarted() {
		mPlayer = Game.PLAYER_NONE;
	}

	@Override
	public boolean nextLocation(int i, int j) {
		int currColor = mGame.getStone_nocheck(i, j);

		mPrevI = mCurrI;
		mPrevJ = mCurrJ;
		mCurrI = i;
		mCurrJ = j;

		if (currColor == Game.PLAYER_NONE) {
			mPlayer = Game.PLAYER_NONE;

		} else if (currColor != mPlayer) {
			mPlayer = currColor;
			mCount = 1;

		} else {
			mCount++;

			if (mCount == 4) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void iterationEnded() {
		if (mCount != 4) {
			mPlayer = Game.PLAYER_NONE;
		}
	}

}
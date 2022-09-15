package de.a0h.quadruple.game;

import java.util.Arrays;

public class GameImplLongMicroStacks extends Game {

	/**
	 * When it's first or second player, the the game is running. When it's
	 * PLAYER_NONE, then the game has ended.
	 */
	public int mCurrPlayer;

	/**
	 * Array containing the field information, one element per column.
	 */
	public long[] mS;

	/**
	 * Number of stones in every column.
	 */
	public int[] mStoneCount;

	public GameImplLongMicroStacks(int height, int width) {
		super(height, width);

		alloc();

		restart();
	}

	private void alloc() {
		mS = new long[mW];
		mStoneCount = new int[mW];
	}

	@Override
	public void restart() {
		Arrays.fill(mS, 0);

		Arrays.fill(mStoneCount, 0);

		mCurrPlayer = PLAYER_FRST;
	}

	@Override
	public void setSize(int h, int w) {
		if (mH != h || mW != w) {
			mH = h;
			mW = w;

			alloc();
		}
	}

	@Override
	public int getCurrentPlayer() {
		return mCurrPlayer;
	}

	@Override
	public void setCurrentPlayer(int currPlayer) {
		mCurrPlayer = currPlayer;
	}

	@Override
	public boolean isFull_nocheck(int j) {
		return mStoneCount[j] == mH;
	}

	@Override
	public boolean isEmpty_nocheck(int j) {
		return mStoneCount[j] == 0;
	}

	@Override
	public boolean isFull() {
		int[] sc = mStoneCount;

		for (int j = 0; j < mW; j++) {
			if (sc[j] != mH) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isRunning() {
		return mCurrPlayer != PLAYER_NONE;
	}

	@Override
	public int move_nocheck(int j) {
		int i = mH - 1 - mStoneCount[j];
		mStoneCount[j]++;

		mS[j] = mS[j] ^ (mS[j] & (1 << i)) | (mCurrPlayer << i);

		toggleCurrentPlayer_nocheck();

		return i;
	}

	@Override
	public void unmove_nocheck(int j) {
		mStoneCount[j]--;
		int i = mH - 1 - mStoneCount[j];

		mS[j] = mS[j] ^ (mS[j] & (1 << i));

		toggleCurrentPlayer_nocheck();
	}

	@Override
	public void toggleCurrentPlayer_nocheck() {
		mCurrPlayer ^= 1;
	}

	@Override
	public int getStone_nocheck(int i, int j) {
		boolean isSet = (mH - 1 - i < mStoneCount[j]);

		int result = isSet ? (((int) (mS[j] >> i)) & 1) : PLAYER_NONE;

		return result;
	}

	@Override
	public int getStoneCount_nocheck(int j) {
		return mStoneCount[j];
	}

	public static int log2OfPowerOf2EqualToOrLowestAbove(int w) {
		if (w == 0) {
			return 0;
		}

		int highestBitSet = Integer.highestOneBit(w);
		int highestBitLoc = Integer.numberOfTrailingZeros(highestBitSet);

		return (highestBitSet == w) ? highestBitLoc : ++highestBitLoc;
	}
}

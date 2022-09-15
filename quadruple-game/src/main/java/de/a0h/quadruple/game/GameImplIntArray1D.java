package de.a0h.quadruple.game;

import java.util.Arrays;

public class GameImplIntArray1D extends Game {

	/**
	 * When it's first or second player, the the game is running. When it's
	 * PLAYER_NONE, then the game has ended.
	 */
	public int mCurrPlayer;

	/**
	 * = log2OfPowerOf2EqualToOrLowestAbove(mW);
	 */
	public int mShiftWidth;

	/**
	 * Array containing the field information, one element per possible stone
	 * location. It's stored in row-major order, i.e. mF[i * width + j] corresponds
	 * to row i, column j. The first row and the first column have index 0. In case
	 * w is not equal to a power of 2 (2^n), the array is made as big as if w was
	 * the next higher power of 2. That allows to address f by mF[(i << shiftWidth)
	 * + j], replacing a multiplication by a bit shift, which is faster.
	 */
	public int[] mF;

	/**
	 * Number of stones in every column.
	 */
	public int[] mStoneCount;

	public GameImplIntArray1D(int height, int width) {
		super(height, width);

		alloc();

		restart();
	}

	private void alloc() {
		mShiftWidth = log2OfPowerOf2EqualToOrLowestAbove(mW);
		mF = new int[mH * (1 << mShiftWidth)];

		mStoneCount = new int[mW];
	}

	@Override
	public void restart() {
		Arrays.fill(mF, PLAYER_NONE);

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
		int i = getNextVacancy_nocheck(j);
		mStoneCount[j]++;

		mF[getArIdx(i, j)] = mCurrPlayer;

		toggleCurrentPlayer_nocheck();

		return i;
	}

	@Override
	public void unmove_nocheck(int j) {
		mStoneCount[j]--;
		int i = mH - 1 - mStoneCount[j];

		// we cold possibly omit this. then we have to modify the getStone()
		// methods and possibly others to look at mStoneCount
		int arIdx = getArIdx(i, j);
		mF[arIdx] = PLAYER_NONE;

		toggleCurrentPlayer_nocheck();
	}

	@Override
	public void toggleCurrentPlayer_nocheck() {
		mCurrPlayer ^= 1;
	}

	@Override
	public int getStone_nocheck(int i, int j) {
		return mF[getArIdx(i, j)];
	}

	private final int getArIdx(int i, int j) {
		// return (i << mShiftWidth) + j;
		return (i * mW) + j;
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

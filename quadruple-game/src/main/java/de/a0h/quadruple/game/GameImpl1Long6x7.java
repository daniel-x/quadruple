package de.a0h.quadruple.game;

import java.util.Arrays;

public class GameImpl1Long6x7 extends Game {

	private static final int H = 6;
	private static final int W = 7;

	private static final int SIZE_STACK_LEN = 3;
	private static final int SIZE_STACK_CONTENT = H;

	private static final int SIZE_STACK_TOTAL = SIZE_STACK_LEN + SIZE_STACK_CONTENT;

	private static final int MASK_STACK_LEN = -1 >>> (32 - SIZE_STACK_LEN);
	// private static final int MASK_STACK_CONTENT = -1 >>> (32 -
	// SIZE_STACK_CONTENT);

	private static final long MASK_ALL_STACK_LENS;
	private static final long VALUE_ALL_STACKS_FULL;

	private static final long VALUE_STACKS_SIZE_1 = 1 << SIZE_STACK_CONTENT;

	public static final String DEBUG_FORMAT_INFO = "" + //
			"6666555555555544444444443333333333222222222211111111110000000000\n" + //
			"3210987654321098765432109876543210987654321098765432109876543210\n" + //
			"_LLLssssssLLLssssssLLLssssssLLLssssssLLLssssssLLLssssssLLLssssss" + //
			"" //
	;

	static {
		MASK_ALL_STACK_LENS = getAllStackLensAssigned(MASK_STACK_LEN);
		VALUE_ALL_STACKS_FULL = getAllStackLensAssigned(H);

//		System.out.println(DEBUG_FORMAT_INFO);
//		System.out.println(toBinString(MASK_STACK_LEN) + " MASK_STACK_LEN");
//		// System.out.println(toBinString(MASK_STACK_CONTENT) + " MASK_STACK_CONTENT");
//		System.out.println(toBinString(MASK_ALL_STACK_LENS) + " MASK_ALL_STACK_LENS");
//		System.out.println(toBinString(VALUE_ALL_STACKS_FULL) + " VALUE_ALL_STACKS_FULL");
	}

	/**
	 * <code>
	 * bit usage<br/>
	 * read from left to right<br/>
	 * .............example bit 27 is here: |<br/>
	 * |6666555555555544444444443333333333222222222211111111110000000000|<br/>
	 * |3210987654321098765432109876543210987654321098765432109876543210|<br/>
	 * |_LLLssssssLLLssssssLLLssssssLLLssssssLLLssssssLLLssssssLLLssssss|<br/>
	 * <br/>
	 * _: unused bit<br/>
	 * LLLssssss: micro-stack<br/>
	 * LLL: 3-bit unsigned integer, stack length<br/>
	 * ...ssssss: 6 bit stack content<br/>
	 * </code>
	 */
	private long s;

	/**
	 * Always -Game.PLAYER_..., because then FRST=0 and SCND=-1=0
	 */
	private long mCurrPlayer;

	public GameImpl1Long6x7(int h, int w) {
		super(h, w);

		if (h != H || w != W) {
			throw new IllegalArgumentException("must be 6x7, but was: " + h + "x" + w);
		}

		restart();
	}

	@Override
	public void setSize(int h, int w) {
		if (h != H || w != W) {
			throw new IllegalArgumentException("must be 6x7, but was: " + h + "x" + w);
		}
	}

	@Override
	public void setCurrentPlayer(int currPlayer) {
		mCurrPlayer = -currPlayer;
	}

	@Override
	public void restart() {
		mCurrPlayer = -PLAYER_FRST;
		s = 0;
	}

	@Override
	public int getStone_nocheck(int i, int j) {
		i = H - 1 - i;
		// int microStack = (int) (s >>> (j * SIZE_STACK_TOTAL));
		int microStack = (int) (s >> (j << 3) >> j);

		int len = (microStack >> SIZE_STACK_CONTENT) & MASK_STACK_LEN;

		return (i < len) ? ((microStack >> i) & 1) : PLAYER_NONE;
	}

	@Override
	public int getStoneCount_nocheck(int j) {
		return (((int) (s >> (j << 3) >> j)) >> SIZE_STACK_CONTENT) & MASK_STACK_LEN;
	}

	@Override
	public boolean isFull_nocheck(int j) {
		return getStoneCount_nocheck(j) == H;
	}

	@Override
	public boolean isEmpty_nocheck(int j) {
		return getStoneCount_nocheck(j) == 0;
	}

	@Override
	public boolean isFull() {
		return (s & MASK_ALL_STACK_LENS) == VALUE_ALL_STACKS_FULL;
	}

	@Override
	public int move_nocheck(int j) {
		// int microStack = (int) (s >>> (j * SIZE_STACK_TOTAL));
		// int microStack = (int) (s >>> (j << 3) >>> j);
		int len = (((int) (s >> (j << 3) >> j)) >> SIZE_STACK_CONTENT) & MASK_STACK_LEN;
		long mask = 1L << (j << 3) << j << len;

		s = (s & ~mask) | (mCurrPlayer & mask);

		s += VALUE_STACKS_SIZE_1 << (j << 3) << j; // increment len

		toggleCurrentPlayer_nocheck();

		return mH - 1 - len;
	}

	@Override
	public void unmove_nocheck(int j) {
		// unsetting the stone isn't necessary. we can keep garbage in the unused part
		// of the micro stack

		s -= VALUE_STACKS_SIZE_1 << (j << 3) << j; // decrement len

		toggleCurrentPlayer_nocheck();
	}

	@Override
	public void toggleCurrentPlayer_nocheck() {
		mCurrPlayer = ~mCurrPlayer;
	}

	@Override
	public int getCurrentPlayer() {
		return (int) -mCurrPlayer;
	}

	public String toBinaryString() {
		return toBinaryString(s);
	}

	public static String toBinaryString(int v) {
		String s = Integer.toBinaryString(v);
		return lpad(s, 32, '0');
	}

	public static String toBinaryString(long v) {
		String s = Long.toBinaryString(v);
		return lpad(s, 64, '0');
	}

	private static long getAllStackLensAssigned(long len) {
		long result = (len & MASK_STACK_LEN) << SIZE_STACK_CONTENT;

		for (int j = 1; j < W; j++) {
			result |= (result << SIZE_STACK_TOTAL);
		}

		return result;
	}

	public static String lpad(String s, int totalLen, char pad) {
		int padLen = totalLen - s.length();

		if (padLen > 0) {
			char[] cAr = new char[totalLen];
			Arrays.fill(cAr, 0, padLen, pad);
			s.getChars(0, s.length(), cAr, padLen);
			s = new String(cAr);
		}

		return s;
	}
}

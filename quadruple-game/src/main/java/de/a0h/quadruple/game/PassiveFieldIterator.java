package de.a0h.quadruple.game;

/**
 * Generates a series of (rowIdx, columnIdx) pairs to traverse a field in all
 * ways possible for making a connection of 4. next() must be called once before
 * accessing the row index or column index. The call to next() for the last
 * valid location returns STATUS_ORDINARY. The next call returns
 * STATUS_TRAVERSAL_END.
 */
public class PassiveFieldIterator {

	public static final int STATUS_ORDINARY = 0;
	public static final int STATUS_ROW_BEGIN = 1;
	public static final int STATUS_TRAVERSAL_END = 2;

	protected static final int PHASE_HORIZONTAL = 0;
	protected static final int PHASE_VERTICAL = 1;

	/**
	 * Traversal in /-direction (bottom left to top right).
	 */
	protected static final int PHASE_DIAG1 = 2;

	/**
	 * Traversal in \-direction (top left to bottom right).
	 */
	protected static final int PHASE_DIAG2 = 3;

	/**
	 * All traversals ended.
	 */
	protected static final int PHASE_DONE = 4;

	protected int mPhase;

	public int mRowIdx;
	public int mColumnIdx;
	protected int mDiag;

	protected int mH;
	protected int mW;

	public PassiveFieldIterator(int w, int h) {
		mW = w;
		mH = h;

		mPhase = PHASE_HORIZONTAL;
		mRowIdx = -1;
		mColumnIdx = mW;
		mColumnIdx--;
	}

	/**
	 * Change mRowIdx and mColumnIdx to the next location. Return true if there is a
	 * next element, false otherwise.
	 */
	public int next() {
		switch (mPhase) {
		case PHASE_HORIZONTAL:
			return nextHorizontal();
		case PHASE_VERTICAL:
			return nextVertical();
		case PHASE_DIAG1:
			return nextDiag1();
		case PHASE_DIAG2:
			return nextDiag2();
		case PHASE_DONE:
			throw new IllegalStateException("iteration was already finished by previous call");
		}

		throw new IllegalArgumentException("unknown phase: " + mPhase);
	}

	private final int nextHorizontal() {
		if (++mColumnIdx == mW) {
			mColumnIdx = 0;

			if (++mRowIdx == mH) {
				mPhase++;
				mRowIdx = 0;
			}

			return STATUS_ROW_BEGIN;
		}

		return STATUS_ORDINARY;
	}

	private final int nextVertical() {
		if (++mRowIdx == mH) {
			mRowIdx = 0;

			if (++mColumnIdx == mW) {
				mPhase++;

				mDiag = 3;
				initDiag1RowStart();
			}

			return STATUS_ROW_BEGIN;
		}

		return STATUS_ORDINARY;
	}

	private final void initDiag1RowStart() {
		mRowIdx = mDiag;
		mColumnIdx = 0;

		if (mRowIdx > mH - 1) {
			mColumnIdx = mRowIdx - (mH - 1);
			mRowIdx = mH - 1;
		}
	}

	private final void initDiag2RowStart() {
		mRowIdx = mH - 1 - mDiag;
		mColumnIdx = 0;

		if (mRowIdx < 0) {
			mColumnIdx = -mRowIdx;
			mRowIdx = 0;
		}
	}

	private final int nextDiag1() {
		mRowIdx--;
		mColumnIdx++;

		if (mRowIdx == -1 || mColumnIdx == mW) {
			if (++mDiag == mH + mW - 1 - 3) {
				mPhase++;

				mDiag = 3;
				initDiag2RowStart();

				return STATUS_ROW_BEGIN;
			}

			initDiag1RowStart();

			return STATUS_ROW_BEGIN;
		}

		return STATUS_ORDINARY;
	}

	private final int nextDiag2() {
		mRowIdx++;
		mColumnIdx++;

		if (mRowIdx == mH || mColumnIdx == mW) {
			if (++mDiag == mH + mW - 1 - 3) {
				mPhase++;

				mRowIdx = -1;
				mColumnIdx = -1;

				return STATUS_TRAVERSAL_END;
			}

			initDiag2RowStart();

			return STATUS_ROW_BEGIN;
		}

		return STATUS_ORDINARY;
	}

	public String toString() {
		return getClass().getSimpleName() + "[phase:" + mPhase + " mRowIdx:" + mRowIdx + " " + " mColumnIdx:"
				+ mColumnIdx + " " + "]";
	}

}

package de.a0h.quadruple.ai.minimax;

import java.util.Arrays;

import de.a0h.quadruple.game.FieldIterator.IterationConsumer;
import de.a0h.quadruple.game.Game;

/**
 * A potential is a vacant location which might later become part of a winning
 * quadruple. The count of a potential is the number of stones of one color
 * which are already present. This count can be 1, 2 or 3.
 * 
 * <p>
 * Instances of this class are not thread-safe.
 * </p>
 */
public class PotentialEvaluater implements IterationConsumer {

	public static final int POT_VALUE_1 = 1 << 16;
	public static final int POT_VALUE_2 = 4 << 16;
	public static final int POT_VALUE_3 = 10 << 16;
	public static final int POT_VALUE_1AND1 = 20 << 16;
	public static final int POT_VALUE_1AND2 = 40 << 16;
	public static final int POT_VALUE_2AND2 = 60 << 16;
	public static final int POT_VALUE_1AND3 = 100 << 16;
	public static final int POT_VALUE_2AND3 = 150 << 16;
	public static final int POT_VALUE_3AND3 = 200 << 16;
	public static final int POT_VALUE_DOUBLE3IMMEDIATE = 500 << 16;

	Game mGame;

	int[] mRecentOwner = new int[4];
	int[] mRecentI = new int[4];
	int[] mRecentJ = new int[4];
	int mLen = 0;

	int[] mOwnerCount = new int[2];

	/**
	 * pot[player][rowIdx][columnIdx]. Saved is the highest counting potential found
	 * for the location.
	 */
	int[][][] mPotField;

	public PotentialEvaluater(Game game) {
		setGame(game);
	}

	private void clearPot(int fieldH, int fieldW) {
		if (false //
				|| mPotField == null //
				|| mPotField[Game.PLAYER_FRST].length != fieldH //
				|| mPotField[Game.PLAYER_FRST][0].length != fieldW //
		) {
			mPotField = new int[2][fieldH][fieldW];
		} else {
			clearPotField(fieldH, fieldW);
		}
	}

	@Override
	public void iterationStarted(int fieldH, int fieldW) {
		clearPot(fieldH, fieldW);
	}

	protected void clearPotField(int fieldH, int fieldW) {
		for (int k = 0; k < 2; k++) {
			int[][] potSubfield = mPotField[k];

			for (int i = 0; i < fieldH; i++) {
				Arrays.fill(potSubfield[i], 0);
			}
		}
	}

	public int getPotentialValue(int player) {
		int ownPotValue = getPotentialValueImpl(player);
		int oppPotValue = getPotentialValueImpl(Game.togglePlayer(player));

		return ownPotValue - oppPotValue;
	}

	private int getPotentialValueImpl(int player) {
		int result = 0;

		int[][] potSubfield = mPotField[player];

		result += getSumSinglePotentialValueImpl(potSubfield);
		result += getSumCombinedPotentialValueImpl(potSubfield);

		return result;
	}

	private int getSumSinglePotentialValueImpl(int[][] potSubfield) {
		int result = 0;

		int fieldH = mGame.getH();
		int fieldW = mGame.getW();

		for (int i = 0; i < fieldH; i++) {
			int[] row = potSubfield[i];

			// performance optimization: early stopping
			boolean rowNoVacancies = true;

			for (int j = 0; j < fieldW; j++) {
				int a = row[j];
				rowNoVacancies &= (a == 0);

				switch (a) {
				case 1:
					result += POT_VALUE_1;
					continue;
				case 2:
					result += POT_VALUE_2;
					continue;
				case 3:
					result += POT_VALUE_3;
					continue;
				case 0:
					continue;
				default:
					throw new IllegalStateException("forgotten case for potential: " + row[j]);
				}
			}

			if (rowNoVacancies) {
				break;
			}
		}

		return result;
	}

	private int getSumCombinedPotentialValueImpl(int[][] potSubfield) {
		int result = 0;

		int count3Immediate = 0;

		boolean rowNoVacancies = false;

		int fieldH = mGame.getH();
		int fieldW = mGame.getW();

		for (int i = 0; i < fieldH - 1; i++) {
			int[] rowA = potSubfield[i];
			int[] rowB = potSubfield[i + 1];

			// performance optimization: early stopping
			rowNoVacancies = true;

			for (int j = 0; j < fieldW; j++) {
				int a = rowA[j];
				int b = rowB[j];
				rowNoVacancies &= (b == 0);

				if (a == 3 && b == 0 && mGame.getStone_nocheck(i, j) != Game.PLAYER_NONE) {
					count3Immediate++;
				}

				int combination = a * b;
				switch (combination) {
				case 9:
					result += POT_VALUE_3AND3;
					continue;
				case 6:
					result += POT_VALUE_2AND3;
					continue;
				case 4:
					result += POT_VALUE_2AND2;
					continue;
				case 3:
					result += POT_VALUE_1AND3;
					continue;
				case 2:
					result += POT_VALUE_1AND2;
					continue;
				case 1:
					result += POT_VALUE_1AND1;
					continue;
				case 0:
					continue;
				default:
					throw new IllegalStateException("forgotten case for combination of potentials: " + combination);
				}
			}

			if (rowNoVacancies) {
				break;
			}
		}

		if (!rowNoVacancies) {
			int[] row = potSubfield[fieldH - 1];

			for (int j = 0; j < fieldW; j++) {
				if (row[j] == 3) {
					count3Immediate++;
				}
			}
		}

		if (count3Immediate >= 2) {
			result += POT_VALUE_DOUBLE3IMMEDIATE;
		}

		return result;
	}

	public void rowStarted() {
		mOwnerCount[0] = 0;
		mOwnerCount[1] = 0;
		mLen = 0;
	}

	@Override
	public boolean nextLocation(int i, int j) {
		int owningPlayer = mGame.getStone_nocheck(i, j);

		if (owningPlayer < -1 || owningPlayer > 1) {
			System.out.println("owningPlayer: " + owningPlayer);
		}

		if (mLen < 4) {
			mRecentOwner[mLen] = owningPlayer;
			mLen++;
		} else {
			if (mRecentOwner[0] != Game.PLAYER_NONE) {
				mOwnerCount[mRecentOwner[0]]--;
			}

			mRecentOwner[0] = mRecentOwner[1];
			mRecentOwner[1] = mRecentOwner[2];
			mRecentOwner[2] = mRecentOwner[3];
			mRecentOwner[3] = owningPlayer;

			mRecentI[0] = mRecentI[1];
			mRecentI[1] = mRecentI[2];
			mRecentI[2] = mRecentI[3];
			mRecentI[3] = i;

			mRecentJ[0] = mRecentJ[1];
			mRecentJ[1] = mRecentJ[2];
			mRecentJ[2] = mRecentJ[3];
			mRecentJ[3] = j;
		}

		if (owningPlayer != Game.PLAYER_NONE) {
			mOwnerCount[owningPlayer]++;
		}

		if (mLen == 4) {
			int ownerOfPot = Game.PLAYER_NONE;
			if (mOwnerCount[Game.PLAYER_FRST] > 0 && mOwnerCount[Game.PLAYER_SCND] == 0) {
				ownerOfPot = Game.PLAYER_FRST;
			} else if (mOwnerCount[Game.PLAYER_SCND] > 0 && mOwnerCount[Game.PLAYER_FRST] == 0) {
				ownerOfPot = Game.PLAYER_SCND;
			}

			if (ownerOfPot != Game.PLAYER_NONE) {
				int[][] potSubfield = mPotField[ownerOfPot];

				for (int k = 0; k < mLen; k++) {
					// if it's vacant, then it's a potential
					if (mRecentOwner[k] == Game.PLAYER_NONE) {
						int pot = potSubfield[mRecentI[k]][mRecentJ[k]];
						pot = Math.max(pot, mOwnerCount[ownerOfPot]);
						potSubfield[mRecentI[k]][mRecentJ[k]] = pot;
					}
				}
			}
		}

		return false;
	}

	@Override
	public void iterationEnded() {
	}

	public void setGame(Game game) {
		mGame = game;
	}
}
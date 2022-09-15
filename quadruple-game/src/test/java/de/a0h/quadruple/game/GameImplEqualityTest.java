package de.a0h.quadruple.game;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class GameImplEqualityTest {

	@Test
	public void shouldGameImplsBehaveEqually() {
		ArrayList<Game> implList = new ArrayList<>();

		int h = 6;
		int w = 7;

		implList.add(new GameImpl1Long6x7(h, w));
		implList.add(new GameImplIntArray1D(h, w));
		implList.add(new GameImplLongMicroStacks(h, w));

		int repeatCount = 20;
		for (int gameIdx = 0; gameIdx < repeatCount; gameIdx++) {
			try {
				exerciseRandomGame(implList, w, gameIdx);
			} catch (Exception e) {
				throw new RuntimeException("couldn't exercise game #" + gameIdx, e);
			} catch (AssertionError e) {
				throw new AssertionError("couldn't exercise game #" + gameIdx, e);
			}

			for (Game impl : implList) {
				impl.restart();
			}
		}
	}

	public static void ensureConsistentStates(ArrayList<Game> implList) {
		ensureConsistentRunningStates(implList);
		ensureConsistentCurrentPlayerStates(implList);
		ensureConsistentWinnerStates(implList);
		ensureConsistentStringRepresentation(implList);
	}

	public static void exerciseRandomGame(ArrayList<Game> implList, int w, long seed) {
		Random rnd = new Random(seed);

		try {
			ensureConsistentStates(implList);
		} catch (Exception e) {
			throw new RuntimeException("problem detected before making a move", e);
		} catch (AssertionError e) {
			throw new AssertionError("problem detected before making a move", e);
		}

		int k = 0;
		while (implList.get(0).isRunning()) {
			try {
				makeOneRandomMove(implList, w, rnd);
			} catch (Exception e) {
				throw new RuntimeException("couldn't exercise move #" + k, e);
			} catch (AssertionError e) {
				throw new AssertionError("couldn't exercise move #" + k, e);
			}

			try {
				ensureConsistentStates(implList);
			} catch (Exception e) {
				throw new RuntimeException("problem detected after move #" + k, e);
			} catch (AssertionError e) {
				throw new AssertionError("problem detected after move #" + k, e);
			}

			k++;
		}
	}

	public static void makeOneRandomMove(ArrayList<Game> implList, int w, Random rnd) {
		int j;
		for (int i = 0;; i++) {
			j = rnd.nextInt(w);
			boolean isFull = implList.get(0).isFull(j);

			if (!isFull) {
				break;
			}

			if (i >= 10000) {
				throw new IllegalStateException("couldn't find valid random move");
			}
		}

		makeOneMove(implList, j);
	}

	public static void makeOneMove(ArrayList<Game> implList, int j) {
		for (Game impl : implList) {
			try {
				impl.move(j);
			} catch (Exception e) {
				String implName = impl.getClass().getSimpleName();
				throw new RuntimeException("move(" + j + ") failed on " + implName + ": " + impl, e);
			}
		}
	}

	public static void ensureConsistentCurrentPlayerStates(ArrayList<Game> implList) {
		ArrayList<Object> stateList = new ArrayList<>();

		for (Game impl : implList) {
			stateList.add(impl.getCurrentPlayer());
		}

		ensureStateConsistent(implList, stateList, "currentPlayer");
	}

	public static void ensureConsistentStringRepresentation(ArrayList<Game> implList) {
		ArrayList<Object> stateList = new ArrayList<>();

		for (Game impl : implList) {
			stateList.add(impl.toString());
		}

		ensureStateConsistent(implList, stateList, "String representation");
	}

	public static void ensureConsistentWinnerStates(ArrayList<Game> implList) {
		ArrayList<Object> stateList = new ArrayList<>();

		for (Game impl : implList) {
			stateList.add(impl.determineWinner());
		}

		ensureStateConsistent(implList, stateList, "winner");
	}

	public static void ensureConsistentRunningStates(ArrayList<Game> implList) {
		ArrayList<Object> stateList = new ArrayList<>();

		for (Game impl : implList) {
			stateList.add(impl.isRunning());
		}

		ensureStateConsistent(implList, stateList, "running ");
	}

	public static void ensureStateConsistent(ArrayList<? extends Object> implList, ArrayList<?> stateList,
			String stateName) {
		Object state0 = stateList.get(0);
		Class<? extends Object> c = state0.getClass();

		for (int i = 1; i < implList.size(); i++) {
			Object stateCurr = stateList.get(i);

			if (c.isArray()) {
				boolean failed = false;

				if (c.getComponentType().equals(Float.TYPE)) {
					failed = !Arrays.equals((float[]) stateCurr, (float[]) state0);
				} else if (c.getComponentType().equals(Integer.TYPE)) {
					failed = !Arrays.equals((int[]) stateCurr, (int[]) state0);
				} else {
					throw new UnsupportedOperationException("not implemented");
				}

				if (failed) {
					fail("" + //
							stateName + " states differ across " + //
							"different implementations: " + //
							getZippedListString(implList, stateList));
				}
			} else {
				if (!stateCurr.equals(state0)) {
					fail("" + //
							stateName + " states differ across " + //
							"different implementations: " + //
							getZippedListString(implList, stateList));
				}
			}
		}

	}

	public static String getZippedListString(ArrayList<? extends Object> implList, ArrayList<?> stateList) {
		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < implList.size(); i++) {
			String implName = implList.get(i).getClass().getSimpleName();

			Object o = stateList.get(i);
			Class<? extends Object> c = o.getClass();

			String oStr;
			if (c.isArray()) {
				if (c.getComponentType().equals(Integer.TYPE)) {
					oStr = Arrays.toString((int[]) o);
				} else if (c.getComponentType().equals(Float.TYPE)) {
					oStr = Arrays.toString((float[]) o);
				} else {
					oStr = o.toString();
				}
			} else {
				oStr = o.toString();
			}

			buf.append(implName + ":" + oStr + " ");
		}

		return buf.toString();
	}
}
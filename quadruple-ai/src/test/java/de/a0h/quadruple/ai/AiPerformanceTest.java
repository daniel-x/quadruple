package de.a0h.quadruple.ai;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import de.a0h.quadruple.ai.minimax.AbPruningAi;
import de.a0h.quadruple.ai.minimax.MinimaxAi;
import de.a0h.quadruple.game.Format;
import de.a0h.quadruple.game.Game;
import de.a0h.quadruple.game.GameImpl1Long6x7;
import de.a0h.quadruple.game.GameImplIntArray1D;
import de.a0h.quadruple.game.GameImplLongMicroStacks;

public class AiPerformanceTest {

	@Test
	public void compare() {
		int h = 6;
		int w = 7;
		int lookAhead = 4;

		String problem = "" + //
				"current_player: x\n" + //
				"0 _______\n" + //
				"1 _______\n" + //
				"2 _______\n" + //
				"3 _______\n" + //
				"4 xxooxxo\n" + //
				"5 ooxxoox\n" + //
				"  0123456\n" //
		;
		Game game = Format.INSTANCE.parse(problem);

		ArrayList<Ai> aiImplList = new ArrayList<>();
		aiImplList.add(new AbPruningAi(lookAhead));
		aiImplList.add(new MinimaxAi(lookAhead));

		ArrayList<Game> gameImplList = new ArrayList<>();
		gameImplList.add(new GameImplIntArray1D(h, w));
		gameImplList.add(new GameImpl1Long6x7(h, w));
		gameImplList.add(new GameImplLongMicroStacks(h, w));

		for (int gameIdx = 0; gameIdx < gameImplList.size(); gameIdx++) {
			game.assignTo(gameImplList.get(gameIdx));
		}

		int measureCount = 20;
		double[][] runtimes;

		// measure
		runtimes = measure(aiImplList, gameImplList, measureCount);
		System.out.println(toString(aiImplList, gameImplList, runtimes));
	}

	double[][] measure(ArrayList<Ai> aiImplList, ArrayList<Game> gameImplList, int repeatCount) {
		long[][] rTimesL = new long[aiImplList.size()][gameImplList.size()];

		for (int aiIdx = 0; aiIdx < aiImplList.size(); aiIdx++) {
			Arrays.fill(rTimesL[aiIdx], Long.MAX_VALUE);
		}

		for (int i = 0; i < repeatCount; i++) {
			for (int aiIdx = 0; aiIdx < aiImplList.size(); aiIdx++) {
				Ai aiImpl = aiImplList.get(aiIdx);

				for (int gameIdx = 0; gameIdx < gameImplList.size(); gameIdx++) {
					Game gameImpl = gameImplList.get(gameIdx);

					long t = System.nanoTime();

					aiImpl.getBestMove(gameImpl, null);

					t = System.nanoTime() - t;

					rTimesL[aiIdx][gameIdx] = Math.min( //
							rTimesL[aiIdx][gameIdx], t);
				}
			}
		}

		double[][] rTimesD = new double[aiImplList.size()][gameImplList.size()];
		for (int aiIdx = 0; aiIdx < aiImplList.size(); aiIdx++) {
			for (int gameIdx = 0; gameIdx < gameImplList.size(); gameIdx++) {
				rTimesD[aiIdx][gameIdx] = rTimesL[aiIdx][gameIdx] / 1000000.0D;
			}
		}

		return rTimesD;
	}

	public static final String lpad(String s, int totalLen, char pad) {
		int padLen = totalLen - s.length();

		if (padLen > 0) {
			char[] cAr = new char[totalLen];
			Arrays.fill(cAr, 0, padLen, pad);
			s.getChars(0, s.length(), cAr, padLen);
			s = new String(cAr);
		}

		return s;
	}

	protected String toString(ArrayList<Ai> aiImplList, ArrayList<Game> gameImplList, double[][] runtimes) {
		int gameColWidth = 0;
		for (int gameIdx = 0; gameIdx < gameImplList.size(); gameIdx++) {
			Game gameImpl = gameImplList.get(gameIdx);
			String gameImplName = gameImpl.getClass().getSimpleName();
			gameColWidth = Math.max(gameColWidth, gameImplName.length());
		}
		gameColWidth += 1;
		int aiColWidth = 0;
		for (int aiIdx = 0; aiIdx < aiImplList.size(); aiIdx++) {
			Ai aiImpl = aiImplList.get(aiIdx);
			String aiImplName = aiImpl.getClass().getSimpleName();
			aiColWidth = Math.max(aiColWidth, aiImplName.length());
		}
		aiColWidth += 1;

		StringBuilder buf = new StringBuilder();

		buf.append(lpad("", aiColWidth, ' '));
		for (int gameIdx = 0; gameIdx < gameImplList.size(); gameIdx++) {
			Game gameImpl = gameImplList.get(gameIdx);

			String gameImplName = gameImpl.getClass().getSimpleName();
			gameImplName = lpad(gameImplName, gameColWidth, ' ');
			buf.append(gameImplName);
		}
		buf.append("\n");

		for (int aiIdx = 0; aiIdx < aiImplList.size(); aiIdx++) {
			Ai aiImpl = aiImplList.get(aiIdx);

			String aiImplName = aiImpl.getClass().getSimpleName();
			aiImplName = lpad(aiImplName, aiColWidth, ' ');
			buf.append(aiImplName);

			for (int gameIdx = 0; gameIdx < gameImplList.size(); gameIdx++) {
				Game gameImpl = gameImplList.get(gameIdx);
				String gameName = gameImpl.getClass().getSimpleName();
				gameName = lpad(gameName, gameColWidth, ' ');

				double rTime = runtimes[aiIdx][gameIdx];

				String rTimeStr = Double.toString(rTime);
				rTimeStr = lpad(rTimeStr, gameColWidth, ' ');

				buf.append(rTimeStr);
			}

			buf.append("\n");
		}

		return buf.toString();
	}

}

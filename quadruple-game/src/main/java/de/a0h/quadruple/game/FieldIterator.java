package de.a0h.quadruple.game;

/**
 * This class is stateless and thus it is thread-safe.
 */
public class FieldIterator {

	public static final FieldIterator INSTANCE = new FieldIterator();

	private FieldIterator() {
	}

	/**
	 * Suitable for winner checks after a stone was placed, this method iterates
	 * over only the areas which could lead to a win together with the stone at the
	 * specified location, i.e. the location is X, the area which is checked is
	 * a:</br>
	 * <code>
	 * current_player: x</br>
	 * 0 _a_____</br>
	 * 1 __a___a</br>
	 * 2 ___a_a_</br>
	 * 3 _aaaXaa</br>
	 * 4 ___aaa_</br>
	 * 5 __a_a_a</br>
	 * . 0123456</br>
	 * </code>
	 */
	public void performNeighborhoodIteration(//
			IterationConsumer consumer, //
			int fieldH, int fieldW, //
			int rowIdx, int columnIdx //
	) {
		int i, j, iEndExcl, jEndExcl;

		consumer.iterationStarted(fieldW, fieldH);

		// horizontally
		consumer.rowStarted();
		i = rowIdx;
		j = Math.max(columnIdx - 3, 0);
		jEndExcl = Math.min(columnIdx + 4, fieldW);
		for (; j < jEndExcl; j++) {
			if (consumer.nextLocation(i, j)) {
				consumer.iterationEnded();
				return;
			}
		}

		// vertically
		consumer.rowStarted();
		i = rowIdx;
		j = columnIdx;
		iEndExcl = Math.min(rowIdx + 4, fieldH);
		for (; i < iEndExcl; i++) {
			if (consumer.nextLocation(i, j)) {
				consumer.iterationEnded();
				return;
			}
		}

		// diagonally in /-direction (bottom left to top right)
		consumer.rowStarted();
		i = rowIdx + 3;
		j = columnIdx - 3;
		if (i > fieldH - 1) {
			j += i - (fieldH - 1);
			i = fieldH - 1;
		}
		if (j < 0) {
			i -= -j;
			j = 0;
		}
		iEndExcl = Math.max(rowIdx - 4, -1);
		jEndExcl = Math.min(columnIdx + 4, fieldW);
		while (i > iEndExcl && j < jEndExcl) {
			if (consumer.nextLocation(i, j)) {
				consumer.iterationEnded();
				return;
			}

			i--;
			j++;
		}

		// diagonally in \-direction (top left to bottom right)
		consumer.rowStarted();
		i = rowIdx - 3;
		j = columnIdx - 3;
		if (i < 0) {
			j += -i;
			i = 0;
		}
		if (j < 0) {
			i += -j;
			j = 0;
		}
		iEndExcl = Math.min(rowIdx + 4, fieldH);
		jEndExcl = Math.min(columnIdx + 4, fieldW);
		while (i < iEndExcl && j < jEndExcl) {
			if (consumer.nextLocation(i, j)) {
				consumer.iterationEnded();
				return;
			}

			i++;
			j++;
		}

		consumer.iterationEnded();
	}

	/**
	 * Lead the specified iteration consumer through an iteration over the whole
	 * field by calling the consumer's methods.
	 */
	public void performFullIteration(IterationConsumer consumer, int fieldH, int fieldW) {
		consumer.iterationStarted(fieldH, fieldW);

		// horizontally, left to right
		for (int i = 0; i < fieldH; i++) {
			consumer.rowStarted();

			for (int j = 0; j < fieldW; j++) {
				if (consumer.nextLocation(i, j)) {
					consumer.iterationEnded();
					return;
				}
			}
		}

		// vertically, top to bottom
		for (int j = 0; j < fieldW; j++) {
			consumer.rowStarted();

			for (int i = 0; i < fieldH; i++) {
				if (consumer.nextLocation(i, j)) {
					consumer.iterationEnded();
					return;
				}
			}
		}

		// diagonals in /-direction (lower left to upper right)
		// start at top left, diagonal 3 (stone top left corner is diagonal 0)
		for (int d = 3; d < fieldW + fieldH - 1 - 3; d++) {
			consumer.rowStarted();

			int i = d;
			int j = 0;
			if (i > fieldH - 1) {
				j = i - (fieldH - 1);
				i = fieldH - 1;
			}

			while (i >= 0 && j < fieldW) {
				if (consumer.nextLocation(i, j)) {
					consumer.iterationEnded();
					return;
				}

				i--;
				j++;
			}
		}

		// diagonals in \-direction (upper left to lower right)
		// start at bottom left diagonal
		for (int d = 3; d < fieldH + fieldW - 1 - 3; d++) {
			consumer.rowStarted();

			int i = fieldH - 1 - d;
			int j = 0;
			if (i < 0) {
				j = -i;
				i = 0;
			}

			while (i < fieldH && j < fieldW) {
				if (consumer.nextLocation(i, j)) {
					consumer.iterationEnded();
					return;
				}

				i++;
				j++;
			}
		}

		consumer.iterationEnded();
	}

	/**
	 * The iterateField(...) method allows to iterate over a field in all ways how
	 * consecutive rows of 4 or more stones can be placed. This is a basis for
	 * various specific algorithms like checking for a winner or promising moves.
	 */
	public static interface IterationConsumer {

		public void iterationStarted(int fieldH, int fieldW);

		public void rowStarted();

		/**
		 * Returns true if iteration shall be aborted, false to go on.
		 */
		public boolean nextLocation(int rowIdx, int columnIdx);

		public void iterationEnded();

	}
}

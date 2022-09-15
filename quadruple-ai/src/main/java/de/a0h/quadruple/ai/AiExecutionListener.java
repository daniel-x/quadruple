package de.a0h.quadruple.ai;

public interface AiExecutionListener {

	public void aiMoveSearchStarted(Ai ai);

	public void aiMoveSearchProgressed(Ai ai, int i, int j);

	public void aiMoveSearchCancelled(Ai ai);

	public void aiMoveSearchFinished(Ai ai);

}

package it.polimi.db2.mission.exceptions;

public class NotEnoughBudget extends Exception {
	private static final long serialVersionUID = 1L;

	public NotEnoughBudget(String message) {
		super(message);
	}
}
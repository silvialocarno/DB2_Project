package it.polimi.db2.mission.entities;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;

/**
 * The persistent class for the expenses database table.
 * 
 */

// test 12343
@Entity
@Table(name = "expenses", schema = "db_expense_management")
@NamedQuery(name = "Expense.findAll", query = "SELECT e FROM Expense e")
public class Expense implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private BigDecimal accomodation;

	private BigDecimal food;

	private BigDecimal transport;

	// Bi-directional one-to-one association to Mission
	@OneToOne
	@JoinColumn(name = "mission")
	private Mission mission;

	public Expense() {
	}

	public Expense(BigDecimal f, BigDecimal a, BigDecimal t) {
		this.food = f;
		this.accomodation = a;
		this.transport = t;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigDecimal getAccomodation() {
		return this.accomodation;
	}

	public void setAccomodation(BigDecimal accomodation) {
		this.accomodation = accomodation;
	}

	public BigDecimal getFood() {
		return this.food;
	}

	public void setFood(BigDecimal food) {
		this.food = food;
	}

	public BigDecimal getTransport() {
		return this.transport;
	}

	public void setTransport(BigDecimal transport) {
		this.transport = transport;
	}

	public Mission getMission() {
		return this.mission;
	}

	public void setMission(Mission mission) {
		/*
		 * for debugging and transaction tracing only
		 * System.out.println("Method setMission of Expense Entity");
		 * JPATxUtils.printTxId(); // prints the JTA hash of the transaction object
		 * JPATxUtils.printTxStatus(); // prints the JTA status of the transaction
		 */
		this.mission = mission;
	}

}
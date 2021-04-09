package it.polimi.db2.mission.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the mission database table.
 * 
 */
@Entity
@Table(name = "mission", schema = "db_expense_management")
@NamedQueries({ @NamedQuery(name = "Mission.findAll", query = "SELECT m FROM Mission m"),
		@NamedQuery(name = "Mission.findByUser", query = "Select m FROM Mission m WHERE m.reporter.id = :repId ORDER BY m.date DESC") })

public class Mission implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Temporal(TemporalType.DATE)
	private Date date;

	private int days;

	private String description;

	private String destination;

	private MissionStatus status;

	// Bi-directional one-to-one association to Expense
	@OneToOne(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
	private Expense expense;

	// Bi-directional many-to-one association to Reporter. Mission is the owner
	// entity
	@ManyToOne
	@JoinColumn(name = "reporter")
	private User reporter;

	// Bi-directional many-to-one association to Project. Mission is the owner
	// entity
	@ManyToOne
	@JoinColumn(name = "project")
	private Project project;

	public Mission() {
	}

	public Mission(Date startDate, int days, String destination, String description, User reporter, Project prj) {
		this.date = startDate;
		this.days = days;
		this.destination = destination;
		this.description = description;
		this.reporter = reporter;
		this.project = prj;
		this.status = MissionStatus.OPEN;
	}

	public Mission(Date startDate, int days, String destination, String description, User reporter) {
		this.date = startDate;
		this.days = days;
		this.destination = destination;
		this.description = description;
		this.reporter = reporter;
		this.status = MissionStatus.OPEN;
	}

	
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getDays() {
		return this.days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDestination() {
		return this.destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public MissionStatus getStatus() {
		return this.status;
	}

	public void setStatus(MissionStatus status) {
		/*
		 * for debugging System.out.println("Method setStatus of mission entity");
		 * JPATxUtils.printTxId(); JPATxUtils.printTxStatus();
		 */
		this.status = status;

	}

	public Expense getExpense() {
		return this.expense;
	}

	public void setExpense(Expense expense) {
		/*
		 * for debugging System.out.println("Method setExpenses of mission entity");
		 * JPATxUtils.printTxId(); JPATxUtils.printTxStatus();
		 */
		this.expense = expense;
		expense.setMission(this); // updates the inverse relationship too. Alternatively alignment can be done by
									// the client
	}

	public User getReporter() {
		return this.reporter;
	}

	public void setReporter(User reporter) {
		this.reporter = reporter;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project prj) {
		this.project = prj;
	}

	public Boolean isOpen() {
		return (status == MissionStatus.OPEN);
	}

	public Boolean isClosed() {
		return (status == MissionStatus.CLOSED);
	}

	public Boolean isReported() {
		return (status == MissionStatus.REPORTED);
	}
}
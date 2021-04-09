package it.polimi.db2.mission.entities;

import java.io.Serializable;

import javax.persistence.*;
//import it.polimi.db2.mission.services.TxUtils;
import java.math.BigDecimal;
import java.util.List;
import java.lang.String;

/**
 * The persistent class for the projects database table.
 * 
 */

@Entity
@Table(name = "projects", schema="db_expense_management")
@NamedQueries({ @NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p"),
		@NamedQuery(name = "Project.findByName", query = "SELECT p FROM Project p WHERE P.name = :name") })

public class Project implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	private BigDecimal budget;

	// Bidirectional many-to-one association to Mission
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project", cascade = CascadeType.ALL)
	private List<Mission> missions;

	public Project() {
	}

	public Project(BigDecimal b, String n) {
		this.budget = b;
		this.name = n;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String n) {
		this.name = n;
	}

	public BigDecimal getBudget() {
		return this.budget;
	}

	public void setBudget(BigDecimal bdg) {
		this.budget = bdg;
	}

	public List<Mission> getMissions() {
		return this.missions;
	}

	public void addMission(Mission mission) {
		getMissions().add(mission);
		mission.setProject(this);
		// aligns both sides of the relationship
		// if mission is new, invoking persist() on reporter cascades also to mission
	}

	public void removeMission(Mission mission) {
		getMissions().remove(mission);
	}

}
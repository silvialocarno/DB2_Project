package it.polimi.db2.mission.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the usertable database table.
 * 
 */
@Entity
@Table(name = "usertable", schema = "db_expense_management")
@NamedQuery(name = "User.checkCredentials", query = "SELECT r FROM User r  WHERE r.username = ?1 and r.password = ?2")

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String name;

	private String password;

	private String surname;

	private String username;

	// Bidirectional many-to-one association to Mission
	/*
	 * Fetch type EAGER allows resorting the relationship list content also in the
	 * client Web servlet after the creation of a new mission. If you leave the
	 * default LAZY policy, the relationship is sorted only at the first access but
	 * then adding a new mission does not trigger the reloading of data from the
	 * database and thus the sort method in the client does not actually re-sort the
	 * list of missions. MERGE is not cascaded because we will modify and merge only
	 * username and surname attributes of the user and do not want to cascade
	 * detached changes to relationship.
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "reporter", cascade = { CascadeType.PERSIST, CascadeType.REMOVE,
			CascadeType.REFRESH })
	@OrderBy("date DESC")
	private List<Mission> missions;

	public User() {
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

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Mission> getMissions() {
		return this.missions;
	}

	public void addMission(Mission mission) {
		getMissions().add(mission);
		mission.setReporter(this);
		// aligns both sides of the relationship
		// if mission is new, invoking persist() on user cascades also to mission
	}

	public void removeMission(Mission mission) {
		getMissions().remove(mission);
	}

}
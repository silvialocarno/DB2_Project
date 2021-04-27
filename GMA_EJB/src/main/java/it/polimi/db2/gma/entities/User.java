package it.polimi.db2.gma.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * The persistent class for the usertable database table.
 * 
 */
@Entity
@Table(name = "user", schema = "db_gamified_marketing_application")
@NamedQuery(name = "User.checkCredentials", query = "SELECT u FROM User u  WHERE u.username = ?1 and u.password = ?2")

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int user_id;

	private String username;

	private String password;

	private String email;

	private Boolean blocked;

	private Boolean admin;

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getBlocked() {
		return blocked;
	}

	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
	private List<Access> accesses;

	@OneToMany(mappedBy = "user")
	private List<Review> reviews;

	public List<Access> getAccesses() {
		return accesses;
	}

	public void setAccesses(List<Access> accesses) {
		this.accesses = accesses;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
}
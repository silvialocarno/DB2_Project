package it.polimi.db2.gma.entities;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the expenses database table.
 * 
 */

@Entity
@Table(name = "product", schema = "db_gamified_marketing_application")

public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int product_id;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] photo;

	private String name;

	public int getProduct_id() {
		return product_id;
	}

	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	@OneToMany(mappedBy = "product")
	private List<Review> reviews;

	@OneToMany(mappedBy = "product")
	private List<Questionnaire> questionnaires;

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}

	public List<Questionnaire> getQuestionnaires() {
		return questionnaires;
	}

	public void setQuestionnaires(List<Questionnaire> questionnaires) {
		this.questionnaires = questionnaires;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public String getPhotoData() {
		return Base64.getMimeEncoder().encodeToString(photo);
	}

	public void setPhoto(byte[] photo) {
		this.photo= photo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
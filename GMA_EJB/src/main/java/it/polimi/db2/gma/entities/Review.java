package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.PK.QuestionnairePK;
import it.polimi.db2.gma.entities.PK.ReviewPK;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the expenses database table.
 *
 */

@Entity
@Table(name = "review", schema = "db_gamified_marketing_application")

public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ReviewPK id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product")
    private Product product;

    private String text;

    public ReviewPK getReviewId() {
        return id;
    }

    public void setReviewId(ReviewPK id) {
        this.id = id;
    }

    public User getReviewUser() {
        return user;
    }

    public void setReviewUser(User user) {
        this.user = user;
    }

    public Product getReviewProduct() {
        return product;
    }

    public void setReviewProduct(Product product) {
        this.product = product;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}


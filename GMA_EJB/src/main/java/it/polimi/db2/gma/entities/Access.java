package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.PK.AccessPK;
import it.polimi.db2.gma.entities.PK.QuestionnairePK;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;

/**
 * The persistent class for the expenses database table.
 *
 */

@Entity
@Table(name = "access", schema = "db_gamified_marketing_application")

public class Access implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int access_id;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public int getAccess_id() {
        return access_id;
    }

    public void setAccess_id(int access_id) {
        this.access_id = access_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
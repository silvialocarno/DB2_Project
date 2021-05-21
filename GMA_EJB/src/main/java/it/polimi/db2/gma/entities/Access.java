package it.polimi.db2.gma.entities;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "access", schema = "db_gamified_marketing_application")
@NamedQuery(name = "Access.getUserAccessOfToday", query = "SELECT a FROM Access a  WHERE a.timestamp >= ?1 AND a.timestamp < ?2 AND a.user.user_id = ?3 ")
public class Access implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int access_id;

    @ManyToOne(fetch = FetchType.LAZY) //We never need to get user of a specific access
    @JoinColumn(name = "user")
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public Access() {

    }

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

    public Access(User user, Date timestamp) {
        this.user = user;
        this.timestamp = timestamp;
    }
}
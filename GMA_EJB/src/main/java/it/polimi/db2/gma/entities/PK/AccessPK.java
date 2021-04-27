package it.polimi.db2.gma.entities.PK;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AccessPK implements Serializable {

    @Column(name = "access_id")
    private int accessId;

    @Column(name = "user")
    private int userId;

    public AccessPK() {
    }

    public int getAccessId() {
        return accessId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessPK accessPK = (AccessPK) o;
        return accessId == accessPK.accessId && userId == accessPK.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessId, userId);
    }
}

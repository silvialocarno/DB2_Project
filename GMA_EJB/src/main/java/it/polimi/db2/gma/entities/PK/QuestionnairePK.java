package it.polimi.db2.gma.entities.PK;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuestionnairePK implements Serializable {

    @Column(name = "user")
    private int userId;

    @Column(name = "product")
    private int productId;

    public QuestionnairePK() {
    }

    public int getUserId() {
        return userId;
    }

    public int getProductId() {
        return productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionnairePK that = (QuestionnairePK) o;
        return userId == that.userId && productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productId);
    }
}

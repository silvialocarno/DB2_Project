package it.polimi.db2.gma.entities.PK;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LeaderboardPK implements Serializable {

    @Column(name = "user")
    private int userId;

    @Column(name = "questionnaire")
    private int questionnaireId;

    public LeaderboardPK() {
    }

    public int getUserId() {
        return userId;
    }

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaderboardPK that = (LeaderboardPK) o;
        return userId == that.userId && questionnaireId == that.questionnaireId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, questionnaireId);
    }
}

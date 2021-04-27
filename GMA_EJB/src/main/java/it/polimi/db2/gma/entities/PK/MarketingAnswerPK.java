package it.polimi.db2.gma.entities.PK;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MarketingAnswerPK implements Serializable {

    @Column(name = "user")
    private int userId;

    @Column(name = "question")
    private int questionId;

    @Column(name = "questionnaire")
    private int questionnaireId;

    public MarketingAnswerPK() {
    }

    public MarketingAnswerPK(int userId, int questionId, int questionnaireId) {
        this.userId = userId;
        this.questionId = questionId;
        this.questionnaireId = questionnaireId;
    }

    public int getUserId() {
        return userId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketingAnswerPK marketingAnswerPK = (MarketingAnswerPK) o;
        return userId == marketingAnswerPK.userId && questionId == marketingAnswerPK.questionId && questionnaireId == marketingAnswerPK.questionnaireId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, questionId, questionnaireId);
    }
}

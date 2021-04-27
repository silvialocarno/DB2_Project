package it.polimi.db2.gma.entities.PK;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuestionPK implements Serializable {

    @Column(name = "question_id")
    private int questionId;

    @Column(name = "questionnaire")
    private int questionnaireId;

    public QuestionPK() {
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
        QuestionPK that = (QuestionPK) o;
        return questionId == that.questionId && questionnaireId == that.questionnaireId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, questionnaireId);
    }
}

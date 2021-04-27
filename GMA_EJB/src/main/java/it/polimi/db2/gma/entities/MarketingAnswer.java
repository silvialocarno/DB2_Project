package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.PK.MarketingAnswerPK;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "marketing_answer", schema = "db_gamified_marketing_application")
public class MarketingAnswer implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private MarketingAnswerPK id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user")
    private User user;

    @MapsId("questionId")
    @Column(name = "question_id")
    private int questionId;

    @ManyToOne
    @MapsId("questionnaireId")
    @JoinColumn(name = "questionnaire")
    private Questionnaire questionnaire;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "questionnaire", referencedColumnName = "questionnaire", insertable = false, updatable = false),
            @JoinColumn(name = "question_id", referencedColumnName = "question_id", insertable = false, updatable = false)
    })
    private Question question;

    public MarketingAnswer(User user, Question question, String answer_text) {
        this.id = new MarketingAnswerPK(user.getUser_id(), question.getQuestionId(), questionnaire.getQuestionnaire_id());
        this.user = user;
        this.questionId = question.getQuestionId();
        this.questionnaire = question.getQuestionnaire();
        this.answer_text = answer_text;
    }

    private String answer_text;

    public MarketingAnswer() {

    }

    public MarketingAnswerPK getAnswerId() {
        return id;
    }

    public void setAnswerId(MarketingAnswerPK id) {
        this.id = id;
    }

    public User getAnswerUser() {
        return user;
    }

    public void setAnswerUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getAnswer_text() {
        return answer_text;
    }

    public void setAnswer_text(String answer_text) {
        this.answer_text = answer_text;
    }
}
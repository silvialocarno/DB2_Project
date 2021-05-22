package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.PK.MarketingAnswerPK;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name = "marketing_answer", schema = "db_gamified_marketing_application")
@NamedQuery(name = "MarketingAnswer.findAllAnswers", query = "SELECT a FROM MarketingAnswer a WHERE a.id.questionnaireId=?1")
public class MarketingAnswer implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private MarketingAnswerPK id;

    @ManyToOne //We need to retrieve the user for the admin inspection
    @MapsId("userId")
    @JoinColumn(name = "user")
    private User user;

    @MapsId("questionId")
    @Column(name = "question_id", insertable = false, updatable = false)
    private int questionId;

    @ManyToOne(fetch = FetchType.LAZY) //We go from the questionnaire to the answers and not viceversa
    @MapsId("questionnaireId")
    @JoinColumn(name = "questionnaire", insertable = false, updatable = false)
    private Questionnaire questionnaire;

    @ManyToOne(cascade = CascadeType.ALL) //From the answer we get the question in the inspection
    @JoinColumns({
            @JoinColumn(name = "questionnaire", referencedColumnName = "questionnaire"),
            @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    })
    private Question question;

    public MarketingAnswer(User user, Question question, String answer_text) {
        this.id = new MarketingAnswerPK(user.getUser_id(), question.getQuestionId(), question.getQuestionnaire().getQuestionnaire_id());
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
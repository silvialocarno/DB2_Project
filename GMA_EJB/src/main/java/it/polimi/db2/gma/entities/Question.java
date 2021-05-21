package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.PK.QuestionPK;

import java.io.Serializable;

import javax.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "question", schema = "db_gamified_marketing_application")
public class Question implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private QuestionPK id;

	@MapsId("questionId")
	@Column(name = "question_id", nullable = false, insertable = false, updatable = false)
	private int questionId;

	@ManyToOne(fetch = FetchType.LAZY) //We don't need to get the questionnaire from the questions
	@MapsId("questionnaireId")
	@JoinColumn(name = "questionnaire")
	private Questionnaire questionnaire;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)  //Same as above
	@JoinColumns({
		@JoinColumn(name = "questionnaire", referencedColumnName = "questionnaire", insertable = false, updatable = false),
		@JoinColumn(name = "question_id", referencedColumnName = "question_id", insertable = false, updatable = false),
	})
	private Set<MarketingAnswer> marketingAnswers = new HashSet<>();

	public Question() {
	}

	private String question_text;

    public Question(Questionnaire questionnaire, String question_text, int i) {
    	this.question_text = question_text;
    	this.questionnaire = questionnaire;
		this.id = new QuestionPK(questionnaire.getQuestionnaire_id(), i);
    }

    public QuestionPK getQuestionPK() {
		return id;
	}

	public void setQuestionPK(QuestionPK id) {
		this.id = id;
	}

	public String getQuestion_text() {
		return question_text;
	}

	public void setQuestion_text(String question_text) {
		this.question_text = question_text;
	}

	public Set<MarketingAnswer> getMarketing_answers() {
		return marketingAnswers;
	}

	public void setMarketing_answers(Set<MarketingAnswer> marketingAnswers) {
		this.marketingAnswers = marketingAnswers;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int question) {
		this.questionId = question;
	}

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

	public void addAnswer(MarketingAnswer marketingAnswer) {
		marketingAnswers.add(marketingAnswer);
		marketingAnswer.setQuestion(this);
	}
}
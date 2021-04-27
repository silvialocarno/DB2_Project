package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.PK.QuestionPK;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;

/**
 * The persistent class for the expenses database table.
 *
 */

@Entity
@Table(name = "question", schema = "db_gamified_marketing_application")
public class Question implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private QuestionPK id;

	@MapsId("questionId")
	@Column(name = "question_id", nullable = false)
	private int questionId;

	@ManyToOne
	@MapsId("questionnaireId")
	@JoinColumn(name = "questionnaire")
	private Questionnaire questionnaire;

	@OneToMany(mappedBy = "question")
	private List<MarketingAnswer> marketingAnswers;

	private String question_text;

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

	public List<MarketingAnswer> getMarketing_answers() {
		return marketingAnswers;
	}

	public void setMarketing_answers(List<MarketingAnswer> marketingAnswers) {
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
package it.polimi.db2.gma.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "questionnaire", schema = "db_gamified_marketing_application")
@NamedQuery(name = "Questionnaire.getQuestOfTheDay", query = "SELECT q FROM Questionnaire q WHERE q.date = CURRENT_DATE ")
public class Questionnaire implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionnaire_id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product product;

    @OneToMany(mappedBy = "questionnaire")
    private List<Question> questions;

    @OneToMany(mappedBy = "questionnaire")
    private List<StatisticalAnswer> statisticalAnswers;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(int questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addStatisticalAnswer(StatisticalAnswer statisticalAnswer) {
        statisticalAnswers.add(statisticalAnswer);
        statisticalAnswer.setStatisticalAnswerQuestionnaire(this);
    }
}
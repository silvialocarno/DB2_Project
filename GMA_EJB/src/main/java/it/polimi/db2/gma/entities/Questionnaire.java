package it.polimi.db2.gma.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "questionnaire", schema = "db_gamified_marketing_application")
@NamedQuery(name = "Questionnaire.getQuestOfTheDay", query = "SELECT q FROM Questionnaire q WHERE q.date = CURRENT_DATE ")
@NamedQuery(name = "Questionnaire.findAllPastQuest", query = "SELECT q FROM Questionnaire q WHERE q.date < CURRENT_DATE")
@NamedQuery(name = "Questionnaire.getQuestOfOneDay", query = "SELECT q FROM Questionnaire q WHERE q.date = ?1")
public class Questionnaire implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionnaire_id;

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) //Required to display the product associated to the questionnaire of the day.
    @JoinColumn(name = "product")
    private Product product;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "questionnaire", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "questionnaire", cascade = CascadeType.ALL, orphanRemoval = true) //Load Questionnaire Data Servlet
    private List<StatisticalAnswer> statisticalAnswers;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "questionnaire", cascade = CascadeType.REMOVE, orphanRemoval = true) //Leaderboard html
    @OrderBy("score DESC")
    private List<Score> leaderboard;

    public Questionnaire(){

    }

    public Questionnaire(Date date) {
        this.date = date;
    }

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

    public List<StatisticalAnswer> getStatisticalAnswers() {
        return statisticalAnswers;
    }

    public void setStatisticalAnswers(List<StatisticalAnswer> statisticalAnswers) {
        this.statisticalAnswers = statisticalAnswers;
    }

    public List<Score> getLeaderboard() {
        return leaderboard;
    }

    public void setLeaderboard(List<Score> leaderboard) {
        this.leaderboard = leaderboard;
    }

    public void addStatisticalAnswer(StatisticalAnswer statisticalAnswer) {
        statisticalAnswers.add(statisticalAnswer);
        statisticalAnswer.setStatisticalAnswerQuestionnaire(this);
    }

    public void addProduct(Product product) {
        this.product= product;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }
}
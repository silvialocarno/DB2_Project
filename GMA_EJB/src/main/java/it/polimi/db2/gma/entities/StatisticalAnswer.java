package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.Enum.Expertise_level;
import it.polimi.db2.gma.entities.Enum.Sex;
import it.polimi.db2.gma.entities.PK.StatisticalAnswerPK;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "leaderboard", schema = "db_gamified_marketing_application")
@NamedQuery(name = "Leaderboard.checkSubmissionByUser", query = "SELECT l.user FROM Leaderboard l WHERE l.questionnaire.date = CURRENT_DATE and l.user= ?1")
public class StatisticalAnswer implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private StatisticalAnswerPK id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("questionnaireId")
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private Expertise_level expertise_level;

    private int age;

    public StatisticalAnswer(User user, Sex sex, int age, Expertise_level expertise_level, Questionnaire questionnaire) {
        this.id = new StatisticalAnswerPK(user.getUser_id(), questionnaire.getQuestionnaire_id());
        this.user = user;
        this.sex = sex;
        this.age = age;
        this.expertise_level = expertise_level;
        this.questionnaire = questionnaire;
    }

    public StatisticalAnswer() {

    }

    public StatisticalAnswerPK getStatisticalAnswerId() {
        return id;
    }

    public void setStatisticalAnswerId(StatisticalAnswerPK id) {
        this.id = id;
    }

    public User getStatisticalAnswerUser() {
        return user;
    }

    public void setStatisticalAnswerUser(User user) {
        this.user = user;
    }

    public Questionnaire getStatisticalAnswerQuestionnaire() {
        return questionnaire;
    }

    public void setStatisticalAnswerQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Expertise_level getExpertise_level() {
        return expertise_level;
    }

    public void setExpertise_level(Expertise_level expertise_level) {
        this.expertise_level = expertise_level;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
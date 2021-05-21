package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.Enum.Expertise_level;
import it.polimi.db2.gma.entities.Enum.Sex;
import it.polimi.db2.gma.entities.PK.StatisticalAnswerPK;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "statistical_answer", schema = "db_gamified_marketing_application")
@NamedQuery(name = "StatisticalAnswer.findAllAnswers", query = "SELECT a FROM StatisticalAnswer a WHERE a.id.questionnaireId=?1")
public class StatisticalAnswer implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private StatisticalAnswerPK id;

    @ManyToOne //EAGER, questionnaireDetails html
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) //We never get the questionnaire from the statistical answer
    @MapsId("questionnaireId")
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private Expertise_level expertise_level;

    private Integer age;

    public StatisticalAnswer(User user, Sex sex, Integer age, Expertise_level expertise_level, Questionnaire questionnaire) {
        this.id = new StatisticalAnswerPK(user.getUser_id(), questionnaire.getQuestionnaire_id());
        this.user = user;
        this.sex = sex;
        this.age = age;
        this.expertise_level = expertise_level;
        this.questionnaire = questionnaire;
    }

    public StatisticalAnswer() {

    }

    public User getAnswerUser() {
        return user;
    }

    public void setAnswerUser(User user) {
        this.user = user;
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
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

    public String getSex() {
        if(sex == null) {
            return "";
        }
        else
            return sex.toString();
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getExpertise_level() {
        if(expertise_level == null) {
            return "";
        }
        else
            return expertise_level.toString();
    }

    public void setExpertise_level(Expertise_level expertise_level) {
        this.expertise_level = expertise_level;
    }

    public String getAge() {
        if(age == null) {
            return "";
        }
        else
            return age.toString();
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.Enum.Sex;
import it.polimi.db2.gma.entities.PK.LeaderboardPK;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "leaderboard", schema = "db_gamified_marketing_application")
@NamedQuery(name = "Leaderboard.checkSubmissionByUser", query = "SELECT l.user FROM Leaderboard l WHERE l.questionnaire.date = CURRENT_DATE and l.user= ?1")
public class Leaderboard implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private LeaderboardPK id;

    private int score;

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

    public LeaderboardPK getLeaderboardId() {
        return id;
    }

    public void setLeaderboardId(LeaderboardPK id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public User getLeaderboardUser() {
        return user;
    }

    public void setLeaderboardUser(User user) {
        this.user = user;
    }

    public Questionnaire getLeaderboardQuestionnaire() {
        return questionnaire;
    }

    public void setLeaderboardQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }
}
package it.polimi.db2.gma.entities;

import it.polimi.db2.gma.entities.Enum.Sex;
import it.polimi.db2.gma.entities.PK.LeaderboardPK;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "score", schema = "db_gamified_marketing_application")
@NamedQuery(name = "Score.checkSubmissionByUser", query = "SELECT s.user FROM Score s WHERE s.questionnaire.date = CURRENT_DATE and s.user= ?1")
public class Score implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private LeaderboardPK id;

    private int score;

    @ManyToOne //EAGER Html leaderboard
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("questionnaireId")
    @JoinColumn(name = "questionnaire_id")
    private Questionnaire questionnaire;

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
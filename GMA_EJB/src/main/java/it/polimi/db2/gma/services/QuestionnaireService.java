package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.*;
import it.polimi.db2.gma.entities.Enum.Expertise_level;
import it.polimi.db2.gma.entities.Enum.Sex;
import it.polimi.db2.gma.exceptions.QuestionnaireException;
import it.polimi.db2.gma.exceptions.UserException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;

@Stateless
public class QuestionnaireService {
	@PersistenceContext(unitName = "GMA_EJB")
	private EntityManager em;

	public QuestionnaireService() {
	}

	public Questionnaire getQuestOfTheDay () throws QuestionnaireException {
		List<Questionnaire> qList = null;
		try {
			qList = em.createNamedQuery("Questionnaire.getQuestOfTheDay", Questionnaire.class)
					.getResultList();
		} catch (PersistenceException e) {
			throw new QuestionnaireException("Could not load the questionnaire");
		}
		if (qList.isEmpty())
			return null;
		else
			return qList.get(0);

	}

	public Boolean checkSubmissionByUser (User u) throws UserException {
		List<User> uList = null;
		try {
			uList = em.createNamedQuery("Leaderboard.checkSubmissionByUser", User.class).setParameter(1, u)
					.getResultList();
		} catch (PersistenceException e) {
			throw new UserException("Could not load the users");
		}
		if(uList.isEmpty())
			return false;
		else
			return true;
	}

	public void compileQuestionnaire (int age, Sex sex, Expertise_level expertise_level, List<String> answers, int userId, int questionnaireId) {
		User user = em.find(User.class, userId);
		Questionnaire questionnaire = em.find(Questionnaire.class, questionnaireId);
		if (questionnaire == null)
			System.out.print("IL QUESTIONARIO E' NULL");
		for(Question q : questionnaire.getQuestions()) {
			MarketingAnswer marketingAnswer = new MarketingAnswer(user, q, answers.get(questionnaire.getQuestions().indexOf(q)));
			q.addAnswer(marketingAnswer);
			em.persist(q);
		}

		StatisticalAnswer statisticalAnswer = new StatisticalAnswer(user, sex, age, expertise_level, questionnaire);
		questionnaire.addStatisticalAnswer(statisticalAnswer);
		em.persist(questionnaire);
	}

	public Questionnaire findQuestionnaireById (int questionnaireId){
		return em.find(Questionnaire.class, questionnaireId);
	}
}

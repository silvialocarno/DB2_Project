package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.*;
import it.polimi.db2.gma.entities.Enum.Expertise_level;
import it.polimi.db2.gma.entities.Enum.Sex;
import it.polimi.db2.gma.exceptions.AccessException;
import it.polimi.db2.gma.exceptions.QuestionnaireException;
import it.polimi.db2.gma.exceptions.UserException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.Date;
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
			qList = em.createNamedQuery("Questionnaire.getQuestOfTheDay", Questionnaire.class).setHint("javax.persistence.cache.storeMode", "REFRESH")
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
			uList = em.createNamedQuery("Score.checkSubmissionByUser", User.class).setParameter(1, u)
					.getResultList();
		} catch (PersistenceException e) {
			throw new UserException("Could not load the users");
		}
		if(uList.isEmpty())
			return false;
		else
			return true;
	}

	public void compileQuestionnaire (Integer age, Sex sex, Expertise_level expertise_level, List<String> answers, int userId, int questionnaireId) {
		User user = em.find(User.class, userId);
		Questionnaire questionnaire = em.find(Questionnaire.class, questionnaireId);

		for(Question q : questionnaire.getQuestions()) {
			MarketingAnswer marketingAnswer = new MarketingAnswer(user, q, answers.get(questionnaire.getQuestions().indexOf(q)));
			q.addAnswer(marketingAnswer);
		}

		StatisticalAnswer statisticalAnswer = new StatisticalAnswer(user, sex, age, expertise_level, questionnaire);
		questionnaire.addStatisticalAnswer(statisticalAnswer);
		em.merge(questionnaire);
	}


	public Questionnaire findQuestionnaireById (int questionnaireId){
		return em.find(Questionnaire.class, questionnaireId);
	}

    public void createQuestionnaire(int product_id, List<String> questions, Date day) {
		Questionnaire questionnaire = new Questionnaire(day);
		em.persist(questionnaire);

		Product product = em.find(Product.class, product_id);
		questionnaire.addProduct(product);

		for(int i=0; i<questions.size(); i++){
			System.out.println("Testo della domanda:" + questions.get(i));
			Question question = new Question(questionnaire, questions.get(i), i);
			questionnaire.addQuestion(question);
		}
		em.flush();
    }

    public List<Questionnaire> findAllPastQuestionnaires(){
		return em.createNamedQuery("Questionnaire.findAllPastQuest", Questionnaire.class).getResultList();
	}


    public List<MarketingAnswer> findAllMarketingAnswers(int questionnaireId){
		return em.createNamedQuery("MarketingAnswer.findAllAnswers", MarketingAnswer.class).setParameter(1, questionnaireId).getResultList();
	}

    public List<StatisticalAnswer> findAllStatisticalAnswers(Integer questionnaireId) {
		return em.createNamedQuery("StatisticalAnswer.findAllAnswers", StatisticalAnswer.class).setParameter(1, questionnaireId).getResultList();
    }

	public void deleteQuestionnaire(Integer questionnaireId) {
		Questionnaire q = em.find(Questionnaire.class, questionnaireId);
		if (q != null) {
			em.remove(q);
		}
	}

    public boolean getQuestOfOneDay(Date date) throws QuestionnaireException {
		Questionnaire questionnaire = null;
		try {
			questionnaire = em.createNamedQuery("Questionnaire.getQuestOfOneDay", Questionnaire.class).setParameter(1, date).getSingleResult();
		} catch (NoResultException e) {
			questionnaire = null;
		} catch (PersistenceException e) {
			throw new QuestionnaireException("Could not load questionnaire");
		}
		return questionnaire != null;
    }
}

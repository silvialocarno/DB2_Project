package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.*;
import it.polimi.db2.gma.exceptions.OffensiveWordException;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Locale;

@Stateless
public class OffensiveWordService {
	@PersistenceContext(unitName = "GMA_EJB")
	private EntityManager em;

	public OffensiveWordService() {
	}

	public boolean checkOffensiveWord(List<String> marketingAnswers) throws OffensiveWordException {
		List<Offensive_word> offensive_words = null;
		try {
			offensive_words = em.createNamedQuery("Offensive_word.findAll", Offensive_word.class).getResultList();
		} catch (PersistenceException e) {
			throw new OffensiveWordException("Could not load the word");
		}
		for(int i=0; i<offensive_words.size(); i++){
			String w = offensive_words.get(i).getWord();
			for(int j=0; j<marketingAnswers.size(); j++){
				String[] words = marketingAnswers.get(j).toLowerCase(Locale.ROOT).replaceAll("[^a-zA-Z ]", "").split(" ");
				for (String word: words) {
					if(word.equals(offensive_words.get(i).getWord()))
						return true;
				}
			}
		}
		return false;
	}
}

package it.polimi.db2.gma.services;

import javax.ejb.Stateless;
import javax.persistence.*;

import it.polimi.db2.gma.entities.Access;
import it.polimi.db2.gma.entities.Questionnaire;
import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.exceptions.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Stateless
public class UserService {
	@PersistenceContext(unitName = "GMA_EJB")
	private EntityManager em;

	public UserService() {
	}

	public User checkCredentials(String usrn, String pwd) throws CredentialsException, NonUniqueResultException {
		List<User> uList = null;
		try {
			uList = em.createNamedQuery("User.checkCredentials", User.class).setParameter(1, usrn).setParameter(2, pwd)
					.getResultList();
		} catch (PersistenceException e) {
			throw new CredentialsException("Could not verify credentals");
		}
		if (uList.isEmpty())
			return null;
		else if (uList.size() == 1)
			return uList.get(0);
		throw new NonUniqueResultException("More than one user registered with same credentials");

	}

    public void registerAccess(User user) throws AccessException {
		Access access;
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);
		try {
			access = em.createNamedQuery("Access.getUserAccessOfToday", Access.class).setParameter(1, java.sql.Date.valueOf(today)).setParameter(2, java.sql.Date.valueOf(tomorrow)).setParameter(3, user.getUser_id())
					.getSingleResult();
		} catch (NoResultException e) {
			access = null;
		} catch (PersistenceException e) {
			throw new AccessException("Could not load access");
		}

		if (access == null) {
			access = new Access(user, new Date());
			user.addAccess(access);
			em.persist(access);
		}
		else {
			access.setTimestamp(new Date());
			em.merge(access);
		}
	}

	public void deleteAccess(User user) throws AccessException {
		Access access;
		LocalDate today = LocalDate.now();
		LocalDate tomorrow = today.plusDays(1);
		try {
			access = em.createNamedQuery("Access.getUserAccessOfToday", Access.class).setParameter(1, java.sql.Date.valueOf(today)).setParameter(2, java.sql.Date.valueOf(tomorrow)).setParameter(3, user.getUser_id())
					.getSingleResult();
		} catch (NoResultException e) {
			access = null;
		} catch (PersistenceException e) {
			throw new AccessException("Could not load access");
		}
		user.deleteAccess(access);
		em.remove(access);
	}

    public void updateUser(User user) {
		em.merge(user);
    }

	public List<User> findAllUsers(){
		return em.createNamedQuery("User.findAll", User.class).getResultList();
	}

	public List<User> getCancelUser(Date day) {
		LocalDate localday = day.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate tomorrow = localday.plusDays(1);
		return em.createNamedQuery("User.getCancelUser", User.class).setParameter(1, java.sql.Date.valueOf(localday)).setParameter(2,java.sql.Date.valueOf(tomorrow)).getResultList();
	}
}

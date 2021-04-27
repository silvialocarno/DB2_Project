package it.polimi.db2.gma.services;

import java.util.List;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.NonUniqueResultException;

import it.polimi.db2.gma.exceptions.*;
import it.polimi.db2.gma.entities.User;

@Stateless
public class MissionService {
	@PersistenceContext(unitName = "MissionExpensesEJB")
	private EntityManager em;
	@EJB(name = "it.polimi.db2.mission.services/MySqlTxUtils")
	private MySqlTxUtils util;

	public MissionService() {
	}

	// If a mission is deleted by a concurrent transaction this method retrieves it
	// from the cache. If a mission is deleted by the JPA application, the
	// persistence context evicts it, this method no longer
	// retrieves it, and relationship sorting by the client works
	public List<it.polimi.db2.mission.entities.Marketing_answer> findMissionsByUser(int userId) {
		User reporter = em.find(User.class, userId);
		List<it.polimi.db2.mission.entities.Marketing_answer> marketinganswers = reporter.getMissions();
		return marketinganswers;
	}

	public List<it.polimi.db2.mission.entities.Marketing_answer> findMissionsByUserRefresh(int userId) {
		User reporter = em.find(User.class, userId);
		em.refresh(reporter);
		List<it.polimi.db2.mission.entities.Marketing_answer> marketinganswers = reporter.getMissions();
		return marketinganswers;
	}

	// If a mission is deleted by a concurrent transaction this method
	// bypasses the cache and sees the correct list. Sorting is done by the query
	public List<it.polimi.db2.mission.entities.Marketing_answer> findMissionsByUserNoCache(int userId) {
		List<it.polimi.db2.mission.entities.Marketing_answer> marketinganswers = em
				.createQuery("Select m from MarketingAnswer m where m.reporter.id = :repId ORDER BY m.date DESC", it.polimi.db2.mission.entities.Marketing_answer.class)
				.setHint("javax.persistence.cache.storeMode", "REFRESH").setParameter("repId", userId).getResultList();

		return marketinganswers;
	}

	// If a mission is deleted by a concurrent transaction this method
	// bypasses the cache and sees the correct list. Sorting is done by the query
	public List<it.polimi.db2.mission.entities.Marketing_answer> findMissionsByUserJPQL(int userId) {
		List<it.polimi.db2.mission.entities.Marketing_answer> marketinganswers = em
				.createQuery("Select m from MarketingAnswer m where m.reporter.id = :repId ORDER BY m.date DESC", it.polimi.db2.mission.entities.Marketing_answer.class)
				.setParameter("repId", userId).getResultList();

		return marketinganswers;
	}

	public it.polimi.db2.mission.entities.Marketing_answer findMissionById(int missionId) {
		it.polimi.db2.mission.entities.Marketing_answer marketinganswer = em.find(it.polimi.db2.mission.entities.Marketing_answer.class, missionId);
		return marketinganswer;
	}

	public void createMission(Date startDate, int days, String destination, String description, int reporterId,
			int projectId) {
		User reporter = em.find(User.class, reporterId);
		it.polimi.db2.mission.entities.Questionnarie prj = em.find(it.polimi.db2.mission.entities.Questionnarie.class, projectId);
		it.polimi.db2.mission.entities.Marketing_answer marketinganswer = new it.polimi.db2.mission.entities.Marketing_answer(startDate, days, destination, description, reporter, prj);
		// for debugging: let's check if mission is managed
		System.out.println("Method createMission before reporter.addMission(mission)");
		System.out.println("Is mission object managed?  " + em.contains(marketinganswer));

		reporter.addMission(marketinganswer); // updates both sides of the relationship
		
		System.out.println("Method createMission AFTER reporter.addMission(mission)");
		System.out.println("Is mission object managed?  " + em.contains(marketinganswer));

		
		em.persist(reporter); // makes also mission object managed via cascading
		
		System.out.println("Method createMission after em.persist()");
		System.out.println("Is mission object managed?  " + em.contains(marketinganswer));

	}

// only for testing horizontal propagation
	public it.polimi.db2.mission.entities.Marketing_answer findUnassignedMission() throws NonUniqueResultException {
		System.out.println("Entering method findUnassignedMission");
		List<it.polimi.db2.mission.entities.Marketing_answer> marketinganswers = null;
		marketinganswers = em.createQuery("Select m from MarketingAnswer m where m.project is null", it.polimi.db2.mission.entities.Marketing_answer.class).getResultList();
		if (marketinganswers.isEmpty())
			return null;
		else if (marketinganswers.size() == 1) {
			marketinganswers.get(0).setDescription("Unassigned Mission");
			JPATxUtils.printTxId(); // Prints the JTA high level transaction hash
			JPATxUtils.printTxStatus(); // Prints the JTA high level transaction status
			return marketinganswers.get(0);
		}
		throw new NonUniqueResultException("More than one test mission without project");
	}

	public void closeMission(int missionId, int reporterId) throws BadMissionReporter, BadMissionForClosing {
		it.polimi.db2.mission.entities.Marketing_answer marketinganswer = em.find(it.polimi.db2.mission.entities.Marketing_answer.class, missionId);
		if (marketinganswer.getReporter().getId() != reporterId) {
			throw new BadMissionReporter("Reporter not authorized to close this mission");
		}

		if (marketinganswer.getStatus() != it.polimi.db2.mission.entities.Question.REPORTED) {
			throw new BadMissionForClosing("Not possible to close a non reported mission");
		}

		marketinganswer.setStatus(it.polimi.db2.mission.entities.Question.CLOSED);
		// for debugging: let's check if mission is managed
		System.out.println("Method closeMission");
		System.out.println("Is mission object managed?  " + em.contains(marketinganswer));
		// em.persist(mission); unnecessary, mission is already managed

	}

	public void deleteMission(int missionId, int reporterId) throws BadMissionReporter {
		it.polimi.db2.mission.entities.Marketing_answer marketinganswer = em.find(it.polimi.db2.mission.entities.Marketing_answer.class, missionId);
		User owner = em.find(User.class, reporterId);
		it.polimi.db2.mission.entities.Questionnarie prj = marketinganswer.getProject();
		if (marketinganswer.getReporter() != owner) {
			throw new BadMissionReporter("Reporter not authorized to delete this mission");
		}
		owner.removeMission(marketinganswer); // this updates both directions of the associations
		prj.removeMission(marketinganswer);
		em.remove(marketinganswer);
	}

	/*
	 * If the reportMission() method is called by the CreateExpenseReportWrong
	 * client servlet, the status of the mission is updated to REPORTED in a
	 * separate and independent transaction with respect to the addition of the
	 * expense report, which violates atomicity of the operation
	 */

	public void reportMission(int missionId, int reporterId)
			throws BadMissionReporter, BadMissionForExpReport, InvalidStatusChange {
		System.out.println("Entering reportMission() method of MissionService component");

		it.polimi.db2.mission.entities.Marketing_answer marketinganswer = null;
		try {
			marketinganswer = em.find(it.polimi.db2.mission.entities.Marketing_answer.class, missionId);
		} catch (PersistenceException e) {
			throw new BadMissionForExpReport("Could not fetch the mission");
		}

		if (marketinganswer.getReporter().getId() != reporterId) {
			throw new BadMissionReporter("Reporter not authorized to report this mission");
		}
		if (marketinganswer.getStatus() != it.polimi.db2.mission.entities.Question.OPEN) {
			throw new BadMissionForExpReport("Mission not open");
		}

		System.out.println("Method reportMission: Setting the mission status to REPORTED");

		marketinganswer.setStatus(it.polimi.db2.mission.entities.Question.REPORTED); // this could be encapsulated into a method

		util.printMySQLTxStatus();
		JPATxUtils.printTxId();
		JPATxUtils.printTxStatus();

		try {
			em.flush(); // ensures status updated in the database before expenseReport addition
		} catch (PersistenceException e) {
			throw new InvalidStatusChange("Status update to REPORTED failed");
		}
		System.out.println("Exiting reportMission() method of MissionService component");

	}

}

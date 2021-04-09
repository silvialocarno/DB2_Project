package it.polimi.db2.mission.services;

import java.util.List;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.NonUniqueResultException;

import it.polimi.db2.mission.exceptions.*;
import it.polimi.db2.mission.entities.Mission;
import it.polimi.db2.mission.entities.MissionStatus;
import it.polimi.db2.mission.entities.User;
import it.polimi.db2.mission.entities.Project;

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
	public List<Mission> findMissionsByUser(int userId) {
		User reporter = em.find(User.class, userId);
		List<Mission> missions = reporter.getMissions();
		return missions;
	}

	public List<Mission> findMissionsByUserRefresh(int userId) {
		User reporter = em.find(User.class, userId);
		em.refresh(reporter);
		List<Mission> missions = reporter.getMissions();
		return missions;
	}

	// If a mission is deleted by a concurrent transaction this method
	// bypasses the cache and sees the correct list. Sorting is done by the query
	public List<Mission> findMissionsByUserNoCache(int userId) {
		List<Mission> missions = em
				.createQuery("Select m from Mission m where m.reporter.id = :repId ORDER BY m.date DESC", Mission.class)
				.setHint("javax.persistence.cache.storeMode", "REFRESH").setParameter("repId", userId).getResultList();

		return missions;
	}

	// If a mission is deleted by a concurrent transaction this method
	// bypasses the cache and sees the correct list. Sorting is done by the query
	public List<Mission> findMissionsByUserJPQL(int userId) {
		List<Mission> missions = em
				.createQuery("Select m from Mission m where m.reporter.id = :repId ORDER BY m.date DESC", Mission.class)
				.setParameter("repId", userId).getResultList();

		return missions;
	}

	public Mission findMissionById(int missionId) {
		Mission mission = em.find(Mission.class, missionId);
		return mission;
	}

	public void createMission(Date startDate, int days, String destination, String description, int reporterId,
			int projectId) {
		User reporter = em.find(User.class, reporterId);
		Project prj = em.find(Project.class, projectId);
		Mission mission = new Mission(startDate, days, destination, description, reporter, prj);
		// for debugging: let's check if mission is managed
		System.out.println("Method createMission before reporter.addMission(mission)");
		System.out.println("Is mission object managed?  " + em.contains(mission));

		reporter.addMission(mission); // updates both sides of the relationship
		
		System.out.println("Method createMission AFTER reporter.addMission(mission)");
		System.out.println("Is mission object managed?  " + em.contains(mission));

		
		em.persist(reporter); // makes also mission object managed via cascading
		
		System.out.println("Method createMission after em.persist()");
		System.out.println("Is mission object managed?  " + em.contains(mission));

	}

// only for testing horizontal propagation
	public Mission findUnassignedMission() throws NonUniqueResultException {
		System.out.println("Entering method findUnassignedMission");
		List<Mission> missions = null;
		missions = em.createQuery("Select m from Mission m where m.project is null", Mission.class).getResultList();
		if (missions.isEmpty())
			return null;
		else if (missions.size() == 1) {
			missions.get(0).setDescription("Unassigned Mission");
			JPATxUtils.printTxId(); // Prints the JTA high level transaction hash
			JPATxUtils.printTxStatus(); // Prints the JTA high level transaction status
			return missions.get(0);
		}
		throw new NonUniqueResultException("More than one test mission without project");
	}

	public void closeMission(int missionId, int reporterId) throws BadMissionReporter, BadMissionForClosing {
		Mission mission = em.find(Mission.class, missionId);
		if (mission.getReporter().getId() != reporterId) {
			throw new BadMissionReporter("Reporter not authorized to close this mission");
		}

		if (mission.getStatus() != MissionStatus.REPORTED) {
			throw new BadMissionForClosing("Not possible to close a non reported mission");
		}

		mission.setStatus(MissionStatus.CLOSED);
		// for debugging: let's check if mission is managed
		System.out.println("Method closeMission");
		System.out.println("Is mission object managed?  " + em.contains(mission));
		// em.persist(mission); unnecessary, mission is already managed

	}

	public void deleteMission(int missionId, int reporterId) throws BadMissionReporter {
		Mission mission = em.find(Mission.class, missionId);
		User owner = em.find(User.class, reporterId);
		Project prj = mission.getProject();
		if (mission.getReporter() != owner) {
			throw new BadMissionReporter("Reporter not authorized to delete this mission");
		}
		owner.removeMission(mission); // this updates both directions of the associations
		prj.removeMission(mission);
		em.remove(mission);
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

		Mission mission = null;
		try {
			mission = em.find(Mission.class, missionId);
		} catch (PersistenceException e) {
			throw new BadMissionForExpReport("Could not fetch the mission");
		}

		if (mission.getReporter().getId() != reporterId) {
			throw new BadMissionReporter("Reporter not authorized to report this mission");
		}
		if (mission.getStatus() != MissionStatus.OPEN) {
			throw new BadMissionForExpReport("Mission not open");
		}

		System.out.println("Method reportMission: Setting the mission status to REPORTED");

		mission.setStatus(MissionStatus.REPORTED); // this could be encapsulated into a method

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

package it.polimi.db2.mission.services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import it.polimi.db2.mission.exceptions.ProjectException;

import it.polimi.db2.mission.entities.Project;
import it.polimi.db2.mission.entities.Expense;
import it.polimi.db2.mission.entities.Mission;

import java.util.List;
import java.math.BigDecimal;

@Stateless
public class ProjectService {
	// For debugging: used to print the native transaction Id of MySQL
	@EJB(name = "it.polimi.db2.mission.services/MySqlTxUtils")
	private MySqlTxUtils util;
	@PersistenceContext(unitName = "MissionExpensesEJB")
	private EntityManager em;

	public ProjectService() {
	}

	public List<Project> findAllProjects() throws ProjectException {
		List<Project> projects = null;
		try {
			projects = em.createNamedQuery("Project.findAll", Project.class).getResultList();

		} catch (PersistenceException e) {
			throw new ProjectException("Cannot load projects");

		}
		return projects;
	}

	public void updateBudget(Expense e, Project p) {
		/*
		 * This method is called by another service (ExpenseReportService) in method
		 * addExpenseReport(). We track the low-level TX id to show how methods of
		 * different components use the same low-level MySql transaction
		 */
		System.out.println("Entering method updateBudget of ProjectService EJB");
		JPATxUtils.printTxId(); // Prints the JTA high level transaction hash
		JPATxUtils.printTxStatus(); // Prints the JTA high level transaction status

		BigDecimal availability = p.getBudget().subtract(e.getAccomodation()).subtract(e.getTransport())
				.subtract(e.getFood());

		/*
		 * util.printMySQLTxStatus(); // Prints the low level MySQL transaction id and
		 * info. However, note that the use of a (native) query made by util.print
		 * forces the write of entity changes to the database so that the new expense
		 * object is written to the database and becomes managed even before calling
		 * em.persist(). This is due to the fact that the new expense has been connected
		 * via setExpense() to the mission object, which is managed.
		 * https://www.eclipse.org/eclipselink/api/2.6/javax/persistence/FlushModeType.
		 * html. Uncomment the instruction to see the Tx Id in MySQL
		 */
		/*
		 * This update may violate a database constraint. Note that in a real
		 * application the business component would ALSO check that the available budget
		 * is sufficient. In this case we demonstrate the roll-back of a transaction
		 * spanning multiple business components: ProjectService and
		 * ExpenseReportService
		 */
		e.getMission().getProject().setBudget(availability);

	}

	public void associateMission(int projectId, int missionId) {
		System.out.println("Entering method associateMission");
		Mission m = em.find(Mission.class, missionId);
		/*
		 * Refresh aligns the mission to the current database state. This allows you to
		 * repeat the test multiple time by setting to null the project of a mission in
		 * the MySQL workbench. Without refreshing the Persistence Context will continue
		 * to consider that the project with id 1 is associated to the mission.
		 */
		em.refresh(m);
		System.out.println("Mission id in method associateMission is " + missionId);
		System.out.println("Mission description in method associateMission is " + m.getDescription());
		Project p = em.find(Project.class, projectId);
		String s = (m == null) ? "Mission is null" : "Mission Id  is " + m.getId();
		System.out.println("Mission Id in method associateMission is " + s);
		System.out.println("Project in method associateMission is " + p.getId());
		if (m != null) {
			m.setDescription("Unassigned mission fixed");
			m.setProject(p);
		}
		JPATxUtils.printTxId(); // Prints the JTA high level transaction hash
		JPATxUtils.printTxStatus(); // Prints the JTA high level transaction status
	}
}
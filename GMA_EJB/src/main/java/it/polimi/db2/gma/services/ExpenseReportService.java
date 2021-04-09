package it.polimi.db2.mission.services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import it.polimi.db2.mission.exceptions.*;
import it.polimi.db2.mission.entities.Mission;
import it.polimi.db2.mission.entities.MissionStatus;
import it.polimi.db2.mission.entities.Expense;

@Stateless
public class ExpenseReportService {
	@PersistenceContext(unitName = "MissionExpensesEJB")
	private EntityManager em;
	@EJB(name = "it.polimi.db2.mission.services/MySqlTxUtils")
	private MySqlTxUtils util;
	// A separate business component is used to show transaction propagation
	@EJB(name = "it.polimi.db2.mission.services/ProjectService")
	private ProjectService prjService;

	public ExpenseReportService() {
	}

	public Expense findExpensesForMission(int missionId) {
		Expense expenseReport = em.find(Mission.class, missionId).getExpense();
		return expenseReport;
	}

	public void addExpenseReport(Expense expenseReport, int missionId, int reporterId)
			throws BadMissionForExpReport, InvalidExpenseReport, BadMissionReporter, NotEnoughBudget {
		// Check that the mission exists and is in OPEN state

		Mission mission = null;
		try {
			mission = em.find(Mission.class, missionId);
		} catch (PersistenceException e) {
			throw new BadMissionForExpReport("Failed to fetch mission");
		} // now mission is managed

		if (mission == null | mission.getStatus() != MissionStatus.OPEN) {
			throw new BadMissionForExpReport("Mission not found or in wrong status");
		}
		// Check that the user is the owner of the mission
		if (mission.getReporter().getId() != reporterId) {
			throw new BadMissionReporter("Reporter not authorized to add expense report");
		}
		try {
			/*
			 * THESE METHOD CALLS ARE PART OF THE TRANSACTION STARTED BY THE
			 * addExpenseReport METHOD CALL AND ARE EXECUTED ATOMICALLY
			 */

			// for debugging
			System.out.println("Method addExpenseReport of ExpenseReportService EJB");
			JPATxUtils.printTxId(); // This prints the high level Id and status of the JTA transaction
			JPATxUtils.printTxStatus();

			/*
			 * The setter methods of the mission managed object execute within the current
			 * persistence context associated to the Entity Manager injected into the
			 * ExpenseReportService business component
			 */

			mission.setStatus(MissionStatus.REPORTED);
			mission.setExpense(expenseReport);
			/*
			 * To have a valid, not null transaction Id in MySQl there must be some updates,
			 * so we query the transaction status after we have made some updates to managed
			 * objects
			 */

			//util.printMySQLTxStatus();
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


			/*
			 * The transaction and transaction-scoped persistence context associated to the
			 * invocation of the addExpenseReport method are propagated to the
			 * updateBudget() method call of the ProjectService EJB component (even if such
			 * service has its own EM, which gets bound to the same Persistence Unit)
			 */
			prjService.updateBudget(expenseReport, mission.getProject());

			// for debugging, let's check if expenseReport is managed
			System.out.println("addExpenseReport method");
			System.out.println("Expense report object BEFORE persist(mission) in the managed state? "
					+ em.contains(expenseReport));

			// expenseReport.setMission(mission); not needed see Mission.setExpense()
			em.persist(mission); // this makes the unmanaged expense report managed via cascading

			// for debugging, let's check if expenseReport is managed
			System.out.println("addExpenseReport method");
			System.out.println(
					"Expense report object AFTER persist(mission) in the managed state? " + em.contains(expenseReport));

			em.flush(); // this is needed to check the DB constraints immediately
			// for debugging
			System.out.println("Exiting method addExpenseReport of ExpenseReportService EJB");
			JPATxUtils.printTxId();
			JPATxUtils.printTxStatus();
		} catch (PersistenceException e) {
			// e.printStackTrace(); used for debugging
			// for debugging
			System.out.println("Method addExpenseReport of ExpenseReportService EJB");
			JPATxUtils.printTxId();
			JPATxUtils.printTxStatus(); // if a constraint is violated STATUS_MARKED_ROLLBACK
			throw new InvalidExpenseReport("Invalid expense report");
		}
	}

	/*
	 * THIS METHOD IS ADDED TO SHOW THE SEPARATE UPDATE OF THE STATUS AND OF THE
	 * EXPENSES MADE IN TWO INDEPENDENT SERVICE METHOD CALLS AND THUS IN TWO
	 * DISTINCT TRANSACTIONS.
	 */

	public void addExpenseReportWrong(Expense expenseReport, int missionId, int reporterId)
			throws BadMissionForExpReport, InvalidExpenseReport, BadMissionReporter, NotEnoughBudget {
		System.out.println("Entering method addExpenseReportWrong of ExpenseReportService EJB");

		Mission mission = null;
		try {
			mission = em.find(Mission.class, missionId);
		} catch (PersistenceException e) {
			throw new BadMissionForExpReport("Failed to fetch mission");
		}

		/*
		 * IN THIS (WRONG) VERSION THE STATUS IS MODIFIED IN AN INDEPENDENT TRANSACTION
		 * OUTSIDE THIS METHOD BEFORE THE EXPENSES ARE ADDED, SO HERE WE HAVE TO TEST
		 * THAT THE MISSION IS IN THE (WRONG) REPORTED STATE INSTEAD OF THE OPEN STATE
		 */
		if (mission == null | mission.getStatus() != MissionStatus.REPORTED) {
			throw new BadMissionForExpReport("Mission not found or in wrong status");
		}
		// Check that the user is the owner of the mission
		if (mission.getReporter().getId() != reporterId) {
			throw new BadMissionReporter("Reporter not authorized to add expense report");
		}
		try {
			/*
			 * WE INTENTIONALLY OMIT ONE OF THE TRANSACTION OPERATIONS, WHICH IS DONE IN AN
			 * INDEPENDENT METHOD CALL BY THE CLIENT
			 * mission.setStatus(MissionStatus.REPORTED);
			 */

			// for debugging
			System.out.println("Inside method addExpenseReportWrong of ExpenseReportService EJB");
			JPATxUtils.printTxId(); // This prints the high level Id and status of the JTA transaction
			JPATxUtils.printTxStatus();

			mission.setExpense(expenseReport);

			util.printMySQLTxStatus();

			prjService.updateBudget(expenseReport, mission.getProject());

			em.persist(mission); // this makes the unmanaged expense report managed via cascading
			em.flush(); // this is needed to check the DB constraints immediately
			// for debugging
			System.out.println("Exiting method addExpenseReportWrong of ExpenseReportService EJB");
			JPATxUtils.printTxId();
			JPATxUtils.printTxStatus();
		} catch (PersistenceException e) {
			// e.printStackTrace(); used for debugging
			System.out.println("Method addExpenseReport of ExpenseReportService EJB");
			JPATxUtils.printTxId();
			JPATxUtils.printTxStatus(); // if a constraint is violated STATUS_MARKED_ROLLBACK
			throw new InvalidExpenseReport("Expense report invalid");
		}
		System.out.println("Exiting method addExpenseReportWrong of ExpenseReportService EJB");
	}

}

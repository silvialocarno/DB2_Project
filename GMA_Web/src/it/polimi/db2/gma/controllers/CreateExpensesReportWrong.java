package it.polimi.db2.controllers;

import java.io.IOException;
import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import it.polimi.db2.mission.services.MissionService;
import it.polimi.db2.mission.services.ExpenseReportService;
import it.polimi.db2.mission.entities.*;
import it.polimi.db2.mission.exceptions.InvalidExpenseReport;
import it.polimi.db2.mission.exceptions.BadMissionForExpReport;
import it.polimi.db2.mission.exceptions.BadMissionReporter;
import it.polimi.db2.mission.exceptions.NotEnoughBudget;

/* THIS IS A WRONG VERSION OF THE CONTROLLER TO SHOWCASE HOW TRANSACTIONS WORK
   ADD AN EXPENSE REPORT THAT VIOLATES THE DB CHECK CONSTRAINT (NOT ENOUGH BUDGET) 
   THE OPERATION WILL NOT BE ATOMIC AND THE MISSION STATUS WILL CHANGE EVEN IF THE REPORT IS INVALID 
   TO TEST THIS VERSION, CHANGE THE FORM ACTION IN THE MissionDetails.html TEMPLATE
*/

@WebServlet("/CreateExpensesReportWrong")
public class CreateExpensesReportWrong extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "it.polimi.db2.mission.services/ExpenseReportService")
	private ExpenseReportService expService;
	@EJB(name = "it.polimi.db2.mission.services/MissionService")
	private MissionService mService;

	public CreateExpensesReportWrong() {
		super();
	}

	public void init() throws ServletException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		// Check params are present and correct
		Expense expenseReport = null;
		Integer missionId = null;
		try {
			missionId = Integer.parseInt(request.getParameter("missionid"));
			double food = Double.parseDouble(request.getParameter("food"));
			double accomodation = Double.parseDouble(request.getParameter("accomodation"));
			double transportation = Double.parseDouble(request.getParameter("transportation"));
			if (food >= 0 && accomodation >= 0 && transportation >= 0) {
				expenseReport = new Expense(BigDecimal.valueOf(food), BigDecimal.valueOf(accomodation),
						BigDecimal.valueOf(transportation));
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
				return;
			}
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}
		// Execute controller logic
		User user = (User) session.getAttribute("user");
		Mission mission;
		try {
			mission = mService.findMissionById(missionId);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot fetch mission");
			return;
		}
		if (mission == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Mission not found");
			return;
		}

		/*
		 * HERE WE SPLIT THE UPDATE INTO TWO INDEPENDENT METHOD CALLS EACH METHOD CALL
		 * RUNS IN ITS OWN TRANSACTION ATOMICITY IS NOT ENFORCED FOR THE TWO UPDATES TO
		 * SEE THE EFFECT INSERT A REPORT WITH EXPENSES GREATER THAN BUDGET
		 */

		/*
		 * THIS STARTS T1 WHERE STATUS IS UPDATED IRRESPECTIVE OF THE BUDGET
		 */
		try {
			mService.reportMission(missionId, user.getId());
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Could not set the mission to REPORTED");
			return;
		}

		/*
		 * THIS STARTS T2 WHERE THE EXPENSE ARE ADDED AND THE BUDGET CHECKED
		 */
		try {
			expService.addExpenseReportWrong(expenseReport, missionId, user.getId());
		} catch (NotEnoughBudget e0) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Not Enough Budget");
			return;
		} catch (BadMissionReporter e1) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Not Authorized");
			return;
		} catch (BadMissionForExpReport e2) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Invalid Expense Report");
			return;
		} catch (InvalidExpenseReport e3) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Invalid Expense Report");
			return;
		}

		// Return view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetMissionDetails?missionid=" + missionId;
		response.sendRedirect(path);
	}

	public void destroy() {
	}

}

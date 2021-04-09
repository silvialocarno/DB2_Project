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


@WebServlet("/CreateExpensesReport")
public class CreateExpensesReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "it.polimi.db2.mission.services/ExpenseReportService")
	private ExpenseReportService expService;
	@EJB(name = "it.polimi.db2.mission.services/MissionService")
	private MissionService mService;

	public CreateExpensesReport() {
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

		// Check parameters are present and correct
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
		try {
			Mission mission = mService.findMissionById(missionId);
			if (mission == null) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Mission not found");
				return;
			}
			expService.addExpenseReport(expenseReport, missionId, user.getId());
		} catch (NotEnoughBudget e) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Not enough budget");
			return;
		} catch (BadMissionForExpReport e1) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Not allowed");
			return;
		} catch (InvalidExpenseReport e2) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Invalid Expense Report");
			return;
		} catch (BadMissionReporter e3) {
			response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Reporter not authorized");
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

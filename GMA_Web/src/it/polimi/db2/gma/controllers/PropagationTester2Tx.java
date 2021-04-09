package it.polimi.db2.controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.persistence.NonUniqueResultException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.db2.mission.entities.Mission;
import it.polimi.db2.mission.services.MissionService;
import it.polimi.db2.mission.services.ProjectService;

/**
 * You must run this servlet directly and check the log to see how transactions
 * have been created. To make the test, remove the not null constraint on the
 * project column of table mission and then insert in the DB one mission with
 * null project. In this test, the client calls TWO business components, which
 * makes the container create TWO distinct transactions.	
 */

@WebServlet("/PropagationTester2")
public class PropagationTester2Tx extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "it.polimi.db2.mission.services/ProjectService")
	private ProjectService prjService;
	@EJB(name = "it.polimi.db2.mission.services/MissionService")
	private MissionService mService;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PropagationTester2Tx() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Mission m = null;
		Integer missionId = null;
		try {
			m = mService.findUnassignedMission(); // call to the first method
			missionId = m.getId();

		} catch (NonUniqueResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		prjService.associateMission(1, missionId); // call to the second method

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

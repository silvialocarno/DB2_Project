package it.polimi.db2.controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * You must run this servlet directly and check the log to see how transactions
 * have been created. To make the test, remove the not null constraint on the
 * project column of table mission and then insert in the DB one mission with
 * null project. In this version of the test the Web client calls ONE business
 * component (HorizontalPropagationTesterService), which makes the container
 * create ONE transaction.
 */
@WebServlet("/PropagationTester")
public class PropagationTester1Tx extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "it.polimi.db2.mission.services/HorizontalPropagationTesterService")
	private HorizontalPropagationTesterService tService;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PropagationTester1Tx() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		tService.testPropagation();
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

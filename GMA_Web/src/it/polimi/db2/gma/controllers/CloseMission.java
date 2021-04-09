package it.polimi.db2.controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.db2.mission.services.MissionService;
import it.polimi.db2.mission.entities.*;
import it.polimi.db2.mission.exceptions.*;

@WebServlet("/CloseMission")
public class CloseMission extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "it.polimi.db2.mission.services/MissionService")
	private MissionService mService;

	public CloseMission() {
		super();
	}

	public void init() throws ServletException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// If the user is not logged in (not present in session) redirect to the login
		String loginpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

		// get and check params
		Integer missionId = null;
		try {
			missionId = Integer.parseInt(request.getParameter("missionid"));
		} catch (NumberFormatException | NullPointerException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		// Update mission status
		User user = (User) session.getAttribute("user");
		try {
			Mission mission = mService.findMissionById(missionId);
			if (mission == null) {
				response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, "Mission not found");
				return;
			}
			mService.closeMission(missionId, user.getId());
		} catch (BadMissionReporter e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Mission not closable");
			return;
		} catch (BadMissionForClosing e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Mission not closable");
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

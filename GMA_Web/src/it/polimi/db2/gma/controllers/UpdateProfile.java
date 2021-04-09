package it.polimi.db2.controllers;

import java.io.IOException;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.db2.mission.services.UserService;
import it.polimi.db2.mission.entities.User;

@WebServlet("/ChangeProfile")
public class UpdateProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@EJB(name = "it.polimi.db2.mission.services/UserService")
	private UserService usrService;

	public UpdateProfile() {
		super();
	}

	public void init() throws ServletException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// If the user is not logged in (not present in session) redirect to the login
		HttpSession session = request.getSession();
		User u = (User) session.getAttribute("user");
		if (session.isNew() || u == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}

		// Get and parse all parameters from request
		boolean isBadRequest = false;
		String name = null;
		String surname = null;
		try {
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));
			isBadRequest = name.isEmpty() || surname.isEmpty();
		} catch (NullPointerException e) {
			isBadRequest = true;
			// e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		if (isBadRequest) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		// Update user details
		String oldName = u.getName();
		String oldSurname = u.getSurname();
		try {
			if (u.getName() != name | u.getSurname() != surname) {
				if (u.getName() != name)
					u.setName(name);
				if (u.getSurname() != surname)
					u.setSurname(surname);
				usrService.updateProfile(u);
			}
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to update profile");
			u.setName(oldName);
			u.setSurname(oldSurname);
			return;
		}

		// return the user to the right view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Home";
		response.sendRedirect(path);
	}

	public void destroy() {
	}

}

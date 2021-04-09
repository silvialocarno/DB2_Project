package it.polimi.db2.controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.db2.mission.services.MissionService;
import it.polimi.db2.mission.entities.*;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/GetMissionDetails")
public class GetMissionDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.mission.services/MissionService")
	private MissionService mService;

	public GetMissionDetails() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		// If a mission with that ID exists for that USER,
		// obtain the expense report for it
		User user = (User) session.getAttribute("user");
		Expense expenses = null;
		Mission mission = null;
		try {
			mission = mService.findMissionById(missionId);
			if (mission == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found");
				return;
			}
			if (mission.getReporter().getId() != user.getId()) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not allowed");
				return;
			}
			expenses = mission.getExpense();
		} catch (Exception e) {
			e.printStackTrace();
			// response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not
			// possible to recover mission");
			return;
		}

		// Redirect to the Home page and add missions to the parameters
		String path = "/WEB-INF/MissionDetails.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("mission", mission);
		ctx.setVariable("expenses", expenses);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
	}

}

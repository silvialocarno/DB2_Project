package it.polimi.db2.controllers;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.mission.services.*;
import it.polimi.db2.mission.entities.*;

@WebServlet("/Home")
public class GoToHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.mission.services/MissionService")
	private MissionService mService;
	@EJB(name = "it.polimi.db2.mission.services/ProjectService")
	private ProjectService pService;

	public GoToHomePage() {
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

		User user = (User) session.getAttribute("user");
		List<Mission> missions = null;
		List<Project> projects = null;

		try {

			/*
			 * HERE WE SHOW SEVERAL WAYS TO DEAL WITH SECOND LEVEL CACHING, AKA SHARED
			 * CACHE. SHARED CACHE IS MAINTAINED BY THE ENTITY MANAGER FACTORY AND SERVES
			 * REQUESTS FROM MULTIPLE ENTITY MANAGERS. IF AN ENTITY IS DELETED OUTSIDE OF
			 * JPA THE SHARED CACHE MAY STILL SEE IT. THUS IF THE DATABASE IS ACCESSED ALSO
			 * BY OTHER NON JPA APPLICATIONS AND JPA LOCKING IS NOT USED, REFRESHING THE
			 * CACHE IS NEEDED TO SEE THE CURRENT DATABASE STATE. IF YOU WANT TO TEST THEM,
			 * CHANGE THE SERVICE METHOD USED AND LOGIN. THEN DELETE SOME MISSIONS WITH THE
			 * MYSQL WORKBENCH, LOGOUT AND LOGIN AGAIN TO SEE THE DIFFERENT BEHAVIORS WRT
			 * THE SHARED CACHE. WE ALSO SHOW USING THE PERSISTENCE CONTEXT AND THE
			 * RELATIONSHIP TO LIST THE MISSION OF THE USER. IN THIS CASE RESORTING IS DONE
			 * AT THE CLIENT AND NOT BY THE QUERY. PLAY WITH THE FETCH MODE OF THE
			 * RELATIONSHIP TO SEE WHAT HAPPENS TO SORTING AFTER YOU INSERT A NEW MISSION
			 */

			/*
			 * These versions uses a JPQ query, with or without hint, which is translated to
			 * / SQL and bypasses the shared cache. Sorting is done by the query.
			 */
			// missions = mService.findMissionsByUserJPQL(user.getId());
			// missions = mService.findMissionsByUserNoCache(user.getId());

			/*
			 * This version uses the relationship collection, fetched eagerly and resorted.
			 * It fetches missions from the shared cache (including ones deleted outside
			 * JPA)
			 */
			// missions = mService.findMissionsByUser(user.getId());
			// missions.sort(Comparator.comparing(Mission::getDate).reversed());

			/*
			 * This version uses the relationship collection and fetches missions from the
			 * shared cache. However it explicitly refreshes the status of the reporter from
			 * the database.  Collection resorting is done by the refresh of the reporter
			 */
			  missions = mService.findMissionsByUserRefresh(user.getId());
			
			projects = pService.findAllProjects();
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to get data");
			return;
		}

		// Redirect to the Home page and add missions to the parameters
		String path = "/WEB-INF/Home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("missions", missions);
		ctx.setVariable("projects", projects);

		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}

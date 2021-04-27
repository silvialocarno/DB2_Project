package it.polimi.db2.gma.controllers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.db2.gma.entities.Enum.Expertise_level;
import it.polimi.db2.gma.entities.Enum.Sex;
import it.polimi.db2.gma.entities.Question;
import it.polimi.db2.gma.entities.Questionnaire;
import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.services.QuestionnaireService;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@WebServlet("/CompileQuestionnaire")
public class CompileQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;

	public CompileQuestionnaire() {
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

		// Get and parse all parameters from request
		int age = 0;
		String sex_string = null;
		String expertise_level_string = null;
		List<String> answers = null;
		int questionnaireId = 0;
		Questionnaire questionnaire = null;
		Sex sex = null;
		Expertise_level expertise_level = null;
		try {
			if (request.getParameter("Age") != null){
			age = Integer.parseInt(request.getParameter("Age"));
				if(age < 16 || age > 100) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid age");
				return;
			}
			}
			if (request.getParameter("Sex") != null) {
				sex_string = StringEscapeUtils.escapeJava(request.getParameter("Sex"));
				sex = Sex.valueOf(sex_string);
			}
			if (request.getParameter("Expertise_Level") != null) {
				expertise_level_string = StringEscapeUtils.escapeJava(request.getParameter("Expertise_Level"));
				expertise_level = Expertise_level.valueOf(expertise_level_string);
			}
			questionnaireId = Integer.parseInt(request.getParameter("questionnaireId"));
			questionnaire = qService.findQuestionnaireById(questionnaireId);
			for (Question q : questionnaire.getQuestions()) {
				String answer = request.getParameter(String.valueOf(q.getQuestionId()));
				if(answer == null) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing answer for mandatory question");
					return;
				}
				answers.add(answer);
			}
		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
		}

		// Create mission in DB
		User user = (User) session.getAttribute("user");
		try {
			qService.compileQuestionnaire (age, sex, expertise_level, answers, user.getUser_id(), questionnaireId);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to compile the questionnaire");
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

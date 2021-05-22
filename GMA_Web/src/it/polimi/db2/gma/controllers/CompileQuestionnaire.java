package it.polimi.db2.gma.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
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
import it.polimi.db2.gma.exceptions.OffensiveWordException;
import it.polimi.db2.gma.services.OffensiveWordService;
import it.polimi.db2.gma.services.QuestionnaireService;
import it.polimi.db2.gma.services.UserService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.thymeleaf.TemplateEngine;

@WebServlet("/CompileQuestionnaire")
public class CompileQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "it.polimi.db2.gma.services/UserService")
	private UserService uService;
	@EJB(name = "it.polimi.db2.gma.services/OffensiveWordService")
	private OffensiveWordService wService;

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

		User user = (User) session.getAttribute("user");

		// If the user is the admin redirect to his Adminhome
		if(user.getAdmin()) {
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}

		// If the user is blocked redirect to home
		if(user.getBlocked()) {
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}

		// Get and parse all parameters from request
		Integer age = null;
		String sex_string = null;
		String expertise_level_string = null;
		List<String> answers = new ArrayList<>();
		int questionnaireId = 0;
		Questionnaire questionnaire = null;
		Sex sex = null;
		Expertise_level expertise_level = null;
		try {
			if (!StringUtils.isBlank(request.getParameter("Age"))) {
				age = Integer.parseInt(request.getParameter("Age"));
				if(age < 16 || age > 100) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid age");
					return;
				}
			}
			if (!StringUtils.isBlank(request.getParameter("Sex"))) {
				sex_string = StringEscapeUtils.escapeJava(request.getParameter("Sex"));
				sex = Sex.valueOf(sex_string);
			}
			if (!StringUtils.isBlank(request.getParameter("Expertise_Level"))) {
				expertise_level_string = StringEscapeUtils.escapeJava(request.getParameter("Expertise_Level").toUpperCase());
				expertise_level = Expertise_level.valueOf(expertise_level_string);
			}

			if(request.getParameter("questionnaireId") == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing questionnaire parameter");
				return;
			}
			questionnaireId = Integer.parseInt(request.getParameter("questionnaireId"));
			questionnaire = qService.findQuestionnaireById(questionnaireId);
			if(questionnaire == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid questionnaire id");
				return;
			}
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

		// Create answers in DB
		try {
			if(wService.checkOffensiveWord(answers)){
				user.setBlocked(true);
				uService.updateUser(user);
			}
			else {
				qService.compileQuestionnaire(age, sex, expertise_level, answers, user.getUser_id(), questionnaireId);
			}
			uService.deleteAccess(user);
		} catch (Exception | OffensiveWordException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to compile the questionnaire");
			return;
		}

		// return the user to the right view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/SubmissionPage";
		response.sendRedirect(path);

	}

	public void destroy() {
	}

}

package it.polimi.db2.gma.controllers;

import it.polimi.db2.gma.entities.Enum.Expertise_level;
import it.polimi.db2.gma.entities.Enum.Sex;
import it.polimi.db2.gma.entities.Question;
import it.polimi.db2.gma.entities.Questionnaire;
import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.exceptions.OffensiveWordException;
import it.polimi.db2.gma.exceptions.QuestionnaireException;
import it.polimi.db2.gma.services.OffensiveWordService;
import it.polimi.db2.gma.services.QuestionnaireService;
import it.polimi.db2.gma.services.UserService;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.thymeleaf.TemplateEngine;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/CreateQuestionnaire")
public class CreateQuestionnaireOLd extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;

	public CreateQuestionnaireOLd() {
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
		List<String> questions = new ArrayList<>();
		int product_id = 0;
		Date day = null;
		try {
			if (!StringUtils.isBlank(request.getParameter("productId"))) {
			product_id = Integer.parseInt(request.getParameter("productId"));
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			day = (Date) sdf.parse(request.getParameter("day"));
			int i = 1;
			while(!StringUtils.isBlank(request.getParameter("question" + i))) {
				questions.add(request.getParameter("question"+i));
				i++;
			}
		} catch (NumberFormatException | NullPointerException | ParseException e) {
			e.printStackTrace();
		}

		try {
			if(qService.getQuestOfOneDay(day)){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Already existing questionnaire of the day");
				return;
			} else{
				qService.createQuestionnaire(product_id, questions, day);
			}
		} catch (QuestionnaireException e) {
			e.printStackTrace();
		}


		// return the user to the right view
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Home";
		response.sendRedirect(path);

	}

	public void destroy() {
	}

}

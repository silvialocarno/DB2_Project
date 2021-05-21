package it.polimi.db2.gma.controllers;

import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.exceptions.QuestionnaireException;
import it.polimi.db2.gma.services.QuestionnaireService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet("/CreateQuestionnaire")
public class CreateQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;

	public CreateQuestionnaire() {
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

		if(!user.getAdmin()) {
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}

		// Get and parse all parameters from request
		String[] ques = request.getParameterValues("question[]");
		int product_id = 0;
		Date day = null;
		List<String> questions = new ArrayList<>(List.of(ques));
		try {
			if (!StringUtils.isBlank(request.getParameter("productId"))) {
				product_id = Integer.parseInt(request.getParameter("productId"));
			} else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid product id");
				return;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			day = (Date) sdf.parse(request.getParameter("day"));

			questions.removeIf(String::isEmpty);
			questions.forEach(StringEscapeUtils::escapeJava);
		} catch (NumberFormatException | NullPointerException | ParseException e) {
			e.printStackTrace();
		}

		try {
			if(qService.getQuestOfOneDay(day)) {
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

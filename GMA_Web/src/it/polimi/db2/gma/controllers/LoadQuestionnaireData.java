package it.polimi.db2.gma.controllers;

import it.polimi.db2.gma.entities.*;
import it.polimi.db2.gma.entities.utils.ImageUtils;
import it.polimi.db2.gma.exceptions.AccessException;
import it.polimi.db2.gma.exceptions.ProductException;
import it.polimi.db2.gma.exceptions.UserException;
import it.polimi.db2.gma.services.ProductService;
import it.polimi.db2.gma.services.QuestionnaireService;
import it.polimi.db2.gma.services.UserService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/QuestionnaireData")
public class LoadQuestionnaireData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "it.polimi.db2.gma.services/UserService")
	private UserService usrService;
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService pService;

	public LoadQuestionnaireData() {
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

		if(!user.getAdmin()) {
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}

		// get and check params
		Integer questionnaireId = null;
		try {
			questionnaireId = Integer.parseInt(request.getParameter("questionnaireid"));
		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}

		Questionnaire questionnaire = qService.findQuestionnaireById(questionnaireId);
		List<MarketingAnswer> marketingAnswers = new ArrayList<>();
		List<StatisticalAnswer> statisticalAnswers = new ArrayList<>();
		List<User> cancelUsers = new ArrayList<>();

		marketingAnswers = qService.findAllMarketingAnswers(questionnaireId);
		statisticalAnswers = qService.findAllStatisticalAnswers(questionnaireId);

		cancelUsers = usrService.getCancelUser(questionnaire.getDate());

		String path = "/WEB-INF/QuestionnaireDetails.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("marketingAnswers", marketingAnswers);
		ctx.setVariable("statisticalAnswers", statisticalAnswers);
		ctx.setVariable("cancelUsers", cancelUsers);
		ctx.setVariable("questionnaire", questionnaire);
		templateEngine.process(path, ctx, response.getWriter());
	}

	public void destroy() {
	}

}

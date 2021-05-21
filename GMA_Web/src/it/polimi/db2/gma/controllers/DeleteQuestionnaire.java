package it.polimi.db2.gma.controllers;

import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.services.QuestionnaireService;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Servlet implementation class DeleteAlbum
 */
@WebServlet("/DeleteQuestionnaire")
public class DeleteQuestionnaire extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @EJB(name = "it.polimi.db2.gma.services/QuestionnaireService")
    private QuestionnaireService qService;

    public DeleteQuestionnaire() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

        Integer questionnaireId = null;
        try {
            questionnaireId = Integer.parseInt(request.getParameter("questionnaireid"));
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Questionnaire parameters");
            return;
        }

        qService.deleteQuestionnaire(questionnaireId);
        String ctxpath = getServletContext().getContextPath();
        String path = ctxpath + "/Deletion";
        response.sendRedirect(path);
    }

}

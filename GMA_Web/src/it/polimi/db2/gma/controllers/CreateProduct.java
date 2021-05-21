package it.polimi.db2.gma.controllers;

import it.polimi.db2.gma.entities.User;
import it.polimi.db2.gma.entities.utils.ImageUtils;
import it.polimi.db2.gma.services.ProductService;
import org.thymeleaf.TemplateEngine;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.InputStream;

@WebServlet("/CreateProduct")
@MultipartConfig
public class CreateProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.gma.services/ProductService")
	private ProductService pService;

	public CreateProduct() {
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

		String name = request.getParameter("name");

		Part imgFile = request.getPart("picture");
		InputStream imgContent = imgFile.getInputStream();
		byte[] imgByteArray = ImageUtils.readImage(imgContent);

		if (name == null || name.isEmpty() || imgByteArray.length == 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing mandatory field of the form");
			return;
		}

		pService.createProduct(name, imgByteArray);

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/Creation";
		response.sendRedirect(path);

	}

	public void destroy() {
	}

}

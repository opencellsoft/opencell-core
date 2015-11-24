package org.meveo.api;

import io.swagger.models.ExternalDocs;
import io.swagger.models.Info;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * @author Edward P. Legaspi
 **/
public class SwaggerBootStrap extends HttpServlet {

	private static final long serialVersionUID = 5397415749526330764L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		Info info = new Info()
				.title("Opencell - Open Source Billing API")
				.description(
						"This is the Opencell Billing Integration Server. You can find out more about Opencell Billing at [https://www.assembla.com/spaces/meveo/](https://www.assembla.com/spaces/meveo/).");

		ServletContext context = config.getServletContext();
		Swagger swagger = new Swagger().info(info);
		swagger.externalDocs(new ExternalDocs("Find out more about Opencell Billing",
				"https://www.assembla.com/spaces/meveo"));

		swagger.tag(new Tag()
				.name("invoice")
				.description("Operation about invoice and report generation")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Invoicing")));
		swagger.tag(new Tag()
				.name("invoicing")
				.description("Manages billing run")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Invoicing")));

		context.setAttribute("swagger", swagger);
	}
}
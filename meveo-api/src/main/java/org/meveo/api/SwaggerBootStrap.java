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

		// settings
		swagger.tag(new Tag().name("provider").description("Operations about provider")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("user").description("Operations about user")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag()
				.name("seller")
				.description(
						"A seller is an entity that sells services. A seller has currency, country and language. This entity can have several hierarchies. For example a seller can sell its services to another seller and so on.")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("tradingLanguage").description("Operations about tradingLanguage")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("tradingCountry").description("Operations about tradingCountry")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("tradingCurrency").description("Operations about tradingCurrency")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag()
				.name("tax")
				.description(
						"Operations about tax. Tax is use when rating a charge. Tax and country are linked together when creating an invoice sub-category.")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("invoiceCategory").description("Operations about invoiceCategory")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("invoiceSubCategory").description("Operations about invoiceSubCategory")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("invoiceSubCategoryCountry")
				.description("Operations about invoiceSubCategoryCountry")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag()
				.name("calendar")
				.description(
						"A charged calendar has 3 types: CHARGED_IMPUTATION, BILLING and COUNTER. Charged imputation is use in recurring charge.")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag()
				.name("billingCycle")
				.description(
						"Billing cycle is use in billing account and is associated with a billing cycle type calendar")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("occTemplate").description("Operations about occ template")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("customFieldTemplate").description("Operations about custom field template")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("customEntityTemplate").description("Operations about custom entity template")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("customEntityInstance").description("Operations about custom entity instance")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("permission").description("Operations about permission")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));
		swagger.tag(new Tag().name("role").description("Operations about role")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Settings")));

		swagger.tag(new Tag()
				.name("subscription")
				.description("Operations about subscription")
				.externalDocs(
						new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Subscription")));

		swagger.tag(new Tag().name("wallet").description("Operations about wallet")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Wallet")));

		// catalog
		swagger.tag(new Tag()
				.name("counterTemplate")
				.description(
						"A counter can be data, duration, monetary and quantity. A good example on the types: data, duration, monetary, quantity")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Catalog")));
		swagger.tag(new Tag()
				.name("charges")
				.description(
						"Charges are what composed a service. A charge must always have a price plan with charge.code=pricePlan.eventCode")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Catalog")));
		swagger.tag(new Tag()
				.name("recurringChargeTemplate")
				.description(
						"Recurring charge is a charge that is applied on a regular basis as defined in the billing calendar=charged imputation. It could be monthly, quarterly or yearly. A recurring charged is also linked to a sub category. A sub category is the entity that linked the charge to tax")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Catalog")));
		swagger.tag(new Tag()
				.name("oneShotChargeTemplate")
				.description(
						"A one shot charge can be Subscription or Termination that is use when a user subscribe or terminate a service. It is also linked to an invoice sub-category")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Catalog")));
		swagger.tag(new Tag()
				.name("usageChargeTemplate")
				.description(
						"A good representation of a usage charge is the prepaid load. For example a customer has 100 load which is measured via counter, each time the customer sends an sms the counter is deducted by 1")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Catalog")));
		swagger.tag(new Tag().name("serviceTemplate").description("Operations about service template")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Catalog")));
		swagger.tag(new Tag().name("offerTemplate").description("Operations about offer template")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Catalog")));
		swagger.tag(new Tag()
				.name("pricePlan")
				.description(
						"A price plan is where we look-up for a charge price. We can restrict a price plan via country, currency and seller")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Catalog")));
		swagger.tag(new Tag().name("triggeredEdr").description("Operations about triggered edr")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Catalog")));

		swagger.tag(new Tag().name("customer").description("Operations about customer")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Accounts")));
		swagger.tag(new Tag().name("customerAccount").description("Operations about customer account")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Accounts")));
		swagger.tag(new Tag().name("billingAccount").description("Operations about billing account")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Accounts")));
		swagger.tag(new Tag().name("userAccount").description("Operations about user account")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Accounts")));
		swagger.tag(new Tag().name("access").description("Operations about access")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Accounts")));
		swagger.tag(new Tag()
				.name("accountHierarchy")
				.description(
						"<pre>"
								+ "This API manage the same set of account hierarchy defined above, the only difference is it creates the entire hierarchy in one service."
								+ "</pre>")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Accounts")));
		swagger.tag(new Tag()
				.name("customerHierarchy")
				.description(
						"What this service does: This service allows to create / update (if exist already) and close / terminate (if termination date is set) a list of customer, customer accounts, billing accounts, user accounts, subscriptions, services, and access in one transaction. It can activate and terminate subscription and service instance. Close customer account. Terminate billing and user account.")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Accounts")));
		swagger.tag(new Tag().name("accountOperation").description("Operations about account operation")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Accounts")));
		swagger.tag(new Tag().name("title").description("Operations about title")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Accounts")));

		swagger.tag(new Tag()
				.name("communication")
				.description("Operations about communication")
				.externalDocs(
						new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Communications")));

		swagger.tag(new Tag()
				.name("filteredList")
				.description("Operations about filtered list")
				.externalDocs(
						new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Filtered_List")));

		swagger.tag(new Tag().name("job").description("Operations about job")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Job")));

		swagger.tag(new Tag()
				.name("mediation")
				.description("Operations about mediation")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Mediation")));

		swagger.tag(new Tag().name("module").description("Operations about module")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Module")));

		swagger.tag(new Tag()
				.name("notification")
				.description("Operations about notification")
				.externalDocs(
						new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Notification")));
		swagger.tag(new Tag()
				.name("notif_webhook")
				.description("Operations about webhook")
				.externalDocs(
						new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Notification")));
		swagger.tag(new Tag()
				.name("notif_email")
				.description("Operations about email")
				.externalDocs(
						new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Notification")));

		swagger.tag(new Tag().name("payment").description("Operations about payment")
				.externalDocs(new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Payment")));

		swagger.tag(new Tag()
				.name("Instance")
				.description("Operations about scriptInstance")
				.externalDocs(
						new ExternalDocs("Find out more", "https://www.assembla.com/spaces/meveo/wiki/Script_Instance")));

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
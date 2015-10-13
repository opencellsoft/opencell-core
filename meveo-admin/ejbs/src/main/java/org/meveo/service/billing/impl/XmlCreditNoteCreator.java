package org.meveo.service.billing.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.CreditNote;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.XMLInvoiceHeaderCategoryDTO;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class XmlCreditNoteCreator extends PersistenceService<CreditNote> {

	@Inject
	private Logger log;

	@Inject
	private InvoiceAgregateService invoiceAgregateService;

	@Inject
	private CatMessagesService catMessagesService;

	private ParamBean paramBean = ParamBean.getInstance();

	private String dueDateFormat = "yyyy-MM-dd";

	public void createXmlCreditNote(CreditNote creditNote) throws BusinessException {
		try {
			String invoicesDir = paramBean.getProperty("providers.rootDir", "/tmp/meveo");
			File billingRundir = new File(invoicesDir + File.separator + creditNote.getProvider().getCode()
					+ File.separator + "invoices" + File.separator + "xml" + File.separator
					+ creditNote.getInvoice().getBillingRun().getId());

			String billingAccountLanguage = creditNote.getInvoice().getBillingAccount().getTradingLanguage()
					.getLanguage().getLanguageCode();
			BillingCycle billingCycle = creditNote.getInvoice().getBillingRun().getBillingCycle();
			boolean entreprise = creditNote.getInvoice().getProvider().isEntreprise();
			int rounding = creditNote.getInvoice().getProvider().getRounding() == null ? 2 : creditNote.getInvoice()
					.getProvider().getRounding();

			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

			Element creditNoteTag = doc.createElement("creditNote");
			creditNoteTag.setAttribute("code", creditNote.getCode());
			doc.appendChild(creditNoteTag);

			Element header = doc.createElement("header");

			log.debug("creating customer");
			Customer customer = creditNote.getInvoice().getBillingAccount().getCustomerAccount().getCustomer();
			Element customerTag = doc.createElement("customer");
			customerTag.setAttribute("id", customer.getId() + "");
			customerTag.setAttribute("code", customer.getCode() + "");
			customerTag.setAttribute("externalRef1", customer.getExternalRef1() != null ? customer.getExternalRef1()
					: "");
			customerTag.setAttribute("externalRef2", customer.getExternalRef2() != null ? customer.getExternalRef2()
					: "");
			customerTag.setAttribute("sellerCode", customer.getSeller().getCode() != null ? customer.getSeller()
					.getCode() : "");
			customerTag.setAttribute("brand", customer.getCustomerBrand().getCode() != null ? customer
					.getCustomerBrand().getCode() : "");
			customerTag.setAttribute("category", customer.getCustomerCategory().getCode() != null ? customer
					.getCustomerCategory().getCode() : "");

			String json = customer.getCustomFieldsAsJson();
			if (json.length() > 0) {
				customerTag.setAttribute("customFields", customer.getCustomFieldsAsJson());
			}
			header.appendChild(customerTag);
			addNameAndAdress(customer, doc, customerTag, billingAccountLanguage);

			log.debug("creating ca");
			CustomerAccount customerAccount = creditNote.getInvoice().getBillingAccount().getCustomerAccount();
			Element customerAccountTag = doc.createElement("customerAccount");
			customerAccountTag.setAttribute("id", customerAccount.getId() + "");
			customerAccountTag.setAttribute("code", customerAccount.getCode() + "");
			customerAccountTag.setAttribute("description", customerAccount.getDescription() + "");
			customerAccountTag.setAttribute("externalRef1",
					customerAccount.getExternalRef1() != null ? customerAccount.getExternalRef1() : "");
			customerAccountTag.setAttribute("externalRef2",
					customerAccount.getExternalRef2() != null ? customerAccount.getExternalRef2() : "");
			customerAccountTag.setAttribute("currency",
					customerAccount.getTradingCurrency().getPrDescription() != null ? customerAccount
							.getTradingCurrency().getPrDescription() : "");
			customerAccountTag.setAttribute("language",
					customerAccount.getTradingLanguage().getPrDescription() != null ? customerAccount
							.getTradingLanguage().getPrDescription() : "");
			json = customerAccount.getCustomFieldsAsJson();
			if (json.length() > 0) {
				customerAccountTag.setAttribute("customFields", customerAccount.getCustomFieldsAsJson());
			}
			customerAccountTag.setAttribute("accountTerminated",
					customerAccount.getStatus().equals(CustomerAccountStatusEnum.CLOSE) + "");

			header.appendChild(customerAccountTag);
			addNameAndAdress(customerAccount, doc, customerAccountTag, billingAccountLanguage);
			addproviderContact(customerAccount, doc, customerAccountTag);

			log.debug("creating ba");
			BillingAccount billingAccount = creditNote.getInvoice().getBillingAccount();
			Element billingAccountTag = doc.createElement("billingAccount");
			if (billingCycle == null) {
				billingCycle = billingAccount.getBillingCycle();
			}
			String billingCycleCode = billingCycle != null ? billingCycle.getCode() + "" : "";
			billingAccountTag.setAttribute("billingCycleCode", billingCycleCode);
			String billingAccountId = billingAccount.getId() + "";
			String billingAccountCode = billingAccount.getCode() + "";
			billingAccountTag.setAttribute("id", billingAccountId);
			billingAccountTag.setAttribute("code", billingAccountCode);
			billingAccountTag.setAttribute("description", billingAccount.getDescription() + "");
			billingAccountTag.setAttribute("externalRef1",
					billingAccount.getExternalRef1() != null ? billingAccount.getExternalRef1() : "");
			billingAccountTag.setAttribute("externalRef2",
					billingAccount.getExternalRef2() != null ? billingAccount.getExternalRef2() : "");
			json = billingAccount.getCustomFieldsAsJson();
			if (json.length() > 0) {
				billingAccountTag.setAttribute("customFields", billingAccount.getCustomFieldsAsJson());
			}
			header.appendChild(billingAccountTag);

			Element emailTag = doc.createElement("email");
			Text emailTxt = doc.createTextNode(billingAccount.getEmail() != null ? billingAccount.getEmail() : "");
			emailTag.appendChild(emailTxt);
			billingAccountTag.appendChild(emailTag);

			addNameAndAdress(billingAccount, doc, billingAccountTag, billingAccountLanguage);
			addPaymentInfo(billingAccount, doc, billingAccountTag);

			Element creditNoteDateTag = doc.createElement("creditNoteDate");
			Text invoiceDateTxt = doc.createTextNode(DateUtils.formatDateWithPattern(creditNote.getCreditNoteDate(),
					"dd/MM/yyyy"));
			creditNoteDateTag.appendChild(invoiceDateTxt);
			header.appendChild(creditNoteDateTag);

			Element dueDateTag = doc.createElement("dueDate");
			Text dueDateTxt = doc
					.createTextNode(DateUtils.formatDateWithPattern(creditNote.getDueDate(), dueDateFormat));
			dueDateTag.appendChild(dueDateTxt);
			header.appendChild(dueDateTag);

			addHeaderCategories(creditNote.getInvoice(), doc, header);
			addDiscounts(creditNote.getInvoice(), doc, header);

			Element amountTag = doc.createElement("amount");
			creditNoteTag.appendChild(amountTag);

			Element currency = doc.createElement("currency");
			Text currencyTxt = doc.createTextNode(creditNote.getInvoice().getBillingAccount().getCustomerAccount()
					.getTradingCurrency().getCurrencyCode());
			currency.appendChild(currencyTxt);
			amountTag.appendChild(currency);

			Element amountWithoutTax = doc.createElement("amountWithoutTax");
			Text amountWithoutTaxTxt = doc.createTextNode(round(creditNote.getAmountWithoutTax(), rounding));
			amountWithoutTax.appendChild(amountWithoutTaxTxt);
			amountTag.appendChild(amountWithoutTax);

			Element amountWithTax = doc.createElement("amountWithTax");
			Text amountWithTaxTxt = doc.createTextNode(round(creditNote.getAmountWithTax(), rounding));
			amountWithTax.appendChild(amountWithTaxTxt);
			amountTag.appendChild(amountWithTax);

			BigDecimal netToPay = BigDecimal.ZERO;
			if (entreprise) {
				netToPay = creditNote.getAmountWithTax();
			} else {
				netToPay = creditNote.getNetToPay();
			}

			Element netToPayElement = doc.createElement("netToPay");
			Text netToPayTxt = doc.createTextNode(round(netToPay, rounding));
			netToPayElement.appendChild(netToPayTxt);
			amountTag.appendChild(netToPayElement);

			addTaxes(creditNote.getInvoice(), doc, amountTag);

			// add invoice
			creditNoteTag.appendChild(addInvoice(creditNote.getInvoice(), doc));

			// add categories
			creditNoteTag.appendChild(addCategories(creditNote.getInvoice(), doc, entreprise));

			// add subscriptions
			addSubscriptions(creditNote.getInvoice().getBillingAccount(), creditNote.getInvoice(), creditNoteTag, doc);

			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// create string from xml tree
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(billingRundir + File.separator
					+ paramBean.getProperty("invoicing.creditNote.prefix", "_CN_") + creditNote.getCode() + ".xml");
			billingRundir.mkdirs();

			trans.transform(source, result);
		} catch (TransformerException e) {
			log.error("Error occured when creating xml for creditNote.id={}. {}", creditNote.getId(), e);
		} catch (ParserConfigurationException e) {
			log.error("Error occured when creating xml for creditNote.id={}. {}", creditNote.getId(), e);
		}
	}

	private void addSubscriptions(BillingAccount billingAccount, Invoice invoice, Element creditNoteTag, Document doc) {

		for (UserAccount userAccount : billingAccount.getUsersAccounts()) {
			if (userAccount.getSubscriptions() != null && userAccount.getSubscriptions().size() > 0) {

				Element subscriptionsTag = null;
				if (invoice.getProvider().getInvoiceConfiguration() != null
						&& invoice.getProvider().getInvoiceConfiguration().getDisplaySubscriptions() != null
						&& invoice.getProvider().getInvoiceConfiguration().getDisplaySubscriptions()) {
					subscriptionsTag = doc.createElement("subscriptions");
					creditNoteTag.appendChild(subscriptionsTag);
				}

				for (Subscription subscription : userAccount.getSubscriptions()) {
					if (invoice.getProvider().getInvoiceConfiguration() != null
							&& invoice.getProvider().getInvoiceConfiguration().getDisplaySubscriptions() != null
							&& invoice.getProvider().getInvoiceConfiguration().getDisplaySubscriptions()) {
						Element subscriptionTag = doc.createElement("subscription");
						subscriptionTag.setAttribute("id", subscription.getId() + "");
						subscriptionTag.setAttribute("code", subscription.getCode() != null ? subscription.getCode()
								: "");
						subscriptionTag.setAttribute("description",
								subscription.getDescription() != null ? subscription.getDescription() : "");

						Element subscriptionDateTag = doc.createElement("subscriptionDate");
						Text subscriptionDateText = null;
						if (subscription.getSubscriptionDate() != null) {
							subscriptionDateText = doc.createTextNode(subscription.getSubscriptionDate().toString());
						} else {
							subscriptionDateText = doc.createTextNode("");
						}
						subscriptionDateTag.appendChild(subscriptionDateText);
						subscriptionTag.appendChild(subscriptionDateTag);

						Element endAgreementTag = doc.createElement("endAgreementDate");
						Text endAgreementText = null;
						if (subscription.getEndAgrementDate() != null) {
							endAgreementText = doc.createTextNode(subscription.getEndAgrementDate().toString());
						} else {
							endAgreementText = doc.createTextNode("");
						}
						endAgreementTag.appendChild(endAgreementText);
						subscriptionTag.appendChild(endAgreementTag);

						if (subscription.getCustomFields() != null && subscription.getCustomFields().size() > 0) {
							addCustomFields(subscription, invoice, doc, subscriptionTag);
						}

						subscriptionsTag.appendChild(subscriptionTag);
					}

					if (subscription.getOffer() != null) {
						OfferTemplate offerTemplate = subscription.getOffer();
						if (invoice.getProvider().getInvoiceConfiguration() != null
								&& invoice.getProvider().getInvoiceConfiguration().getDisplayOffers() != null
								&& invoice.getProvider().getInvoiceConfiguration().getDisplayOffers()) {
							addOffers(offerTemplate, invoice, doc, creditNoteTag);
						}

						if (invoice.getProvider().getInvoiceConfiguration() != null
								&& invoice.getProvider().getInvoiceConfiguration().getDisplayServices() != null
								&& invoice.getProvider().getInvoiceConfiguration().getDisplayServices()) {
							addServices(offerTemplate, invoice, doc, creditNoteTag);
						}
					}
				}
			}
		}
	}

	private void addOffers(OfferTemplate offerTemplate, Invoice invoice, Document doc, Element creditNoteTag) {
		NodeList offerList = doc.getElementsByTagName("offers");

		Element offersTag = null;
		if (offerList != null && offerList.getLength() > 0) {
			offersTag = (Element) offerList.item(0);
		} else {
			offersTag = doc.createElement("offers");
			creditNoteTag.appendChild(offersTag);
		}

		Element offerTag = doc.createElement("offer");
		offerTag.setAttribute("id", offerTemplate.getId() + "");
		offerTag.setAttribute("code", offerTemplate.getCode() != null ? offerTemplate.getCode() : "");
		offerTag.setAttribute("description", offerTemplate.getDescription() != null ? offerTemplate.getDescription()
				: "");
		offersTag.appendChild(offerTag);
	}

	private void addServices(OfferTemplate offerTemplate, Invoice invoice, Document doc, Element creditNoteTag) {
		if (offerTemplate.getServiceTemplates() != null && offerTemplate.getServiceTemplates().size() > 0) {
			NodeList serviceList = doc.getElementsByTagName("services");

			Element servicesTag = null;
			if (serviceList != null && serviceList.getLength() > 0) {
				servicesTag = (Element) serviceList.item(0);
			} else {
				servicesTag = doc.createElement("services");
				creditNoteTag.appendChild(servicesTag);
			}

			for (ServiceTemplate serviceTemplate : offerTemplate.getServiceTemplates()) {
				Element serviceTag = doc.createElement("service");
				serviceTag.setAttribute("id", serviceTemplate.getId() + "");
				serviceTag.setAttribute("code", serviceTemplate.getCode() != null ? serviceTemplate.getCode() : "");
				serviceTag.setAttribute("description",
						serviceTemplate.getDescription() != null ? serviceTemplate.getDescription() : "");

				Element calendarTag = doc.createElement("calendar");
				Text calendarText = null;
				if (serviceTemplate.getInvoicingCalendar() != null) {
					calendarText = doc.createTextNode(serviceTemplate.getInvoicingCalendar().getCode());
				} else {
					calendarText = doc.createTextNode("");
				}
				calendarTag.appendChild(calendarText);

				servicesTag.appendChild(serviceTag);
			}
		}
	}

	private void addCustomFields(Subscription subscription, Invoice invoice, Document doc, Element parent) {
		Iterator<String> keys = subscription.getCustomFields().keySet().iterator();

		Element customFieldsTag = doc.createElement("customFields");
		parent.appendChild(customFieldsTag);

		while (keys.hasNext()) {
			String key = keys.next();
			CustomFieldInstance cfi = subscription.getCustomFields().get(key);

			if (!StringUtils.isBlank(cfi.getValueAsString())) {
				Element customFieldTag = doc.createElement("customField");
				customFieldTag.setAttribute("id", cfi.getId() + "");
				customFieldTag.setAttribute("code", cfi.getCode() != null ? cfi.getCode() : "");

				Text customFieldText = doc.createTextNode(cfi.getValueAsString());
				customFieldTag.appendChild(customFieldText);

				customFieldsTag.appendChild(customFieldTag);
			}
		}
	}

	public Element addCategories(Invoice invoice, Document doc, boolean enterprise) {
		log.debug("add categories");

		String languageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();

		Element categoriesTag = doc.createElement("categories");

		int rounding = invoice.getProvider().getRounding() == null ? 2 : invoice.getProvider().getRounding();

		List<CategoryInvoiceAgregate> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregate>();

		for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
			if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
				CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
				categoryInvoiceAgregates.add(categoryInvoiceAgregate);
			}
		}

		Collections.sort(categoryInvoiceAgregates, new Comparator<CategoryInvoiceAgregate>() {
			public int compare(CategoryInvoiceAgregate c0, CategoryInvoiceAgregate c1) {
				if (c0.getInvoiceCategory() != null && c1.getInvoiceCategory() != null
						&& c0.getInvoiceCategory().getSortIndex() != null
						&& c1.getInvoiceCategory().getSortIndex() != null) {
					return c0.getInvoiceCategory().getSortIndex().compareTo(c1.getInvoiceCategory().getSortIndex());
				}
				return 0;
			}
		});

		for (CategoryInvoiceAgregate categoryInvoiceAgregate : categoryInvoiceAgregates) {

			InvoiceCategory invoiceCategory = categoryInvoiceAgregate.getInvoiceCategory();

			String invoiceCategoryLabel = invoiceCategory != null ? catMessagesService.getMessageDescription(
					invoiceCategory, languageCode) : "";
			Element category = doc.createElement("category");
			category.setAttribute("description", (invoiceCategoryLabel != null) ? invoiceCategoryLabel : "");
			category.setAttribute("code",
					invoiceCategory != null && invoiceCategory.getCode() != null ? invoiceCategory.getCode() : "");
			categoriesTag.appendChild(category);

			Element amountWithoutTax = doc.createElement("amountWithoutTax");
			Text amountWithoutTaxTxt = doc
					.createTextNode(round(categoryInvoiceAgregate.getAmountWithoutTax(), rounding));
			amountWithoutTax.appendChild(amountWithoutTaxTxt);
			category.appendChild(amountWithoutTax);

			// generate sub categories
			Element subCategories = doc.createElement("subCategories");
			category.appendChild(subCategories);
			Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = categoryInvoiceAgregate
					.getSubCategoryInvoiceAgregates();

			for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : subCategoryInvoiceAgregates) {
				InvoiceSubCategory invoiceSubCat = subCatInvoiceAgregate.getInvoiceSubCategory();

				String invoiceSubCategoryLabel = invoiceSubCat != null ? catMessagesService.getMessageDescription(
						invoiceSubCat, languageCode) : "";
				Element subCategory = doc.createElement("subCategory");
				subCategories.appendChild(subCategory);
				subCategory.setAttribute("description", (invoiceSubCategoryLabel != null) ? invoiceSubCategoryLabel
						: "");
				subCategory.setAttribute("code", invoiceSubCat.getCode());
				String taxesCode = "";
				String taxesPercent = "";
				String sep = "";
				for (Tax tax : subCatInvoiceAgregate.getSubCategoryTaxes()) {
					taxesCode = taxesCode + sep + tax.getCode();
					taxesPercent = taxesPercent + sep + round(tax.getPercent(), rounding);
					sep = ";";
				}
				subCategory.setAttribute("taxCode", taxesCode);
				subCategory.setAttribute("taxPercent", taxesPercent);
				subCategory.setAttribute("amountWithTax", round(subCatInvoiceAgregate.getAmountWithTax(), rounding));
				subCategory.setAttribute("amountWithoutTax",
						round(subCatInvoiceAgregate.getAmountWithoutTax(), rounding));
				subCategory.setAttribute("amountTax", round(subCatInvoiceAgregate.getAmountTax(), rounding));
			}
		}

		return categoriesTag;
	}

	private Element addInvoice(Invoice invoice, Document doc) {
		Element invoiceTag = doc.createElement("invoice");

		int rounding = invoice.getProvider().getRounding() == null ? 2 : invoice.getProvider().getRounding();

		invoiceTag.setAttribute("number", invoice.getInvoiceNumber());
		invoiceTag.setAttribute("invoiceDate", DateUtils.formatDateWithPattern(invoice.getInvoiceDate(), "dd/MM/yyyy"));
		invoiceTag.setAttribute("amountWithoutTax", round(invoice.getAmountWithoutTax(), rounding));
		invoiceTag.setAttribute("amountWithTax", round(invoice.getAmountWithTax(), rounding));
		invoiceTag.setAttribute("amountTax", round(invoice.getAmountTax(), rounding));

		return invoiceTag;
	}

	private void addTaxes(Invoice invoice, Document doc, Element parent) throws BusinessException {
		log.debug("adding taxes...");
		Element taxes = doc.createElement("taxes");
		int rounding = invoice.getProvider().getRounding() == null ? 2 : invoice.getProvider().getRounding();

		taxes.setAttribute("total", round(invoice.getAmountTax(), rounding));
		parent.appendChild(taxes);
		Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();

		for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
			if (invoiceAgregate instanceof TaxInvoiceAgregate) {
				TaxInvoiceAgregate taxInvoiceAgregate = (TaxInvoiceAgregate) invoiceAgregate;
				TaxInvoiceAgregate taxAgregate = null;

				if (taxInvoiceAgregateMap.containsKey(taxInvoiceAgregate.getTax().getId())) {
					taxAgregate = taxInvoiceAgregateMap.get(taxInvoiceAgregate.getTax().getId());
					taxAgregate.setAmountTax(taxAgregate.getAmountTax().add(taxInvoiceAgregate.getAmountTax()));
					taxAgregate.setAmountWithoutTax(taxAgregate.getAmountWithoutTax().add(
							taxInvoiceAgregate.getAmountWithoutTax()));
				} else {
					taxAgregate = new TaxInvoiceAgregate();
					taxAgregate.setTaxPercent(taxInvoiceAgregate.getTaxPercent());
					taxAgregate.setTax(taxInvoiceAgregate.getTax());
					taxAgregate.setAmountTax(taxInvoiceAgregate.getAmountTax());
					taxAgregate.setAmountWithoutTax(taxInvoiceAgregate.getAmountWithoutTax());
					taxInvoiceAgregateMap.put(taxInvoiceAgregate.getTax().getId(), taxAgregate);
				}
			}
		}

		int taxId = 0;
		for (TaxInvoiceAgregate taxInvoiceAgregate : taxInvoiceAgregateMap.values()) {
			Element tax = doc.createElement("tax");

			tax.setAttribute("id", ++taxId + "");
			tax.setAttribute("code", taxInvoiceAgregate.getTax().getCode() + "");

			String languageCode = "";
			try {
				languageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
			} catch (NullPointerException e) {
				log.error("Billing account must have a trading language.");
				throw new BusinessException("Billing account must have a trading language.");
			}

			String taxDescription = catMessagesService.getMessageDescription(taxInvoiceAgregate.getTax(), languageCode);
			Element taxName = doc.createElement("name");
			Text taxNameTxt = doc.createTextNode(taxDescription != null ? taxDescription : "");
			taxName.appendChild(taxNameTxt);
			tax.appendChild(taxName);

			Element percent = doc.createElement("percent");
			Text percentTxt = doc.createTextNode(round(taxInvoiceAgregate.getTaxPercent(), rounding));
			percent.appendChild(percentTxt);
			tax.appendChild(percent);

			Element taxAmount = doc.createElement("amount");
			Text amountTxt = doc.createTextNode(round(taxInvoiceAgregate.getAmountTax(), rounding));
			taxAmount.appendChild(amountTxt);
			tax.appendChild(taxAmount);

			Element amountHT = doc.createElement("amountHT");
			Text amountHTTxt = doc.createTextNode(round(taxInvoiceAgregate.getAmountWithoutTax(), rounding));
			amountHT.appendChild(amountHTTxt);
			tax.appendChild(amountHT);

			taxes.appendChild(tax);
		}
	}

	private void addHeaderCategories(Invoice invoice, Document doc, Element parent) {
		log.debug("add header categories");

		boolean entreprise = invoice.getProvider().isEntreprise();
		LinkedHashMap<String, XMLInvoiceHeaderCategoryDTO> headerCategories = new LinkedHashMap<String, XMLInvoiceHeaderCategoryDTO>();
		List<CategoryInvoiceAgregate> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregate>();

		for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
			if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
				CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
				categoryInvoiceAgregates.add(categoryInvoiceAgregate);
			}
		}

		Collections.sort(categoryInvoiceAgregates, new Comparator<CategoryInvoiceAgregate>() {
			public int compare(CategoryInvoiceAgregate c0, CategoryInvoiceAgregate c1) {
				if (c0.getInvoiceCategory() != null && c1.getInvoiceCategory() != null
						&& c0.getInvoiceCategory().getSortIndex() != null
						&& c1.getInvoiceCategory().getSortIndex() != null) {
					return c0.getInvoiceCategory().getSortIndex().compareTo(c1.getInvoiceCategory().getSortIndex());
				}
				return 0;
			}
		});

		for (CategoryInvoiceAgregate categoryInvoiceAgregate : categoryInvoiceAgregates) {
			InvoiceCategory invoiceCategory = categoryInvoiceAgregate.getInvoiceCategory();
			XMLInvoiceHeaderCategoryDTO headerCat = null;
			if (headerCategories.containsKey(invoiceCategory.getCode())) {
				headerCat = headerCategories.get(invoiceCategory.getCode());
				headerCat.addAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax());
				headerCat.addAmountWithTax(categoryInvoiceAgregate.getAmountWithTax());
			} else {
				headerCat = new XMLInvoiceHeaderCategoryDTO();
				headerCat.setDescription(invoiceCategory.getDescription());
				headerCat.setCode(invoiceCategory.getCode());
				headerCat.setAmountWithoutTax(categoryInvoiceAgregate.getAmountWithoutTax());
				headerCat.setAmountWithTax(categoryInvoiceAgregate.getAmountWithTax());
				headerCategories.put(invoiceCategory.getCode(), headerCat);
			}

		}

		addHeaderCategories(headerCategories, doc, parent, entreprise, invoice.getProvider());
	}

	private void addHeaderCategories(LinkedHashMap<String, XMLInvoiceHeaderCategoryDTO> headerCategories, Document doc,
			Element parent, boolean entreprise, Provider provider) {

		int rounding = provider.getRounding() == null ? 2 : provider.getRounding();

		log.debug("add header categories");

		Element categories = doc.createElement("categories");
		parent.appendChild(categories);
		for (XMLInvoiceHeaderCategoryDTO xmlInvoiceHeaderCategoryDTO : headerCategories.values()) {

			Element category = doc.createElement("category");
			category.setAttribute("label", xmlInvoiceHeaderCategoryDTO.getDescription());
			category.setAttribute("code", xmlInvoiceHeaderCategoryDTO != null
					&& xmlInvoiceHeaderCategoryDTO.getCode() != null ? xmlInvoiceHeaderCategoryDTO.getCode() : "");
			categories.appendChild(category);

			Element amountWithoutTax = doc.createElement("amountWithoutTax");
			Text amountWithoutTaxTxt = doc.createTextNode(round(xmlInvoiceHeaderCategoryDTO.getAmountWithoutTax(),
					rounding));
			amountWithoutTax.appendChild(amountWithoutTaxTxt);
			category.appendChild(amountWithoutTax);

			if (entreprise) {
				for (RatedTransaction headerTransaction : xmlInvoiceHeaderCategoryDTO.getRatedtransactions().values()) {

					Element line = doc.createElement("line");

					Element lebel = doc.createElement("label");
					Text lebelTxt = doc.createTextNode("");
					if (headerTransaction.getWalletOperationId() != null) {
						WalletOperation walletOperation = getEntityManager().find(WalletOperation.class,
								headerTransaction.getWalletOperationId());
						lebelTxt = doc.createTextNode(walletOperation.getDescription() != null ? walletOperation
								.getDescription() : "");
					} else {
						lebelTxt = doc.createTextNode(headerTransaction.getDescription() != null ? headerTransaction
								.getDescription() : "");
					}

					lebel.appendChild(lebelTxt);
					line.appendChild(lebel);
					// log.info("addHeaderCategories2 headerRatedTransaction amountHT="
					// + headerTransaction.getAmountWithoutTax());
					Element lineUnitAmountWithoutTax = doc.createElement("unitAmountWithoutTax");
					Text lineUnitAmountWithoutTaxTxt = doc.createTextNode(round(
							headerTransaction.getUnitAmountWithoutTax(), rounding));
					lineUnitAmountWithoutTax.appendChild(lineUnitAmountWithoutTaxTxt);
					line.appendChild(lineUnitAmountWithoutTax);
					Element lineAmountWithoutTax = doc.createElement("amountWithoutTax");
					Text lineAmountWithoutTaxTxt = doc.createTextNode(round(headerTransaction.getAmountWithoutTax(),
							rounding));
					lineAmountWithoutTax.appendChild(lineAmountWithoutTaxTxt);
					line.appendChild(lineAmountWithoutTax);

					category.appendChild(line);
				}
			}
		}
	}

	private void addDiscounts(Invoice invoice, Document doc, Element parent) {
		int rounding = invoice.getProvider().getRounding() == null ? 2 : invoice.getProvider().getRounding();
		Element discounts = doc.createElement("discounts");

		parent.appendChild(discounts);
		for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : invoiceAgregateService
				.findDiscountAggregates(invoice)) {

			Element discount = doc.createElement("discount");
			discount.setAttribute("discountPlanCode", subCategoryInvoiceAgregate.getDiscountPlanCode());
			discount.setAttribute("discountPlanItemCode", subCategoryInvoiceAgregate.getDiscountPlanItemCode());
			discount.setAttribute("invoiceSubCategoryCode", subCategoryInvoiceAgregate.getInvoiceSubCategory()
					.getCode());
			discount.setAttribute("discountAmountWithoutTax",
					round(subCategoryInvoiceAgregate.getAmountWithoutTax(), rounding) + "");
			discount.setAttribute("discountPercent", round(subCategoryInvoiceAgregate.getDiscountPercent(), rounding)
					+ "");

			discounts.appendChild(discount);
		}
	}

	private String round(BigDecimal amount, Integer scale) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		if (scale == null) {
			scale = 2;
		}
		amount = amount.setScale(scale, RoundingMode.HALF_UP);
		return amount.toPlainString();
	}

	public void addPaymentInfo(BillingAccount billingAccount, Document doc, Element parent) {
		log.debug("add payment info");

		Element paymentMethod = doc.createElement("paymentMethod");
		parent.appendChild(paymentMethod);
		if (billingAccount.getPaymentMethod() != null) {
			paymentMethod.setAttribute("type", billingAccount.getPaymentMethod().name());
		}

		Element bankCoordinates = doc.createElement("bankCoordinates");
		Element bankCode = doc.createElement("bankCode");
		Element branchCode = doc.createElement("branchCode");
		Element accountNumber = doc.createElement("accountNumber");
		Element accountOwner = doc.createElement("accountOwner");
		Element key = doc.createElement("key");
		Element iban = doc.createElement("IBAN");
		bankCoordinates.appendChild(bankCode);
		bankCoordinates.appendChild(branchCode);
		bankCoordinates.appendChild(accountNumber);
		bankCoordinates.appendChild(accountOwner);
		bankCoordinates.appendChild(key);
		bankCoordinates.appendChild(iban);
		paymentMethod.appendChild(bankCoordinates);

		Element paymentTerm = doc.createElement("paymentTerm");
		parent.appendChild(paymentTerm);
		paymentTerm.setAttribute("type", billingAccount.getPaymentTerm() != null ? billingAccount.getPaymentTerm()
				.toString() : "");

		if (billingAccount.getBankCoordinates() != null && billingAccount.getBankCoordinates().getBankCode() != null) {
			Text bankCodeTxt = doc
					.createTextNode(billingAccount.getBankCoordinates().getBankCode() != null ? billingAccount
							.getBankCoordinates().getBankCode() : "");
			bankCode.appendChild(bankCodeTxt);

			Text branchCodeTxt = doc
					.createTextNode(billingAccount.getBankCoordinates().getBranchCode() != null ? billingAccount
							.getBankCoordinates().getBranchCode() : "");
			branchCode.appendChild(branchCodeTxt);

			Text accountNumberTxt = doc
					.createTextNode(billingAccount.getBankCoordinates().getAccountNumber() != null ? billingAccount
							.getBankCoordinates().getAccountNumber() : "");
			accountNumber.appendChild(accountNumberTxt);

			Text accountOwnerTxt = doc
					.createTextNode(billingAccount.getBankCoordinates().getAccountOwner() != null ? billingAccount
							.getBankCoordinates().getAccountOwner() : "");
			accountOwner.appendChild(accountOwnerTxt);

			Text keyTxt = doc.createTextNode(billingAccount.getBankCoordinates().getKey() != null ? billingAccount
					.getBankCoordinates().getKey() : "");
			key.appendChild(keyTxt);

			if (billingAccount.getBankCoordinates().getIban() != null) {
				Text ibanTxt = doc
						.createTextNode(billingAccount.getBankCoordinates().getIban() != null ? billingAccount
								.getBankCoordinates().getIban() : "");
				iban.appendChild(ibanTxt);
			}
		}
	}

	public void addproviderContact(AccountEntity account, Document doc, Element parent) {
		log.debug("add provider");

		if (account.getPrimaryContact() != null) {
			Element providerContactTag = doc.createElement("providerContact");
			parent.appendChild(providerContactTag);
			if (account.getPrimaryContact().getFirstName() != null) {
				Element firstName = doc.createElement("firstName");
				Text firstNameTxt = doc.createTextNode(account.getPrimaryContact().getFirstName());
				firstName.appendChild(firstNameTxt);
				providerContactTag.appendChild(firstName);
			}

			if (account.getPrimaryContact().getLastName() != null) {
				Element name = doc.createElement("lastname");
				Text nameTxt = doc.createTextNode(account.getPrimaryContact().getLastName());
				name.appendChild(nameTxt);
				providerContactTag.appendChild(name);
			}

			if (account.getPrimaryContact().getEmail() != null) {
				Element email = doc.createElement("email");
				Text emailTxt = doc.createTextNode(account.getPrimaryContact().getEmail());
				email.appendChild(emailTxt);
				providerContactTag.appendChild(email);
			}

			if (account.getPrimaryContact().getFax() != null) {
				Element fax = doc.createElement("fax");
				Text faxTxt = doc.createTextNode(account.getPrimaryContact().getFax());
				fax.appendChild(faxTxt);
				providerContactTag.appendChild(fax);

			}

			if (account.getPrimaryContact().getMobile() != null) {

				Element mobile = doc.createElement("mobile");
				Text mobileTxt = doc.createTextNode(account.getPrimaryContact().getMobile());
				mobile.appendChild(mobileTxt);
				providerContactTag.appendChild(mobile);
			}

			if (account.getPrimaryContact().getPhone() != null) {
				Element phone = doc.createElement("phone");
				Text phoneTxt = doc.createTextNode(account.getPrimaryContact().getPhone());
				phone.appendChild(phoneTxt);
				providerContactTag.appendChild(phone);
			}
		}
	}

	public void addNameAndAdress(AccountEntity account, Document doc, Element parent, String languageCode) {
		log.debug("add name and address");

		if (!(account instanceof Customer)) {
			Element nameTag = doc.createElement("name");
			parent.appendChild(nameTag);

			Element quality = doc.createElement("title");
			if (account.getName().getTitle() != null) {
				Text qualityTxt = doc.createTextNode(catMessagesService.getMessageDescription(account.getName()
						.getTitle(), languageCode));
				quality.appendChild(qualityTxt);
			}

			nameTag.appendChild(quality);
			if (account.getName().getFirstName() != null) {
				Element firstName = doc.createElement("firstName");
				Text firstNameTxt = doc.createTextNode(account.getName().getFirstName());
				firstName.appendChild(firstNameTxt);
				nameTag.appendChild(firstName);
			}

			Element name = doc.createElement("name");
			if (account.getName().getLastName() != null) {
				Text nameTxt = doc.createTextNode(account.getName().getLastName());
				name.appendChild(nameTxt);
			}
			nameTag.appendChild(name);
		}

		Element addressTag = doc.createElement("address");
		Element address1 = doc.createElement("address1");
		if (account.getAddress().getAddress1() != null) {
			Text adress1Txt = doc.createTextNode(account.getAddress().getAddress1());
			address1.appendChild(adress1Txt);
		}
		addressTag.appendChild(address1);

		Element address2 = doc.createElement("address2");
		if (account.getAddress().getAddress2() != null) {
			Text adress2Txt = doc.createTextNode(account.getAddress().getAddress2());
			address2.appendChild(adress2Txt);
		}
		addressTag.appendChild(address2);

		Element address3 = doc.createElement("address3");
		if (account.getAddress().getAddress3() != null) {
			Text adress3Txt = doc.createTextNode(account.getAddress().getAddress3() != null ? account.getAddress()
					.getAddress3() : "");
			address3.appendChild(adress3Txt);
		}
		addressTag.appendChild(address3);

		Element city = doc.createElement("city");
		Text cityTxt = doc.createTextNode(account.getAddress().getCity() != null ? account.getAddress().getCity() : "");
		city.appendChild(cityTxt);
		addressTag.appendChild(city);

		Element postalCode = doc.createElement("postalCode");
		Text postalCodeTxt = doc.createTextNode(account.getAddress().getZipCode() != null ? account.getAddress()
				.getZipCode() : "");
		postalCode.appendChild(postalCodeTxt);
		addressTag.appendChild(postalCode);

		Element state = doc.createElement("state");
		addressTag.appendChild(state);

		Element country = doc.createElement("country");
		Text countryTxt = doc.createTextNode(account.getAddress().getCountry() != null ? account.getAddress()
				.getCountry() : "");
		country.appendChild(countryTxt);
		addressTag.appendChild(country);

		parent.appendChild(addressTag);
	}

}

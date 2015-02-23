/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.billing.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.XMLInvoiceHeaderCategoryDTO;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.CatMessagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

@Stateless
public class XMLInvoiceCreator extends PersistenceService<Invoice> {

	private static final String dueDateFormat = "yyyy-MM-dd";

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	@Inject
	private CatMessagesService catMessagesService;

	private static Logger log = LoggerFactory.getLogger(XMLInvoiceCreator.class);

	@Asynchronous
	public Future<Boolean> createXMLInvoice(Invoice invoice, File billingRundir) throws BusinessException {
		log.debug("creating xml invoice");

		try {
		    String billingAccountLanguage = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();
		    
			boolean entreprise = invoice.getProvider().isEntreprise();
			int rounding = invoice.getProvider().getRounding() == null ? 2 : invoice.getProvider().getRounding();

			if (BillingRunStatusEnum.VALIDATED.equals(invoice.getBillingRun().getStatus())
					&& invoice.getInvoiceNumber() == null) {
				invoiceService.setInvoiceNumber(invoice);
			}
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element invoiceTag = doc.createElement("invoice");
			Element header = doc.createElement("header");
			invoiceTag.setAttribute("number", invoice.getInvoiceNumber());
			invoiceTag.setAttribute("invoiceCounter", invoice.getAlias());
			invoiceTag.setAttribute("id", invoice.getId().toString());
			invoiceTag.setAttribute("customerId", invoice.getBillingAccount().getCustomerAccount().getCustomer()
					.getCode()
					+ "");
			invoiceTag.setAttribute("customerAccountCode",
					invoice.getBillingAccount().getCustomerAccount().getCode() != null ? invoice.getBillingAccount()
							.getCustomerAccount().getCode() : "");
			BillingCycle billingCycle = invoice.getBillingRun().getBillingCycle();
			invoiceTag.setAttribute("templateName", billingCycle != null
					&& billingCycle.getBillingTemplateName() != null ? billingCycle.getBillingTemplateName()
					: "default");
			doc.appendChild(invoiceTag);
			invoiceTag.appendChild(header);

			log.debug("creating customer");
			Customer customer = invoice.getBillingAccount().getCustomerAccount().getCustomer();
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
			CustomerAccount customerAccount = invoice.getBillingAccount().getCustomerAccount();
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
			json = customerAccount.getCustomFieldsAsJson();
			if (json.length() > 0) {
				customerAccountTag.setAttribute("customFields", customerAccount.getCustomFieldsAsJson());
			}
			header.appendChild(customerAccountTag);

			/*
			 * EntityManager em = getEntityManager(); Query billingQuery = em
			 * .createQuery(
			 * "select si from ServiceInstance si join si.subscription s join s.userAccount ua join ua.billingAccount ba join ba.customerAccount ca where ca.id = :customerAccountId"
			 * ); billingQuery.setParameter("customerAccountId",
			 * customerAccount.getId()); List<ServiceInstance> services =
			 * (List<ServiceInstance>) billingQuery .getResultList();
			 * 
			 * 
			 * 
			 * boolean terminated = services.size() > 0 ?
			 * isAllServiceInstancesTerminated(services) : false;
			 */

			customerAccountTag.setAttribute("accountTerminated",
					customerAccount.getStatus().equals(CustomerAccountStatusEnum.CLOSE) + "");

			header.appendChild(customerAccountTag);
			addNameAndAdress(customerAccount, doc, customerAccountTag, billingAccountLanguage);
			addproviderContact(customerAccount, doc, customerAccountTag);

			log.debug("creating ba");
			BillingAccount billingAccount = invoice.getBillingAccount();
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

			/*if (billingAccount.getName() != null && billingAccount.getName().getTitle() != null) {
				// Element company = doc.createElement("company");
				Text companyTxt = doc.createTextNode(billingAccount.getName().getTitle().getIsCompany() + "");
				billingAccountTag.appendChild(companyTxt);
			}*/

			Element email = doc.createElement("email");
			Text emailTxt = doc.createTextNode(billingAccount.getEmail() != null ? billingAccount.getEmail() : "");
			email.appendChild(emailTxt);
			billingAccountTag.appendChild(email);

			addNameAndAdress(billingAccount, doc, billingAccountTag, billingAccountLanguage);

			addPaymentInfo(billingAccount, doc, billingAccountTag);

			Element invoiceDate = doc.createElement("invoiceDate");
			Text invoiceDateTxt = doc.createTextNode(DateUtils.formatDateWithPattern(invoice.getInvoiceDate(),
					"dd/MM/yyyy"));
			invoiceDate.appendChild(invoiceDateTxt);
			header.appendChild(invoiceDate);

			Element dueDate = doc.createElement("dueDate");
			Text dueDateTxt = doc.createTextNode(DateUtils.formatDateWithPattern(invoice.getDueDate(), dueDateFormat));
			dueDate.appendChild(dueDateTxt);
			header.appendChild(dueDate);

			Element comment = doc.createElement("comment");
			Comment commentText = doc.createComment(invoice.getComment() != null ? invoice.getComment() : "");
			comment.appendChild(commentText);
			header.appendChild(comment);

			addHeaderCategories(invoice, doc, header);

			Element amount = doc.createElement("amount");
			invoiceTag.appendChild(amount);

			Element currency = doc.createElement("currency");
			Text currencyTxt = doc.createTextNode("EUR");
			currency.appendChild(currencyTxt);
			amount.appendChild(currency);

			Element amountWithoutTax = doc.createElement("amountWithoutTax");
			Text amountWithoutTaxTxt = doc.createTextNode(round(invoice.getAmountWithoutTax(), rounding));
			amountWithoutTax.appendChild(amountWithoutTaxTxt);
			amount.appendChild(amountWithoutTax);

			Element amountWithTax = doc.createElement("amountWithTax");
			Text amountWithTaxTxt = doc.createTextNode(round(invoice.getAmountWithTax(), rounding));
			amountWithTax.appendChild(amountWithTaxTxt);
			amount.appendChild(amountWithTax);

			BigDecimal netToPay = BigDecimal.ZERO;
			if (entreprise) {
				netToPay = invoice.getAmountWithTax();
			} else {
				netToPay = invoice.getNetToPay();
			}

			/*
			 * Element balanceElement = doc.createElement("balance"); Text
			 * balanceTxt = doc.createTextNode(round(balance));
			 * balanceElement.appendChild(balanceTxt);
			 * amount.appendChild(balanceElement);
			 */

			Element netToPayElement = doc.createElement("netToPay");
			Text netToPayTxt = doc.createTextNode(round(netToPay, rounding));
			netToPayElement.appendChild(netToPayTxt);
			amount.appendChild(netToPayElement);

			addTaxes(invoice, doc, amount);

			Element detail = doc.createElement("detail");
			invoiceTag.appendChild(detail);
			addUserAccounts(invoice, doc, detail, entreprise);

			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			//trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// create string from xml tree
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(billingRundir
					+ File.separator
					+ (invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber()
							: invoice.getTemporaryInvoiceNumber()) + ".xml");
			log.debug("source=" + source.toString());

			trans.transform(source, result);
			return new AsyncResult<Boolean>(true);
		} catch (TransformerException e) {
			log.error("Error occured when creating xml for invoiceID={}. {}", invoice.getId(), e);
		} catch (ParserConfigurationException e) {
			log.error("Error occured when creating xml for invoiceID={}. {}", invoice.getId(), e);
		}
		return new AsyncResult<Boolean>(false);
	}

	public void addUserAccounts(Invoice invoice, Document doc, Element parent, boolean enterprise) {
		log.debug("add user account");

		Element userAccounts = doc.createElement("userAccounts");
		parent.appendChild(userAccounts);
		BillingAccount billingAccount = invoice.getBillingAccount();
		String billingAccountLanguage = billingAccount.getTradingLanguage().getLanguage().getLanguageCode();

		for (UserAccount userAccount : billingAccount.getUsersAccounts()) {
			Element userAccountTag = doc.createElement("userAccount");
			userAccountTag.setAttribute("id", userAccount.getId() + "");
			userAccountTag.setAttribute("code", userAccount.getCode() != null ? userAccount.getCode() : "");
			userAccountTag.setAttribute("description",
					userAccount.getDescription() != null ? userAccount.getDescription() : "");
			String json = userAccount.getCustomFieldsAsJson();
			if (json.length() > 0) {
				userAccountTag.setAttribute("customFields", userAccount.getCustomFieldsAsJson());
			}
			userAccounts.appendChild(userAccountTag);
			addNameAndAdress(userAccount, doc, userAccountTag, billingAccountLanguage);
			addCategories(userAccount, invoice, doc, userAccountTag, true, enterprise);
		}

	}

	public void addNameAndAdress(AccountEntity account, Document doc, Element parent, String languageCode) {
		log.debug("add name and address");

		if (!(account instanceof Customer)) {
			Element nameTag = doc.createElement("name");
			parent.appendChild(nameTag);

			Element quality = doc.createElement("quality");
			if (account.getName().getTitle() != null) {
                Text qualityTxt = doc.createTextNode(catMessagesService.getMessageDescription(account.getName().getTitle(), languageCode));
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

	public static void addproviderContact(AccountEntity account, Document doc, Element parent) {

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

	public static void addPaymentInfo(BillingAccount billingAccount, Document doc, Element parent) {

		log.debug("add payment info");

		Element paymentMethod = doc.createElement("paymentMethod");
		parent.appendChild(paymentMethod);
		paymentMethod.setAttribute("type", billingAccount.getPaymentMethod().toString());

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

	public void addCategories(UserAccount userAccount, Invoice invoice, Document doc, Element parent,
			boolean generateSubCat, boolean enterprise) {

		log.debug("add categories");

		long startDate = System.currentTimeMillis();
		String languageCode = invoice.getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode();

		Element categories = doc.createElement("categories");
		parent.appendChild(categories);
		boolean entreprise = invoice.getProvider().isEntreprise();
		int rounding = invoice.getProvider().getRounding() == null ? 2 : invoice.getProvider().getRounding();

		List<CategoryInvoiceAgregate> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregate>();

		for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {
			if (invoiceAgregate.getUserAccount().getId() == userAccount.getId()) {
				if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
					CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
					categoryInvoiceAgregates.add(categoryInvoiceAgregate);
				}
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
			category.setAttribute("label", (invoiceCategoryLabel != null) ? invoiceCategoryLabel : "");
			category.setAttribute("code",
					invoiceCategory != null && invoiceCategory.getCode() != null ? invoiceCategory.getCode() : "");
			categories.appendChild(category);
			Element amountWithoutTax = doc.createElement("amountWithoutTax");
			Text amountWithoutTaxTxt = doc
					.createTextNode(round(categoryInvoiceAgregate.getAmountWithoutTax(), rounding));
			amountWithoutTax.appendChild(amountWithoutTaxTxt);
			category.appendChild(amountWithoutTax);

			if (!entreprise) {
				Element amountWithTax = doc.createElement("amountWithTax");
				Text amountWithTaxTxt = doc.createTextNode(round(categoryInvoiceAgregate.getAmountWithTax(), rounding));
				amountWithTax.appendChild(amountWithTaxTxt);
				category.appendChild(amountWithTax);
			}
			if (generateSubCat) {
				Element subCategories = doc.createElement("subCategories");
				category.appendChild(subCategories);
				Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = categoryInvoiceAgregate
						.getSubCategoryInvoiceAgregates();

				for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : subCategoryInvoiceAgregates) {
					InvoiceSubCategory invoiceSubCat = subCatInvoiceAgregate.getInvoiceSubCategory();
					List<RatedTransaction> transactions = ratedTransactionService.getRatedTransactions(
							subCatInvoiceAgregate.getWallet(), subCatInvoiceAgregate.getInvoice(),
							subCatInvoiceAgregate.getInvoiceSubCategory());

					String invoiceSubCategoryLabel = invoiceSubCat != null ? catMessagesService.getMessageDescription(
							invoiceSubCat, languageCode) : "";
					Element subCategory = doc.createElement("subCategory");
					subCategories.appendChild(subCategory);
					subCategory.setAttribute("label", (invoiceSubCategoryLabel != null) ? invoiceSubCategoryLabel : "");
					subCategory.setAttribute("code", invoiceSubCat.getCode());
					subCategory.setAttribute("taxCode", subCatInvoiceAgregate.getSubCategoryTax().getCode());
					subCategory.setAttribute("taxPercent", subCatInvoiceAgregate.getSubCategoryTax().getPercent()
							.toPlainString());

					for (RatedTransaction ratedTransaction : transactions) {
						BigDecimal transactionAmount = entreprise ? ratedTransaction.getAmountWithTax()
								: ratedTransaction.getAmountWithoutTax();
						if (transactionAmount == null) {
							transactionAmount = BigDecimal.ZERO;
						}

						Element line = doc.createElement("line");
						String code = "", description = "";
						if (ratedTransaction.getWalletOperationId() != null) {
							WalletOperation walletOperation = getEntityManager().find(WalletOperation.class,
									ratedTransaction.getWalletOperationId());
							code = walletOperation.getCode();
							description = walletOperation.getDescription();
						} else {
							code = ratedTransaction.getCode();
							description = ratedTransaction.getDescription();
						}

						line.setAttribute("code", code != null ? code : "");

						if (ratedTransaction.getParameter1() != null) {
							line.setAttribute("param1", ratedTransaction.getParameter1());
						}
						if (ratedTransaction.getParameter2() != null) {
							line.setAttribute("param2", ratedTransaction.getParameter2());
						}
						if (ratedTransaction.getParameter3() != null) {
							line.setAttribute("param3", ratedTransaction.getParameter3());
						}

                        if (ratedTransaction.getPriceplan() != null) {
                            Element pricePlan = doc.createElement("pricePlan");
                            pricePlan.setAttribute("code", ratedTransaction.getPriceplan().getCode());
                            pricePlan.setAttribute("description", catMessagesService.getMessageDescription(ratedTransaction.getPriceplan(), languageCode));
                            line.appendChild(pricePlan);
                        }						
                        
						Element lebel = doc.createElement("label");
						Text lebelTxt = doc.createTextNode(description != null ? description : "");

						lebel.appendChild(lebelTxt);
						line.appendChild(lebel);

						if (!StringUtils.isBlank(ratedTransaction.getUnityDescription())) {
							Element lineUnit = doc.createElement("unit");
							Text lineUnitTxt = doc.createTextNode(ratedTransaction.getUnityDescription());
							lineUnit.appendChild(lineUnitTxt);
							line.appendChild(lineUnit);
						}
						Element lineUnitAmountWithoutTax = doc.createElement("unitAmountWithoutTax");
						Text lineUnitAmountWithoutTaxTxt = doc.createTextNode(round(
								ratedTransaction.getUnitAmountWithoutTax(), rounding));
						lineUnitAmountWithoutTax.appendChild(lineUnitAmountWithoutTaxTxt);
						line.appendChild(lineUnitAmountWithoutTax);

						Element lineAmountWithoutTax = doc.createElement("amountWithoutTax");
						Text lineAmountWithoutTaxTxt = doc.createTextNode(round(ratedTransaction.getAmountWithoutTax(),
								rounding));
						lineAmountWithoutTax.appendChild(lineAmountWithoutTaxTxt);
						line.appendChild(lineAmountWithoutTax);

						if (!enterprise) {
							Element lineAmountWithTax = doc.createElement("amountWithTax");
							Text lineAmountWithTaxTxt = doc.createTextNode(round(ratedTransaction.getAmountWithTax(),
									rounding));
							lineAmountWithTax.appendChild(lineAmountWithTaxTxt);
							line.appendChild(lineAmountWithTax);
						}

						Element quantity = doc.createElement("quantity");
						Text quantityTxt = doc.createTextNode(ratedTransaction.getQuantity() != null ? ratedTransaction
								.getQuantity().toPlainString() : "");
						quantity.appendChild(quantityTxt);
						line.appendChild(quantity);

						Element usageDate = doc.createElement("usageDate");
						Text usageDateTxt = doc.createTextNode(ratedTransaction.getUsageDate() != null ? DateUtils
								.formatDateWithPattern(ratedTransaction.getUsageDate(), "dd/MM/yyyy") + "" : "");
						usageDate.appendChild(usageDateTxt);
						line.appendChild(usageDate);

						subCategory.appendChild(line);

					}
				}
			}
		}

		log.info("addCategorries time: " + (System.currentTimeMillis() - startDate));
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
				log.debug("ba={}, tradingLanguage={}", invoice.getBillingAccount(), invoice.getBillingAccount()
						.getTradingLanguage());
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

		long startDate = System.currentTimeMillis();
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

				/**
				 * if (entreprise) { Set<SubCategoryInvoiceAgregate>
				 * subCategoryInvoiceAgregates = categoryInvoiceAgregate
				 * .getSubCategoryInvoiceAgregates();
				 * 
				 * for (SubCategoryInvoiceAgregate subCatInvoiceAgregate :
				 * subCategoryInvoiceAgregates) { List<RatedTransaction>
				 * transactions = ratedTransactionService
				 * .getRatedTransactions(subCatInvoiceAgregate .getWallet(),
				 * subCatInvoiceAgregate .getInvoice(), subCatInvoiceAgregate
				 * .getInvoiceSubCategory());
				 * 
				 * log.info("subCatInvoiceAgregate code=" +
				 * subCatInvoiceAgregate.getId() + ",transactions=" +
				 * subCatInvoiceAgregate.getRatedtransactions() .size());
				 * 
				 * Collections.sort(transactions, new
				 * Comparator<RatedTransaction>() { public int
				 * compare(RatedTransaction c0, RatedTransaction c1) { if
				 * (c0.getWalletOperationId() != null &&
				 * c1.getWalletOperationId() != null) { return c0
				 * .getWalletOperationId() .compareTo(
				 * c1.getWalletOperationId()); } return 0; } }); Map<Long,
				 * RatedTransaction> headerRatedTransactions = headerCat
				 * .getRatedtransactions(); for (RatedTransaction
				 * ratedTrnsaction : transactions) { BigDecimal
				 * transactionAmountWithTax = ratedTrnsaction
				 * .getAmountWithTax(); if (transactionAmountWithTax == null) {
				 * transactionAmountWithTax = BigDecimal.ZERO; }
				 * 
				 * if ((!invoice.getProvider() .isDisplayFreeTransacInInvoice()
				 * && transactionAmountWithTax .compareTo(BigDecimal.ZERO) ==
				 * 0)) { continue; }
				 * 
				 * RatedTransaction headerRatedTransaction = null;
				 * 
				 * if (headerRatedTransactions.containsKey(ratedTrnsaction
				 * .getId())) { headerRatedTransaction = headerRatedTransactions
				 * .get(ratedTrnsaction.getId()); headerRatedTransaction
				 * .setAmountWithoutTax(headerRatedTransaction
				 * .getAmountWithoutTax() .add(ratedTrnsaction
				 * .getAmountWithoutTax())); headerRatedTransaction
				 * .setAmountWithTax(headerRatedTransaction
				 * .getAmountWithTax().add( transactionAmountWithTax));
				 * 
				 * } else { headerRatedTransaction = new RatedTransaction();
				 * headerRatedTransaction.setId(ratedTrnsaction .getId());
				 * headerRatedTransaction .setAmountWithoutTax(ratedTrnsaction
				 * .getAmountWithoutTax()); headerRatedTransaction
				 * .setAmountWithTax(ratedTrnsaction .getAmountWithTax());
				 * headerRatedTransactions.put( ratedTrnsaction.getId(),
				 * headerRatedTransaction); } log.info(
				 * "addHeaderCategories headerRatedTransaction amoutHT=" +
				 * headerRatedTransaction.getAmountWithoutTax());
				 * 
				 * } log.info(
				 * "addHeaderCategories headerRatedTransactions.size=" +
				 * headerRatedTransactions.size()); log.info(
				 * "addHeaderCategories headerCat.getRatedtransactions().size="
				 * + headerCat.getRatedtransactions().size());
				 * 
				 * }
				 */
			}

		}
		addHeaderCategories(headerCategories, doc, parent, entreprise, invoice.getProvider());

		log.info("addHeaderCategories time: " + (System.currentTimeMillis() - startDate));
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
			if (!entreprise) {
				Element amountWithTax = doc.createElement("amountWithTax");
				Text amountWithTaxTxt = doc.createTextNode(round(xmlInvoiceHeaderCategoryDTO.getAmountWithTax(),
						rounding));
				amountWithTax.appendChild(amountWithTaxTxt);
				category.appendChild(amountWithTax);
			}

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
					log.info("addHeaderCategories2 headerRatedTransaction amountHT="
							+ headerTransaction.getAmountWithoutTax());
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

	private static String round(BigDecimal amount, Integer scale) {
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		if (scale == null) {
			scale = 2;
		}
		amount = amount.setScale(scale, RoundingMode.HALF_UP);
		return amount.toPlainString();
	}

	@SuppressWarnings("unused")
	private boolean isAllServiceInstancesTerminated(List<ServiceInstance> serviceInstances) {
		for (ServiceInstance service : serviceInstances) {
			boolean serviceActive = service.getStatus() == InstanceStatusEnum.ACTIVE;
			if (serviceActive) {
				return false;
			}
		}
		return true;
	}

}

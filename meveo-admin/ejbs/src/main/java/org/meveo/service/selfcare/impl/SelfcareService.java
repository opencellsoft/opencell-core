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
package org.meveo.service.selfcare.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.EmailNotFoundException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.CustomerAccountStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.crm.impl.EmailService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.selfcare.local.SelfcareServiceLocal;

@Stateless
public class SelfcareService extends PersistenceService<CustomerAccount>
		implements SelfcareServiceLocal {

	@Inject
	private EmailService emailService;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private ResourceBundle resourceMessages;

	public Boolean authenticate(String username, String password)
			throws BusinessException, EmailNotFoundException {
		log.info("start authenticate with username:#0,password:#1", username,
				password);
		if ((username == null || username.equals(""))
				|| (password == null || password.equals(""))) {
			log.warn("Error: required is null with username:#0,password:#1",
					username, password);
			throw new BusinessException(
					"Error when username or password is null!");
		}
		Boolean result = false;
		CustomerAccount customerAccount = findCustomerAccoundByEmail(username);
		if (customerAccount.getStatus() == CustomerAccountStatusEnum.CLOSE) {
			throw new BusinessException("Account closed");
		}

		if (password.equals(customerAccount.getPassword())) {
			result = true;
		}
		log.info("successfully end authenticate with result:#0", result);
		return result;
	}

	public void sendPassword(String email) throws BusinessException,
			EmailNotFoundException {

		log.info("start sendPassword with email:#0", email);
		ParamBean param = ParamBean.getInstance();
		String from = param.getProperty("selfcare.email.from",
				"selfcare@manaty.net");
		String sendpasswordSubject = resourceMessages
				.getString("selfcareemail.sendpassword.subject");// "Your password to log into Seflcare!";
		String sendpasswordBody = resourceMessages
				.getString("selfcareemail.sendpassword.body");// "\n\nyour username:%s\nyour password:%s\n\n";

		log.info("send password for selfcare with email:" + email + ",subject:"
				+ sendpasswordSubject);

		CustomerAccount customerAccount = findCustomerAccoundByEmail(email);

		sendpasswordBody = String.format(sendpasswordBody, email,
				customerAccount.getPassword());

		List<String> to = new ArrayList<String>();
		to.add(email);
		log.info("send email details: from:#0,to:#1,subject:#2,body:#3", from,
				to, sendpasswordSubject, sendpasswordBody);
		emailService.sendEmail(from, to, null, sendpasswordSubject,
				sendpasswordBody, null);
		log.info("successfully send email to #0", email);
	}

	@SuppressWarnings("unchecked")
	private CustomerAccount findCustomerAccoundByEmail(String email)
			throws BusinessException, EmailNotFoundException {
		// @TODO if more customerAccount with the same email? email should
		// attach to customer?
		log.info("start findCustomerAccountByEmail with email:#0", email);
		if (email == null || email.equals("")) {
			log.warn("Error when email is null");
			throw new BusinessException("Error when email is null!");
		}
		List<CustomerAccount> result = null;
		result = this
				.getEntityManager()
				.createQuery(
						"from "
								+ CustomerAccount.class.getSimpleName()
								+ "  where lower(contactInformation.email)=:email")
				.setParameter("email", email.toLowerCase()).getResultList();
		if (result == null || result.size() == 0) {// if more than 0, how to
			// do???
			log.warn("Error when nonexisted customer account with email:#0",
					email);
			throw new EmailNotFoundException(
					"Retrieve null customer accounts with email:" + email);
		}
		log.info(
				"successfully end findCustomerAccountByEmail email:#0 with result size:#1",
				email, result.size());
		return result.get(0);
	}

	public Boolean updatePassword(String username, String oldpassword,
			String newpassword) throws BusinessException {
		Boolean result = Boolean.FALSE;
		log.info(
				"start updatePassword with username:#0,password:#1,newPassword:#2",
				username, oldpassword, newpassword);
		if ((username == null || username.equals(""))
				|| (oldpassword == null || oldpassword.equals(""))
				|| (newpassword == null || newpassword.equals(""))) {
			log.warn(
					"Error: required is null with username:#0,password:#1,newpassword:#2",
					username, oldpassword, newpassword);
			throw new BusinessException(
					"Error when username or password or newpassword is null!");
		}
		CustomerAccount customerAccount = findCustomerAccoundByEmail(username);

		if (oldpassword.equals(customerAccount.getPassword())) {
			customerAccount.setPassword(newpassword);
			this.update(customerAccount);
			result = Boolean.TRUE;
		}
		log.info("successfully end updatePassword with result:#0", result);
		return result;
	}

	public CustomerAccount getCustomerAccount(String username)
			throws BusinessException {
		log.info("start searching with username:#0");
		if ((username == null || username.equals(""))) {
			log.warn("Error: required is null with username:#0", username);
			throw new BusinessException("Error when username is null!");
		}
		CustomerAccount customerAccount = findCustomerAccoundByEmail(username);
		return customerAccount;
	}

	@SuppressWarnings("unchecked")
	public List<BillingAccount> getBillingAccounts(String username)
			throws BusinessException {
		log.info("start searching customer account for username:#0");
		CustomerAccount customerAccount = getCustomerAccount(username);
		log.info("start searching billing accounts for user: #0",
				customerAccount.getCode());
		List<BillingAccount> result = null;
		result = this
				.getEntityManager()
				.createQuery(
						"from " + BillingAccount.class.getSimpleName()
								+ "  where customer_account_id=:id")
				.setParameter("id", customerAccount.getId()).getResultList();
		return result;
	}

	// FIXME : must add provider code
	public List<Invoice> getBillingAccountInvoices(String code)
			throws BusinessException {
		// log.info("start searching invoices for billing account:#0",
		// billingAccount.getCode());
		// List<Invoice> invoices =
		// billingAccountService.invoiceList(billingAccount);
		return null;// invoices;
	}

	public List<Invoice> getBillingAccountValidatedInvoices(String code)
			throws BusinessException {
		log.info("start searching validated invoices for billing account:#0",
				code);
		List<Invoice> invoices = getBillingAccountInvoices(code);
		if (invoices != null) {
			Iterator<Invoice> it = invoices.iterator();
			while (it.hasNext()) {
				Invoice invoice = it.next();
				if (invoice.getBillingRun() != null
						&& invoice.getBillingRun().getStatus() != BillingRunStatusEnum.VALIDATED
						&& invoice.getPdf() != null) {
					it.remove();
				}
			}
		}
		return invoices;
	}

	public BigDecimal getAccountBalance(String code) throws BusinessException {
		log.info("start getAccountBalance for code:#0", code);
		BigDecimal balance = customerAccountService.customerAccountBalanceDue(
				null, code, new Date());
		log.info("calculating balance for customer acccount:#0", balance);
		return balance;
	}

	public byte[] getPDFInvoice(String invoiceNumber, String providerCode)
			throws BusinessException {
		Invoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber,
				providerCode);
		if (invoice != null) {
			return invoice.getPdf();
		} else {
			return null;
		}
	}

	public byte[] getPDFInvoice(String invoiceNumber) throws BusinessException {
		Invoice invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
		if (invoice != null) {
			return invoice.getPdf();
		} else {
			return null;
		}
	}

	public void sendMail(String from, List<String> to, List<String> cc,
			String subject, String body, List<File> files)
			throws BusinessException {
		emailService.sendEmail(from, to, cc, subject, body, files);
	}

	public void sendEmailCreationSpace(String email) throws BusinessException,
			EmailNotFoundException {
		log.info("start sendEmailCreationSpace with email:#0", email);
		ParamBean param = ParamBean.getInstance();
		String from = param.getProperty("selfcare.email.from",
				"selfcare@manaty.net");
		String sendpasswordSubject = resourceMessages
				.getString("selfcareemail.creationSpace.subject");
		String sendpasswordBody = resourceMessages
				.getString("selfcareemail.creationSpace.body");
		CustomerAccount customerAccount = findCustomerAccoundByEmail(email);
		sendpasswordBody = String.format(sendpasswordBody,
				customerAccount.getPassword());
		sendpasswordSubject = String.format(sendpasswordSubject,
				customerAccount.getName().toString());
		log.info("send password for selfcare with email:" + email + ",subject:"
				+ sendpasswordSubject);
		List<String> to = new ArrayList<String>();
		to.add(email);
		log.info("send email details: from:#0,to:#1,subject:#2,body:#3", from,
				to, sendpasswordSubject, sendpasswordBody);
		emailService.sendEmail(from, to, null, sendpasswordSubject,
				sendpasswordBody, null);
		log.info("successfully send email to #0", email);
	}

}

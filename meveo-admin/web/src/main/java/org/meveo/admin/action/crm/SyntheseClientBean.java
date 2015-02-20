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
package org.meveo.admin.action.crm;

import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
//import org.meveo.model.billing.Invoice;
//import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.selfcare.local.SelfcareServiceLocal;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class SyntheseClientBean extends BaseBean<BillingAccount> {

	private static final long serialVersionUID = 1L;

	@Inject
	private SelfcareServiceLocal selfcareService;

	@Inject
	private BillingAccountService billingAccountService;

	// @Named TODO migration
	// @Produces
	//private CustomerAccount synCustomerAccount;

	// @Named TODO migration
	// @Produces
	private BillingAccount synBillingAccount;

	// @Named TODO migration
	// @Produces
	//private List<Invoice> synInvoices;

	// @Named TODO migration
	// @Produces
	//private BigDecimal customerAccountBalance;

	public SyntheseClientBean() {
		super(BillingAccount.class);
	}

	// TODO: @Begin(nested = true)
	// @Factory("synBillingAccount")
	public void init() {
		synBillingAccount = (BillingAccount) initEntity();
		if (synBillingAccount.getId() == null) {
			return;
		}
		//synCustomerAccount = synBillingAccount.getCustomerAccount();
		//try {
			//customerAccountBalance = selfcareService
			//		.getAccountBalance(synCustomerAccount.getCode());
			//synInvoices = selfcareService
			//		.getBillingAccountValidatedInvoices(synBillingAccount
			///				.getCode());
		//} catch (BusinessException e) {
		//	log.error("Error:#0 when try to retrieve accountBalance with #1",
		//			e.getMessage(), synCustomerAccount.getCode());
		//}
	}

	@Override
	protected IPersistenceService<BillingAccount> getPersistenceService() {
		return billingAccountService;
	}

	public void downloadPdf(String invoiceNumber) {

		byte[] pdf = null;
		try {
			pdf = selfcareService.getPDFInvoice(invoiceNumber);
		} catch (BusinessException e1) {
			log.error("Error:#0, when retrieve pdf array with number #1",
					e1.getMessage(), invoiceNumber);
		}
		if (pdf == null || pdf.length == 0) {
			return;
		}
		try {
			javax.faces.context.FacesContext context = javax.faces.context.FacesContext
					.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context
					.getExternalContext().getResponse();
			res.setContentType("application/pdf");
			res.setContentLength(pdf.length);
			res.addHeader("Content-disposition",
					"attachment;filename=\"invoice_" + invoiceNumber + ".pdf\"");

			ServletOutputStream out = res.getOutputStream();

			out.write(pdf);
			out.flush();
			out.close();
			context.responseComplete();

		} catch (IOException e) {
			log.error("Error:#0, when output invoice with number #1",
					e.getMessage(), invoiceNumber);
		}

	}
}

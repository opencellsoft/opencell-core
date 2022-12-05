/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.crm;

import jakarta.faces.view.ViewScoped;
//import java.math.BigDecimal;
//import java.util.List;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.BillingAccount;
//import org.meveo.model.billing.Invoice;
//import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;

@Named
@ViewScoped
public class SyntheseClientBean extends BaseBean<BillingAccount> {

	private static final long serialVersionUID = 1L;

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

	/*public void downloadPdf(String invoiceNumber) {

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
			jakarta.faces.context.FacesContext context = jakarta.faces.context.FacesContext
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

	}*/

}

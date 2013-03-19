/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.crm;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.selfcare.local.SelfcareServiceLocal;

/**
 * 
 * 
 * @author Tyshan(tyshan@manaty.net)
 * @created 2011.2.15
 */
@Named
// TODO: @Scope(ScopeType.CONVERSATION)
public class SyntheseClientBean extends BaseBean<BillingAccount> {

	private static final long serialVersionUID = 1L;

	@Inject
	private SelfcareServiceLocal selfcareService;

	@Inject
	private BillingAccountService billingAccountService;

	@Named
	@Produces
	private CustomerAccount synCustomerAccount;

	@Named
	@Produces
	private BillingAccount synBillingAccount;

	@Named
	@Produces
	private List<Invoice> synInvoices;

	@Named
	@Produces
	private BigDecimal customerAccountBalance;

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
		synCustomerAccount = synBillingAccount.getCustomerAccount();
		try {
			customerAccountBalance = selfcareService
					.getAccountBalance(synCustomerAccount.getCode());
			synInvoices = selfcareService.getBillingAccountValidatedInvoices(synBillingAccount
					.getCode());
		} catch (BusinessException e) {
			log.error("Error:#0 when try to retrieve accountBalance with #1", e.getMessage(),
					synCustomerAccount.getCode());
		}
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
			log.error("Error:#0, when retrieve pdf array with number #1", e1.getMessage(),
					invoiceNumber);
		}
		if (pdf == null || pdf.length == 0) {
			return;
		}
		try {
			javax.faces.context.FacesContext context = javax.faces.context.FacesContext
					.getCurrentInstance();
			HttpServletResponse res = (HttpServletResponse) context.getExternalContext()
					.getResponse();
			res.setContentType("application/pdf");
			res.setContentLength(pdf.length);
			res.addHeader("Content-disposition", "attachment;filename=\"invoice_" + invoiceNumber
					+ ".pdf\"");

			ServletOutputStream out = res.getOutputStream();

			out.write(pdf);
			out.flush();
			out.close();
			context.responseComplete();

		} catch (IOException e) {
			log.error("Error:#0, when output invoice with number #1", e.getMessage(), invoiceNumber);
		}

	}
}

package org.meveo.api.invoice;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.payments.impl.CustomerAccountService;

/**
 * @author R.AITYAAZZA
 * 
 */
@Stateless
public class PdfInvoiceApi extends BaseApi {

	@Inject
	ProviderService providerService;

	@Inject
	InvoiceService invoiceService;

	@Inject
	BillingAccountService billingAccountService;

	@Inject
	private CustomerAccountService customerAccountService;

	public byte[] getPDFInvoice(String invoiceNumber,
			String customerAccountCode, User currentUser) throws Exception {
		Invoice invoice = new Invoice();
		if (!StringUtils.isBlank(invoiceNumber)
				&& !StringUtils.isBlank(customerAccountCode)) {
			Provider provider = currentUser.getProvider();
			CustomerAccount customerAccount = customerAccountService
					.findByCode(customerAccountCode, provider);
			if (customerAccount == null) {
				throw new BusinessException(
						"Cannot find customer account with code="
								+ customerAccountCode);
			}
			invoice = invoiceService.getInvoice(invoiceNumber, customerAccount);
		} else {
			if (StringUtils.isBlank(invoiceNumber)) {
				missingParameters.add("invoiceNumber");
			}

			if (StringUtils.isBlank(customerAccountCode)) {
				missingParameters.add("CustomerAccountCode");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}

		return invoice.getPdf();
	}
}
package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
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
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
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
				&& !StringUtils.isBlank(customerAccountCode)
			) {
			Provider provider = currentUser.getProvider();
			CustomerAccount customerAccount = customerAccountService
					.findByCode(em, customerAccountCode, provider);
			if (customerAccount == null) {
				throw new BusinessException(
						"Cannot find customer account with code="
								+ customerAccountCode);
			}
			invoice = invoiceService.getInvoice(em, invoiceNumber,
					customerAccount);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(invoiceNumber)) {
				missingFields.add("invoiceNumber");
			}

			if (StringUtils.isBlank(customerAccountCode)) {
				missingFields.add("CustomerAccountCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());

		}

		return invoice.getPdf();
	}

}
package org.meveo.apiv2.billing.impl;

import org.meveo.apiv2.billing.ImmutableInvoice;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.order.Order;

public class InvoiceMapper extends ResourceMapper<org.meveo.apiv2.billing.Invoice, Invoice> {

	@Override
	protected org.meveo.apiv2.billing.Invoice toResource(Invoice entity) {

		return ImmutableInvoice.builder().billingAccount(buildById(entity.getBillingAccount()))
				.billingRun(buildById(entity.getBillingRun())).recordedInvoice(buildById(entity.getRecordedInvoice()))
				.status(entity.getStatus()).paymentStatus(entity.getPaymentStatus())
				.invoiceNumber(entity.getInvoiceNumber()).productDate(entity.getProductDate())
				.invoiceDate(entity.getInvoiceDate()).dueDate(entity.getDueDate()).amount(entity.getAmount())
				.discount(entity.getDiscount()).amountWithoutTax(entity.getAmountWithoutTax())
				.amountTax(entity.getAmountTax()).amountWithTax(entity.getAmountWithTax())
				.netToPay(entity.getNetToPay()).iban(entity.getIban()).alias(entity.getAlias())
				.tradingCurrency(buildById(entity.getTradingCurrency()))
				.tradingCountry(buildById(entity.getTradingCountry()))
				.tradingLanguage(buildById(entity.getTradingLanguage())).comment(entity.getComment())
				.invoiceType(buildById(entity.getInvoiceType())).quote(buildById(entity.getQuote()))
				.subscription(buildById(entity.getSubscription())).order(buildById(entity.getOrder()))
				.xmlFilename(entity.getXmlFilename()).pdfFilename(entity.getPdfFilename())
				.paymentMethod(buildById(entity.getPaymentMethod())).dueBalance(entity.getDueBalance())
				.seller(buildById(entity.getSeller())).externalRef(entity.getExternalRef())
				.rejectReason(entity.getRejectReason()).initialCollectionDate(entity.getInitialCollectionDate())
				.statusDate(entity.getStatusDate()).xmlDate(entity.getXmlDate()).pdfDate(entity.getPdfDate())
				.emailSentDate(entity.getEmailSentDate()).paymentStatusDate(entity.getPaymentStatusDate())
				.startDate(entity.getStartDate()).endDate(entity.getEndDate()).rawAmount(entity.getRawAmount())
				.discountRate(entity.getDiscountRate()).discountAmount(entity.getDiscountAmount())
				.invoiceAdjustmentCurrentSellerNb(entity.getInvoiceAdjustmentCurrentSellerNb())
				.invoiceAdjustmentCurrentProviderNb(entity.getInvoiceAdjustmentCurrentProviderNb())
				.previousInvoiceNumber(entity.getPreviousInvoiceNumber()).draft(entity.getDraft())
				.code(entity.getCode()).description(entity.getDescription()).build();
	}

	private ImmutableResource buildById(BaseEntity entity) {
		return entity != null ? ImmutableResource.builder().id(entity.getId()).build() : null;
	}

	@Override
	protected Invoice toEntity(org.meveo.apiv2.billing.Invoice resource) {
		Invoice invoice = new Invoice();

		if (resource.getBillingRun() != null) {
			BillingRun billingRun = new BillingRun();
			billingRun.setId(resource.getBillingRun().getId());
			invoice.setBillingRun(billingRun);
		}

		BillingAccount billingAccount = new BillingAccount();
		billingAccount.setId(resource.getBillingAccount().getId());
		invoice.setBillingAccount(billingAccount);

		invoice.setDiscountRate(resource.getDiscountRate());
		invoice.setAmountWithTax(resource.getAmountWithTax());

		invoice.setStartDate(resource.getStartDate());
		invoice.setEndDate(resource.getEndDate());

		if (resource.getOrder() != null) {
			Order order = new Order();
			order.setId(resource.getOrder().getId());
			invoice.setOrder(order);
		}

		return invoice;
	}
}

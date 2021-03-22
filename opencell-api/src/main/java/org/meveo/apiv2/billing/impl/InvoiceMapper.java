package org.meveo.apiv2.billing.impl;

import java.util.stream.Collectors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.apiv2.billing.ImmutableInvoice;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.order.Order;

public class InvoiceMapper extends ResourceMapper<org.meveo.apiv2.billing.Invoice, Invoice> {

	@Override
	public org.meveo.apiv2.billing.Invoice toResource(Invoice entity) {
		try {
			ImmutableInvoice resource = (ImmutableInvoice) initResource(ImmutableInvoice.class, entity);
			return ImmutableInvoice.builder().from(resource).id(entity.getId())
					.billingRun(buildById(entity.getBillingRun()))
					.recordedInvoice(buildById(entity.getRecordedInvoice()))
					.tradingCurrency(buildById(entity.getTradingCurrency()))
					.tradingCountry(buildById(entity.getTradingCountry()))
					.tradingLanguage(buildById(entity.getTradingLanguage())).quote(buildById(entity.getQuote()))
					.paymentMethod(buildById(entity.getPaymentMethod()))
					.listLinkedInvoices(entity.getLinkedInvoices() == null ? null : entity.getLinkedInvoices().stream().map(x->x.getId()).collect(Collectors.toList()))
					.subscription(buildById(entity.getSubscription())).order(buildById(entity.getOrder())).build();
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	private ImmutableResource buildById(BaseEntity entity) {
		return entity != null ? ImmutableResource.builder().id(entity.getId()).build() : null;
	}

	@Override
	public Invoice toEntity(org.meveo.apiv2.billing.Invoice resource) {
		try {
			Invoice invoice = initEntity(resource, new Invoice());
			invoice.setId(resource.getId());
			if (resource.getBillingRun() != null) {
				BillingRun billingRun = new BillingRun();
				billingRun.setId(resource.getBillingRun().getId());
				invoice.setBillingRun(billingRun);
			}

			if (resource.getOrder() != null) {
				Order order = new Order();
				order.setId(resource.getOrder().getId());
				invoice.setOrder(order);
			}

			return invoice;
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}
}

package org.meveo.apiv2.billing.impl;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import org.meveo.apiv2.billing.ImmutableInvoiceLine;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.model.cpq.commercial.OrderLot;
import org.meveo.service.billing.impl.InvoiceService;

public class InvoiceLineMapper extends ResourceMapper<org.meveo.apiv2.billing.InvoiceLine, InvoiceLine> {

	@Inject
	private InvoiceService invoiceService;

	@Override
	protected org.meveo.apiv2.billing.InvoiceLine toResource(InvoiceLine entity) {

		return ImmutableInvoiceLine.builder().billingAccountCode(getCode(entity.getBillingAccount()))
				.amountWithoutTax(entity.getAmountWithoutTax())
				.amountTax(entity.getAmountTax()).amountWithTax(entity.getAmountWithTax())
				/*
				.billingRun(getCode(entity.getBillingRun())).recordedInvoiceLine(getCode(entity.getRecordedInvoiceLine()))
				.status(entity.getStatus()).paymentStatus(entity.getPaymentStatus())
				.invoiceLineNumber(entity.getInvoiceLineNumber()).productDate(entity.getProductDate())
				.invoiceLineDate(entity.getInvoiceLineDate()).dueDate(entity.getDueDate()).amount(entity.getAmount())
				.discount(entity.getDiscount())
				.netToPay(entity.getNetToPay()).iban(entity.getIban()).alias(entity.getAlias())
				.tradingCurrency(getCode(entity.getTradingCurrency()))
				.tradingCountry(getCode(entity.getTradingCountry()))
				.tradingLanguage(getCode(entity.getTradingLanguage())).comment(entity.getComment())
				.invoiceLineType(getCode(entity.getInvoiceLineType())).quote(getCode(entity.getQuote()))
				.subscription(getCode(entity.getSubscription())).order(getCode(entity.getOrder()))
				.xmlFilename(entity.getXmlFilename()).pdfFilename(entity.getPdfFilename())
				.paymentMethod(getCode(entity.getPaymentMethod())).dueBalance(entity.getDueBalance())
				.seller(getCode(entity.getSeller())).externalRef(entity.getExternalRef())
				.rejectReason(entity.getRejectReason()).initialCollectionDate(entity.getInitialCollectionDate())
				.statusDate(entity.getStatusDate()).xmlDate(entity.getXmlDate()).pdfDate(entity.getPdfDate())
				.emailSentDate(entity.getEmailSentDate()).paymentStatusDate(entity.getPaymentStatusDate())
				.startDate(entity.getStartDate()).endDate(entity.getEndDate()).rawAmount(entity.getRawAmount())
				.discountRate(entity.getDiscountRate()).discountAmount(entity.getDiscountAmount())
				.invoiceLineAdjustmentCurrentSellerNb(entity.getInvoiceLineAdjustmentCurrentSellerNb())
				.invoiceLineAdjustmentCurrentProviderNb(entity.getInvoiceLineAdjustmentCurrentProviderNb())
				.previousInvoiceLineNumber(entity.getPreviousInvoiceLineNumber()).draft(entity.getDraft())
				.description(entity.getDescription())*/
				.build();
	}

	private String getCode(BusinessEntity entity) {
		return entity != null ? entity.getCode() : null;
	}

	@Override
	protected InvoiceLine toEntity(org.meveo.apiv2.billing.InvoiceLine resource) {
		InvoiceLine invoiceLine = new InvoiceLine();
		invoiceLine.setPrestation(resource.getPrestation());
		invoiceLine.setQuantity (resource.getQuantity());
		invoiceLine.setUnitPrice( resource.getUnitPrice());
		invoiceLine.setDiscountRate( resource.getDiscountRate());
		invoiceLine.setAmountWithoutTax( resource.getAmountWithoutTax());
		invoiceLine.setTaxRate( resource.getTaxRate());
		invoiceLine.setAmountWithTax( resource.getAmountWithTax());
		invoiceLine.setAmountTax( resource.getAmountTax());
		invoiceLine.setOrderRef( resource.getOrderRef());
		invoiceLine.setAccessPoint( resource.getAccessPoint());
		invoiceLine.setValueDate( resource.getValueDate());
		invoiceLine.setOrderNumber( resource.getOrderNumber());
		invoiceLine.setDiscountAmount( resource.getDiscountAmount());
		invoiceLine.setLabel( resource.getLabel());
		invoiceLine.setRawAmount( resource.getRawAmount());
		invoiceLine.setTaxRecalculated( resource.isTaxRecalculated());
		
		invoiceLine.setServiceInstance((ServiceInstance)tryToFindByEntityClassAndCode(ServiceInstance.class, resource.getServiceInstanceCode()));
		invoiceLine.setSubscription((Subscription)tryToFindByEntityClassAndCode(Subscription.class, resource.getSubscriptionCode()));
		invoiceLine.setProduct((Product)tryToFindByEntityClassAndCode(Product.class, resource.getProductCode()));
		invoiceLine.setAccountingArticle((AccountingArticle)tryToFindByEntityClassAndCode(AccountingArticle.class, resource.getAccountingArticleCode()));
		invoiceLine.setServiceTemplate((ServiceTemplate)tryToFindByEntityClassAndCode(ServiceTemplate.class, resource.getServiceTemplateCode()));
		invoiceLine.setDiscountPlan((DiscountPlan)tryToFindByEntityClassAndCode(DiscountPlan.class, resource.getDiscountPlanCode()));
		invoiceLine.setTax((Tax)tryToFindByEntityClassAndCode(Tax.class, resource.getTaxCode()));
		invoiceLine.setOrderLot((OrderLot)tryToFindByEntityClassAndCode(OrderLot.class, resource.getOrderLotCode()));
		invoiceLine.setBillingAccount((BillingAccount)tryToFindByEntityClassAndCode(BillingAccount.class, resource.getBillingAccountCode()));
		invoiceLine.setOfferTemplate((OfferTemplate)tryToFindByEntityClassAndCode(OfferTemplate.class, resource.getOfferTemplateCode()));
		/*
		invoiceLine.setProductVersion((ProductVersion)tryToFindByEntityClassAndCode(ProductVersion.class, resource.getProductVersionCode()));
		invoiceLine.setOfferServiceTemplate((OfferServiceTemplate)tryToFindByEntityClassAndCode(OfferServiceTemplate.class, resource.getOfferServiceTemplateCode()));
		invoiceLine.setCommercialOrder((CommercialOrder)tryToFindByEntityClassAndCode(CommercialOrder.class, resource.getCommercialOrderCode()));
		invoiceLine.setBillingRun((BillingRun)tryToFindByEntityClassAndCode(BillingRun.class, resource.getBillingRunCode()));
		 */
		
		return invoiceLine;
	}
	
    public BusinessEntity tryToFindByEntityClassAndCode(Class<? extends BusinessEntity> entity, String code) {
    	if(code==null) {
    		return null;
    	}
        QueryBuilder qb = new QueryBuilder(entity, "entity", null);
        qb.addCriterion("entity", "=", code, true);

        try {
            return (BusinessEntity) qb.getQuery(invoiceService.getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException("No entity of type "+entity.getSimpleName()+"with code '"+code+"' found");
        } catch (NonUniqueResultException e) {
        	throw new ForbiddenException("More than one entity of type "+entity.getSimpleName()+" with code '"+code+"' found");
        }
    }
}

package org.meveo.apiv2.billing.impl;

import static java.util.stream.Collectors.toList;
import static org.meveo.commons.utils.EjbUtils.getServiceInterface;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.invoice.GenerateInvoiceRequestDto;
import org.meveo.apiv2.billing.GenerateInvoiceInput;
import org.meveo.apiv2.billing.GenerateInvoiceResult;
import org.meveo.apiv2.billing.InvoiceLine;
import org.meveo.apiv2.billing.ImmutableGenerateInvoiceResult;
import org.meveo.apiv2.billing.ImmutableInvoice;
import org.meveo.apiv2.billing.ImmutableInvoiceLine;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.ordering.ResourceMapper;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.Invoice;
import org.meveo.model.order.Order;
import org.meveo.service.billing.impl.article.AccountingArticleService;

public class InvoiceMapper extends ResourceMapper<org.meveo.apiv2.billing.Invoice, Invoice> {

	private AccountingArticleService accountingArticleService =
			(AccountingArticleService) getServiceInterface(AccountingArticleService.class.getSimpleName());

	@Override
	public org.meveo.apiv2.billing.Invoice toResource(Invoice entity) {
		try {
			ImmutableInvoice resource = (ImmutableInvoice) initResource(ImmutableInvoice.class, entity);
			return ImmutableInvoice.builder().from(resource).id(entity.getId())
					.billingRun(buildById(entity.getBillingRun()))
					.recordedInvoice(buildById(entity.getRecordedInvoice()))
					.tradingCurrency(buildById(entity.getTradingCurrency()))
					.tradingCountry(buildById(entity.getTradingCountry()))
					.discountPlan(buildById(entity.getDiscountPlan()))
					.tradingLanguage(buildById(entity.getTradingLanguage())).quote(buildById(entity.getQuote()))
					.paymentMethod(buildById(entity.getPaymentMethod()))
					.listLinkedInvoices(entity.getLinkedInvoices() == null ? null : entity.getLinkedInvoices().stream().map(x -> x.getLinkedInvoiceValue().getId()).collect(toList()))
					.invoiceLines(buildInvoiceLines(entity.getInvoiceLines()))
					.subscription(buildById(entity.getSubscription())).order(buildById(entity.getOrder())).build();
		} catch (Exception e) {
			throw new BusinessException(e);
		}
	}

	private List<InvoiceLine> buildInvoiceLines(List<org.meveo.model.billing.InvoiceLine> invoiceLines) {
		if(invoiceLines != null) {
			return invoiceLines.stream()
					.map(il ->
							ImmutableInvoiceLine.builder()
									.id(il.getId())
									.accountingArticleCode(accountingArticleService.findById(il.getAccountingArticle().getId()).getCode())
									.build())
					.collect(toList());
		} else {
			return null;
		}
	}

	public List<org.meveo.apiv2.billing.Invoice> toResources(List<Invoice> invoices) {
		if(CollectionUtils.isEmpty(invoices)) {
			return null;
		}
		return invoices.stream().map(i->toResource(i)).collect(toList());
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

	public GenerateInvoiceRequestDto toGenerateInvoiceRequestDto(GenerateInvoiceInput invoiceInput) {
		GenerateInvoiceRequestDto generateInvoiceRequestDto = new GenerateInvoiceRequestDto();
		generateInvoiceRequestDto.setTargetCode(invoiceInput.getTargetCode());
		generateInvoiceRequestDto.setTargetType(invoiceInput.getTargetType());
		generateInvoiceRequestDto.setGenerateAO(invoiceInput.isGenerateAO());
		generateInvoiceRequestDto.setGeneratePDF(invoiceInput.isGeneratePDF());
		generateInvoiceRequestDto.setGenerateXML(invoiceInput.isGenerateXML());
		generateInvoiceRequestDto.setInvoicingDate(invoiceInput.getInvoicingDate());
		generateInvoiceRequestDto.setApplyMinimum(invoiceInput.getApplyMinimum());
		generateInvoiceRequestDto.setBillingAccountCode(invoiceInput.getBillingAccountCode());
		generateInvoiceRequestDto.setCustomFields(invoiceInput.getCustomFields());
		generateInvoiceRequestDto.setFilter(invoiceInput.getFilters());
		generateInvoiceRequestDto.setIncludeRatedTransactions(invoiceInput.isIncludeRatedTransactions());
		generateInvoiceRequestDto.setLastTransactionDate(invoiceInput.getLastTransactionDate());
		generateInvoiceRequestDto.setFirstTransactionDate(invoiceInput.getFirstTransactionDate());
		generateInvoiceRequestDto.setSkipValidation(invoiceInput.isSkipValidation());
		generateInvoiceRequestDto.setOrderNumber(invoiceInput.getOrderNumber());
		generateInvoiceRequestDto.setApplyBillingRules(invoiceInput.isApplyBillingRules());
		generateInvoiceRequestDto.setOpenOrderCode(invoiceInput.getOpenOrderCode());
		generateInvoiceRequestDto.setPurchaseOrder(invoiceInput.getPurchaseOrder());
		return generateInvoiceRequestDto;
	}

	public GenerateInvoiceResult toGenerateInvoiceResult(Invoice invoice, String invoiceTypeCode, Long recordedInvoice) {
		GenerateInvoiceResult invoiceResult = ImmutableGenerateInvoiceResult.builder()
				.id(invoice.getId())
				.amount(Optional.ofNullable(invoice.getAmount()).orElse(BigDecimal.ZERO))
				.invoiceDate(invoice.getInvoiceDate())
				.temporaryInvoiceNumber(Optional.ofNullable(invoice.getTemporaryInvoiceNumber()).orElse(""))
				.dueDate(invoice.getDueDate())
				.amountTax(invoice.getAmountTax())
				.amountWithTax(invoice.getAmountTax())
				.amountWithoutTax(invoice.getAmountWithoutTax())
				.invoiceNumber(invoice.getInvoiceNumber())
				.xmlFilename(invoice.getXmlFilename())
				.pdfFilename(invoice.getPdfFilename())
				.rawAmount(invoice.getRawAmount())
				.discountAmount(invoice.getDiscountAmount())
				.invoiceTypeCode(invoiceTypeCode)
				.accountOperationId(recordedInvoice)
				.discount(invoice.getDiscount())
				.isAlreadySent(invoice.isAlreadySent())
				.isDetailedInvoice(invoice.isDetailedInvoice())
				.isAlreadyAddedDiscount(invoice.isAlreadyAddedDiscount())
				.isAlreadyAppliedMinimum(invoice.isAlreadyAppliedMinimum())
				.isDontSend(invoice.isDontSend())
				.isPrepaid(invoice.isPrepaid())
				.initialCollectionDate(invoice.getInitialCollectionDate())
				.netToPay(invoice.getNetToPay())
				.status(invoice.getStatus())
				.paymentMethodType(invoice.getPaymentMethodType())
				.paymentStatus(invoice.getPaymentStatus())
				.statusDate(invoice.getStatusDate())
				.startDate(invoice.getStartDate())
				.endDate(invoice.getEndDate())
				.description(invoice.getDescription())
				.previousInvoiceNumber(invoice.getPreviousInvoiceNumber())
				.purchaseOrder(invoice.getExternalPurchaseOrderNumber())
				.build();
		return invoiceResult;
	}
}

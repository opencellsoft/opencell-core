package org.meveo.admin.action.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.CreditNote;
import org.meveo.model.billing.CreditNoteLine;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.CreditNoteService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.XmlCreditNoteCreator;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class CreditNoteBean extends BaseBean<CreditNote> {

	private static final long serialVersionUID = 8194911872467001626L;

	@Inject
	private CreditNoteService creditNoteService;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	@RequestParam()
	private Instance<Long> invoiceIdParam;

	@Inject
	private XmlCreditNoteCreator xmlCreditNoteCreator;

	private List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates;

	public CreditNoteBean() {
		super(CreditNote.class);
	}

	@Override
	public CreditNote initEntity() {
		CreditNote creditNote = super.initEntity();

		if (creditNote.isTransient() && invoiceIdParam != null && invoiceIdParam.get() != null) {
			if (creditNote.getInvoice() == null) {
				Invoice invoice = invoiceService.findById(invoiceIdParam.get());
				creditNote.setInvoice(invoice);
				creditNote.setBillingAccount(invoice.getBillingAccount());
				creditNote.setAmountWithoutTax(invoice.getAmountWithoutTax());
				creditNote.setAmountWithTax(invoice.getAmountWithTax());
				creditNote.setNetToPay(invoice.getNetToPay());
				creditNote.setCode(creditNoteService.getCreditNoteNumber(creditNote, getCurrentUser()));

				Map<String, BigDecimal> taxAmounts = new HashMap<String, BigDecimal>();

				for (InvoiceAgregate invoiceAgregate : creditNote.getInvoice().getInvoiceAgregates()) {
					if (invoiceAgregate instanceof TaxInvoiceAgregate) {
						TaxInvoiceAgregate taxInvoiceAgregate = (TaxInvoiceAgregate) invoiceAgregate;
						taxAmounts.put(taxInvoiceAgregate.getTax().getCode(), taxInvoiceAgregate.getAmountTax());
					}
				}

				creditNote.setTaxAmounts(taxAmounts);
			}

			if (creditNote.getCreditNoteLines() == null || creditNote.getCreditNoteLines().size() == 0) {
				List<CreditNoteLine> creditNoteLines = new ArrayList<CreditNoteLine>();

				for (InvoiceAgregate invoiceAgregate : creditNote.getInvoice().getInvoiceAgregates()) {
					if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
						((SubCategoryInvoiceAgregate) invoiceAgregate).getInvoiceSubCategory().getCode();

						SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = (SubCategoryInvoiceAgregate) invoiceAgregate;

						CreditNoteLine creditNoteLine = new CreditNoteLine();
						creditNoteLine.setCreditNote(creditNote);
						creditNoteLine.setDescription(subCategoryInvoiceAgregate.getInvoiceSubCategory()
								.getDescription());
						creditNoteLine.setInvoiceSubCategory(subCategoryInvoiceAgregate.getInvoiceSubCategory());
						creditNoteLine.setAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
						creditNoteLine.setAmountWithTax(subCategoryInvoiceAgregate.getAmountWithTax());
						creditNoteLine.setTaxAmount(subCategoryInvoiceAgregate.getAmountTax());

						creditNoteLines.add(creditNoteLine);
					}
				}

				creditNote.setCreditNoteLines(creditNoteLines);
			}
		}

		return creditNote;
	}

	public void reCompute(CreditNoteLine line) {
		line.computeWithTax();
	}

	@Override
	public String getEditViewName() {
		return "creditNoteDetail";
	}

	@Override
	protected IPersistenceService<CreditNote> getPersistenceService() {
		return creditNoteService;
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		// save the sequence
		if (entity.isTransient()) {
			Long creditNoteNo = Long.parseLong(entity.getCode());
			creditNoteService.updateCreditNoteNb(entity, creditNoteNo);
		}

		super.saveOrUpdate(killConversation);

		// create xml credit note
		xmlCreditNoteCreator.createXmlCreditNote(entity);

		return "/pages/billing/invoices/invoiceDetail.jsf?objectId=" + entity.getInvoice().getId() + "&cid="
				+ conversation.getId() + "&faces-redirect=true&includeViewParams=true";
	}

	public Instance<Long> getInvoiceIdParam() {
		return invoiceIdParam;
	}

	public void setInvoiceIdParam(Instance<Long> invoiceIdParam) {
		this.invoiceIdParam = invoiceIdParam;
	}

	public Long getInvoiceId() {
		if (getEntity() != null && getEntity().getInvoice() != null) {
			return getEntity().getInvoice().getId();
		}

		return invoiceIdParam.get();
	}

	public List<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAgregates() {
		return subCategoryInvoiceAgregates;
	}

	public void setSubCategoryInvoiceAgregates(List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates) {
		this.subCategoryInvoiceAgregates = subCategoryInvoiceAgregates;
	}

	public BigDecimal totalCreditNoteAmountWithoutTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && entity.getCreditNoteLines() != null) {
			for (CreditNoteLine line : entity.getCreditNoteLines()) {
				total = total.add(line.getAmountWithoutTax());
			}
		}

		return total;
	}

	public BigDecimal totalCreditNoteAmountTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && entity.getCreditNoteLines() != null) {
			for (CreditNoteLine line : entity.getCreditNoteLines()) {
				total = total.add(line.getTaxAmount());
			}
		}

		return total;
	}

	public BigDecimal totalCreditNoteAmountWithTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity != null && entity.getCreditNoteLines() != null) {
			for (CreditNoteLine line : entity.getCreditNoteLines()) {
				total = total.add(line.getAmountWithTax());
			}
		}

		return total;
	}

}

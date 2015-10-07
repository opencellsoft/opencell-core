package org.meveo.admin.action.billing;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.CreditNote;
import org.meveo.model.billing.CreditNoteLine;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.CreditNoteService;
import org.meveo.service.billing.impl.InvoiceService;
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

	private List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates;

	public CreditNoteBean() {
		super(CreditNote.class);
	}

	@Override
	public CreditNote initEntity() {
		CreditNote obj = super.initEntity();

		if (invoiceIdParam != null && invoiceIdParam.get() != null) {
			obj.setInvoice(invoiceService.findById(invoiceIdParam.get()));

			// initialize credit note lines
			for (InvoiceAgregate invoiceAgregate : obj.getInvoice().getInvoiceAgregates()) {
				if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
					SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = (SubCategoryInvoiceAgregate) invoiceAgregate;

					CreditNoteLine creditNoteLine = new CreditNoteLine();
					creditNoteLine.setCreditNote(getEntity());
					creditNoteLine.setInvoiceSubCategory(subCategoryInvoiceAgregate.getInvoiceSubCategory());
					creditNoteLine.setAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
					creditNoteLine.setAmountWithTax(subCategoryInvoiceAgregate.getAmountWithTax());
					creditNoteLine.setTaxAmount(subCategoryInvoiceAgregate.getAmountTax());
				}
			}
		}

		return obj;
	}

	@Override
	public String getEditViewName() {
		return "creditNote";
	}

	@Override
	protected IPersistenceService<CreditNote> getPersistenceService() {
		return creditNoteService;
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		super.saveOrUpdate(killConversation);

		return null;
	}

	public Instance<Long> getInvoiceIdParam() {
		return invoiceIdParam;
	}

	public void setInvoiceIdParam(Instance<Long> invoiceIdParam) {
		this.invoiceIdParam = invoiceIdParam;
	}

	public Long getInvoiceId() {
		if (entity != null && entity.getInvoice() != null) {
			return entity.getInvoice().getId();
		}

		return invoiceIdParam.get();
	}

	public List<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAgregates() {
		return subCategoryInvoiceAgregates;
	}

	public void setSubCategoryInvoiceAgregates(List<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates) {
		this.subCategoryInvoiceAgregates = subCategoryInvoiceAgregates;
	}

}

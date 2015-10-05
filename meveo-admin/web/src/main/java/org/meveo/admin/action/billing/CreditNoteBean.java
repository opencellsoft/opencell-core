package org.meveo.admin.action.billing;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.CreditNote;
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

	public CreditNoteBean() {
		super(CreditNote.class);
	}

	@Override
	public CreditNote initEntity() {
		CreditNote obj = super.initEntity();

		if (invoiceIdParam != null && invoiceIdParam.get() != null) {
			obj.setInvoice(invoiceService.findById(invoiceIdParam.get()));
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

}

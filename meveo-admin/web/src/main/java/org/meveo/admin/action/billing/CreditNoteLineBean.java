package org.meveo.admin.action.billing;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.CreditNoteLine;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.CreditNoteLineService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class CreditNoteLineBean extends BaseBean<CreditNoteLine> {

	private static final long serialVersionUID = -2742820995152331003L;

	@Inject
	private CreditNoteLineService creditNoteServiceLineService;

	@Override
	protected IPersistenceService<CreditNoteLine> getPersistenceService() {
		return creditNoteServiceLineService;
	}

	public CreditNoteLineBean() {
		super(CreditNoteLine.class);
	}

}

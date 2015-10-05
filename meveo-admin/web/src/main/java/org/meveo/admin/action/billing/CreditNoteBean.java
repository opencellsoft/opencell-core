package org.meveo.admin.action.billing;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.CreditNote;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.CreditNoteService;
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

	@Override
	protected IPersistenceService<CreditNote> getPersistenceService() {
		return creditNoteService;
	}

}

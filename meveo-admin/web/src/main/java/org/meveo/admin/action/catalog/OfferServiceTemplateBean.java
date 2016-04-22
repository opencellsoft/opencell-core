package org.meveo.admin.action.catalog;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OfferServiceTemplateService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class OfferServiceTemplateBean extends BaseBean<OfferServiceTemplate> {

	private static final long serialVersionUID = -1507330065588654187L;

	@Inject
	private OfferServiceTemplateService offerServiceTemplateService;

	public OfferServiceTemplateBean() {
		super(OfferServiceTemplate.class);
	}

	@Override
	protected IPersistenceService<OfferServiceTemplate> getPersistenceService() {
		return offerServiceTemplateService;
	}

}

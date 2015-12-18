package org.meveo.admin.action.catalog;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.bom.BOMEntity;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessOfferService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class BusinessOfferBean extends BaseBean<BOMEntity> {

	private static final long serialVersionUID = 8222060379099238520L;

	@Inject
	private BusinessOfferService bomEntityService;

	public BusinessOfferBean() {
		super(BOMEntity.class);
	}

	@Override
	protected IPersistenceService<BOMEntity> getPersistenceService() {
		return bomEntityService;
	}

	@Override
	protected String getListViewName() {
		return "businessOffers";
	}

}

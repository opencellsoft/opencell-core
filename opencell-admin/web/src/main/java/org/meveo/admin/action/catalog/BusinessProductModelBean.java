package org.meveo.admin.action.catalog;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.admin.module.GenericModuleBean;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.BusinessProductModelService;

/**
 * @author Edward P. Legaspi
 */
@Named
@ViewScoped
public class BusinessProductModelBean extends GenericModuleBean<BusinessProductModel> {

	private static final long serialVersionUID = -1731438369618574084L;

	@Inject
	protected BusinessProductModelService businessProductModelService;

	public BusinessProductModelBean() {
		super(BusinessProductModel.class);
	}

	@Override
	protected IPersistenceService<BusinessProductModel> getPersistenceService() {
		return businessProductModelService;
	}

}

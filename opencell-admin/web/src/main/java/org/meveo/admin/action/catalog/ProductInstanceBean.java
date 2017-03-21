package org.meveo.admin.action.catalog;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.model.billing.ProductInstance;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.ProductInstanceService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class ProductInstanceBean extends CustomFieldBean<ProductInstance> {

	private static final long serialVersionUID = -3583227845810897037L;

	@Inject
	private ProductInstanceService productInstanceService;
	
	public ProductInstanceBean() {
		super(ProductInstance.class);
	}

	@Override
	public ProductInstance initEntity() {
		ProductInstance result = super.initEntity();

		return result;
	}	

	@Override
	protected IPersistenceService<ProductInstance> getPersistenceService() {
		return productInstanceService;
	}

}
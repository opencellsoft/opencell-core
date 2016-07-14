package org.meveo.admin.action.catalog;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ProductTemplateService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 */
@Named
@ViewScoped
public class ProductTemplateBean extends BaseBean<ProductTemplate> {

	private static final long serialVersionUID = -7002455215420815747L;

	@Inject
	protected ProductTemplateService productTemplateService;

	public ProductTemplateBean() {
		super(ProductTemplate.class);
	}

	@Override
	protected IPersistenceService<ProductTemplate> getPersistenceService() {
		return productTemplateService;
	}

}

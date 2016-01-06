package org.meveo.admin.action.catalog;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.CustomFieldBean;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class OfferTemplateCategoryBean extends CustomFieldBean<OfferTemplateCategory> {

	private static final long serialVersionUID = 1L;

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	public OfferTemplateCategoryBean() {
		super(OfferTemplateCategory.class);
	}

	@Override
	protected IPersistenceService<OfferTemplateCategory> getPersistenceService() {
		return offerTemplateCategoryService;
	}

	@Override
	protected String getListViewName() {
		return "offerTemplateCategories";
	}

}

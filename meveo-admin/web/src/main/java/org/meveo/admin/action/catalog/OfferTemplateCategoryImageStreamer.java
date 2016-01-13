package org.meveo.admin.action.catalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.ImageStreamer;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateCategoryService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ApplicationScoped
public class OfferTemplateCategoryImageStreamer extends ImageStreamer<OfferTemplateCategory> {

	@Inject
	private OfferTemplateCategoryService offerTemplateCategoryService;

	@Override
	public PersistenceService<OfferTemplateCategory> getPersistenceService() {
		return offerTemplateCategoryService;
	}

	@Override
	public byte[] getImageArr(OfferTemplateCategory obj) {
		return obj.getImageAsByteArr();
	}

}

package org.meveo.admin.action.catalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.ImageStreamer;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.OfferTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ApplicationScoped
public class OfferTemplateImageStreamer extends ImageStreamer<OfferTemplate> {

	@Inject
	private OfferTemplateService offerTemplateService;

	@Override
	public PersistenceService<OfferTemplate> getPersistenceService() {
		return offerTemplateService;
	}

	@Override
	public byte[] getImageArr(OfferTemplate obj) {
		if (obj.getImageAsByteArr() == null) {
			return downloadUrl(getClass().getClassLoader().getResource("img/no_picture.png"));
		}
		return obj.getImageAsByteArr();
	}

}

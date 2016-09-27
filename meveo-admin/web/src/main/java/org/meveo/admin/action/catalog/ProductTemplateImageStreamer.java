package org.meveo.admin.action.catalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.ImageStreamer;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.ProductTemplateService;

/**
 * @author Edward P. Legaspi
 */
@Named
@ApplicationScoped
public class ProductTemplateImageStreamer extends ImageStreamer<ProductTemplate> {

	@Inject
	private ProductTemplateService productTemplateService;

	@Override
	public PersistenceService<ProductTemplate> getPersistenceService() {
		return productTemplateService;
	}

	@Override
	public byte[] getImageArr(ProductTemplate obj) {
		if (obj.getImageAsByteArr() == null) {
			return downloadUrl(getClass().getClassLoader().getResource("img/no_picture.png"));
		}
		return obj.getImageAsByteArr();
	}

}

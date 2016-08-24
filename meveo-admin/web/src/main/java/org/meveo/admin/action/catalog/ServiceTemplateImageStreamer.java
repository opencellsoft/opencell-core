package org.meveo.admin.action.catalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.ImageStreamer;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.ServiceTemplateService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ApplicationScoped
public class ServiceTemplateImageStreamer extends ImageStreamer<ServiceTemplate> {

	@Inject
	private ServiceTemplateService serviceTemplateService;

	@Override
	public PersistenceService<ServiceTemplate> getPersistenceService() {
		return serviceTemplateService;
	}

	@Override
	public byte[] getImageArr(ServiceTemplate obj) {
		if (obj.getImageAsByteArr() == null) {
			return downloadUrl(getClass().getClassLoader().getResource("img/no_picture.png"));
		}
		return obj.getImageAsByteArr();
	}

}

package org.meveo.api.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.MediaDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.Product;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.MediaService;
import org.meveo.service.cpq.ProductService;

@Stateless
public class MediaApi extends BaseApi {

	
	private static final String MEDIA_EXIST_ALREADY = "Media for product code : %s and name : %s, already exist";
	/**
	 * 
	 */
	@Inject private MediaService mediaService;
	@Inject private ProductService productService;
	@Inject private ServiceTemplateService  serviceTemplateService;
	
	public MediaDto createMedia(MediaDto mediaDto) {
		if(Strings.isEmpty(mediaDto.getProductCode()))
			missingParameters.add("productCode");
		if(Strings.isEmpty(mediaDto.getServiceTemplateCode()))
			missingParameters.add("serviceTemplateCode");
		if(Strings.isEmpty(mediaDto.getMediaName()))
			missingParameters.add("mediaName");
		if(Strings.isEmpty(mediaDto.getLabel()))
			missingParameters.add("label");
		if(mediaDto.getMediaType() == null)
			missingParameters.add("mediaType");
		
		//check if there any Media exist with productCode and mediaName
		if(mediaService.findByProductAndMediaName(mediaDto.getProductCode(), mediaDto.getMediaName()) != null)
			throw new EntityAlreadyExistsException(String.format(MEDIA_EXIST_ALREADY, mediaDto.getProductCode(), mediaDto.getMediaName()) );
		final Product product = productService.findByCode(mediaDto.getProductCode());
		if(product == null)
			throw new EntityDoesNotExistsException(Product.class, mediaDto.getProductCode());
		final ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(mediaDto.getServiceTemplateCode());
		if(serviceTemplate == null)
			throw new EntityDoesNotExistsException(ServiceTemplate.class, mediaDto.getServiceTemplateCode());
		final Media m = new Media(); 
		m.setProduct(product);
		m.setServiceTemplate(serviceTemplate);
		m.setMediaName(mediaDto.getMediaName());
		m.setLabel(mediaDto.getLabel());
		m.setMediaType(mediaDto.getMediaType());
		m.setMain(mediaDto.isMain());
		m.setMediaPath(mediaDto.getMediaPath());
		
		mediaService.create(m);
		
		return mediaDto;
	}
	
	public MediaDto updateMedia(MediaDto mediaDto) {
		
	}
	
}

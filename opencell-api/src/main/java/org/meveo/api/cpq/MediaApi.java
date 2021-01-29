package org.meveo.api.cpq;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.MediaDto;
import org.meveo.api.dto.cpq.MediaListResponsDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.security.config.annotation.FilterProperty;
import org.meveo.api.security.config.annotation.FilterResults;
import org.meveo.api.security.config.annotation.SecuredBusinessEntityMethod;
import org.meveo.api.security.filter.ListFilter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.Product;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.MediaService;
import org.meveo.service.cpq.ProductService;

import com.thoughtworks.xstream.io.binary.Token.Attribute;

@Stateless
public class MediaApi extends BaseApi {

	
	private static final String MEDIA_EXIST_ALREADY = "Media for product code : %s and name : %s, already exist";
	private static final String MEDIA_DOESNT_EXIT = "No Media found for the key (%s, %s)";
    private static final String DEFAULT_SORT_ORDER_ID = "id";
	/**
	 * 
	 */
	@Inject private MediaService mediaService;
	@Inject private ProductService productService;
	@Inject private ServiceTemplateService  serviceTemplateService;
	@Inject private OfferTemplateService offerTemplateService;
	@Inject private AttributeService attributeService;
	
	public MediaDto createMedia(MediaDto mediaDto) {
		if(Strings.isEmpty(mediaDto.getMediaName()))
			missingParameters.add("mediaName");
		if(Strings.isEmpty(mediaDto.getLabel()))
			missingParameters.add("label");
		if(mediaDto.getMediaType() == null)
			missingParameters.add("mediaType");
		if(mediaDto.isMain() == null)
			missingParameters.add("main");
		handleMissingParameters();
		final Media m = new Media(); 
		if(!Strings.isEmpty(mediaDto.getProductCode())) {
			final Product product = productService.findByCode(mediaDto.getProductCode());
			if(product == null)
				throw new EntityDoesNotExistsException(Product.class, mediaDto.getProductCode());
			m.setProduct(product);
		}
		if(!Strings.isEmpty(mediaDto.getServiceTemplateCode())) {
			final ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(mediaDto.getServiceTemplateCode());
			if(serviceTemplate == null)
				throw new EntityDoesNotExistsException(ServiceTemplate.class, mediaDto.getServiceTemplateCode());
			m.setServiceTemplate(serviceTemplate);
		}
		if(!Strings.isEmpty(mediaDto.getOfferCode())) {
			final OfferTemplate offerTemplate = offerTemplateService.findByCode(mediaDto.getOfferCode());
			if(offerTemplate == null)
				throw new EntityDoesNotExistsException(OfferTemplate.class, mediaDto.getOfferCode());
			m.setOffer(offerTemplate);
		}
		if(!Strings.isEmpty(mediaDto.getAttributeCode())) {
			var attribute = attributeService.findByCode(mediaDto.getAttributeCode());
			if(attribute == null)
				throw new EntityDoesNotExistsException(Attribute.class, mediaDto.getAttributeCode());
			m.setAttribute(attribute);
		}
		m.setMediaName(mediaDto.getMediaName());
		m.setLabel(mediaDto.getLabel());
		m.setMediaType(mediaDto.getMediaType());
		m.setMain(mediaDto.isMain());
		m.setMediaPath(mediaDto.getMediaPath());
		
		mediaService.create(m);
		
		return new MediaDto(m);
	}
	
	public MediaDto updateMedia(MediaDto mediaDto) {
		if(mediaDto.getId() == null)
			missingParameters.add("id");
		handleMissingParameters();
		Media m = mediaService.findById(mediaDto.getId());
		if(m == null)
			throw new EntityDoesNotExistsException(Media.class, mediaDto.getId());
		if(!Strings.isEmpty(mediaDto.getServiceTemplateCode())) {
			final ServiceTemplate serviceTemplate = serviceTemplateService.findByCode(mediaDto.getServiceTemplateCode());
			if(serviceTemplate == null)
				throw new EntityDoesNotExistsException(ServiceTemplate.class, mediaDto.getServiceTemplateCode());
			m.setServiceTemplate(serviceTemplate);
		}
		
		if(!Strings.isEmpty(mediaDto.getProductCode())) {
			final Product product = productService.findByCode(mediaDto.getProductCode());
			if(product == null)
				throw new EntityDoesNotExistsException(Product.class, mediaDto.getProductCode());
			m.setProduct(product);
		}
		
		if(!Strings.isEmpty(mediaDto.getOfferCode())) {
			final OfferTemplate offerTemplate = offerTemplateService.findByCode(mediaDto.getOfferCode());
			if(offerTemplate == null)
				throw new EntityDoesNotExistsException(OfferTemplate.class, mediaDto.getOfferCode());
			m.setOffer(offerTemplate);
		}
		if(!Strings.isEmpty(mediaDto.getMediaName()))
			m.setMediaName(mediaDto.getMediaName());
		if(!Strings.isEmpty(mediaDto.getLabel()))
			m.setLabel(mediaDto.getLabel());
		if(mediaDto.getMediaType() != null)
			m.setMediaType(mediaDto.getMediaType());

		if(!Strings.isEmpty(mediaDto.getAttributeCode())) {
			var attribute = attributeService.findByCode(mediaDto.getAttributeCode());
			if(attribute == null)
				throw new EntityDoesNotExistsException(Attribute.class, mediaDto.getAttributeCode());
			m.setAttribute(attribute);
		}
		if(mediaDto.isMain() != null)
			m.setMain(mediaDto.isMain());
		if(!Strings.isEmpty(mediaDto.getMediaPath()))
			m.setMediaPath(mediaDto.getMediaPath());
		mediaService.update(m);
		return new MediaDto(m);
	}
	
	public MediaDto findByCode(String productCode, String mediaName) {
		if(Strings.isEmpty(productCode))
			missingParameters.add("productCode");

		if(Strings.isEmpty(mediaName))
			missingParameters.add("mediaName");
		
		handleMissingParameters();
		final Media media = mediaService.findByProductAndMediaName(productCode, mediaName);
		if(media == null)
			throw new EntityDoesNotExistsException(String.format(MEDIA_DOESNT_EXIT, productCode, mediaName));
		return new MediaDto(media);
	}
	
	public void deleteMedia(String productCode, String mediaName) {
		if(Strings.isEmpty(productCode))
			missingParameters.add("productCode");

		if(Strings.isEmpty(mediaName))
			missingParameters.add("mediaName");
		handleMissingParameters();
		final Media media = mediaService.findByProductAndMediaName(productCode, mediaName);
		if(media == null)
			throw new EntityDoesNotExistsException(String.format(MEDIA_DOESNT_EXIT, productCode, mediaName));
		mediaService.remove(media);
	}
	
	@SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
    @FilterResults(propertyToFilter = "media.medias", 
    			itemPropertiesToFilter = { 
    							@FilterProperty(property = "product", entityClass = Product.class),
    							@FilterProperty(property = "serviceTemplate", entityClass = ServiceTemplate.class) }, totalRecords = "media.listSize")
	public MediaListResponsDto listMedia(PagingAndFiltering pagingAndFiltering) {
		 String sortBy = DEFAULT_SORT_ORDER_ID;
	        if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
	            sortBy = pagingAndFiltering.getSortBy();
	        }
	        var filters = new HashedMap<String, Object>();
			 pagingAndFiltering.getFilters().forEach( (key, value) -> {
				 String newKey = key.replace("offerCode", "offer.code")
						 .replace("productCode", "product.code")
						 .replace("serviceTemplateCode", "serviceTemplate.code")
						 .replace("attributeCode", "attribute.code");
				 filters.put(key.replace(key, newKey), value);
			 });
			 pagingAndFiltering.getFilters().clear();
			 pagingAndFiltering.getFilters().putAll(filters);
			 List<String> fields = Arrays.asList("offer", "product", "attribute", "serviceTemplate");
	        PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, org.primefaces.model.SortOrder.ASCENDING, fields, pagingAndFiltering, Media.class);
	        
	        Long totalCount = mediaService.count(paginationConfiguration);
	        MediaListResponsDto result = new MediaListResponsDto();
	        
	        if(totalCount > 0) {
	        	mediaService.list(paginationConfiguration).stream().forEach( m -> {
	        		result.getMedia().getMedias().add(new MediaDto(m));
	        	});
		        result.getMedia().setListSize(totalCount.intValue());
	        }
	    	return result;
	}
	
}

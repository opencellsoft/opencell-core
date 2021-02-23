package org.meveo.api.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.MediaDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.cpq.Media;
import org.meveo.service.cpq.MediaService;

@Stateless
public class MediaApi extends BaseApi {
 
	private static final String MEDIA_DOESNT_EXIT = "No Media found for the key (%s, %s)"; 
 
	@Inject 
	private MediaService mediaService;  
	
	public MediaDto createMedia(MediaDto mediaDto) {
		if(StringUtils.isBlank(mediaDto.getCode()))
			missingParameters.add("code");
		if(StringUtils.isBlank(mediaDto.getMediaName()))
			missingParameters.add("mediaName");
		if(StringUtils.isBlank(mediaDto.getLabel()))
			missingParameters.add("label");
		if(mediaDto.getMediaType() == null)
			missingParameters.add("mediaType");
		if(mediaDto.isMain() == null)
			missingParameters.add("main");
		handleMissingParameters();
		if (mediaService.findByCode(mediaDto.getCode()) != null) {
			throw new EntityAlreadyExistsException(Media.class, mediaDto.getCode());
		}
		final Media media = new Media(); 
		media.setCode(mediaDto.getCode());
		media.setDescription(mediaDto.getDescription());
		media.setMediaName(mediaDto.getMediaName());
		media.setLabel(mediaDto.getLabel());
		media.setMediaType(mediaDto.getMediaType());
		media.setMain(mediaDto.isMain());
		media.setMediaPath(mediaDto.getMediaPath());
		mediaService.create(media);
		return new MediaDto(media);
	}
	
	public MediaDto updateMedia(MediaDto mediaDto) {
		if(StringUtils.isBlank(mediaDto.getCode()))
			missingParameters.add("code");
		handleMissingParameters();
		Media media = mediaService.findByCode(mediaDto.getCode());
		if(media == null)
			throw new EntityDoesNotExistsException(Media.class, mediaDto.getId()); 
		if(!Strings.isEmpty(mediaDto.getMediaName()))
			media.setMediaName(mediaDto.getMediaName());
		if(!Strings.isEmpty(mediaDto.getLabel()))
			media.setLabel(mediaDto.getLabel());
		if(mediaDto.getMediaType() != null)
			media.setMediaType(mediaDto.getMediaType()); 
		if(mediaDto.isMain() != null)
			media.setMain(mediaDto.isMain());
		if(!Strings.isEmpty(mediaDto.getMediaPath()))
			media.setMediaPath(mediaDto.getMediaPath());
		mediaService.update(media);
		return new MediaDto(media);
	}
 
	
	public void deleteMedia(String code) { 
		if(StringUtils.isBlank(code))
			missingParameters.add("code");
		handleMissingParameters();
		final Media media = mediaService.findByCode(code);
		if(media == null)
			throw new EntityDoesNotExistsException(String.format(MEDIA_DOESNT_EXIT, code));
		mediaService.remove(media);
	} 
	
	
	public MediaDto findByCode(String code) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		MediaDto result = new MediaDto();
		Media media = mediaService.findByCode(code);
		if (media == null) {
			throw new EntityDoesNotExistsException(Media.class, code);
		}
		result = new MediaDto(media);

		return result;
	}
	
	
	/*@SecuredBusinessEntityMethod(resultFilter = ListFilter.class)
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
	}*/
	
}

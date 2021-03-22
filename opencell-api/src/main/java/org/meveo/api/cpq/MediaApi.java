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
		populateCustomFields(mediaDto.getCustomFields(), media, true);
		mediaService.create(media);
		MediaDto result = new MediaDto(media);
		result.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(media));
		return result;
	}
	
	public MediaDto updateMedia(MediaDto mediaDto) {
		if(StringUtils.isBlank(mediaDto.getCode()))
			missingParameters.add("code");
		handleMissingParameters();
		Media media = mediaService.findByCode(mediaDto.getCode());
		if(media == null)
			throw new EntityDoesNotExistsException(Media.class, mediaDto.getCode()); 
		if(!StringUtils.isBlank(mediaDto.getDescription()))
			media.setDescription(mediaDto.getDescription());
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
		populateCustomFields(mediaDto.getCustomFields(), media, false);
		mediaService.update(media);
		MediaDto result = new MediaDto(media);
		result.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(media));
		return result;
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
		result.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(media));
		return result;
	}
	  
	
}

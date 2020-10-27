package org.meveo.api.cpq;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.tags.TagType;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.cpq.TagTypeService;
import org.meveo.service.cpq.exception.TagException;
import org.meveo.service.cpq.exception.TagTypeException;

import com.google.common.collect.Lists;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
public class TagApi extends BaseApi {

	@Inject
	private TagService tagService;
    @Inject
    private SellerService sellerService;
    @Inject
    private TagTypeService tagTypeService;
    @Inject
    private ProductVersionService productVersionService;
	
	
	public void create(TagDto dto) {
		
		checkParams(dto);
		
		final Tag tag = new Tag();
		tag.setCode(dto.getCode());
		tag.setDescription(dto.getDescription());		
		tag.setSeller(sellerService.findByCode(dto.getSellerCode()));
		tag.setName(dto.getName());
		
		try {
			final TagType tagType = tagTypeService.findByCode(dto.getTagTypeCode());
			tag.setTagType(tagType);
		} catch (TagTypeException e) {
			throw new BusinessApiException(e);
		}
		
		tag.setParentTag(tagService.findByCode(dto.getParentTagCode()));
		tag.setFilterEl(dto.getFilterEl());
		
		tagService.create(tag);
	}
	
	public void update(TagDto dto) {

		checkParams(dto);
		final Tag tag = tagService.findByCode(dto.getCode());
		if(tag == null || tag.getId() == null) {
			throw new BusinessApiException("Tag unknown from code " + dto.getCode());
		}

		try {
			final TagType tagType = tagTypeService.findByCode(dto.getTagTypeCode());
			tag.setTagType(tagType);
		} catch (TagTypeException e) {
			throw new BusinessApiException(e);
		}
		
		
		tag.setCode(dto.getCode());
		tag.setDescription(dto.getDescription());		
		tag.setSeller(sellerService.findByCode(dto.getSellerCode()));
		tag.setName(dto.getName());
		tag.setParentTag(tagService.findByCode(dto.getParentTagCode()));
		tag.setFilterEl(dto.getFilterEl());

		tagService.update(tag);
	}
	
	public void remove(Long id) {
		final Tag tag = tagService.findById(id);
		if(tag == null) {
			throw new BusinessApiException("Missing Tag with Id : " + id);
		}
		
		var productVersions = productVersionService.findByTags(Arrays.asList(new Long[] {tag.getId()}));
		if(!productVersions.isEmpty()) {
			throw new BusinessApiException("Tag contains product, it can not be deleted");
		}
		tagService.remove(tag);
	}

	public void remove(String codeTag) {
		final Tag tag = tagService.findByCode(codeTag);
		if(tag == null) {
			throw new BusinessApiException("Missing Tag with code : " + codeTag);
		}
		
		var productVersions = productVersionService.findByTags(Arrays.asList(new Long[] {tag.getId()}));
		if(!productVersions.isEmpty()) {
			throw new BusinessApiException("Tag contains product, it can not be deleted");
		}
		tagService.remove(tag);
	}
	
	public TagDto findByCode(String code) {
		final Tag tag = tagService.findByCode(code);
		if(tag == null) return null;
		return new TagDto(tag);
	}
	
	public TagDto findById(Long id) {
		final Tag tag = tagService.findById(id);
		if(tag == null) return null;
		return new TagDto(tag);
	}
	
	
	private void checkParams(TagDto dto) {
		
		if(Strings.isEmpty(dto.getCode())) {
			missingParameters.add("code");
		}
		if(Strings.isEmpty(dto.getName())) {
			missingParameters.add("name");
		}
		if(Strings.isEmpty(dto.getTagTypeCode())) {
			missingParameters.add("TagTypeCode");
		}
		if(Strings.isEmpty(dto.getTagTypeCode())) {
			missingParameters.add("label");
		}
		handleMissingParameters();
	}
	
	
}

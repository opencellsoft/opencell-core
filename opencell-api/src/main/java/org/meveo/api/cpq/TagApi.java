package org.meveo.api.cpq;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.cpq.TagTypeDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.admin.Seller;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.tags.TagType;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.cpq.TagTypeService;

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

	/**
	 * create new Tag from TagDto.<br />
	 * the parameters code, name, label and tag type code must not be empty
	 * @param tagDto
	 */
	public Long create(TagDto tagDto) {
		
		checkParams(tagDto);
		if(tagService.findByCode(tagDto.getCode()) != null) {
			throw new EntityAlreadyExistsException(Tag.class, tagDto.getCode());
		}
		final Tag tag = new Tag();
		tag.setCode(tagDto.getCode());
		tag.setDescription(tagDto.getDescription());		
		tag.setSeller(sellerService.findByCode(tagDto.getSellerCode()));
		tag.setName(tagDto.getName());
		
		final TagType tagType = tagTypeService.findByCode(tagDto.getTagTypeCode());
		if(tagType == null) {
			throw new EntityDoesNotExistsException(TagType.class, tagDto.getTagTypeCode());
		}
		tag.setTagType(tagType);
		
		tag.setParentTag(tagService.findByCode(tagDto.getParentTagCode()));
		tag.setFilterEl(tagDto.getFilterEl());
		
		tagService.create(tag);
		return tag.getId();
	}
	
	/**
	 * @param tagDto
	 */
	public TagDto update(TagDto tagDto) {

		checkParams(tagDto);
		final Tag tag = tagService.findByCode(tagDto.getCode());
		if(tag == null || tag.getId() == null) {
			throw new BusinessApiException("Tag unknown from code " + tagDto.getCode());
		}

		if(!StringUtils.isBlank(tagDto.getTagTypeCode())) {
			TagType tagType = tagTypeService.findByCode(tagDto.getTagTypeCode());
			if(tagType == null) {
				throw new EntityDoesNotExistsException(TagType.class, tagDto.getTagTypeCode());
			}
			tag.setTagType(tagType);
		}
			
		
		if(!StringUtils.isBlank(tagDto.getSellerCode())) {
			Seller seller = sellerService.findByCode(tagDto.getSellerCode());
			if(seller == null) {
				throw new EntityDoesNotExistsException(Seller.class, tagDto.getSellerCode());
			}
			tag.setSeller(seller);
		}
		
		
		tag.setDescription(tagDto.getDescription());		
		tag.setName(tagDto.getName());
		tag.setParentTag(tagService.findByCode(tagDto.getParentTagCode()));
		tag.setFilterEl(tagDto.getFilterEl());

		tagService.update(tag);
		return tagDto;
	}
	

	/**
	 * remove Tag by its code
	 * @param codeTag
	 */
	public void removeTag(String codeTag) {
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
	
	/**
	 * retrieve tag from code
	 * @param code
	 * @return
	 */
	public TagDto findTagByCode(String code) {
		if(Strings.isEmpty(code)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		final Tag tag = tagService.findByCode(code);
		if(tag == null) {
			throw new EntityDoesNotExistsException(Tag.class, code);
		}
		return new TagDto(tag);
	}
	
	/**
	 * retrieve tag from id
	 * @param id
	 * @return
	 */
	public TagDto findTagById(Long id) {
		final Tag tag = tagService.findById(id);
		if(tag == null) return null;
		return new TagDto(tag);
	}
	
	
	/**
	 * check of these parameters  code / name / tag type code / labe are null
	 * @param tagDto
	 */
	private void checkParams(TagDto tagDto) {
		
		if(Strings.isEmpty(tagDto.getCode())) {
			missingParameters.add("code");
		}
		if(Strings.isEmpty(tagDto.getName())) {
			missingParameters.add("name");
		}
		if(Strings.isEmpty(tagDto.getTagTypeCode())) {
			missingParameters.add("TagTypeCode");
		}
		handleMissingParameters();
	}
	

	/**
	 * create new Tag type 
	 * @param tagTypeDto
	 */
	public Long create(TagTypeDto tagTypeDto) {
		checkCodeTagTypeExist(tagTypeDto);
		if(tagTypeService.findByCode(tagTypeDto.getCode()) != null) {
			throw new EntityAlreadyExistsException(TagType.class, tagTypeDto.getCode());
		}
		final TagType tagType = new TagType();
		
		tagType.setCode(tagTypeDto.getCode());
		tagType.setDescription(tagType.getDescription());
		tagType.setSeller(sellerService.findByCode(tagTypeDto.getCode()));
		
		tagTypeService.create(tagType);
		return tagType.getId();
	}
	
	/**
	 * update tag type
	 * @param tagTypeDto
	 */
	public TagTypeDto update(TagTypeDto tagTypeDto) {
		checkCodeTagTypeExist(tagTypeDto);
		TagType tagType = tagTypeService.findByCode(tagTypeDto.getCode());
		if(tagType == null) {
			throw new EntityDoesNotExistsException(TagType.class, tagTypeDto.getCode());
		}
		tagType.setDescription(tagTypeDto.getDescription());
		tagType.setSeller(sellerService.findByCode(tagTypeDto.getCode()));
		
		tagTypeService.update(tagType);
		return tagTypeDto;
	}
	
	/**
	 * @param codeTag
	 */
	public void deleteTagType(String codeTag) {
		tagTypeService.removeTagType(codeTag);
	}
	
	/**
	 * retrieve tag type from code
	 * @param code
	 * @return
	 */
	public TagTypeDto findTagTypeByCode(String code) {
		return new TagTypeDto(tagTypeService.findByCode(code));
	}
	
	
	private void checkCodeTagTypeExist(TagTypeDto dto) {
		if(Strings.isEmpty(dto.getCode())) {
			missingParameters.add("code");
		}
		handleMissingParameters();
	}
	
}

package org.meveo.api.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.cpq.TagTypeDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.tags.TagType;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.cpq.TagTypeService;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@Stateless
public class TagApi extends BaseApi {

	@Inject
	private TagService tagService;
    @Inject
    private SellerService sellerService;
    @Inject
    private TagTypeService tagTypeService;
    @Inject
    private BillingAccountService billingAccountService;
    @Inject
    private ProductVersionService productVersionService;

	/**
	 * create new Tag from TagDto.<br />
	 * the parameters code, name, label and tag type code must not be empty
	 * @param tagDto
	 */
	public Long create(TagDto tagDto) {
	try {
	
		checkParams(tagDto);
		if(tagService.findByCode(tagDto.getCode()) != null) {
			throw new EntityAlreadyExistsException(Tag.class, tagDto.getCode());
		}
		final Tag tag = new Tag();
		tag.setCode(tagDto.getCode());
		tag.setDescription(tagDto.getDescription());		
		tag.setSeller(sellerService.findByCode(tagDto.getSellerCode()));
		tag.setName(tagDto.getName());
		
		TagType tagType = tagTypeService.findByCode(tagDto.getTagTypeCode());
		if(tagType == null) {
			throw new EntityDoesNotExistsException(TagType.class, tagDto.getTagTypeCode());
		}
		tag.setTagType(tagType);

		if(!StringUtils.isBlank(tagDto.getParentTagCode())) {
			Tag parentTag=tagService.findByCode(tagDto.getParentTagCode());
			if(parentTag!=null) 
				if(!tagDto.getParentTagCode().equalsIgnoreCase(tagDto.getCode()))
					tag.setParentTag(parentTag);
				else
					throw new BusinessApiException("Parent and child has the same code !!");
			}
		tag.setFilterEl(tagDto.getFilterEl());
		
		tagService.create(tag);
		return tag.getId();
	} catch (BusinessApiException e) {
		throw new BusinessApiException(e);
	}
	}
	
	/**
	 * @param tagDto
	 */
	public TagDto update(TagDto tagDto) {

		checkParams(tagDto);
		final Tag tag = tagService.findByCode(tagDto.getCode()); 
		if (tag == null) {
			throw new EntityDoesNotExistsException(Tag.class, tagDto.getCode());
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
			if(seller != null) {
				tag.setSeller(seller);
			}
		}
		tag.setDescription(tagDto.getDescription());		
		tag.setName(tagDto.getName());
		tag.setFilterEl(tagDto.getFilterEl());
		Tag parentTag = null;
		if(!Strings.isEmpty(tagDto.getParentTagCode())) {
			parentTag = tagService.findByCode(tagDto.getParentTagCode());
			if(parentTag!= null) {
				if(!parentTag.getCode().contentEquals(tagDto.getCode())) {
					if(parentTag.getParentTag() != null) {
						if(parentTag.getParentTag().getCode().contentEquals(tagDto.getCode()))
							throw new BusinessApiException("Tag code : "+ parentTag.getCode() + " already has a tag parent with code : " + tagDto.getCode());
					}
				}
				else
					throw new BusinessApiException("Parent and child of tag line has the same code !!");
			}
		}
		tag.setParentTag(parentTag);
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
			throw new EntityDoesNotExistsException(Tag.class, codeTag);
		}
		try {
			tagService.remove(tag);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BusinessApiException("This tag can not be deleted");
		}
		
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
		tagType.setDescription(tagTypeDto.getDescription());
		tagType.setSeller(sellerService.findByCode(tagTypeDto.getSellerCode()));
		
		tagTypeService.create(tagType);
		return tagType.getId();
	}
	
	/**
	 * update tag type
	 * @param tagTypeDto
	 */
	public TagType update(TagTypeDto tagTypeDto) throws MeveoApiException {
		checkCodeTagTypeExist(tagTypeDto); 
		TagType tagType =null;
		try {
			tagType = tagTypeService.findByCode(tagTypeDto.getCode());
			if (tagType == null) {
				throw new EntityDoesNotExistsException(TagType.class, tagTypeDto.getCode());
			}
			tagType.setDescription(tagTypeDto.getDescription());
			tagType.setSeller(sellerService.findByCode(tagTypeDto.getSellerCode()));
			tagTypeService.update(tagType);
		} catch (MeveoApiException e) {
			throw new MeveoApiException(e);
		}

		return tagType;
	}
	
	/**
	 * @param codeTag
	 */
	public void deleteTagType(String codeTag) {
		tagTypeService.removeTagType(codeTag);
	}
	 
	
	 /**
	 * @param code
	 * @return TagTypeDto
	 */
	public TagTypeDto findTagTypeByCode(String code){
		if(Strings.isEmpty(code)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		TagType tagType =tagTypeService.findByCode(code);
		if (tagType == null) {
			throw new EntityDoesNotExistsException(TagType.class, code);
		}
		return new TagTypeDto(tagType);
	}
	
	
	private void checkCodeTagTypeExist(TagTypeDto dto) {
		if(Strings.isEmpty(dto.getCode())) {
			missingParameters.add("code");
		}
		handleMissingParameters();
	}
	
}

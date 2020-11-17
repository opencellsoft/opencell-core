package org.meveo.api.cpq;

import java.util.Arrays;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.cpq.TagTypeDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.tags.TagType;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.cpq.TagTypeService;
import org.meveo.service.cpq.exception.TagTypeException;

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
    private BillingAccountService billingAccountService;
    @Inject
    private ProductVersionService productVersionService;

	/**
	 * create new Tag from TagDto.<br />
	 * the parameters code, name, label and tag type code must not be empty
	 * @param tagDto
	 */
    public TagDto create(TagDto tagDto) {
    	try {
    		checkParams(tagDto);
    		Tag tag = new Tag();
    		tag.setCode(tagDto.getCode());
    		tag.setDescription(tagDto.getDescription());		
    		tag.setSeller(sellerService.findByCode(tagDto.getSellerCode()));
    		tag.setName(tagDto.getName());

    		TagType tagType = tagTypeService.findByCode(tagDto.getTagTypeCode());
    		if(tagType!=null) {
    		tag.setTagType(tagType);
    		}
    		BillingAccount ba = billingAccountService.findByCode(tagDto.getBillingAccountCode());
    		if(ba!=null) {
    		tag.setBillingAccount(ba);
    		}
    		if(!StringUtils.isBlank(tagDto.getParentTagCode())) {
    		Tag parentTag=tagService.findByCode(tagDto.getParentTagCode());
    		if(parentTag!=null)
    			tag.setParentTag(parentTag);
    		}
    		tag.setFilterEl(tagDto.getFilterEl());
    		tagService.create(tag);
    	} catch (BusinessApiException e) {
    		throw new BusinessApiException(e);
    	}




    	return tagDto;
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

		try {
			if(!StringUtils.isBlank(tagDto.getTagTypeCode())) {
				TagType tagType = tagTypeService.findByCode(tagDto.getTagTypeCode());
				tag.setTagType(tagType);
			}
			
		} catch (BusinessApiException e) {
			throw new BusinessApiException("unknown TagType with code " + tagDto.getTagTypeCode());
		}
		
		try {
			if(!StringUtils.isBlank(tagDto.getSellerCode())) {
				Seller seller = sellerService.findByCode(tagDto.getSellerCode());
				tag.setSeller(seller);
			}
		} catch (NoResultException e) {
			throw new BusinessApiException("unknown Seller with code " + tagDto.getTagTypeCode());
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
		final Tag tag = tagService.findByCode(code);
		if(tag == null) return null;
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
	public TagTypeDto create(TagTypeDto tagTypeDto) {
		checkCodeTagTypeExist(tagTypeDto);
		
		final TagType tagType = new TagType();
		
		tagType.setCode(tagTypeDto.getCode());
		tagType.setDescription(tagType.getDescription());
		tagType.setSeller(sellerService.findByCode(tagTypeDto.getSellerCode()));
		
		tagTypeService.create(tagType);
		return tagTypeDto;
	}
	
	/**
	 * update tag type
	 * @param tagTypeDto
	 */
	public TagTypeDto update(TagTypeDto tagTypeDto) {
		checkCodeTagTypeExist(tagTypeDto);
		TagType tagType = null;
		try {
			tagType = tagTypeService.findByCode(tagTypeDto.getCode());
		} catch (BusinessApiException e) {
			throw new BusinessApiException(e);
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
		try {
			tagTypeService.removeTagType(codeTag);
		} catch (TagTypeException e) {
			throw new BusinessApiException(e);
		}
	}
	
	/**
	 * retrieve tag type from code
	 * @param code
	 * @return
	 */
	public TagTypeDto findTagTypeByCode(String code) {
		try {
			return new TagTypeDto(tagTypeService.findByCode(code));
		} catch (BusinessApiException e) {
			throw new BusinessApiException(e);
		}
	}
	
	
	private void checkCodeTagTypeExist(TagTypeDto dto) {
		if(Strings.isEmpty(dto.getCode())) {
			missingParameters.add("code");
		}
		handleMissingParameters();
	}
	
}

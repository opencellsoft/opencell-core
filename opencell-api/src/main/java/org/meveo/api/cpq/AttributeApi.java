package org.meveo.api.cpq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.response.cpq.GetAttributeDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductVersionResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.GroupedAttributeService;
import org.meveo.service.cpq.MediaService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.TagService;

/**
 * @author Mbarek-Ay
 * @version 11.0
 */
@Stateless
public class AttributeApi extends BaseCrudApi<Attribute, AttributeDTO> {

	@Inject
	private AttributeService attributeService; 

	@Inject
	private GroupedAttributeService groupedAttributeService;

	@Inject
	private ChargeTemplateService<ChargeTemplate>   chargeTemplateService;
	
	@Inject
	private ProductVersionService  productVersionService;
	
	@Inject
	private ProductService  productService;
	
	@Inject
	private TagService  tagService;
	
    @Inject
    private MediaService mediaService;
	

	@Override
	public Attribute create(AttributeDTO postData) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (attributeService.findByCode(postData.getCode()) != null) {
			throw new EntityAlreadyExistsException(Attribute.class, postData.getCode());
		}

		handleMissingParametersAndValidate(postData);

		// check if groupedAttributes  exists
		Attribute attribute = new Attribute();
		attribute.setCode(postData.getCode());
		attribute.setDescription(postData.getDescription());
		attribute.setPriority(postData.getPriority());
		attribute.setDisplay(postData.isDisplay());
		attribute.setMandatory(postData.isMandatory());
		attribute.setAttributeType(postData.getAttributeType());
		attribute.setSequence(postData.getSequence());
		attribute.setAllowedValues(postData.getAllowedValues());
		attribute.setChargeTemplates(chargeTemplateService.getChargeTemplatesByCodes(postData.getChargeTemplateCodes()));
		attribute.setUnitNbDecimal(postData.getUnitNbDecimal());
		attributeService.create(attribute);
		processTags(postData,attribute);
		processAssignedAttributes(postData,attribute);
		processMedias(postData,attribute);
		return attribute;
	}
	
	private void processTags(AttributeDTO postData, Attribute attribute) {
		List<String> tagCodes = postData.getTagCodes(); 
		if(tagCodes != null && !tagCodes.isEmpty()){
			Set<Tag> tags=new HashSet<Tag>();
			for(String code:tagCodes) {
				Tag tag=tagService.findByCode(code);
				if(tag == null) { 
					throw new EntityDoesNotExistsException(Tag.class,code);
				}
				tag.setAttribute(attribute);
				tags.add(tag);
			}
			attribute.getTags().addAll(tags);
		}
	} 
	
	 private void processMedias(AttributeDTO postData, Attribute attribute) {
			Set<String> mediaCodes = postData.getMediaCodes(); 
			if(mediaCodes != null && !mediaCodes.isEmpty()){
				List<Media> medias=new ArrayList<Media>();
				for(String code:mediaCodes) {
					Media media=mediaService.findByCode(code);
					if(media == null) { 
						throw new EntityDoesNotExistsException(Media.class,code);
					}
					medias.add(media);
				}
				attribute.setMedias(medias);
			}else {
				attribute.getMedias().clear();
			}
		}
	
	private void processAssignedAttributes(AttributeDTO postData, Attribute attribute) {
		List<String> assignedAttrCodes = postData.getAssignedAttributeCodes(); 
		if(assignedAttrCodes != null && !assignedAttrCodes.isEmpty()){
			Set<Attribute> assignedAttributes=new HashSet<Attribute>();
			for(String code:assignedAttrCodes) {
				Attribute attr=attributeService.findByCode(code);
				if(attr == null) { 
					throw new EntityDoesNotExistsException(Attribute.class,code);
				} 
				attr.setParentAttribute(attribute);
				assignedAttributes.add(attr);
			}
			attribute.getAssignedAttributes().addAll(assignedAttributes);
		}else {
			for(Attribute att : attribute.getAssignedAttributes()) {
				att.setParentAttribute(null);
			}
			
		}
	}
	


	@Override
	public Attribute update(AttributeDTO postData) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		} 
		Attribute attribute=attributeService.findByCode(postData.getCode());
		if (attribute== null) {
			throw new EntityDoesNotExistsException(Attribute.class, postData.getCode());
		} 
		attribute.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
		attribute.setDescription(postData.getDescription());
		attribute.setPriority(postData.getPriority());
		attribute.setDisplay(postData.isDisplay());
		attribute.setMandatory(postData.isMandatory());
		attribute.setAttributeType(postData.getAttributeType());
		attribute.setSequence(postData.getSequence());
		attribute.setAllowedValues(postData.getAllowedValues());
		attribute.setChargeTemplates(chargeTemplateService.getChargeTemplatesByCodes(postData.getChargeTemplateCodes()));
		if(postData.getUnitNbDecimal() != null) {
			attribute.setUnitNbDecimal(postData.getUnitNbDecimal());
		}
		if(attribute.getTags() != null && !attribute.getTags().isEmpty()) {
			for (Tag tag : attribute.getTags()) {
				tag.setAttribute(null);
				tagService.update(tag);
			}
		}
		processTags(postData,attribute);
		processAssignedAttributes(postData,attribute);
		processMedias(postData,attribute);
		attributeService.update(attribute);
		return attribute;
	}

	public GetAttributeDtoResponse findByCode(String code) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
			handleMissingParameters();
		}
		
		Attribute attribute = attributeService.findByCode(code);
		if (attribute == null) {
			throw new EntityDoesNotExistsException(Attribute.class, code);
		} 
		ChargeTemplateDto chargeTemplateDto=null;
		Set<ChargeTemplateDto> chargeTemplateDtos=new HashSet<ChargeTemplateDto>();
		for(ChargeTemplate charge : attribute.getChargeTemplates()) {
			chargeTemplateDto=new ChargeTemplateDto(charge,entityToDtoConverter.getCustomFieldsDTO(charge));
			chargeTemplateDtos.add(chargeTemplateDto);
		}
		TagDto tagDto=null;
		List<TagDto> tagDtos=new ArrayList<TagDto>();
		for(Tag tag : attribute.getTags()) {
			tagDto=new TagDto(tag);
			tagDtos.add(tagDto);
		}
		
		AttributeDTO attributeDto=null;
		List<AttributeDTO> assignedAttributes=new ArrayList<AttributeDTO>();
		for(Attribute attr : attribute.getAssignedAttributes()) {
			attributeDto=new AttributeDTO(attr);
			assignedAttributes.add(attributeDto);
		}
		
		GetAttributeDtoResponse result = new GetAttributeDtoResponse(attribute,chargeTemplateDtos,tagDtos,assignedAttributes,true); 
		return result;
	}

	public void remove(String code) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
			handleMissingParameters();
		}
		Attribute attribute=attributeService.findByCode(code);
		if (attribute== null) {
			throw new EntityDoesNotExistsException(Attribute.class, code);
		}
		attributeService.remove(attribute);

	} 

	public Attribute createOrUpdate(AttributeDTO postData) throws MeveoApiException, BusinessException {
		if (attributeService.findByCode(postData.getCode()) != null) {
			return update(postData);
		} else {
			return create(postData);
		}
	}
	
	
	
	public GetProductDtoResponse listPost(String productCode,String currentProductVersion,OfferContextDTO offerContextDto) { 
		if(Strings.isEmpty(productCode)) {
			missingParameters.add("productCode");
		} 
		if(Strings.isEmpty(currentProductVersion)) {
			missingParameters.add("currentProductVersion");
		} 

		Product product = productService.findByCode(productCode);
		if(product==null) {
			throw new EntityDoesNotExistsException(Product.class,productCode);
		}
		ProductVersion productVersion = productVersionService.findByProductAndVersion(productCode,Integer.parseInt(currentProductVersion));
		if(productVersion==null) {
			throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentProductVersion,"currentVersion");
		}
       GetProductVersionResponse getProductVersionResponse=new GetProductVersionResponse(productVersion,true,false);
		
		GetProductDtoResponse result = new GetProductDtoResponse(product);   
		result.setCurrentProductVersion(getProductVersionResponse);
		return result;
	}

	
 
	
}

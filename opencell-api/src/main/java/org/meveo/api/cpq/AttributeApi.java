package org.meveo.api.cpq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.exception.ConstraintViolationException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.GroupedAttributeDto;
import org.meveo.api.dto.cpq.MediaDto;
import org.meveo.api.dto.cpq.OfferContextConfigDTO;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.response.cpq.GetAttributeDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductVersionResponse;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.AttributeBaseEntity;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.CommercialRuleLineService;
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
	private ChargeTemplateService<ChargeTemplate>   chargeTemplateService;
	
	@Inject
	private ProductVersionService  productVersionService;
	
	@Inject
	private ProductService  productService;
	
	@Inject
	private TagService  tagService;
	
    @Inject
    private MediaService mediaService;

    @Inject
    private CommercialRuleLineService commercialRuleLineService;
	

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
		attribute.setAttributeType(postData.getAttributeType());
		attribute.setAllowedValues(postData.getAllowedValues());
		attribute.setChargeTemplates(chargeTemplateService.getChargeTemplatesByCodes(postData.getChargeTemplateCodes()));
		attribute.setUnitNbDecimal(postData.getUnitNbDecimal());
		attribute.setDisabled(postData.isDisabled() == null ? false : postData.isDisabled());
		populateCustomFields(postData.getCustomFields(), attribute, true);
		validateAttribute(attribute);
		attributeService.create(attribute);
		processTags(postData, attribute);
		processAssignedAttributes(postData, attribute);
		processMedias(postData, attribute);
		return attribute;
	}

	public void validateAttribute(Attribute attribute) throws MeveoApiException {
		if (attribute.getAttributeType() != null && attribute.getAttributeType().equals(AttributeTypeEnum.LIST_MULTIPLE_NUMERIC)) {
			if (attribute.getAllowedValues() == null || attribute.getAllowedValues().isEmpty()) {
				return;
			}
			for (String value : attribute.getAllowedValues()) {
				if (!value.matches("\\d+")) {
					throw new MeveoApiException(value + " is not a valid number");
				}
			}
		}

	}

	private void processTags(AttributeDTO postData, Attribute attribute) {
		List<String> tagCodes = postData.getTagCodes();
		if (tagCodes != null && !tagCodes.isEmpty()) {
			Set<Tag> tags = new HashSet<Tag>();
			for (String code : tagCodes) {
				Tag tag = tagService.findByCode(code);
				if (tag == null) {
					throw new EntityDoesNotExistsException(Tag.class, code);
				}
				tags.add(tag);
			}
			attribute.setTags(new ArrayList<>(tags));
		} else {
			attribute.getTags().clear();
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
		if (assignedAttrCodes != null && !assignedAttrCodes.isEmpty()) {
			Set<Attribute> assignedAttributes = new HashSet<Attribute>();
			for (String code : assignedAttrCodes) {
				Attribute attr = attributeService.findByCode(code);
				if (attr == null) {
					throw new EntityDoesNotExistsException(Attribute.class, code);
				}
				attr.setParentAttribute(attribute);
				assignedAttributes.add(attr);
			}
			attribute.getAssignedAttributes().addAll(assignedAttributes);
		} else {
			if (!attribute.getAssignedAttributes().isEmpty()) {
				attributeService.updateParentAttribute(attribute.getId());
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
		attribute.setAttributeType(postData.getAttributeType());
		attribute.setAllowedValues(postData.getAllowedValues());
		attribute.setChargeTemplates(chargeTemplateService.getChargeTemplatesByCodes(postData.getChargeTemplateCodes()));
        attribute.setDisabled(postData.isDisabled() == null ? false : postData.isDisabled());
		if(postData.getUnitNbDecimal() != null) {
			attribute.setUnitNbDecimal(postData.getUnitNbDecimal());
		}
        populateCustomFields(postData.getCustomFields(), attribute, true);
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
		ChargeTemplateDto chargeTemplateDto = null;
		Set<ChargeTemplateDto> chargeTemplateDtos = new HashSet<ChargeTemplateDto>();
		for (ChargeTemplate charge : attribute.getChargeTemplates()) {
			chargeTemplateDto = new ChargeTemplateDto(charge, entityToDtoConverter.getCustomFieldsDTO(charge));
			chargeTemplateDtos.add(chargeTemplateDto);
		}
		TagDto tagDto = null;
		List<TagDto> tagDtos = new ArrayList<TagDto>();
		for (Tag tag : attribute.getTags()) {
			tagDto = new TagDto(tag);
			tagDtos.add(tagDto);
		}

		AttributeDTO attributeDto = null;
		List<AttributeDTO> assignedAttributes = new ArrayList<AttributeDTO>();
		for (Attribute attr : attribute.getAssignedAttributes()) {
			attributeDto = new AttributeDTO(attr);
			assignedAttributes.add(attributeDto);
		}
		GetAttributeDtoResponse result = new GetAttributeDtoResponse(attribute, chargeTemplateDtos, tagDtos, assignedAttributes, true);
		result.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(attribute));
		return result;
	}

	@Override
	public void remove(String code) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
			handleMissingParameters();
		}
		Attribute attribute = attributeService.findByCode(code);
		if (attribute == null) {
			throw new EntityDoesNotExistsException(Attribute.class, code);
		}
		try {
			attributeService.remove(attribute);
			attributeService.commit();
		} catch(Exception e) {
			if (ExceptionUtils.indexOfThrowable(e, ConstraintViolationException.class) > -1) {
				throw new DeleteReferencedEntityException(Attribute.class, code);
			}
			throw new MeveoApiException(e.getMessage());
		}
	}

	@Override
	public Attribute createOrUpdate(AttributeDTO postData) throws MeveoApiException, BusinessException {
		if (attributeService.findByCode(postData.getCode()) != null) {
			return update(postData);
		} else {
			return create(postData);
		}
	}

	public GetProductDtoResponse listPost(String productCode, String currentProductVersion, OfferContextDTO offerContextDto) {
		if (Strings.isEmpty(productCode)) {
			missingParameters.add("productCode");
		}
		if (Strings.isEmpty(currentProductVersion)) {
			missingParameters.add("currentProductVersion");
		}

		Product product = productService.findByCode(productCode);
		if (product == null) {
			throw new EntityDoesNotExistsException(Product.class, productCode);
		}
		ProductVersion productVersion = productVersionService.findByProductAndVersion(productCode, Integer.parseInt(currentProductVersion));
		if (productVersion == null) {
			throw new EntityDoesNotExistsException(ProductVersion.class, productCode, "productCode", "" + currentProductVersion, "currentVersion");
		}
		GetProductVersionResponse getProductVersionResponse = new GetProductVersionResponse(productVersion, true, true);
		getProductVersionResponse.getProductAttributes().stream().forEach(att -> {
			List<Long> sourceRules = commercialRuleLineService.getSourceProductAttributeRules(att.getAttributeCode(), productCode);
			if (sourceRules != null && !sourceRules.isEmpty()) {
				att.setRuled(true);
			}
		});

		GetProductDtoResponse result = new GetProductDtoResponse(product);
		result.setCurrentProductVersion(getProductVersionResponse);
		result.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(product));
		return result;
	}

	
	public GetAttributeDtoResponse populateAttributToDto(AttributeBaseEntity attributeEntity , OfferContextConfigDTO config) throws MeveoApiException {
		
		if (attributeEntity == null) {
			missingParameters.add("productVersionAttribute");
			handleMissingParameters();
		}
		Attribute attribute=attributeEntity.getAttribute();
		if (attribute == null) {
			missingParameters.add("attribute");
			handleMissingParameters();
		}

		Set<ChargeTemplateDto> chargeTemplateDtos = new HashSet<>();
		if (config != null && config.isLoadAttributeChargeTemplates()) {
			for (ChargeTemplate charge : attribute.getChargeTemplates()) {
				chargeTemplateDtos.add(new ChargeTemplateDto(charge, entityToDtoConverter.getCustomFieldsDTO(charge)));
			}
		}

		List<TagDto> tagDtos = new ArrayList<>();
		if (config != null && config.isLoadAttributeTags()) {
			for (Tag tag : attribute.getTags()) {
				tagDtos.add(new TagDto(tag));
			}
		}

		List<AttributeDTO> assignedAttributes = new ArrayList<>();
		if (config != null && config.isLoadAttributeAssignedAttr()) {
			for (Attribute attr : attribute.getAssignedAttributes()) {
				assignedAttributes.add(new AttributeDTO(attr));
			}
		}

		List<MediaDto> medias=new ArrayList<>();
		if (config != null && config.isLoadAttributeMedia()) {
			if (attribute.getMedias() != null && !attribute.getMedias().isEmpty()) {
				medias = attribute.getMedias().stream().map(MediaDto::new).collect(Collectors.toList());
			}
		}

		List<GroupedAttributeDto> groupedAttributes = new ArrayList<>();
		if (config != null && config.isLoadAttributeGroupedAttribute()) {
			groupedAttributes = java.util.Optional.ofNullable(attribute.getGroupedAttributes()).orElse(Collections.emptyList())
					.stream()
					.map(GroupedAttributeDto::new)
					.collect(Collectors.toList());
		}

		GetAttributeDtoResponse result = new GetAttributeDtoResponse(attributeEntity, chargeTemplateDtos, tagDtos,
				assignedAttributes, medias, groupedAttributes);
		result.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(attribute));

		return result;
	}
	
	
	public GroupedAttributeDto populateGroupedAttributToDto(GroupedAttributes groupedAttribute) throws MeveoApiException {
		log.debug("populateGroupedAttributToDto groupedAttribute={}",groupedAttribute!=null?groupedAttribute.getCode():null);
		if (groupedAttribute == null) {
			missingParameters.add("groupedAttribute");
			handleMissingParameters();
		}

		Set<String> attributeCodes= new HashSet<>();
		attributeCodes = Optional.ofNullable(groupedAttribute.getAttributes()).orElse(Collections.emptyList())
					.stream()
					.map(v -> v.getCode())
					.collect(Collectors.toSet());
		
		log.debug("populateGroupedAttributToDto attributeCodes={}",attributeCodes);
		
		GroupedAttributeDto result = new GroupedAttributeDto(groupedAttribute);
		result.setAttributeCodes(attributeCodes);
		result.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(groupedAttribute));
		log.debug("populateGroupedAttributToDto CustomFields={}",result.getCustomFields()!=null && result.getCustomFields().getCustomField()!=null?result.getCustomFields().getCustomField().size():null);

		return result;
	}
	
 
	
}

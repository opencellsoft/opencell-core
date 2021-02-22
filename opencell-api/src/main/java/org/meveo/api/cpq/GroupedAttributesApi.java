package org.meveo.api.cpq;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.elasticsearch.common.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.GroupedAttributeDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.GroupedAttributeService;
import org.meveo.service.cpq.ProductVersionService;

@Stateless
public class GroupedAttributesApi extends BaseApi{

	@Inject
	private GroupedAttributeService groupedAttributeService;
	@Inject
	private ProductVersionService productVersionService;
	@Inject
	private AttributeService attributeService;
	
	

	/**
	 * <ul>
	 *  <li>check if groupedAttributeCode already exist if no throw an exception</li>
	 *	<li>check if all attributeCodes exist , if no throw an exception</li>
	 *	<li>check if  attribute passed in params is already assigned to a group, if no throw an exception</li>
	 *	<li>assign the group to all services templates passed in params (groupedAttribute field)</li>
	 *</ul>
	 * @param groupedAttributeCode
	 * @param attributeCodes
	 * @param isNew
	 */
	public List<Attribute> addToGroup(GroupedAttributes groupedAttribute, List<AttributeDTO> attributes, boolean isNew) {
		var templates = attributes.stream().map(attributeDTO -> {
							final Attribute template = attributeService.findByCode(attributeDTO.getCode());
							if(template == null) 
								throw new EntityDoesNotExistsException(Attribute.class, attributeDTO.getCode());
							if(isNew) {
								if(template.getGroupedAttributes() != null)
									throw new BusinessException("Service code " + template.getCode() + " is already assigned to a group code " + template.getGroupedAttributes().getCode());
							}
							return template;
						}).collect(Collectors.toList());
		
		templates.stream().forEach(template -> {
			template.setGroupedAttributes(groupedAttribute);
			attributeService.update(template);
		});
		return templates;
	}
	
	/**
	 * @param groupedAttributeDto
	 */
	public GroupedAttributeDto createGroupedAttribute(GroupedAttributeDto groupedAttributeDto) {
		checkParams(groupedAttributeDto);
		final GroupedAttributes groupedAttribute = new GroupedAttributes();
		if(groupedAttributeService.findByCode(groupedAttributeDto.getCode()) != null)
				throw new EntityAlreadyExistsException(GroupedAttributes.class, groupedAttributeDto.getCode());
		groupedAttribute.setCode(groupedAttributeDto.getCode());
		groupedAttribute.setDescription(groupedAttributeDto.getDescription());
		try {
			if(!Strings.isEmpty(groupedAttributeDto.getProductCode())) {
				ProductVersion version=productVersionService.findByProductAndVersion(groupedAttributeDto.getProductCode(), groupedAttributeDto.getProductVersion());
				if(version==null) {
					throw new MeveoApiException("The product version "+groupedAttributeDto.getProductVersion()+" does not exist for this product");
				}
				groupedAttribute.setProductVersion(productVersionService.findByProductAndVersion(groupedAttributeDto.getProductCode(), groupedAttributeDto.getProductVersion()));
			}
				
		}catch(MeveoApiException e) {
			if(e instanceof EntityDoesNotExistsException == false)
				throw new 	MeveoApiException(e);
			else
				throw e;
		}
		groupedAttribute.setDisplay(groupedAttributeDto.isDisplay());
		groupedAttribute.setMandatory(groupedAttributeDto.isMandatory());
		groupedAttribute.setDisabled(groupedAttributeDto.isDisabled());
		List<Attribute> attributes = new ArrayList<Attribute>();
		groupedAttributeService.create(groupedAttribute);
		if(groupedAttributeDto.getAttributes() != null && !groupedAttributeDto.getAttributes().isEmpty()) {
			attributes = addToGroup(groupedAttribute, groupedAttributeDto.getAttributes(), true);
		}
		return new GroupedAttributeDto(groupedAttribute, attributes);
	}
	
	/**
	 * retrieve and update Grouped service, if no one exist it will throw a BusinessException
	 * @param groupedAttributeDto
	 */
	public void updateGroupedAttribute(GroupedAttributeDto groupedAttributeDto) {
		checkParams(groupedAttributeDto);
		final GroupedAttributes groupedAttribute = groupedAttributeService.findByCode(groupedAttributeDto.getCode());
		if(groupedAttribute == null) {
			throw new EntityDoesNotExistsException(GroupedAttributes.class, groupedAttributeDto.getCode());
		}
		try {
			if(!Strings.isEmpty(groupedAttributeDto.getProductCode()))
				groupedAttribute.setProductVersion(productVersionService.findByProductAndVersion(groupedAttributeDto.getProductCode(), groupedAttributeDto.getProductVersion()));
		}catch(MeveoApiException e) {
			if(e instanceof EntityDoesNotExistsException == false)
				throw new 	MeveoApiException(e);
			else
				throw e;
		}
		if(!Strings.isEmpty(groupedAttributeDto.getDescription()))
			groupedAttribute.setDescription(groupedAttributeDto.getDescription());
		if(groupedAttributeDto.getAttributes() != null && !groupedAttributeDto.getAttributes().isEmpty()) {
			addToGroup(groupedAttribute, groupedAttributeDto.getAttributes(), false);
		}
		groupedAttribute.setDisplay(groupedAttributeDto.isDisplay());
		groupedAttribute.setMandatory(groupedAttributeDto.isMandatory());
		groupedAttribute.setDisabled(groupedAttributeDto.isDisabled());
		groupedAttributeService.update(groupedAttribute);
	}
	
	/**
	 * @param code
	 */
	public void removeGroupedAttribute(String code) {
		final GroupedAttributes groupedAttribute = groupedAttributeService.findByCode(code);
		if(groupedAttribute == null) {
			throw new EntityDoesNotExistsException(GroupedAttributes.class, code);
		}
		groupedAttributeService.remove(groupedAttribute);
	}

	/**
	 * find grouped service by code
	 * @param code
	 * @return throw BusinessException if no result found
	 */
	public GroupedAttributeDto findGroupedAttributeByCode(String code) {
		GroupedAttributes groupedAttribute =  groupedAttributeService.findByCode(code);
		if(groupedAttribute == null) {
			throw new EntityDoesNotExistsException(GroupedAttributes.class, code);
		}
		var attributes = attributeService.findByGroupedAttribute(groupedAttribute);
		return new GroupedAttributeDto(groupedAttribute, attributes);
	}
	
	private void checkParams(GroupedAttributeDto groupedAttributeDto) {
		if(Strings.isEmpty(groupedAttributeDto.getCode())) {
			missingParameters.add("code");
		}
		handleMissingParameters();
	}
}

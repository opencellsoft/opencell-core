package org.meveo.api.cpq;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.elasticsearch.common.Strings;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.GroupedAttributeDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.GroupedAttributeService;

@Stateless
public class GroupedAttributesApi extends BaseApi{

	@Inject
	private GroupedAttributeService groupedAttributeService;
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
	public List<Attribute> addToGroup(GroupedAttributes groupedAttribute, Set<String> set, boolean isNew) {
		var templates = set.stream().map(code -> {
							return loadEntityByCode(attributeService, code, Attribute.class);
						}).collect(Collectors.toList());
		groupedAttribute.setAttributes(templates);
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
		groupedAttribute.setDisplay(groupedAttributeDto.isDisplay());
		groupedAttribute.setMandatory(groupedAttributeDto.isMandatory());
		groupedAttribute.setDisabled(groupedAttributeDto.isDisabled());
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes = addToGroup(groupedAttribute, groupedAttributeDto.getAttributeCodes(), true);
		groupedAttributeService.create(groupedAttribute);
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
		if(!Strings.isEmpty(groupedAttributeDto.getDescription()))
			groupedAttribute.setDescription(groupedAttributeDto.getDescription());
		addToGroup(groupedAttribute, groupedAttributeDto.getAttributeCodes(), false);
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
		return new GroupedAttributeDto(groupedAttribute, groupedAttribute.getAttributes());
	}
	
	private void checkParams(GroupedAttributeDto groupedAttributeDto) {
		if(Strings.isEmpty(groupedAttributeDto.getCode())) {
			missingParameters.add("code");
		}
		handleMissingParameters();
	}
}

package org.meveo.api.cpq;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.elasticsearch.common.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.GroupedServiceDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.GroupedService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.GroupedServiceService;
import org.meveo.service.cpq.ProductVersionService;

public class GroupedServiceApi extends BaseApi{

	@Inject
	private GroupedServiceService groupedServiceService;
	@Inject
	private ProductVersionService productVersionService;
	@Inject
	private ServiceTemplateService serviceTemplateService;
	
	

	/**
	 * <ul>
	 *  <li>check if groupedServiceCode already exist if no throw an exception</li>
	 *	<li>check if all serviceTemplateCodes exist , if no throw an exception</li>
	 *	<li>check if  serviceTemplate passed in params is already assigned to a group, if no throw an exception</li>
	 *	<li>assign the group to all services templates passed in params (groupedService field)</li>
	 *</ul>
	 * @param groupedServiceCode
	 * @param serviceTemplateCodes
	 * @param isNew
	 */
	public void attachServiceTemplate(GroupedService groupedService, List<String> serviceTemplateCodes, boolean isNew) {
		var templates = serviceTemplateCodes.stream().map(code -> {
							final ServiceTemplate template = serviceTemplateService.findByCode(code);
							if(template == null) 
								throw new EntityDoesNotExistsException(ServiceTemplate.class, code);
							if(isNew) {
								if(template.getGroupedService() != null)
									throw new BusinessException("Service code " + template.getCode() + " is already assigned to a group code " + template.getGroupedService().getCode());
							}
							return template;
						}).collect(Collectors.toList());
		
		templates.stream().forEach(template -> {
			template.setGroupedService(groupedService);
			serviceTemplateService.update(template);
		});
	}
	
	/**
	 * @param groupedServiceDto
	 */
	public Long createGroupedService(GroupedServiceDto groupedServiceDto) {
		checkParams(groupedServiceDto);
		final GroupedService groupedService = new GroupedService();
		if(groupedServiceService.findByCode(groupedServiceDto.getCode()) != null)
				throw new EntityAlreadyExistsException(GroupedService.class, groupedServiceDto.getCode());
		groupedService.setCode(groupedServiceDto.getCode());
		groupedService.setDescription(groupedServiceDto.getDescription());
		try {
			if(!Strings.isEmpty(groupedServiceDto.getProductCode()))
				groupedService.setProductVersion(productVersionService.findByProductAndVersion(groupedServiceDto.getProductCode(), groupedServiceDto.getProdcutVersion()));
		}catch(MeveoApiException e) {
			if(e instanceof EntityDoesNotExistsException == false)
				throw new 	MeveoApiException(e);
		}
		groupedService.setDisplay(groupedServiceDto.isDisplay());
		groupedService.setMandatory(groupedServiceDto.isMandatory());
		groupedServiceService.create(groupedService);
		if(groupedServiceDto.getServiceCodes() != null && !groupedServiceDto.getServiceCodes().isEmpty()) {
			attachServiceTemplate(groupedService, groupedServiceDto.getServiceCodes(), true);
		}
		return groupedService.getId();
	}
	
	/**
	 * retrieve and update Grouped service, if no one exist it will throw a BusinessException
	 * @param groupedServiceDto
	 */
	public void updateGroupedService(GroupedServiceDto groupedServiceDto) {
		checkParams(groupedServiceDto);
		final GroupedService groupedService = groupedServiceService.findByCode(groupedServiceDto.getCode());
		if(groupedService == null) {
			throw new EntityDoesNotExistsException(GroupedService.class, groupedServiceDto.getCode());
		}
		groupedService.setDescription(groupedServiceDto.getDescription());
		try {
			if(!Strings.isEmpty(groupedServiceDto.getProductCode()))
				groupedService.setProductVersion(productVersionService.findByProductAndVersion(groupedServiceDto.getProductCode(), groupedServiceDto.getProdcutVersion()));
		}catch(MeveoApiException e) {
			if(e instanceof EntityDoesNotExistsException == false)
				throw new 	MeveoApiException(e);
		}
		if(groupedServiceDto.getServiceCodes() != null && !groupedServiceDto.getServiceCodes().isEmpty()) {
			attachServiceTemplate(groupedService, groupedServiceDto.getServiceCodes(), false);
		}
		groupedService.setDisplay(groupedServiceDto.isDisplay());
		groupedService.setMandatory(groupedServiceDto.isMandatory());
		groupedServiceService.update(groupedService);
	}
	
	/**
	 * @param code
	 */
	public void removeGroupedService(String code) {
		final GroupedService groupedService = groupedServiceService.findByCode(code);
		if(groupedService == null) {
			throw new EntityDoesNotExistsException(GroupedService.class, code);
		}
		groupedServiceService.remove(groupedService);
	}

	/**
	 * find grouped service by code
	 * @param code
	 * @return throw BusinessException if no result found
	 */
	public GroupedServiceDto findGroupedServiceByCode(String code) {
		GroupedService groupedService =  groupedServiceService.findByCode(code);
		if(groupedService == null) {
			throw new EntityDoesNotExistsException(GroupedService.class, code);
		}
		return new GroupedServiceDto(groupedService);
	}
	
	private void checkParams(GroupedServiceDto groupedServiceDto) {
		if(Strings.isEmpty(groupedServiceDto.getCode())) {
			missingParameters.add("code");
		}
		handleMissingParameters();
	}
}

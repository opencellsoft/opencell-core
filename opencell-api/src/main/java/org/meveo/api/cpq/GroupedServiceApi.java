package org.meveo.api.cpq;

import javax.inject.Inject;

import org.elasticsearch.common.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.GroupedServiceDto;
import org.meveo.model.cpq.GroupedService;
import org.meveo.service.cpq.GroupedServiceService;
import org.meveo.service.cpq.ProductService;

public class GroupedServiceApi extends BaseApi{

	@Inject
	private GroupedServiceService groupedServiceService;
	@Inject
	private ProductService productService;
	
	
	/**
	 * @param groupedServiceDto
	 */
	public void createGroupedService(GroupedServiceDto groupedServiceDto) {
		checkParams(groupedServiceDto);
		final GroupedService groupedService = new GroupedService();
		
		groupedService.setCode(groupedServiceDto.getCode());
		groupedService.setDescription(groupedServiceDto.getDescription());
		groupedService.setProduct(productService.findByCode(groupedServiceDto.getProductCode()));
		
		groupedServiceService.create(groupedService);
	}
	
	/**
	 * retrieve and update Grouped service, if no one exist it will throw a BusinessException
	 * @param groupedServiceDto
	 */
	public void updateGroupedService(GroupedServiceDto groupedServiceDto) {
		checkParams(groupedServiceDto);
		
		final GroupedService groupedService = groupedServiceService.findByCode(groupedServiceDto.getCode());
		if(groupedService == null) {
			throw new BusinessException("No Grouped Service found for code = " + groupedServiceDto.getCode());
		}

		groupedService.setDescription(groupedServiceDto.getDescription());
		groupedService.setProduct(productService.findByCode(groupedServiceDto.getProductCode()));
		groupedServiceService.update(groupedService);
	}
	
	/**
	 * @param id
	 */
	public void removeGroupedService(Long id) {
		groupedServiceService.remove(id);
	}
	
	/**
	 * @param code
	 */
	public void removeGroupedService(String code) {
		final GroupedService groupedService = groupedServiceService.findByCode(code);
		if(groupedService == null) {
			throw new BusinessException("No Grouped Service found for code = "+ code);
		}
		groupedServiceService.remove(groupedService);
	}

	/**
	 * find grouped service by id
	 * @param id
	 * @return throw BusinessException if no result found
	 */
	public GroupedServiceDto findGroupedServiceByCode(Long id) {
		GroupedService groupedService = groupedServiceService.findById(id);

		if(groupedService == null) {
			throw new BusinessException("No Grouped Service found for id= " + id);
		}
		return new GroupedServiceDto(groupedService);
	}
	
	/**
	 * find grouped service by code
	 * @param code
	 * @return throw BusinessException if no result found
	 */
	public GroupedServiceDto findGroupedServiceById(String code) {
		GroupedService groupedService =  groupedServiceService.findByCode(code);
		if(groupedService == null) {
			throw new BusinessException("No Grouped Service found for code= " + code);
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

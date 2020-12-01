package org.meveo.api.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.ServiceTypeDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.cpq.ServiceType;
import org.meveo.model.cpq.enums.ServiceTypeEnum;
import org.meveo.service.catalog.impl.ServiceTypeService;

@Stateless
public class ServiceTypeApi extends BaseCrudApi<ServiceType, ServiceTypeDto> {
	
	@Inject
    private ServiceTypeService serviceTypeService;
	
	/**
	 * Creates a new ServiceType entity.
	 * 
	 * @param postData posted data to API
	 * 
	 * @throws MeveoApiException meveo api exception
	 * @throws BusinessException business exception.
	 */
	public ServiceType create(ServiceTypeDto postData) throws MeveoApiException, BusinessException {

		String serviceTypeCode = postData.getCode();
		ServiceTypeEnum serviceTypeEnum = postData.getServiceType();

		if (StringUtils.isBlank(serviceTypeCode)) {
			missingParameters.add("serviceTypeCode");
		}
		if (StringUtils.isBlank(serviceTypeEnum)) {
			missingParameters.add("serviceType");
		}

		handleMissingParametersAndValidate(postData);
		ServiceType serviceType = serviceTypeService.findByCode(serviceTypeCode);
		if (serviceType != null) {
			throw new EntityAlreadyExistsException(ServiceType.class, serviceTypeCode);
		}
		serviceType = new ServiceType();
		serviceType.setCode(serviceTypeCode);
		serviceType.setDescription(postData.getDescription());
		serviceType.setServiceType(serviceTypeEnum); 
		serviceTypeService.create(serviceType);
		return serviceType;
	}

	/**
	 * Updates a ServiceType Entity based on ServiceType code.
	 * 
	 * @param postData posted data to API
	 * 
	 * @throws MeveoApiException meveo api exception
	 * @throws BusinessException business exception.
	 */
	public ServiceType update(ServiceTypeDto postData) throws MeveoApiException, BusinessException {
		String serviceTypeCode = postData.getCode();
		if (StringUtils.isBlank(serviceTypeCode)) {
			missingParameters.add("serviceTypeCode");
		}
		handleMissingParametersAndValidate(postData);

		ServiceType serviceType = serviceTypeService.findByCode(serviceTypeCode);
		if (serviceType == null) {
			throw new EntityDoesNotExistsException(ServiceType.class, serviceTypeCode);
		}
		serviceType.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
		serviceType.setDescription(postData.getDescription());
		serviceType.setServiceType(postData.getServiceType()); 
		serviceType = serviceTypeService.update(serviceType);

		return serviceType;
	}

	/**
	 * Remove a ServiceType Entity based on ServiceType code.
	 * 
	 * @param serviceTypeCode
	 * 
	 * @throws MeveoApiException meveo api exception
	 * @throws BusinessException business exception.
	 */

	
	public void remove(String serviceTypeCode) throws MeveoApiException, BusinessException { 

		if (StringUtils.isBlank(serviceTypeCode)) {
			missingParameters.add("serviceTypeCode");
		}

		ServiceType serviceType = serviceTypeService.findByCode(serviceTypeCode);
		if (serviceType == null) {
			throw new EntityDoesNotExistsException(ServiceType.class, serviceTypeCode);
		}

		serviceTypeService.remove(serviceType);
	}
 
 
	

}

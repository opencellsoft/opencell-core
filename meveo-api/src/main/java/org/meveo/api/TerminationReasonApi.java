package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.TerminationReasonService;

@Stateless
public class TerminationReasonApi extends BaseApi {

	@Inject
	private TerminationReasonService terminationReasonService;

	/**
	 * creates a SubscriptionTerminationReason based on code and description.
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void create(TerminationReasonDto postData, User currentUser) throws MeveoApiException {

		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			
			if (terminationReasonService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(SubscriptionTerminationReason.class, postData.getCode());
			}
			
			SubscriptionTerminationReason subscriptionTerminationReason = new SubscriptionTerminationReason();
			
			subscriptionTerminationReason.setCode(postData.getCode());
			subscriptionTerminationReason.setDescription(postData.getDescription());
			subscriptionTerminationReason.setApplyAgreement(postData.isApplyAgreement());
			subscriptionTerminationReason.setApplyReimbursment(postData.isApplyReimbursment());
			subscriptionTerminationReason.setApplyTerminationCharges(postData.isApplyTerminationCharges());
			
			terminationReasonService.create(subscriptionTerminationReason, currentUser, currentUser.getProvider());
			
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

	}
	
	/**
	 * updates a SubscriptionTerminationReason based on code.
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void update(TerminationReasonDto postData, User currentUser) throws MeveoApiException {
		
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			
			SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(postData.getCode(), currentUser.getProvider());
			
			if (subscriptionTerminationReason == null) {
				throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getCode());
			}
			
			subscriptionTerminationReason.setDescription(postData.getDescription());
			subscriptionTerminationReason.setApplyAgreement(postData.isApplyAgreement());
			subscriptionTerminationReason.setApplyReimbursment(postData.isApplyReimbursment());
			subscriptionTerminationReason.setApplyTerminationCharges(postData.isApplyTerminationCharges());
			
			terminationReasonService.update(subscriptionTerminationReason, currentUser);
			
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
	
	/**
	 * removes a SubscriptionTerminationReason from db based on code.
	 * @param code
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void remove(String code, User currentUser) throws MeveoApiException {
		
		if (!StringUtils.isBlank(code)) {
			
			SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(code, currentUser.getProvider());
			
			if (subscriptionTerminationReason == null) {
				throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, code);
			}
			
			terminationReasonService.remove(subscriptionTerminationReason);
			
		} else {
			missingParameters.add("code");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		
	}
	
	/**
	 * create or updates a SubscriptionTerminationReason.
	 * @param postData
	 * @param currentUser
	 * @throws MeveoApiException
	 */
	public void createOrUpdate(TerminationReasonDto postData, User currentUser) throws MeveoApiException {
		
		if (!StringUtils.isBlank(postData.getCode())) {
			
			SubscriptionTerminationReason subscriptionTerminationReason 
				= terminationReasonService.findByCode(postData.getCode(), currentUser.getProvider());
			
			if (subscriptionTerminationReason == null) {
				create(postData, currentUser);
			} else {
				update(postData, currentUser);
			}
			
			
		} else {
			missingParameters.add("code");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}
	
	/**
	 * Retrieves a SubscriptionTerminationReason based on code.
	 * @param code
	 * @param currentUser
	 * @return
	 * @throws MeveoApiException
	 */
	public TerminationReasonDto find(String code, User currentUser) throws MeveoApiException {
		TerminationReasonDto terminationReasonDto = null;
		
		if (!StringUtils.isBlank(code)) {
			
			SubscriptionTerminationReason subscriptionTerminationReason 
				= terminationReasonService.findByCode(code, currentUser.getProvider());
			
			if (subscriptionTerminationReason == null) {
				throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, code);
			}
			
			terminationReasonDto = new TerminationReasonDto(subscriptionTerminationReason);
			
		} else {
			missingParameters.add("code");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		
		return terminationReasonDto;
	}
	
	/**
	 * Retrieves all SubscriptionTerminationReason attached to the given provider.
	 * @param provider
	 * @return
	 * @throws MeveoApiException
	 */
	public List<TerminationReasonDto> list(Provider provider) throws MeveoApiException {
		List<TerminationReasonDto> terminationReasonDtos = new ArrayList<TerminationReasonDto>();
		
		List<SubscriptionTerminationReason> subscriptionTerminationReasons = terminationReasonService.list();
		
		if (subscriptionTerminationReasons != null && !subscriptionTerminationReasons.isEmpty()) {
			for (SubscriptionTerminationReason subscriptionTerminationReason: subscriptionTerminationReasons) {
				TerminationReasonDto terminationReasonDto = new TerminationReasonDto(subscriptionTerminationReason);
				terminationReasonDtos.add(terminationReasonDto);
			}
		}
		
		return terminationReasonDtos;
	}
	
}

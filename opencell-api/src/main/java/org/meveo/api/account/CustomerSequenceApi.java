package org.meveo.api.account;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.sequence.CustomerSequenceDto;
import org.meveo.api.dto.sequence.GenericSequenceValueResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.sequence.GenericSequenceApi;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.CustomerSequence;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.catalog.impl.CustomerSequenceService;

/**
 * API class for managing Customer Sequence. Handles both REST and SOAP calls.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
public class CustomerSequenceApi extends BaseApi {

	@Inject
	private SellerService sellerService;

	@Inject
	private CustomerSequenceService customerSequenceService;

	public void createCustomerSequence(CustomerSequenceDto postData) throws MeveoApiException, BusinessException {
		validateCustomerSequence(postData);

		if ((customerSequenceService.findByCode(postData.getCode())) != null) {
			throw new EntityAlreadyExistsException(CustomerSequence.class, postData.getCode());
		}

		CustomerSequence customerSequence = toCustomerSequence(postData, null);

		customerSequenceService.create(customerSequence);
	}

	public void updateCustomerSequence(CustomerSequenceDto postData) throws BusinessException, MeveoApiException {
		validateCustomerSequence(postData);

		CustomerSequence customerSequence = customerSequenceService.findByCode(postData.getCode());
		if (customerSequence == null) {
			throw new EntityDoesNotExistsException(CustomerSequence.class, postData.getCode());
		}

		toCustomerSequence(postData, customerSequence);

		customerSequenceService.update(customerSequence);
	}

	public List<CustomerSequenceDto> findAll() {
		List<CustomerSequenceDto> result = new ArrayList<>();
		List<CustomerSequence> customerSequences = customerSequenceService.list();
		if (customerSequences != null && !customerSequences.isEmpty()) {
			result = customerSequences.stream().map(p -> fromCustomerSequence(p, null)).collect(Collectors.toList());
		}

		return result;
	}

	public GenericSequenceValueResponseDto getNextNumber(String code)
			throws MissingParameterException, EntityDoesNotExistsException {
		GenericSequenceValueResponseDto result = new GenericSequenceValueResponseDto();

		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
		}

		handleMissingParameters();

		CustomerSequence customerSequence = customerSequenceService.findByCode(code);
		if (customerSequence == null) {
			throw new EntityDoesNotExistsException(CustomerSequence.class, code);
		}

		customerSequenceService.getNextNumber(customerSequence);

		if (customerSequence.getSeller() != null) {
			result.setSeller(customerSequence.getSeller().getCode());
		}

		GenericSequence genericSequence = customerSequence.getGenericSequence();
		String sequenceNumber = StringUtils.getLongAsNChar(genericSequence.getCurrentSequenceNb(),
				genericSequence.getSequenceSize());
		result.setSequence(GenericSequenceApi.fromGenericSequence(genericSequence));
		result.setValue(genericSequence.getPrefix() + sequenceNumber);

		return result;
	}

	public void validateCustomerSequence(CustomerSequenceDto postData) throws MeveoApiException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (postData.getGenericSequence() == null) {
			missingParameters.add("genericSequence");
		}
		if (postData.getGenericSequence().getSequenceSize() > 20) {
			throw new MeveoApiException("sequenceSize must be <= 20.");
		}
		if (StringUtils.isBlank(postData.getSeller())) {
			missingParameters.add("seller");
		}

		handleMissingParameters();
	}

	public CustomerSequence toCustomerSequence(CustomerSequenceDto source, CustomerSequence target) {
		if (target == null) {
			target = new CustomerSequence();
		}
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());
		if (source.getGenericSequence() != null) {
			target.setGenericSequence(
					GenericSequenceApi.toGenericSequence(source.getGenericSequence(), target.getGenericSequence()));
		}
		if (!StringUtils.isBlank(source.getSeller())) {
			target.setSeller(sellerService.findByCode(source.getSeller()));
		}

		return target;
	}

	public CustomerSequenceDto fromCustomerSequence(CustomerSequence source, CustomerSequenceDto target) {
		if (target == null) {
			target = new CustomerSequenceDto();
		}
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());
		if (source.getGenericSequence() != null) {
			target.setGenericSequence(GenericSequenceApi.fromGenericSequence(source.getGenericSequence()));
		}
		if (!StringUtils.isBlank(source.getSeller())) {
			target.setSeller(source.getSeller().getCode());
		}

		return target;
	}

}

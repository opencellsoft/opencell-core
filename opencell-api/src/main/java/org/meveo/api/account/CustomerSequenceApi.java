/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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
@Deprecated
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
		String prefix = genericSequence.getPrefix();
		result.setValue((prefix == null ? "" : prefix) + sequenceNumber);

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

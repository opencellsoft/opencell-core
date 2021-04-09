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

package org.meveo.api.payment;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.sequence.GenericSequenceDto;
import org.meveo.api.dto.sequence.GenericSequenceValueResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.sequence.GenericSequenceApi;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Provider;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.service.crm.impl.ProviderService;

/**
 * API class for managing RumSequence. Handles both REST and SOAP calls.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Stateless
@Deprecated
public class RumSequenceApi extends BaseApi {

	@Inject
	private ProviderService providerService;

	public void update(GenericSequenceDto postData) throws BusinessException, MeveoApiException {
		if (postData.getSequenceSize() > 20) {
			throw new MeveoApiException("sequenceSize must be <= 20.");
		}

		Provider provider = providerService.findById(appProvider.getId());
		provider.setRumSequence(GenericSequenceApi.toGenericSequence(postData, provider.getRumSequence()));
		providerService.update(provider);
	}

	public GenericSequenceValueResponseDto getNextMandateNumber() throws BusinessException {
		GenericSequenceValueResponseDto result = new GenericSequenceValueResponseDto();

		GenericSequence rumSequence = providerService.getNextMandateNumber();
		String rumSequenceNumber = StringUtils.getLongAsNChar(rumSequence.getCurrentSequenceNb(), rumSequence.getSequenceSize());
		result.setSequence(GenericSequenceApi.fromGenericSequence(rumSequence));
		String prefix = rumSequence.getPrefix();
		result.setValue((prefix == null ? "" : prefix) + rumSequenceNumber);

		return result;
	}

}

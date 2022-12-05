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

package org.meveo.api.communication;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.communication.CommunicationRequestDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.communication.impl.MeveoInstanceService;

@Stateless
public class CommunicationApi extends BaseApi {

	@Inject
	MeveoInstanceService meveoInstanceService;

	/**
	 * @param communicationRequestDto communication request
	 * @throws MeveoApiException meveo api exception
	 */
	public void inboundCommunication(CommunicationRequestDto communicationRequestDto) throws MeveoApiException {
	    String meveoInstanceCode = null;
        if (communicationRequestDto == null) {
	        missingParameters.add("Request");
	    } else {
	        meveoInstanceCode = communicationRequestDto.getMeveoInstanceCode();
	        if (StringUtils.isBlank(meveoInstanceCode)) {
	            missingParameters.add("MeveoInstanceCode");
	        }

	        if (StringUtils.isBlank(communicationRequestDto.getSubject())) {
	            missingParameters.add("Subject");
	        }
	    }
	    
		
		
		handleMissingParameters();
		

		MeveoInstance meveoInstance = meveoInstanceService.findByCode(meveoInstanceCode);
		if (meveoInstance != null) {
			// if(meveoInstance.getStatus() == MeveoInstanceStatusEnum.UNKNOWN)
			meveoInstanceService.fireInboundCommunicationEvent(communicationRequestDto);
		} else {
			throw new EntityDoesNotExistsException(MeveoInstance.class, meveoInstanceCode);
		}
	}

}

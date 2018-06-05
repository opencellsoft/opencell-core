package org.meveo.api.communication;

import javax.ejb.Stateless;
import javax.inject.Inject;

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

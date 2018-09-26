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
		result.setValue(rumSequence.getPrefix() + rumSequenceNumber);

		return result;
	}

}

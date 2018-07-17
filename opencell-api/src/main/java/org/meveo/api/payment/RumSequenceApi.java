package org.meveo.api.payment;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.RumSequenceDto;
import org.meveo.api.dto.response.payment.RumSequenceValueResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.RumSequence;
import org.meveo.service.crm.impl.ProviderService;

/**
 * API class for managing RumSequence. Handles both REST and SOAP calls.
 * 
 * @author Edward P. Legaspi
 * @LastModifiedVersion 5.2
 */
@Stateless
public class RumSequenceApi extends BaseApi {

	@Inject
	private ProviderService providerService;

	public void update(RumSequenceDto postData) throws BusinessException, MeveoApiException {
		if (postData.getSequenceSize() > 20) {
			throw new MeveoApiException("sequenceSize must be <= 20.");
		}

		Provider provider = providerService.findById(appProvider.getId());
		provider.setRumSequence(toRumSequence(postData, provider.getRumSequence()));
		providerService.update(provider);
	}

	public RumSequenceValueResponseDto getNextMandateNumber() throws BusinessException {
		RumSequenceValueResponseDto result = new RumSequenceValueResponseDto();

		RumSequence rumSequence = providerService.getNextMandateNumber();
		String rumSequenceNumber = StringUtils.getLongAsNChar(rumSequence.getCurrentSequenceNb(), rumSequence.getSequenceSize());
		result.setRumSequenceDto(fromRumSequence(rumSequence));
		result.setValue(rumSequence.getPrefix() + rumSequenceNumber);

		return result;
	}

	public RumSequence toRumSequence(RumSequenceDto source, RumSequence target) {
		if (target == null) {
			target = new RumSequence();
		}
		target.setPrefix(source.getPrefix());
		target.setSequenceSize(source.getSequenceSize());

		return target;
	}

	public RumSequenceDto fromRumSequence(RumSequence source) {
		RumSequenceDto target = new RumSequenceDto();

		target.setPrefix(source.getPrefix());
		target.setSequenceSize(source.getSequenceSize());
		if (source.getCurrentSequenceNb() != null) {
			target.setCurrentSequenceNb(source.getCurrentSequenceNb());
		}

		return target;
	}

}

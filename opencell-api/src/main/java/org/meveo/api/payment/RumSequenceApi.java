package org.meveo.api.payment;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.RumSequenceDto;
import org.meveo.api.dto.response.payment.RumSequenceValueResponseDto;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.RumSequence;
import org.meveo.service.crm.impl.ProviderService;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class RumSequenceApi extends BaseApi {

	@Inject
	private ProviderService providerService;

	public void update(RumSequenceDto postData) throws BusinessException {
		Provider provider = providerService.refreshOrRetrieve(appProvider);
		provider.setRumSequence(toRumSequence(postData, provider.getRumSequence()));
		providerService.update(provider);
	}

	public RumSequenceValueResponseDto getNextMandateNumber() {
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
		if (source.getCurrentSequenceNb() != null) {
			target.setCurrentSequenceNb(source.getCurrentSequenceNb());
		}

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

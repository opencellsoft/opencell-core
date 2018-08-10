package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.sequence.GenericSequenceDto;
import org.meveo.api.dto.sequence.GenericSequenceValueResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.RumSequenceApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.RumSequenceRs;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class RumSequenceRsImpl extends BaseRs implements RumSequenceRs {

	@Inject
	private RumSequenceApi rumSequenceApi;

	@Override
	public ActionStatus update(GenericSequenceDto postData) {
		ActionStatus result = new ActionStatus();

		try {
			rumSequenceApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public GenericSequenceValueResponseDto getNextMandateNumber() {
		GenericSequenceValueResponseDto result = new GenericSequenceValueResponseDto();

		try {
			result = rumSequenceApi.getNextMandateNumber();
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

}

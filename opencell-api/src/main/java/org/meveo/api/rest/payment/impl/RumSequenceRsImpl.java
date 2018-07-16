package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.payment.RumSequenceDto;
import org.meveo.api.dto.response.payment.RumSequenceValueResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.RumSequenceApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.RumSequenceRs;

/**
 * @author Edward P. Legaspi
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class RumSequenceRsImpl extends BaseRs implements RumSequenceRs {

	@Inject
	private RumSequenceApi rumSequenceApi;

	@Override
	public ActionStatus update(RumSequenceDto postData) {
		ActionStatus result = new ActionStatus();

		try {
			rumSequenceApi.update(postData);
		} catch (Exception e) {
			processException(e, result);
		}

		return result;
	}

	@Override
	public RumSequenceValueResponseDto getNextMandateNumber() {
		RumSequenceValueResponseDto result = new RumSequenceValueResponseDto();

		try {
			result = rumSequenceApi.getNextMandateNumber();
		} catch (Exception e) {
			processException(e, result.getActionStatus());
		}

		return result;
	}

}

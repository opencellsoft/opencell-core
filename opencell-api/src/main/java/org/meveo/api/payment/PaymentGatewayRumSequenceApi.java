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
import org.meveo.api.dto.payment.PaymentGatewayRumSequenceDto;
import org.meveo.api.dto.sequence.GenericSequenceValueResponseDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.sequence.GenericSequenceApi;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.payments.PaymentGatewayRumSequence;
import org.meveo.model.sequence.GenericSequence;
import org.meveo.service.payments.impl.PaymentGatewayRumSequenceService;
import org.meveo.service.payments.impl.PaymentGatewayService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
@Stateless
public class PaymentGatewayRumSequenceApi extends BaseApi {

	@Inject
	private PaymentGatewayRumSequenceService paymentGatewayRumSequenceService;

	@Inject
	private PaymentGatewayService paymentGatewayService;

	public void validatePaymentGatewayRumSequence(PaymentGatewayRumSequenceDto postData) throws MeveoApiException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (postData.getGenericSequence() == null) {
			missingParameters.add("genericSequence");
		}
		if (postData.getGenericSequence().getSequenceSize() > 20) {
			throw new MeveoApiException("sequenceSize must be <= 20.");
		}
		if (StringUtils.isBlank(postData.getPaymentGateway())) {
			missingParameters.add("paymentGateway");
		}

		handleMissingParameters();
	}

	public void create(PaymentGatewayRumSequenceDto postData) throws MeveoApiException, BusinessException {
		validatePaymentGatewayRumSequence(postData);

		if ((paymentGatewayRumSequenceService.findByCode(postData.getCode())) != null) {
			throw new EntityAlreadyExistsException(PaymentGatewayRumSequence.class, postData.getCode());
		}

		PaymentGatewayRumSequence rumSequence = toPaymentGatewayRumSequence(postData, null);

		paymentGatewayRumSequenceService.create(rumSequence);
	}

	public void update(PaymentGatewayRumSequenceDto postData) throws MeveoApiException, BusinessException {
		validatePaymentGatewayRumSequence(postData);

		PaymentGatewayRumSequence rumSequence = paymentGatewayRumSequenceService.findByCode(postData.getCode());
		if (rumSequence == null) {
			throw new EntityDoesNotExistsException(PaymentGatewayRumSequence.class, postData.getCode());
		}

		toPaymentGatewayRumSequence(postData, rumSequence);

		paymentGatewayRumSequenceService.update(rumSequence);
	}

	public PaymentGatewayRumSequenceDto find(String code) throws EntityDoesNotExistsException {
		PaymentGatewayRumSequence rumSequence = paymentGatewayRumSequenceService.findByCode(code);
		if (rumSequence == null) {
			throw new EntityDoesNotExistsException(PaymentGatewayRumSequence.class, code);
		}

		return fromPaymentGatewayRumSequence(rumSequence, null);
	}

	public void delete(String code) throws EntityDoesNotExistsException, BusinessException {
		PaymentGatewayRumSequence rumSequence = paymentGatewayRumSequenceService.findByCode(code);
		if (rumSequence == null) {
			throw new EntityDoesNotExistsException(PaymentGatewayRumSequence.class, code);
		}

		paymentGatewayRumSequenceService.remove(rumSequence);
	}

	public GenericSequenceValueResponseDto getNextNumber(String code) throws MissingParameterException, EntityDoesNotExistsException {
		GenericSequenceValueResponseDto result = new GenericSequenceValueResponseDto();

		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
		}

		handleMissingParameters();

		PaymentGatewayRumSequence rumSequence = paymentGatewayRumSequenceService.findByCode(code);
		if (rumSequence == null) {
			throw new EntityDoesNotExistsException(PaymentGatewayRumSequence.class, code);
		}

		paymentGatewayRumSequenceService.getNextNumber(rumSequence);

		if (rumSequence.getPaymentGateway() != null) {
			result.setPaymentGateway(rumSequence.getPaymentGateway().getCode());
		}

		GenericSequence genericSequence = rumSequence.getGenericSequence();
		String sequenceNumber = StringUtils.getLongAsNChar(genericSequence.getCurrentSequenceNb(),
				genericSequence.getSequenceSize());
		result.setSequence(GenericSequenceApi.fromGenericSequence(genericSequence));
		String prefix = genericSequence.getPrefix();
		result.setValue((prefix == null ? "" : prefix) + sequenceNumber);

		return result;
	}

	private PaymentGatewayRumSequence toPaymentGatewayRumSequence(PaymentGatewayRumSequenceDto source,
			PaymentGatewayRumSequence target) {
		if (target == null) {
			target = new PaymentGatewayRumSequence();
		}
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());
		if (source.getGenericSequence() != null) {
			target.setGenericSequence(
					GenericSequenceApi.toGenericSequence(source.getGenericSequence(), target.getGenericSequence()));
		}
		if (!StringUtils.isBlank(source.getPaymentGateway())) {
			target.setPaymentGateway(paymentGatewayService.findByCode(source.getPaymentGateway()));
		}

		return target;
	}

	public PaymentGatewayRumSequenceDto fromPaymentGatewayRumSequence(PaymentGatewayRumSequence source,
			PaymentGatewayRumSequenceDto target) {
		if (target == null) {
			target = new PaymentGatewayRumSequenceDto();
		}
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());
		if (source.getGenericSequence() != null) {
			target.setGenericSequence(GenericSequenceApi.fromGenericSequence(source.getGenericSequence()));
		}
		if (!StringUtils.isBlank(source.getPaymentGateway())) {
			target.setPaymentGateway(source.getPaymentGateway().getCode());
		}

		return target;
	}

}

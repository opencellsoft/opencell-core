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

package org.meveo.api.rest.impl;

import static java.util.Optional.ofNullable;
import static org.meveo.api.dto.ActionStatusEnum.SUCCESS;

import org.meveo.api.custom.GenericCodeApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.custom.GenericCodeDto;
import org.meveo.api.dto.custom.GenericCodeResponseDto;
import org.meveo.api.dto.custom.GetGenericCodeResponseDto;
import org.meveo.api.dto.custom.SequenceDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.GenericCodeRs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class GenericCodeRsImpl extends BaseRs implements GenericCodeRs {

	@Inject
	private GenericCodeApi genericCodeApi;

	@Override
	public GenericCodeResponseDto getGenericCode(GenericCodeDto codeDto) {
		GenericCodeResponseDto genericCodeResponseDto = new GenericCodeResponseDto();
		genericCodeResponseDto.setGeneratedCode(genericCodeApi.getGenericCode(codeDto));
		ofNullable(codeDto.getSequence())
				.ifPresent(sequenceDto-> genericCodeResponseDto.setSequenceType(sequenceDto.getSequenceType().name()));
		return genericCodeResponseDto;
	}

	@Override
	public ActionStatus create(GenericCodeDto codeDto) {
		ActionStatus result = new ActionStatus(SUCCESS, "");
		try {
			genericCodeApi.create(codeDto);
		} catch (Exception exception) {
			processException(exception, result);
		}
		return result;
	}

	@Override
	public ActionStatus update(GenericCodeDto codeDto) {
		ActionStatus result = new ActionStatus(SUCCESS, "");
		try {
			genericCodeApi.update(codeDto);
		} catch (Exception exception) {
			processException(exception, result);
		}
		return result;
	}

	@Override
	public GetGenericCodeResponseDto find(String entityClass) {
		return genericCodeApi.find(entityClass)
				.orElseThrow(() -> new EntityDoesNotExistsException("No generic code associated to entityClass : " + entityClass));
	}

	@Override
	public ActionStatus createSequence(SequenceDto sequenceDto) {
		ActionStatus result = new ActionStatus(SUCCESS, "");
		try {
			genericCodeApi.createSequence(sequenceDto);
		} catch (Exception exception) {
			processException(exception, result);
		}
		return result;
	}
}
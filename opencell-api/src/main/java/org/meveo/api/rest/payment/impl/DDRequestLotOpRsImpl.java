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

package org.meveo.api.rest.payment.impl;

import java.util.Date;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.response.payment.DDRequestLotOpsResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.DDRequestLotOpApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.DDRequestLotOpRs;
import org.meveo.model.payments.DDRequestOpStatusEnum;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class DDRequestLotOpRsImpl extends BaseRs implements DDRequestLotOpRs {

    @Inject
    private DDRequestLotOpApi ddrequestLotOpApi;

	@Override
	public ActionStatus create(DDRequestLotOpDto dto) {
      ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

      try {
          ddrequestLotOpApi.create(dto);
      } catch (MeveoApiException e) {
          result.setStatus(ActionStatusEnum.FAIL);
          result.setMessage(e.getMessage());
          log.error("error occurred while creating account operation ", e);
      } catch (Exception e) {
          result.setStatus(ActionStatusEnum.FAIL);
          result.setMessage(e.getMessage());
          log.error("error generated while creating account operation ", e);
      }

      return result;	}

	@Override
	public DDRequestLotOpsResponseDto list(Date fromDueDate, Date toDueDate, DDRequestOpStatusEnum status) {
      DDRequestLotOpsResponseDto result = new DDRequestLotOpsResponseDto();

      try {
          result.setDdrequestLotOps(ddrequestLotOpApi.listDDRequestLotOps(fromDueDate, toDueDate, status));
//      } catch (MeveoApiException e) {
//          result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
//          result.getActionStatus().setMessage(e.getMessage());
//          log.error("error occurred while getting list account operation ", e);
      } catch (Exception e) {
          result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
          result.getActionStatus().setMessage(e.getMessage());
          log.error("error generated while getting list account operation ", e);
      }

      return result;
  }
}

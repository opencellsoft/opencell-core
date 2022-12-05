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

package org.meveo.api.pub.rest.document.impl;

import java.util.List;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.YouSignApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.document.sign.SignCallbackDto;
import org.meveo.api.dto.document.sign.SignFileResponseDto;
import org.meveo.api.dto.document.sign.YousignEventEnum;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.pub.rest.document.YousignCallbackRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * The default Implementation of YousignCallbackRs.
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class YousignCallbackRsImpl extends BaseRs implements YousignCallbackRs {

    @Inject
    private YouSignApi youSignApi;

    @Override
    public ActionStatus youSignCallback(SignCallbackDto callbackDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            YousignEventEnum event = callbackDto.getEventName();
            switch (event) {
            case PROCEDURE_FINISHED:
                this.downloadFilesById(callbackDto.getProcedure().getFiles());
                break;
            case PROCEDURE_STARTED: // Not yet needed
            default:
                result = new ActionStatus(ActionStatusEnum.FAIL, " Event not supported : " + event);
                break;
            }
        } catch (Exception e) {
            processException(e, new ActionStatus(ActionStatusEnum.FAIL, e.getMessage()));
        }

        return result;
    }

    private void downloadFilesById(List<SignFileResponseDto> files) throws MeveoApiException {
        for (SignFileResponseDto fileResponseDto :files) {
            this.youSignApi.downloadFileByIdAndSaveInServer(fileResponseDto.getId().substring(7), fileResponseDto.getName());
        }
    }

}

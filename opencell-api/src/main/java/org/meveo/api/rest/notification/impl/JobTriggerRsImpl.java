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

package org.meveo.api.rest.notification.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.response.notification.GetJobTriggerResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.notification.JobTriggerApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.notification.JobTriggerRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.notification.JobTrigger;

/**
 * @author Tyshan Shi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class JobTriggerRsImpl extends BaseRs implements JobTriggerRs {

    @Inject
    private JobTriggerApi jobTriggerApi;

    @Override
    public ActionStatus create(JobTriggerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            JobTrigger jobTrigger = jobTriggerApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(jobTrigger.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus update(JobTriggerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            jobTriggerApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetJobTriggerResponseDto find(String notificationCode) {
        GetJobTriggerResponseDto result = new GetJobTriggerResponseDto();

        try {
            result.setJobTriggerDto(jobTriggerApi.find(notificationCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus remove(String notificationCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            jobTriggerApi.remove(notificationCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(JobTriggerDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            JobTrigger jobTrigger = jobTriggerApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(jobTrigger.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            jobTriggerApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            jobTriggerApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
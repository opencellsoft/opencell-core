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

package org.meveo.api.rest.job.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.job.JobExecutionResultDto;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.job.*;
import org.meveo.api.job.JobApi;
import org.meveo.api.job.JobInstanceApi;
import org.meveo.api.job.TimerEntityApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.job.JobRs;
import org.meveo.model.jobs.MeveoJobCategoryEnum;

/**
 * @author Edward P. Legaspi
 * @author Adnane Boubia
 * @lastModifiedVersion 5.0
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class JobRsImpl extends BaseRs implements JobRs {

    @Inject
    private JobApi jobApi;

    @Inject
    private JobInstanceApi jobInstanceApi;

    @Inject
    private TimerEntityApi timerEntityApi;

    @Override
    public JobExecutionResultResponseDto execute(JobInstanceInfoDto jobInstanceInfoDto) {
        JobExecutionResultResponseDto result = new JobExecutionResultResponseDto();

        try {
            JobExecutionResultDto executionResult = jobApi.executeJob(jobInstanceInfoDto);
            result.setJobExecutionResultDto(executionResult);
            result.getActionStatus().setMessage(executionResult.getId() == null ? "NOTHING_TO_DO" : executionResult.getId().toString());

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public JobExecutionResultResponseDto execution(JobInstanceInfoDto postData) {
        if(postData.isStart())
            return execute(postData);
        else {
            JobExecutionResultResponseDto jobExecutionResultResponseDto = new JobExecutionResultResponseDto();
            jobExecutionResultResponseDto.setActionStatus(stop(postData.getCode()));
            return jobExecutionResultResponseDto;
        }
    }

    @Override
    public ActionStatus stop(String jobInstanceCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
             jobApi.stopJob(jobInstanceCode);           
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus create(JobInstanceDto jobInstanceDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            jobInstanceApi.create(jobInstanceDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createV2(JobInstanceDto jobInstanceDto) {
        return create(jobInstanceDto);
    }

    @Override
    public ActionStatus createTimer(TimerEntityDto timerEntityDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            timerEntityApi.create(timerEntityDto);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(JobInstanceDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            jobInstanceApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(JobInstanceDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            jobInstanceApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public JobInstanceResponseDto find(String jobInstanceCode) {
        JobInstanceResponseDto result = new JobInstanceResponseDto();

        try {
            result.setJobInstanceDto(jobInstanceApi.find(jobInstanceCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public JobInstanceResponseDto findV2(String jobInstanceCode) {
        return find(jobInstanceCode);
    }

    @Override
    public ActionStatus remove(String jobInstanceCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            jobInstanceApi.remove(jobInstanceCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateTimer(TimerEntityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            timerEntityApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateTimer(TimerEntityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            timerEntityApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public TimerEntityResponseDto findTimer(String timerCode) {
        TimerEntityResponseDto result = new TimerEntityResponseDto();

        try {
            result.setTimerEntity(timerEntityApi.find(timerCode));

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public TimerEntityResponseDto findTimerV2(String timerCode) {
        return findTimer(timerCode);
    }

    @Override
    public ActionStatus removeTimer(String timerCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            timerEntityApi.remove(timerCode);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public JobExecutionResultResponseDto findJobExecutionResult(String code, Long jobExecutionResultId) {
        JobExecutionResultResponseDto result = new JobExecutionResultResponseDto();
        try {
            result.setJobExecutionResultDto(jobApi.findJobExecutionResult(code, jobExecutionResultId));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public JobExecutionResultResponseDto findJobExecutionResultV2(String code, Long jobExecutionResultId) {
        return findJobExecutionResult(code, jobExecutionResultId);
    }

    @Override
    public JobExecutionResultsResponseDto list(String query, String fields, Integer offset,
                                               Integer limit, String sortBy, SortOrder sortOrder) {
        try {
            return jobApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            JobExecutionResultsResponseDto result = new JobExecutionResultsResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    @Override
    public JobExecutionResultsResponseDto list(PagingAndFiltering pagingAndFiltering) {
        try {
            return jobApi.list(pagingAndFiltering);
        } catch (Exception e) {
            JobExecutionResultsResponseDto result = new JobExecutionResultsResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }


    @Override
    public JobCategoriesResponseDto listCategories() {
        JobCategoriesResponseDto result = new JobCategoriesResponseDto();
        try {
            result.setJobCategories(MeveoJobCategoryEnum.values());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }
    
    @Override
	public JobInstanceListResponseDto list(Integer offset, Integer limit, boolean mergedCF, String sortBy, SortOrder sortOrder) {
		JobInstanceListResponseDto result = new JobInstanceListResponseDto();

        try {
            result = jobInstanceApi.list(mergedCF, new PagingAndFiltering(null, null, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
	}    
    
}
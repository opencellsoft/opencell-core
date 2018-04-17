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
import org.meveo.api.dto.response.job.JobExecutionResultResponseDto;
import org.meveo.api.dto.response.job.JobInstanceResponseDto;
import org.meveo.api.dto.response.job.TimerEntityResponseDto;
import org.meveo.api.job.JobApi;
import org.meveo.api.job.JobInstanceApi;
import org.meveo.api.job.TimerEntityApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.job.JobRs;

/**
 * @author Edward P. Legaspi
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
    
}
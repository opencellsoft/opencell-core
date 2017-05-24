package org.meveo.api.rest.job.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.response.job.JobInstanceResponseDto;
import org.meveo.api.job.JobInstanceApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.job.JobInstanceRs;

/**
 * 
 * @author Manu Liwanag
 * 
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class JobInstanceRsImpl extends BaseRs implements JobInstanceRs {

    @Inject
    private JobInstanceApi jobInstanceApi;

    @Override
    public ActionStatus create(JobInstanceDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            jobInstanceApi.create(postData);
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
}

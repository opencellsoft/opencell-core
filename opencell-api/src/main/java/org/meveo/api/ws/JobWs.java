package org.meveo.api.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.job.JobCategoriesResponseDto;
import org.meveo.api.dto.response.job.JobExecutionResultResponseDto;
import org.meveo.api.dto.response.job.JobInstanceListResponseDto;
import org.meveo.api.dto.response.job.JobInstanceResponseDto;
import org.meveo.api.dto.response.job.TimerEntityResponseDto;

/**
 * @author Edward P. Legaspi
 * @author Adnane Boubia
 * @lastModifiedVersion 5.0
 */
@WebService
public interface JobWs extends IBaseWs {

    // job instance

    @WebMethod
    JobExecutionResultResponseDto execute(@WebParam(name = "jobInstanceInfo") JobInstanceInfoDto postData);

    @WebMethod
    ActionStatus stop(@WebParam(name = "jobInstanceCode") String jobInstanceCode);

    @WebMethod
    ActionStatus create(@WebParam(name = "jobInstance") JobInstanceDto postData);

    @WebMethod
    ActionStatus update(@WebParam(name = "jobInstance") JobInstanceDto postData);

    @WebMethod
    ActionStatus createOrUpdateJobInstance(@WebParam(name = "jobInstance") JobInstanceDto postData);

    @WebMethod
    JobInstanceResponseDto findJobInstance(@WebParam(name = "jobInstanceCode") String jobInstanceCode);

    @WebMethod
    ActionStatus removeJobInstance(@WebParam(name = "jobInstanceCode") String jobInstanceCode);

    /**
     * Enable a Job instance by its code
     * 
     * @param code Job instance code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableJobInstance(@WebParam(name = "code") String code);

    /**
     * Disable a Job instance by its code
     * 
     * @param code Job instance code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableJobInstance(@WebParam(name = "code") String code);

    // timer

    @WebMethod
    ActionStatus createTimer(@WebParam(name = "timerEntity") TimerEntityDto postData);

    @WebMethod
    ActionStatus updateTimer(@WebParam(name = "timerEntity") TimerEntityDto postData);

    @WebMethod
    ActionStatus createOrUpdateTimer(@WebParam(name = "timerEntity") TimerEntityDto postData);

    @WebMethod
    TimerEntityResponseDto findTimer(@WebParam(name = "timerCode") String timerCode);

    @WebMethod
    ActionStatus removeTimer(@WebParam(name = "timerCode") String timerCode);

    /**
     * Enable a Timer scheduler by its code
     * 
     * @param code Timer scheduler code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enableTimer(@WebParam(name = "code") String code);

    /**
     * Disable a Timer scheduler by its code
     * 
     * @param code Timer scheduler code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disableTimer(@WebParam(name = "code") String code);

    @WebMethod
    JobExecutionResultResponseDto findJobExecutionResult(@WebParam(name = "code") String code, @WebParam(name = "jobExecutionResultId") Long jobExecutionResultId);
    
    /**
     * List job categories
     * 
     * @return object containing the list of job categories
     */
    @WebMethod
    JobCategoriesResponseDto listCategories();
    
    /**
     * List job instances
     * 
     * @param mergedCF Should inherited custom field values be included. Deprecated in v. 4.7.2 Use pagingAndFiltering.fields="inheritedCF" instead
     * @param pagingAndFiltering Paging and filtering criteria
     * @return List of job instances
     */
    @WebMethod
    JobInstanceListResponseDto list(@Deprecated @WebParam(name = "mergedCF") Boolean mergedCF, @WebParam(name = "pagingAndFiltering") PagingAndFiltering pagingAndFiltering);

}
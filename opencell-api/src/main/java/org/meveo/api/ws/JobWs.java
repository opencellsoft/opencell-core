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
@Deprecated
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
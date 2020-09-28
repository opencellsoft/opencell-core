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

package org.meveo.api.job;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.job.JobExecutionResultDto;
import org.meveo.api.dto.job.JobExecutionResultsDto;
import org.meveo.api.dto.job.JobInstanceInfoDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.job.JobExecutionResultsResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.cache.JobCacheContainerProvider;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.JobInstanceService;
import org.primefaces.model.SortOrder;

/**
 * @author anasseh
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 **/
@Stateless
public class JobApi extends BaseApi {

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private JobExecutionService jobExecutionService;

    @Inject
    private JobCacheContainerProvider jobCacheContainerProvider;

    /**
     * Execute job.
     * 
     * @param jobExecution Job execution info
     * @return Job execution result identifier
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public JobExecutionResultDto executeJob(JobInstanceInfoDto jobExecution) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(jobExecution.getCode()) && StringUtils.isBlank(jobExecution.getTimerName())) {
            missingParameters.add("timerName or code");
        }
        handleMissingParameters();

        String code = jobExecution.getCode() != null ? jobExecution.getCode() : jobExecution.getTimerName();

        JobInstance jobInstance = jobInstanceService.findByCode(code);
        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, code);
        }
        
        jobInstanceService.createMissingCustomFieldTemplates(jobInstance);
        
        // populate customFields
        try {
            populateCustomFields(jobExecution.getCustomFields(), jobInstance, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        // #3063 Ability to pass parameters when running job instance                                                                                  
        this.setJobRunTimeJobValues(jobExecution, jobInstance);

        if (jobExecution.isForceExecution()) {
            jobCacheContainerProvider.resetJobRunningStatus(jobInstance.getId());
        }

        Long executionId = jobExecutionService.executeJobWithResultId(jobInstance, null);

        return findJobExecutionResult(null, executionId);
    }

    
    /**
     * Sets the run time job values.
     *
     * @param jobExecution the job execution
     * @param jobInstance the job instance
     * @throws MeveoApiException 
     */
    private void setJobRunTimeJobValues(JobInstanceInfoDto jobExecution, JobInstance jobInstance) throws MeveoApiException {
        
        Map<String , Object> jobRunTimeValues = new HashMap<>();
        
        final String  runOnNodes = jobExecution.getRunOnNodes();
        if (isNotEmpty(runOnNodes)) {
            jobRunTimeValues.put("runOnNodes", runOnNodes);
        }
        final String parameters = jobExecution.getParameters();
        if (isNotEmpty(parameters)) {
            jobRunTimeValues.put("parameters", parameters);
        }
        
        CustomFieldsDto customFieldsDto = jobExecution.getCustomFields();
        if (customFieldsDto != null && CollectionUtils.isNotEmpty(customFieldsDto.getCustomField())) {
            List<CustomFieldDto> cfDtos = customFieldsDto.getCustomField();
            this.validateAndConvertCustomFields(cfDtos, jobInstance);
            
            for (CustomFieldDto cfDto : cfDtos) {
                jobRunTimeValues.put(cfDto.getCode(), cfDto.getValueConverted());
            }
        }
        jobInstance.setRunTimeValues(jobRunTimeValues);
    }

    /**
     * Stop running job
     * 
     * @param jobInstanceCode job instance code to stop
     * @throws MeveoApiException Meveo api exception
     */
    public void stopJob(String jobInstanceCode) throws MeveoApiException {
        if (StringUtils.isBlank(jobInstanceCode)) {
            missingParameters.add("jobInstanceCode");
        }
        handleMissingParameters();
        org.meveo.model.jobs.JobInstance jobInstance = jobInstanceService.findByCode(jobInstanceCode);
        if (jobInstance == null) {
            throw new EntityDoesNotExistsException(JobInstance.class, jobInstanceCode);
        }

        try {
            jobExecutionService.stopJob(jobInstance);
        } catch (BusinessException e) {
            throw new MeveoApiException(e.getMessage());
        }
    }

    /**
     * Retrieve job execution result. 
     * 		Can be matched by:
     * 			JobInstance code, in that case the last execution is returned
     * 			JobExecution id, the job execution is matched by ID.
     *      If both code and id are provided, an InvalidParameterException is thrown.
     * 
     * @param code The job instance code. If used,the last job execution instance is returned.
     * @param id Job execution result identifier.
     * @return Job execution result DTO
     * @throws MeveoApiException meveo api exception
     */
    public JobExecutionResultDto findJobExecutionResult(String code, Long id) throws MeveoApiException {
        JobExecutionResultDto jobExecutionResultDto;
        if (StringUtils.isBlank(code) && StringUtils.isBlank(id)) {
            missingParameters.add("id or code");
            handleMissingParameters();
        }
        if(!StringUtils.isBlank(code) && !StringUtils.isBlank(id)) {
        	throw new InvalidParameterException("Selection by both 'id' and 'code' is not allowed");
        }

        JobExecutionResultImpl jobExecutionResult = new JobExecutionResultImpl();
        if (!StringUtils.isBlank(id)) {
            jobExecutionResult = jobExecutionService.findById(id);
            if (jobExecutionResult == null) {
                throw new EntityDoesNotExistsException(JobExecutionResultImpl.class, id);
            }

        } else if (!StringUtils.isBlank(code)) {
            JobInstance jobInstance = jobInstanceService.findByCode(code);
            if (jobInstance == null) {
                throw new EntityDoesNotExistsException(JobInstance.class, code);
            }

            jobExecutionResult = jobExecutionService.findLastExecutionByInstance(jobInstance);
            if (jobExecutionResult == null) {
                throw new EntityDoesNotExistsException(JobExecutionResultImpl.class, code);
            }
        }

        jobExecutionResultDto = new JobExecutionResultDto(jobExecutionResult);

        if (jobExecutionResult.getEndDate() == null) {
            List<String> nodeNames = jobCacheContainerProvider.getNodesJobIsRuningOn(jobExecutionResult.getJobInstance().getId());
            if (nodeNames != null && !nodeNames.isEmpty()) {
                jobExecutionResultDto.setRunningOnNodes(StringUtils.concatenate(",", nodeNames));
            }
        }

        return jobExecutionResultDto;
    }

    public JobExecutionResultsResponseDto list(PagingAndFiltering pagingAndFiltering) throws MeveoApiException {

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        PaginationConfiguration paginationConfig =
                toPaginationConfiguration(pagingAndFiltering.getSortBy(),
                        SortOrder.ASCENDING, null, pagingAndFiltering, JobExecutionResultImpl.class);

        Long totalCount = jobExecutionService.count(paginationConfig);

        JobExecutionResultsResponseDto jobExecutionResultsResponse = new JobExecutionResultsResponseDto();
        JobExecutionResultsDto jobExecutionResult = new JobExecutionResultsDto();
        jobExecutionResult.setTotalNumberOfRecords(totalCount);

        jobExecutionResultsResponse.setPaging(pagingAndFiltering);
        jobExecutionResultsResponse.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<JobExecutionResultImpl> jobExecutionResults = jobExecutionService.list(paginationConfig);
            jobExecutionResult.setJobExecutionResults(jobExecutionResults
                                                .stream()
                                                .map(JobExecutionResultDto::new)
                                                .collect(Collectors.toList()));
        }
        jobExecutionResultsResponse.setJobExecutionResult(jobExecutionResult);
        return jobExecutionResultsResponse;
    }
}
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

package org.meveo.api.finance;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ReportExtractExecutionException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.finance.ReportExtractExecutionResultDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.finance.ReportExtractExecutionResultsResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractsResponseDto;
import org.meveo.api.dto.response.finance.RunReportExtractDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.ReportExtract;
import org.meveo.model.finance.ReportExtractExecutionOrigin;
import org.meveo.model.finance.ReportExtractExecutionResult;
import org.meveo.model.finance.ReportExtractResultTypeEnum;
import org.meveo.model.finance.ReportExtractScriptTypeEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.finance.ReportExtractExecutionResultService;
import org.meveo.service.finance.ReportExtractService;
import org.meveo.service.script.ScriptInstanceService;
import org.primefaces.model.SortOrder;

/**
 * The CRUD Api for ReportExtract Entity.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @since 5.0
 * @lastModifiedVersion 7.0
 */
@Stateless
public class ReportExtractApi extends BaseCrudApi<ReportExtract, ReportExtractDto> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ReportExtractService reportExtractService;

    @Inject
    private ReportExtractExecutionResultService reportExtractExecutionResultService;

    @Override
    public ReportExtract create(ReportExtractDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getScriptType())) {
            missingParameters.add("scriptType");
        }
        if (StringUtils.isBlank(postData.getFilenameFormat())) {
            missingParameters.add("filenameFormat");
        }
        handleMissingParameters();

        if (reportExtractService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(ReportExtract.class, postData.getCode());
        }
        if (StringUtils.isBlank(postData.getReportExtractResultType())) {
            postData.setReportExtractResultType(ReportExtractResultTypeEnum.CSV);
        }

        ReportExtract reportExtract = convertReportExtractFromDto(postData, null);
        reportExtractService.create(reportExtract);
        return reportExtract;
    }

    @Override
    public ReportExtract update(ReportExtractDto postData) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getScriptType())) {
            missingParameters.add("scriptType");
        }
        if (StringUtils.isBlank(postData.getFilenameFormat())) {
            missingParameters.add("filenameFormat");
        }
        handleMissingParameters();

        ReportExtract reportExtract = reportExtractService.findByCode(postData.getCode());
        if (reportExtract == null) {
            throw new EntityDoesNotExistsException(ReportExtract.class, postData.getCode());
        }
        if (StringUtils.isBlank(postData.getReportExtractResultType())) {
            postData.setReportExtractResultType(ReportExtractResultTypeEnum.CSV);
        }

        convertReportExtractFromDto(postData, reportExtract);
        reportExtract = reportExtractService.update(reportExtract);
        return reportExtract;
    }

	public ReportExtractsResponseDto list(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {
		if (pagingAndFiltering == null) {
			pagingAndFiltering = new PagingAndFiltering();
		}

		PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null,
				pagingAndFiltering, ReportExtractExecutionResult.class);
		Long totalCount = reportExtractService.count(paginationConfig);
		ReportExtractsResponseDto result = new ReportExtractsResponseDto();

		result.setPaging(pagingAndFiltering);
		result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

		if (totalCount > 0) {
			List<ReportExtract> reportExtractExecutionResults = reportExtractService.list(paginationConfig);
			for (ReportExtract e : reportExtractExecutionResults) {
				result.getReportExtracts().add(new ReportExtractDto(e));
			}
		}

		return result;
	}

    @Override
    public ReportExtractDto find(String code) throws EntityDoesNotExistsException {
        ReportExtract reportExtract = reportExtractService.findByCode(code);
        if (reportExtract == null) {
            throw new EntityDoesNotExistsException(ReportExtract.class, code);
        }

        return new ReportExtractDto(reportExtract);
    }

    private ReportExtract convertReportExtractFromDto(ReportExtractDto dto, ReportExtract reportExtractToUpdate) throws EntityDoesNotExistsException {

        ReportExtract reportExtract = reportExtractToUpdate;
        if (reportExtract == null) {
            reportExtract = new ReportExtract();
            if (dto.isDisabled() != null) {
                reportExtract.setDisabled(dto.isDisabled());
            }
        }

        reportExtract.setCategory(dto.getCategory());
        reportExtract.setOutputDir(dto.getOutputDir());
        reportExtract.setCode(dto.getCode());
        reportExtract.setDescription(dto.getDescription());
        reportExtract.setEndDate(dto.getEndDate());
        reportExtract.setFilenameFormat(dto.getFilenameFormat());
        reportExtract.setFileSeparator(dto.getFileSeparator());
        reportExtract.setParams(dto.getParams());
        reportExtract.setStartDate(dto.getStartDate());
        reportExtract.setScriptType(dto.getScriptType());
        reportExtract.setReportExtractResultType(dto.getReportExtractResultType());
        reportExtract.setStyle(dto.getStyle());
        reportExtract.setImagePath(dto.getImagePath());
        reportExtract.setReportExtractResultType(dto.getReportExtractResultType());
        if (dto.getScriptType().equals(ReportExtractScriptTypeEnum.JAVA)) {
            ScriptInstance scriptInstance = scriptInstanceService.findByCode(dto.getScriptInstanceCode());
            if (scriptInstance == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, dto.getScriptInstanceCode());
            }
            reportExtract.setScriptInstance(scriptInstance);
            reportExtract.setSqlQuery(null);
        } else {
            reportExtract.setSqlQuery(dto.getSqlQuery());
            reportExtract.setScriptInstance(null);
        }

        return reportExtract;
    }

    public ReportExtractExecutionResultDto runReportExtract(RunReportExtractDto postData) throws BusinessException, EntityDoesNotExistsException, ReportExtractExecutionException {
        ReportExtract reportExtract = reportExtractService.findByCode(postData.getCode());
        if (reportExtract == null) {
            throw new EntityDoesNotExistsException(ReportExtract.class, postData.getCode());
        }
        
        return new ReportExtractExecutionResultDto(reportExtractService.runReport(reportExtract, postData.getParams(), ReportExtractExecutionOrigin.API));
    }

    public ReportExtractExecutionResultsResponseDto listReportExtractRunHistory(PagingAndFiltering pagingAndFiltering) throws InvalidParameterException {
        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        PaginationConfiguration paginationConfig = toPaginationConfiguration("code", SortOrder.ASCENDING, null, pagingAndFiltering, ReportExtractExecutionResult.class);
        Long totalCount = reportExtractExecutionResultService.count(paginationConfig);
        ReportExtractExecutionResultsResponseDto result = new ReportExtractExecutionResultsResponseDto();

        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

        if (totalCount > 0) {
            List<ReportExtractExecutionResult> reportExtractExecutionResults = reportExtractExecutionResultService.list(paginationConfig);
            for (ReportExtractExecutionResult e : reportExtractExecutionResults) {
                result.getReportExtractExecutionResults().add(new ReportExtractExecutionResultDto(e));
            }
        }

        return result;
    }

    public ReportExtractExecutionResultDto findReportExtractHistory(Long id) {
        ReportExtractExecutionResultDto result = new ReportExtractExecutionResultDto();
        ReportExtractExecutionResult entity = reportExtractExecutionResultService.findById(id);
        if (entity != null) {
            result = new ReportExtractExecutionResultDto(entity);
        }

        return result;
    }

    public ReportExtractExecutionResultsResponseDto listReportExtractRunHistoryByRECode(String code) {
        ReportExtractExecutionResultsResponseDto result = new ReportExtractExecutionResultsResponseDto();

        ReportExtract re = reportExtractService.findByCode(code);
        if (re != null && re.getExecutionResults() != null) {
            result.setReportExtractExecutionResults(re.getExecutionResults().stream().map(p -> new ReportExtractExecutionResultDto(p)).collect(Collectors.toList()));
        }

        return result;
    }
}

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

package org.meveo.api.rest.finance.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.finance.ReportExtractExecutionResultResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractExecutionResultsResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractsResponseDto;
import org.meveo.api.dto.response.finance.RunReportExtractDto;
import org.meveo.api.finance.ReportExtractApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.finance.ReportExtractRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.finance.ReportExtract;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 7.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ReportExtractRsImpl extends BaseRs implements ReportExtractRs {

    @Inject
    private ReportExtractApi reportExtractApi;

    @Override
    public ActionStatus create(ReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ReportExtract reportExtract = reportExtractApi.create(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(reportExtract.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(ReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(ReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            ReportExtract reportExtract = reportExtractApi.createOrUpdate(postData);
            if (StringUtils.isBlank(postData.getCode())) {
                result.setEntityCode(reportExtract.getCode());
            }
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String reportExtractCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.remove(reportExtractCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ReportExtractResponseDto find(String reportExtractCode) {
        ReportExtractResponseDto result = new ReportExtractResponseDto();

        try {
            result.setReportExtract(reportExtractApi.find(reportExtractCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
    
    @Override
	public ReportExtractsResponseDto listGet(String query, String fields, Integer offset, Integer limit, String sortBy,
			SortOrder sortOrder) {
        ReportExtractsResponseDto result = new ReportExtractsResponseDto();

        try {
            result = reportExtractApi.list(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
    
	@Override
	public ReportExtractsResponseDto listPost(PagingAndFiltering pagingAndFiltering) {
		ReportExtractsResponseDto result = new ReportExtractsResponseDto();

        try {
            result = reportExtractApi.list(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
	}

    @Override
    public ReportExtractExecutionResultResponseDto runReport(RunReportExtractDto postData) {
    	ReportExtractExecutionResultResponseDto result = new ReportExtractExecutionResultResponseDto();

        try {
        	result.setReportExtractExecutionResult(reportExtractApi.runReportExtract(postData));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus enable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            reportExtractApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(String code) {
        ActionStatus result = new ActionStatus();

        try {
            reportExtractApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ReportExtractExecutionResultsResponseDto listReportExtractRunHistoryPost(PagingAndFiltering pagingAndFiltering) {
        ReportExtractExecutionResultsResponseDto result = new ReportExtractExecutionResultsResponseDto();

        try {
            result = reportExtractApi.listReportExtractRunHistory(pagingAndFiltering);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ReportExtractExecutionResultsResponseDto listReportExtractRunHistoryGet(String query, String fields, Integer offset, Integer limit, String sortBy, SortOrder sortOrder) {
        ReportExtractExecutionResultsResponseDto result = new ReportExtractExecutionResultsResponseDto();

        try {
            result = reportExtractApi.listReportExtractRunHistory(new PagingAndFiltering(query, fields, offset, limit, sortBy, sortOrder));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ReportExtractExecutionResultResponseDto findReportExtractHistory(Long id) {
        ReportExtractExecutionResultResponseDto result = new ReportExtractExecutionResultResponseDto();

        try {
            result.setReportExtractExecutionResult(reportExtractApi.findReportExtractHistory(id));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ReportExtractExecutionResultsResponseDto findReportExtractHistory(String code) {
        ReportExtractExecutionResultsResponseDto result = new ReportExtractExecutionResultsResponseDto();

        try {
            result = reportExtractApi.listReportExtractRunHistoryByRECode(code);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}
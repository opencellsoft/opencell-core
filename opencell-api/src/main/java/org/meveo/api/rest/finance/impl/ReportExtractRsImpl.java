package org.meveo.api.rest.finance.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.response.finance.ReportExtractResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractsResponseDto;
import org.meveo.api.dto.response.finance.RunReportExtractDto;
import org.meveo.api.finance.ReportExtractApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.finance.ReportExtractRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * @author Edward P. Legaspi
 * @created 7 Feb 2018
 * @lastModifiedVersion 5.0
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ReportExtractRsImpl extends BaseRs implements ReportExtractRs {

    @Inject
    private ReportExtractApi reportExtractApi;

    @Override
    public ActionStatus create(ReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.create(postData);
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
            reportExtractApi.createOrUpdate(postData);
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
    public ReportExtractsResponseDto list() {
        ReportExtractsResponseDto result = new ReportExtractsResponseDto();

        try {
            result.setReportExtracts(reportExtractApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus runReport(RunReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.runReportExtract(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}

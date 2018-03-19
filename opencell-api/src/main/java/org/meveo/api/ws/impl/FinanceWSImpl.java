package org.meveo.api.ws.impl;

import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.finance.ReportExtractDto;
import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.finance.ReportExtractResponseDto;
import org.meveo.api.dto.response.finance.ReportExtractsResponseDto;
import org.meveo.api.dto.response.finance.RunReportExtractDto;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtoResponse;
import org.meveo.api.dto.response.payment.RevenueRecognitionRuleDtosResponse;
import org.meveo.api.finance.ReportExtractApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.RevenueRecognitionRuleApi;
import org.meveo.api.ws.FinanceWs;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@WebService(serviceName = "FinanceWs", endpointInterface = "org.meveo.api.ws.FinanceWs")
@Interceptors({ WsRestApiInterceptor.class })
public class FinanceWSImpl extends BaseWs implements FinanceWs {

    @Inject
    private RevenueRecognitionRuleApi rrrApi;
    
    @Inject
    private ReportExtractApi reportExtractApi;

    @Override
    public ActionStatus createRevenueRecognitionRule(RevenueRecognitionRuleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            rrrApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateRevenueRecognitionRule(RevenueRecognitionRuleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            rrrApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus deleteRevenueRecognitionRule(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            rrrApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public RevenueRecognitionRuleDtosResponse listRevenueRecognitionRules() {
        RevenueRecognitionRuleDtosResponse result = new RevenueRecognitionRuleDtosResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        result.getActionStatus().setMessage("");
        try {
            List<RevenueRecognitionRuleDto> dtos = rrrApi.list();
            result.setRevenueRecognitionRules(dtos);
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public RevenueRecognitionRuleDtoResponse getRevenueRecognitionRule(String code) {
        RevenueRecognitionRuleDtoResponse result = new RevenueRecognitionRuleDtoResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setRevenueRecognitionRuleDto(rrrApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateRevenueRecognitionRule(RevenueRecognitionRuleDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            rrrApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createReportExtract(ReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateReportExtract(ReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateReportExtract(ReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeReportExtract(String reportExtractCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.remove(reportExtractCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ReportExtractsResponseDto listReportExtract() {
        ReportExtractsResponseDto result = new ReportExtractsResponseDto();

        try {
            result.setReportExtracts(reportExtractApi.list());
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ReportExtractResponseDto findReportExtract(String reportExtractCode) {
        ReportExtractResponseDto result = new ReportExtractResponseDto();

        try {
            result.setReportExtract(reportExtractApi.find(reportExtractCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }
    
    @Override
    public ActionStatus runReportExtract(RunReportExtractDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            reportExtractApi.runReportExtract(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
}
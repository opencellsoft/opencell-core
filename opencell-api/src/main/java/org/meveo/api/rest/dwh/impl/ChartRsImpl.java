package org.meveo.api.rest.dwh.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.response.dwh.GetChartResponse;
import org.meveo.api.dwh.ChartApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.dwh.ChartRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ChartRsImpl extends BaseRs implements ChartRs {

    @Inject
    private ChartApi chartApi;

    @Override
    public ActionStatus create(ChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(ChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String chartCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.remove(chartCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetChartResponse find(String chartCode) {
        GetChartResponse result = new GetChartResponse();

        try {
            result.setChartDto(chartApi.find(chartCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdate(ChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createBarChart(BarChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createPieChart(PieChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createLineChart(LineChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateBarChart(BarChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updatePieChart(PieChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateLineChart(LineChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}

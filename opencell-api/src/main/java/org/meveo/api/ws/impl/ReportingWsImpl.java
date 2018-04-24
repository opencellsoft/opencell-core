package org.meveo.api.ws.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.GetMeasurableQuantityResponse;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.response.dwh.GetChartResponse;
import org.meveo.api.dto.response.dwh.GetMeasuredValueResponse;
import org.meveo.api.dwh.ChartApi;
import org.meveo.api.dwh.MeasurableQuantityApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.ReportingWs;
import org.meveo.model.dwh.MeasurementPeriodEnum;

/**
 * @author Edward P. Legaspi
 **/
@WebService(serviceName = "ReportingWs", endpointInterface = "org.meveo.api.ws.ReportingWs")
@Interceptors({ WsRestApiInterceptor.class })
public class ReportingWsImpl extends BaseWs implements ReportingWs {

    @Inject
    private MeasurableQuantityApi measurableQuantityApi;

    @Inject
    private ChartApi chartApi;

    @Override
    public ActionStatus createMeasurableQuantity(MeasurableQuantityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            measurableQuantityApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateMeasurableQuantity(MeasurableQuantityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            measurableQuantityApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateMeasurableQuantity(MeasurableQuantityDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            measurableQuantityApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetMeasurableQuantityResponse findMeasurableQuantity(String code) {
        GetMeasurableQuantityResponse result = new GetMeasurableQuantityResponse();
        try {
            result.setMeasurableQuantityDto(measurableQuantityApi.find(code));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
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
    public ActionStatus createOrUpdateBarChart(BarChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus removeChart(String chartCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.remove(chartCode);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetChartResponse findChart(String chartCode) {
        GetChartResponse result = new GetChartResponse();

        try {
            result.setChartDto(chartApi.find(chartCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
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
    public ActionStatus createOrUpdatePieChart(PieChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.createOrUpdate(postData);
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
    public ActionStatus updateLineChart(LineChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateLineChart(LineChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createChart(ChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus updateChart(ChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.update(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus createOrUpdateChart(ChartDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            chartApi.createOrUpdate(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public GetMeasuredValueResponse findMVByDateAndPeriod(String code, Date fromDate, Date toDate, MeasurementPeriodEnum period, String mqCode) {
        GetMeasuredValueResponse result = new GetMeasuredValueResponse();

        try {
            result.setMeasuredValues(measurableQuantityApi.findMVByDateAndPeriod(code, fromDate, toDate, period, mqCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public ActionStatus enableMeasurableQuantity(String code) {
        ActionStatus result = new ActionStatus();

        try {
            measurableQuantityApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableMeasurableQuantity(String code) {
        ActionStatus result = new ActionStatus();

        try {
            measurableQuantityApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enableChart(String code) {
        ActionStatus result = new ActionStatus();

        try {
            chartApi.enableOrDisable(code, true);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disableChart(String code) {
        ActionStatus result = new ActionStatus();

        try {
            chartApi.enableOrDisable(code, false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
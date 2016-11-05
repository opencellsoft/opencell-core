package org.meveo.api.ws;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.response.dwh.GetChartResponse;
import org.meveo.api.dto.response.dwh.GetMeasuredValueResponse;
import org.meveo.model.dwh.MeasurementPeriodEnum;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface ReportingWs extends IBaseWs {

    @WebMethod
    public ActionStatus createMeasurableQuantity(@WebParam(name = "measurableQuantity") MeasurableQuantityDto postData);

    @WebMethod
    public ActionStatus updateMeasurableQuantity(@WebParam(name = "measurableQuantity") MeasurableQuantityDto postData);

    @WebMethod
    public ActionStatus createOrUpdateMeasurableQuantity(@WebParam(name = "measurableQuantity") MeasurableQuantityDto postData);

    @WebMethod
    public ActionStatus createChart(@WebParam(name = "chart") ChartDto postData);

    @WebMethod
    public ActionStatus updateChart(@WebParam(name = "chart") ChartDto postData);

    @WebMethod
    public ActionStatus createOrUpdateChart(@WebParam(name = "chart") ChartDto postData);

    @WebMethod
    public ActionStatus createBarChart(@WebParam(name = "barChart") BarChartDto postData);

    @WebMethod
    public ActionStatus updateBarChart(@WebParam(name = "barChart") BarChartDto postData);

    @WebMethod
    public ActionStatus createOrUpdateBarChart(@WebParam(name = "barChart") BarChartDto postData);

    @WebMethod
    public ActionStatus createPieChart(@WebParam(name = "pieChart") PieChartDto postData);

    @WebMethod
    public ActionStatus updatePieChart(@WebParam(name = "pieChart") PieChartDto postData);

    @WebMethod
    public ActionStatus createOrUpdatePieChart(@WebParam(name = "pieChart") PieChartDto postData);

    @WebMethod
    public ActionStatus createLineChart(@WebParam(name = "lineChart") LineChartDto postData);

    @WebMethod
    public ActionStatus updateLineChart(@WebParam(name = "lineChart") LineChartDto postData);

    @WebMethod
    public ActionStatus createOrUpdateLineChart(@WebParam(name = "lineChart") LineChartDto postData);

    @WebMethod
    public ActionStatus removeChart(@WebParam(name = "chartCode") String chartCode);

    @WebMethod
    public GetChartResponse findChart(@WebParam(name = "chartCode") String chartCode);
    
	@WebMethod
	public GetMeasuredValueResponse findMVByDateAndPeriod(@WebParam(name = "code") String code, @WebParam(name = "fromDate") Date fromDate, @WebParam(name = "toDate") Date toDate,
			@WebParam(name = "period") MeasurementPeriodEnum period, @WebParam(name = "mqCode") String mqCode);

}

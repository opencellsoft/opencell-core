package org.meveo.api.dwh;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveocrm.model.dwh.BarChart;
import org.meveocrm.model.dwh.Chart;
import org.meveocrm.model.dwh.LineChart;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.PieChart;
import org.meveocrm.services.dwh.ChartService;
import org.meveocrm.services.dwh.MeasurableQuantityService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ChartApi extends BaseApi {

    @Inject
    private ChartService<PieChart> pieChartService;

    @Inject
    private ChartService<LineChart> lineChartService;

    @Inject
    private ChartService<BarChart> barChartService;

    @Inject
    private MeasurableQuantityService measurableQuantityService;

    @Inject
    private MeasurableQuantityApi measurableQuantityApi;

    @Inject
    private ChartService<Chart> chartService;

    public void create(ChartDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getMeasurableQuantity() == null || StringUtils.isBlank(postData.getMeasurableQuantity().getCode())) {
            missingParameters.add("measurableQuantity.code");
        }

        handleMissingParameters();

        Chart chart = chartService.findByCode(postData.getCode(), currentUser.getProvider());
        if (chart != null) {
            throw new EntityAlreadyExistsException(Chart.class, postData.getCode());
        }

        if (postData instanceof PieChartDto) {
            PieChart pieChart = fromDTO((PieChartDto) postData, currentUser, null);
            pieChartService.create(pieChart, currentUser);

        } else if (postData instanceof LineChartDto) {
            LineChart lineChart = fromDTO((LineChartDto) postData, currentUser, null);
            lineChartService.create(lineChart, currentUser);

        } else if (postData instanceof BarChartDto) {
            BarChart barChart = fromDTO((BarChartDto) postData, currentUser, null);
            barChartService.create(barChart, currentUser);
        
        } else {
            throw new InvalidParameterException();
        }
    }

    public void update(ChartDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (postData.getMeasurableQuantity() == null || StringUtils.isBlank(postData.getMeasurableQuantity().getCode())) {
            missingParameters.add("measurableQuantity.code");
        }

        handleMissingParameters();

        Chart chart = chartService.findByCode(postData.getCode(), currentUser.getProvider());
        if (chart == null) {
            throw new EntityDoesNotExistsException(Chart.class, postData.getCode());
        }

        if (chart instanceof PieChart) {
            PieChart pieChart = fromDTO((PieChartDto) postData, currentUser, (PieChart) chart);
            pieChartService.update(pieChart, currentUser);

        } else if (chart instanceof LineChart) {
            LineChart lineChart = fromDTO((LineChartDto) postData, currentUser, (LineChart) chart);
            lineChartService.update(lineChart, currentUser);

        } else if (chart instanceof BarChart) {
            BarChart barChart = fromDTO((BarChartDto) postData, currentUser, (BarChart) chart);
            barChartService.update(barChart, currentUser);
            
        } else {
            throw new InvalidParameterException();
        }
    }

    public ChartDto find(String chartCode, User currentUser) throws MeveoApiException {

        if (StringUtils.isBlank(chartCode)) {
            missingParameters.add("chartCode");
            handleMissingParameters();
        }

        ChartDto result = null;

        Chart chart = chartService.findByCode(chartCode, currentUser.getProvider());

        if (chart == null) {
            throw new EntityDoesNotExistsException(Chart.class, chartCode);
        }

        if (chart instanceof PieChart) {
            result = new PieChartDto((PieChart) chart);
        } else if (chart instanceof LineChart) {
            result = new LineChartDto((LineChart) chart);
        } else if (chart instanceof BarChart) {
            result = new BarChartDto((BarChart) chart);
        }

        return result;
    }

    public void remove(String chartCode, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(chartCode)) {
            missingParameters.add("chartCode");
            handleMissingParameters();
        }

        Chart chart = chartService.findByCode(chartCode, currentUser.getProvider());
        if (chart == null) {
            throw new EntityDoesNotExistsException(Chart.class, chartCode);
        }

        chartService.remove(chart, currentUser);
    }

    public void createOrUpdate(ChartDto postData, User currentUser) throws MeveoApiException, BusinessException {
        Chart chart = chartService.findByCode(postData.getCode(), currentUser.getProvider());
        if (chart == null) {
            // create
            create(postData, currentUser);
        } else {
            // update
            update(postData, currentUser);
        }
    }

    public List<ChartDto> list(String chartCode, User currentUser) {

        List<Chart> charts = null;
        if (StringUtils.isBlank(chartCode)) {
            charts = chartService.list(currentUser.getProvider());
        } else {
            charts = chartService.findByCodeLike(chartCode, currentUser.getProvider());
        }

        List<ChartDto> chartDtos = new ArrayList<ChartDto>();

        for (Chart chart : charts) {
            if (chart instanceof PieChart) {
                chartDtos.add(new PieChartDto((PieChart) chart));
            } else if (chart instanceof LineChart) {
                chartDtos.add(new LineChartDto((LineChart) chart));
            } else if (chart instanceof BarChart) {
                chartDtos.add(new BarChartDto((BarChart) chart));
            }
        }

        return chartDtos;
    }

    private PieChart fromDTO(PieChartDto dto, User currentUser, PieChart chartToUpdate) throws MeveoApiException, BusinessException {

        PieChart chart = new PieChart();
        if (chartToUpdate != null) {
            chart = chartToUpdate;
        }
        populateChartFromDto(dto, currentUser, chart);

        chart.setFilled(dto.isFilled());
        chart.setLegendPosition(dto.getLegendPosition());
        chart.setSeriesColors(dto.getSeriesColors());
        chart.setDiameter(dto.getDiameter());
        chart.setSliceMargin(dto.getSliceMargin());
        chart.setShadow(dto.isShadow());
        chart.setShowDataLabels(dto.isShowDataLabels());
        chart.setLegendCols(dto.getLegendCols());
        chart.setLegendRows(dto.getLegendRows());

        return chart;
    }

    private LineChart fromDTO(LineChartDto dto, User currentUser, LineChart chartToUpdate) throws MeveoApiException, BusinessException {

        LineChart chart = new LineChart();
        if (chartToUpdate != null) {
            chart = chartToUpdate;
        }
        populateChartFromDto(dto, currentUser, chart);

        chart.setFilled(dto.isFilled());
        chart.setLegendPosition(dto.getLegendPosition());
        chart.setSeriesColors(dto.getSeriesColors());
        chart.setShadow(dto.isShadow());
        chart.setMinX(dto.getMinX());
        chart.setMaxX(dto.getMaxX());
        chart.setMinY(dto.getMinY());
        chart.setMaxY(dto.getMaxY());
        chart.setBreakOnNull(dto.isBreakOnNull());
        chart.setXaxisLabel(dto.getXaxisLabel());
        chart.setYaxisLabel(dto.getYaxisLabel());
        chart.setXaxisAngle(dto.getXaxisAngle());
        chart.setYaxisAngle(dto.getYaxisAngle());
        chart.setStacked(dto.isStacked());
        chart.setZoom(dto.isZoom());
        chart.setAnimate(dto.isAnimate());
        chart.setShowDataTip(dto.isShowDataTip());
        chart.setDatatipFormat(dto.getDatatipFormat());
        chart.setLegendCols(dto.getLegendCols());
        chart.setLegendRows(dto.getLegendRows());

        return chart;
    }

    private BarChart fromDTO(BarChartDto dto, User currentUser, BarChart chartToUpdate) throws MeveoApiException, BusinessException {

        BarChart chart = new BarChart();
        if (chartToUpdate != null) {
            chart = chartToUpdate;
        }
        populateChartFromDto(dto, currentUser, chart);

        chart.setLegendPosition(dto.getLegendPosition());
        chart.setBarPadding(dto.getBarPadding());
        chart.setBarMargin(dto.getBarMargin());
        chart.setOrientation(dto.getOrientation());
        chart.setStacked(dto.isStacked());
        chart.setMin(dto.getMin());
        chart.setMax(dto.getMax());
        chart.setBreakOnNull(dto.isBreakOnNull());
        chart.setXaxisLabel(dto.getXaxisLabel());
        chart.setYaxisLabel(dto.getYaxisLabel());
        chart.setXaxisAngle(dto.getXaxisAngle());
        chart.setYaxisAngle(dto.getYaxisAngle());
        chart.setLegendCols(dto.getLegendCols());
        chart.setLegendRows(dto.getLegendRows());
        chart.setZoom(dto.isZoom());
        chart.setAnimate(dto.isAnimate());
        chart.setShowDataTip(dto.isShowDataTip());
        chart.setShowDataTip(dto.isShowDataTip());

        return chart;
    }

    private void populateChartFromDto(ChartDto dto, User currentUser, Chart chartToUpdate) throws MeveoApiException, BusinessException {

        chartToUpdate.setCode(dto.getCode());
        chartToUpdate.setDescription(dto.getDescription());
        // Should create it or update measurableQuantity only it has full information only
        if (!dto.getMeasurableQuantity().isCodeOnly()) {
            measurableQuantityApi.createOrUpdate(dto.getMeasurableQuantity(), currentUser);
        }
        MeasurableQuantity measurableQuantity = measurableQuantityService.findByCode(dto.getMeasurableQuantity().getCode(), currentUser.getProvider());
        if (measurableQuantity == null) {
            throw new EntityDoesNotExistsException(MeasurableQuantity.class, dto.getMeasurableQuantity().getCode());
        }
        chartToUpdate.setMeasurableQuantity(measurableQuantity);
        chartToUpdate.setWidth(dto.getWidth());
        chartToUpdate.setHeight(dto.getHeight());
        chartToUpdate.setStyle(dto.getStyle());
        chartToUpdate.setStyleClass(dto.getStyleClass());
        chartToUpdate.setExtender(dto.getExtender());
        chartToUpdate.setVisible(dto.getVisible());
    }
}
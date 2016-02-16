package org.meveo.api.dwh;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.dwh.ChartDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveocrm.model.dwh.LegendPositionEnum;
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
	private ChartService<PieChart> lineChartService;

	@Inject
	private ChartService<PieChart> barChartService;

	@Inject
	private MeasurableQuantityService measurableQuantityService;

	public void create(ChartDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getChartType()) && !StringUtils.isBlank(postData.getCode())
				&& !StringUtils.isBlank(postData.getMeasurableQuantityCode())) {
			if (postData.getChartType().equals("PIE")) {
				createPieChart((PieChartDto) postData, currentUser);
			} else if (postData.getChartType().equals("LINE")) {

			} else if (postData.getChartType().equals("BAR")) {

			}
		} else {
			if (StringUtils.isBlank(postData.getChartType())) {
				missingParameters.add("chartType");
			}
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getMeasurableQuantityCode())) {
				missingParameters.add("measurableQuantityCode");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	private void createPieChart(PieChartDto postData, User currentUser) throws MeveoApiException {
		PieChart pieChart = new PieChart();
		pieChart.setCode(postData.getCode());
		pieChart.setDescription(postData.getDescription());
		MeasurableQuantity measurableQuantity = measurableQuantityService.findByCode(
				postData.getMeasurableQuantityCode(), currentUser.getProvider());
		if (measurableQuantity == null) {
			throw new EntityDoesNotExistsException(MeasurableQuantity.class, postData.getMeasurableQuantityCode());
		}
		pieChart.setMeasurableQuantity(measurableQuantity);
		pieChart.setWidth(postData.getWidth());
		pieChart.setHeight(postData.getHeight());
		pieChart.setStyle(postData.getStyle());
		pieChart.setStyleClass(postData.getStyleClass());
		pieChart.setExtender(postData.getExtender());
		pieChart.setVisible(postData.getVisible());
		pieChart.setFilled(postData.isFilled());
		if (!StringUtils.isBlank(postData.getLegendPosition())) {
			try {
				pieChart.setLegendPosition(LegendPositionEnum.valueOf(postData.getLegendPosition()));
			} catch (IllegalArgumentException e) {
				log.warn("INVALID ENUM for {} with value={}", LegendPositionEnum.class, postData.getLegendPosition());
			}
		}
		pieChart.setSeriesColors(postData.getSeriesColors());
		pieChart.setDiameter(postData.getDiameter());
		pieChart.setSliceMargin(postData.getSliceMargin());
		pieChart.setShadow(postData.isShadow());
		pieChart.setShowDataLabels(postData.isShowDataLabels());
		pieChart.setLegendCols(postData.getLegendCols());
		pieChart.setLegendRows(postData.getLegendRows());

		pieChartService.create(pieChart, currentUser, currentUser.getProvider());
	}

	public ChartDto find(String itemCode, User currentUser) {
		ChartDto result = new ChartDto();

		// TODO Manu: populate dto

		return result;
	}

	// TODO Manu - implement RUD + List

}

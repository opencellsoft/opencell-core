package org.meveocrm.admin.action.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.meveo.model.dwh.Chart;
import org.primefaces.model.chart.ChartModel;

public class ChartEntityModel <C extends Chart,M extends ChartModel>{

	protected C chart;
	protected M model;
	
	private Date minDate;

	private Date maxDate;

	private String dimension1;
	private String dimension2;
	private String dimension3;
	private String dimension4;


	private List<String> dimension1List = new ArrayList<String>();
	private List<String> dimension2List = new ArrayList<String>();
	private List<String> dimension3List = new ArrayList<String>();
	private List<String> dimension4List = new ArrayList<String>();

	public ChartEntityModel() {
	}

	public C getChart() {
		return chart;
	}

	public void setChart(C chart) {
		this.chart = chart;
		setRange(new Date());
	}

	public M getModel() {
		return model;
	}

	public void setModel(M model) {
		this.model = model;
	}

	private void setRange(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);
		minDate = cal.getTime();

		switch(chart.getMeasurableQuantity().getMeasurementPeriod()){
		case DAILY:
			cal.add(Calendar.MONTH, 1);
			break;
		case MONTHLY:
		case WEEKLY:
		case YEARLY:
			cal.add(Calendar.YEAR, 1);
			break;
		default:
			break;
		}
		cal.add(Calendar.DATE, -1);
		maxDate = cal.getTime();
	}
	
	public Date getMinDate() {
		return minDate;
	}

	public void setMinDate(Date date) {
		setRange(date);
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date date) {
		setRange(date);
	}

	public String getDimension1() {
		return dimension1;
	}

	public void setDimension1(String dimension1) {
		this.dimension1 = dimension1;
	}

	public String getDimension2() {
		return dimension2;
	}

	public void setDimension2(String dimension2) {
		this.dimension2 = dimension2;
	}

	public String getDimension3() {
		return dimension3;
	}

	public void setDimension3(String dimension3) {
		this.dimension3 = dimension3;
	}

	public String getDimension4() {
		return dimension4;
	}

	public void setDimension4(String dimension4) {
		this.dimension4 = dimension4;
	}
	public List<String> getDimension1List() {
		return dimension1List;
	}

	public void setDimension1List(List<String> dimension1List) {
		this.dimension1List = dimension1List;
	}

	public List<String> getDimension2List() {
		return dimension2List;
	}

	public void setDimension2List(List<String> dimension2List) {
		this.dimension2List = dimension2List;
	}

	public List<String> getDimension3List() {
		return dimension3List;
	}

	public void setDimension3List(List<String> dimension3List) {
		this.dimension3List = dimension3List;
	}

	public List<String> getDimension4List() {
		return dimension4List;
	}

	public void setDimension4List(List<String> dimension4List) {
		this.dimension4List = dimension4List;
	}

}

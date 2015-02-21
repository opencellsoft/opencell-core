/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveocrm.admin.action.reporting;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.service.base.local.IPersistenceService;
import org.meveocrm.model.dwh.MeasurableQuantity;
import org.meveocrm.model.dwh.MeasuredValue;
import org.meveocrm.model.dwh.MeasurementPeriodEnum;
import org.meveocrm.services.dwh.MeasurableQuantityService;
import org.meveocrm.services.dwh.MeasuredValueService;
import org.omnifaces.cdi.ViewScoped;
import org.omnifaces.util.Messages;
import org.primefaces.event.CellEditEvent;

@Named
@ViewScoped
public class MeasurementBean extends BaseBean<MeasuredValue> {

	private static final long serialVersionUID = 883901110961710869L;

	@Inject
	MeasuredValueService measuredValueService;

	private SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

	ParamBean paramBean = ParamBean.getInstance();

	private List<MeasuredValue> mainMVModel;

	@Inject
	private MeasurableQuantityService mqService;

	private MeasuredValue selectedMV;
	private String measuredPeriod;
	private MeasurementPeriodEnum period;
	private String measurableQuantityCode;
	private MeasurableQuantity measurableQuantity;
	private Date selectedDate;
	private String dimension1Filter;
	private String dimension2Filter;
	private String dimension3Filter;
	private String dimension4Filter;

	public MeasurementBean() {
		super(MeasuredValue.class);
	}

	public String getMeasuredPeriod() {
		return measuredPeriod;
	}

	public void setMeasuredPeriod(String measuredPeriod) {
		this.measuredPeriod = measuredPeriod;
		period = MeasurementPeriodEnum.valueOf(measuredPeriod);
	}

	public String getMeasurableQuantityCode() {
		return measurableQuantityCode;
	}

	public void setMeasurableQuantityCode(String measurableQuantityCode) {
		if (measurableQuantityCode != null
				&& !measurableQuantityCode.equals(this.measurableQuantityCode)) {
			this.measurableQuantityCode = measurableQuantityCode;
			this.dimension1Filter = null;
			this.dimension2Filter = null;
			this.dimension3Filter = null;
			this.dimension4Filter = null;
			this.mainMVModel = null;
			selectMeasurableQuantity();
		}
		this.measurableQuantityCode = measurableQuantityCode;
	}

	public MeasurableQuantity getMeasurableQuantity() {
		return measurableQuantity;
	}

	public Date getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
		this.mainMVModel = null;
	}

	public MeasurementPeriodEnum[] getMeasuredPeriodEnums() {
		return MeasurementPeriodEnum.values();
	}

	public MeasuredValue getSelectedMV() {
		return selectedMV;
	}

	public void setSelectedMV(MeasuredValue selectedMV) {
		this.selectedMV = selectedMV;
	}

	public String getDimension1Filter() {
		return dimension1Filter;
	}

	public void setDimension1Filter(String dimension1Filter) {
		this.dimension1Filter = dimension1Filter;
		this.dimension2Filter = null;
		this.dimension3Filter = null;
		this.dimension4Filter = null;
		selectMeasurableQuantity();
	}

	public String getDimension2Filter() {
		return dimension2Filter;
	}

	public void setDimension2Filter(String dimension2Filter) {
		this.dimension2Filter = dimension2Filter;
		this.dimension3Filter = null;
		this.dimension4Filter = null;
		selectMeasurableQuantity();
	}

	public String getDimension3Filter() {
		return dimension3Filter;
	}

	public void setDimension3Filter(String dimension3Filter) {
		this.dimension3Filter = dimension3Filter;
		this.dimension4Filter = null;
		selectMeasurableQuantity();
	}

	public String getDimension4Filter() {
		return dimension4Filter;
	}

	public void setDimension4Filter(String dimension4Filter) {
		this.dimension4Filter = dimension4Filter;
		selectMeasurableQuantity();
	}

	public List<MeasuredValue> getMainMVModel() {
		log.info("mainMVModel:"
				+ (mainMVModel == null ? "null" : mainMVModel.size()));
		return mainMVModel;
	}

	public List<String> getMeasurableQuantityCodes() {
		List<MeasurableQuantity> mqlist = mqService.list();
		List<String> codes = new ArrayList<String>();
		for (MeasurableQuantity mq : mqlist) {
			if (!codes.contains(mq.getCode())) {
				codes.add(mq.getCode());
			}
		}
		Collections.sort(codes);
		return codes;
	}

	private void selectMeasurableQuantity() {
		List<MeasurableQuantity> mqlist = null;
		if (StringUtils.isBlank(measurableQuantityCode)) {
			measurableQuantity = null;
		} else {
			mqlist = mqService.listByCodeAndDim(measurableQuantityCode,
					dimension1Filter, dimension2Filter, dimension3Filter,
					dimension4Filter);
			if (mqlist.size() == 1) {
				measurableQuantity = mqlist.get(0);
				dimension1Filter = measurableQuantity.getDimension1();
				dimension2Filter = measurableQuantity.getDimension2();
				dimension3Filter = measurableQuantity.getDimension3();
				dimension4Filter = measurableQuantity.getDimension4();
			} else {
				measurableQuantity = null;
			}
		}
	}

	@SuppressWarnings("unused")
	public List<String> getDimension(Integer i) throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {

		List<String> dimensionList = new ArrayList<String>();
		Field field = null;
		field = MeasurableQuantity.class.getDeclaredField("dimension"
				+ i.toString());
		field.setAccessible(true);

		List<MeasurableQuantity> mqList = sortMQList(mqService.list());

		if (field != null) {
			for (MeasurableQuantity mq : mqList) {
				if (i == 1) {
					if (!dimensionList.contains(mq.getDimension1())
							&& mq.getDimension1() != null) {
						if (dimension1Filter != null) {
							if (!dimension1Filter.isEmpty()) {
								if (mq.getDimension1().equals(dimension1Filter)) {
									dimensionList.add(mq.getDimension1());
								}
							} else {
								dimensionList.add(mq.getDimension1());
							}

						}

					}
				} else if (i > 1) {
					if (dimension1Filter != null) {
						String fieldValue = (String) field.get(mq);
						if (!dimension1Filter.isEmpty()) {
							if (mq.getDimension1().equals(dimension1Filter)) {
								if (fieldValue != null) {
									dimensionList.add(fieldValue);
								} else {
									if (dimensionList.size() > 0) {
										dimensionList.add(fieldValue);
									}
								}
							}
						} else {

							if (fieldValue != null) {
								dimensionList.add(fieldValue);
							} else {
								if (dimensionList.size() > 0) {
									dimensionList.add(fieldValue);
								}
							}

						}
					}
				}
			}
			return dimensionList;
		}

		return null;
	}

	public List<MeasurableQuantity> sortMQList(List<MeasurableQuantity> mqList) {
		List<MeasurableQuantity> sortedMQList = mqList;
		Collections.sort(sortedMQList, new Comparator<MeasurableQuantity>() {
			public int compare(MeasurableQuantity mq1, MeasurableQuantity mq2) {

				if (mq1.getDimension1() == null && mq2.getDimension1() != null) {
					return 1;
				} else if (mq1.getDimension1() != null
						&& mq2.getDimension1() == null) {
					return -1;
				} else if (mq1.getDimension1() == null
						&& mq2.getDimension1() == null) {
					return 0;
				} else if (mq1.getDimension1() != null
						&& mq2.getDimension1() != null) {
					return mq1.getDimension1().compareTo(mq2.getDimension1());
				}

				return 0;
			}
		});

		return sortedMQList;
	}

	public Integer getColspan(String dimensionName, Integer dimensionNum)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {

		List<MeasurableQuantity> mqList = new ArrayList<MeasurableQuantity>();

		for (int i = 4; i > dimensionNum; i--) {
			Field field = null;
			field = MeasurableQuantity.class.getDeclaredField("dimension"
					+ String.valueOf(i));
			field.setAccessible(true);
			Field headField = null;
			headField = MeasurableQuantity.class.getDeclaredField("dimension"
					+ String.valueOf(dimensionNum));
			headField.setAccessible(true);
			for (MeasurableQuantity mq : mqService.list()) {
				if (field.get(mq) != null) {
					if (headField.get(mq).equals(dimensionName)) {
						mqList.add(mq);
					}
				}
			}
			if (mqList.size() > 0) {
				return mqList.size();
			}
		}

		return 0;
	}

	public Boolean hasDimension(Integer dimensionNum)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {

		Map<Integer, List<String>> mqMap = new TreeMap<Integer, List<String>>();

		for (int j = 1; j < 5; j++) {
			mqMap.put(j, getDimension(j));
		}
		if (mqMap.get(dimensionNum).size() > 0) {
			return true;
		}

		return false;
	}

	public void generateMVModel() throws ParseException {
		if (measurableQuantity != null && selectedDate != null) {
			mainMVModel = new ArrayList<MeasuredValue>();
			Calendar cal = Calendar.getInstance();
			cal.setTime(selectedDate);

			int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			for (int i = 0; i < daysInMonth; i++) {
				String dateCol = StringUtils.leftPad(
						StringUtils.leftPad(
								String.valueOf(String.valueOf(i + 1)), 2, '0')
								+ "/"
								+ String.valueOf(cal.get(Calendar.MONTH) + 1),
						2, '0')
						+ "/" + String.valueOf(cal.get(Calendar.YEAR));
				Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
						.parse(dateCol);
				MeasuredValue mv = measuredValueService.getByDate(date, period,
						measurableQuantity);
				if (mv == null) {
					mv = new MeasuredValue();
					mv.setMeasurableQuantity(measurableQuantity);
					mv.setDate(date);
					mv.setMeasurementPeriod(period);
				}
				mainMVModel.add(mv);
			}
		}
	}

	public String saveMV() throws BusinessException, ParseException {
		if (selectedMV.getValue() != null) {
			if (selectedMV.isTransient() && selectedMV != null) {
				getPersistenceService().create(selectedMV);
				Messages.addGlobalInfo("save.successful", new Object[] {});
			} else if (!selectedMV.isTransient() && selectedMV != null) {
				getPersistenceService().update(selectedMV);
				Messages.addGlobalInfo("update.successful", new Object[] {});
			}
		}

		return null;
	}

	public List<String> getDimensionList(int dim) {
		List<String> dimList = new ArrayList<String>();
		if (measurableQuantityCode != null) {
			List<MeasurableQuantity> mqlist = mqService
					.listByCode(measurableQuantityCode);
			for (MeasurableQuantity mq : mqlist) {
				String dimension = "";
				switch (dim) {
				case 1:
					dimension = mq.getDimension1();
					break;
				case 2:
					dimension = mq.getDimension2();
					break;
				case 3:
					dimension = mq.getDimension3();
					break;
				case 4:
					dimension = mq.getDimension4();
					break;
				}
				if (!dimList.contains(dimension)) {
					dimList.add(dimension);
				}
			}
		}
		return dimList;
	}

	public Boolean hasSubDimension(String dimensionName, Integer dimensionNum)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {
		List<MeasurableQuantity> subdimensionList = new ArrayList<MeasurableQuantity>();

		Field field = null;
		field = MeasurableQuantity.class.getDeclaredField("dimension"
				+ String.valueOf(dimensionNum + 1));
		field.setAccessible(true);

		Field headField = null;
		headField = MeasurableQuantity.class.getDeclaredField("dimension"
				+ String.valueOf(dimensionNum));
		headField.setAccessible(true);

		for (MeasurableQuantity mq : mqService.list()) {
			if (field.get(mq) != null) {
				if (headField.get(mq).equals(dimensionName)) {
					subdimensionList.add(mq);
				}
			}
		}
		if (subdimensionList.size() > 0) {
			return true;
		}
		return false;
	}

	public void onCellEdit(CellEditEvent event) throws BusinessException,
			ParseException {

		/*
		 * String columnIndex = event .getColumn() .getColumnKey() .replace(
		 * "mqTableForm:mqTable:" + String.valueOf(event.getRowIndex()) +
		 * ":col", "");
		 */
		selectedMV = mainMVModel.get(event.getRowIndex());

		saveMV();

	}

	public HSSFCellStyle getCellStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		return style;
	}

	public void generateExcelReport(Object document)
			throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		String messageBundleName = facesContext.getApplication()
				.getMessageBundle();
		ResourceBundle messageBundle = ResourceBundle
				.getBundle(messageBundleName);

		HSSFWorkbook workbook = (HSSFWorkbook) document;
		HSSFSheet sheet = workbook.getSheetAt(0);

		Row row = sheet.createRow(1);
		Cell dateCell = row.createCell(0);
		dateCell.setCellValue("Date");
		dateCell.setCellStyle(getCellStyle(workbook));

		int j = 1;
		while (hasDimension(j) && j < 4) {
			if (j > 1 && getDimension(j).size() > 0) {
				row = sheet.createRow(j);
			}
			log.info(getDimension(j).toString());
			Integer dimCounter = 1;
			Integer colspan = 0;
			for (String dimension : getDimension(j)) {
				Integer colFrom = dimCounter + colspan;
				Cell cell = row.createCell(dimCounter + colspan);

				cell.setCellStyle(getCellStyle(workbook));
				if (hasSubDimension(dimension, j)) {
					colspan += getColspan(dimension, j);
					Integer colTo = colspan;
					sheet.addMergedRegion(new CellRangeAddress(j, j, colFrom,
							colTo));
					for (int i = dimCounter + 1; i <= colspan; i++) {
						Cell blankCell = row.createCell(i);
						blankCell.setCellStyle(getCellStyle(workbook));
					}

				} else {
					dimCounter++;
				}

				if (dimension1Filter != null && !dimension1Filter.isEmpty()) {
					if (dimension1Filter.equals(dimension) && j <= 1) {
						cell.setCellValue(dimension);
					} else if (j > 1) {
						cell.setCellValue(dimension);
					}
				} else {
					cell.setCellValue(dimension);
				}
			}
			j++;

		}

		// for (List<MeasuredValue> mv : mainMVModel) {
		row = sheet.createRow(j);
		int mvCounter = 0;
		for (MeasuredValue subMV : mainMVModel) {
			Cell cell = row.createCell(mvCounter);
			if (mvCounter == 0) {
				cell.setCellValue(sdf1.format(subMV.getDate()));
			} else {
				if (subMV.getValue() != null
						&& subMV.getMeasurementPeriod() == period) {
					cell.setCellValue(subMV.getValue()==null?0:subMV.getValue().doubleValue());
				}
			}

			cell.setCellStyle(getCellStyle(workbook));

			sheet.autoSizeColumn(mvCounter, true);
			mvCounter++;
		}
		j++;

		// }

		HSSFRow reportTitleRow = sheet.getRow(0);
		HSSFCell reportTitleCell = reportTitleRow.createCell(0);

		reportTitleCell.setCellValue(messageBundle
				.getString("menu.measuredValues")
				+ " "
				+ new SimpleDateFormat("MMMM").format(selectedDate)
				+ ","
				+ new SimpleDateFormat("yyyy").format(selectedDate)
				+ " "
				+ messageBundle
						.getString("entity.measuredvalue.measurementPeriod")
				+ " : "
				+ messageBundle.getString("enum.measurementperiod."
						+ measuredPeriod));

		sheet.autoSizeColumn(0);
	}

	public List<String> getMeasurePeriods() {
		List<String> periods = new ArrayList<String>();
		for (MeasurementPeriodEnum period1 : MeasurementPeriodEnum.values()) {
			periods.add(period1.name());
		}
		return periods;
	}

	@Override
	protected IPersistenceService<MeasuredValue> getPersistenceService() {
		return measuredValueService;
	}

	@Override
	protected String getListViewName() {
		return "measuredValueDetail";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

}

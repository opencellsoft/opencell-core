package org.meveocrm.admin.action.reporting;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.enterprise.context.ConversationScoped;
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
import org.omnifaces.util.Messages;
import org.primefaces.event.CellEditEvent;

@Named
@ConversationScoped
public class MeasurementBean extends
		BaseBean<MeasuredValue> {

	private static final long serialVersionUID = 883901110961710869L;

	@Inject
	MeasuredValueService measuredValueService;
	
	private SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");

	ParamBean paramBean = ParamBean.getInstance();

	private List<List<MeasuredValue>> mainMVModel;

	@Inject
	private MeasurableQuantityService mqService;

	private MeasuredValue selectedMV;
	private String measuredPeriod;
	private MeasurementPeriodEnum period;
	private Date selectedDate;
	private String dimension1Filter;

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

	public Date getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
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
	}

	public List<List<MeasuredValue>> getMainMVModel() {
		return mainMVModel;
	}

	public void setMainMVModel(List<List<MeasuredValue>> mainMVModel) {
		this.mainMVModel = mainMVModel;
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
		if (selectedDate != null) {
			mainMVModel = new ArrayList<List<MeasuredValue>>();
			Calendar cal = Calendar.getInstance();
			cal.setTime(selectedDate);

			List<MeasurableQuantity> mqList = new ArrayList<MeasurableQuantity>();

			if (dimension1Filter != null) {
				if (!dimension1Filter.isEmpty()) {
					for (MeasurableQuantity mq : mqService.list()) {
						if (mq.getDimension1().equals(dimension1Filter)) {
							mqList.add(mq);
						}
					}
					mqList = sortMQList(mqList);
				} else {
					mqList = sortMQList(mqService.list());
				}
			}
			int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			for (int i = 0; i < daysInMonth; i++) {
				String dateCol = StringUtils.leftPad(
						StringUtils.leftPad(
								String.valueOf(String.valueOf(i + 1)), 2, '0')
								+ "/"
								+ String.valueOf(cal.get(Calendar.MONTH) + 1),
						2, '0')
						+ "/" + String.valueOf(cal.get(Calendar.YEAR));

				MeasuredValue mv = new MeasuredValue();

				Date mvDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
						.parse(dateCol);
				mv.setDate(mvDate);
				List<MeasuredValue> dateList = new ArrayList<MeasuredValue>();

				dateList.add(mv);

				for (int j = 0; j < mqList.size(); j++) {
					MeasuredValue newMV = new MeasuredValue();
					newMV.setDate(mvDate);
					newMV.setMeasurableQuantity(mqList.get(j));
					newMV.setMeasurementPeriod(period);
					dateList.add(newMV);

				}
				mainMVModel.add(dateList);
			}

			for (MeasuredValue mv : getPersistenceService().list()) {
				if (dimension1Filter != null) {
					if (!dimension1Filter.isEmpty()) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(mv.getDate());
						if (cal.get(Calendar.MONTH) == calendar
								.get(Calendar.MONTH)
								&& cal.get(Calendar.YEAR) == calendar
										.get(Calendar.YEAR)
								&& mv.getMeasurementPeriod() == period) {
							mainMVModel.get(
									calendar.get(Calendar.DAY_OF_MONTH) - 1)
									.set(mqList.indexOf(mv
											.getMeasurableQuantity()) + 1, mv);

						}
					} else {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(mv.getDate());
						if (cal.get(Calendar.MONTH) == calendar
								.get(Calendar.MONTH)
								&& cal.get(Calendar.YEAR) == calendar
										.get(Calendar.YEAR)
								&& mv.getMeasurementPeriod() == period) {
							mainMVModel.get(
									calendar.get(Calendar.DAY_OF_MONTH) - 1)
									.set(mqList.indexOf(mv
											.getMeasurableQuantity()) + 1, mv);

						}
					}
				}
			}
		}

	}

	public String saveMV() throws BusinessException, ParseException {
		if (selectedMV.getValue() != null) {
			if (selectedMV.isTransient() && selectedMV != null) {
				getPersistenceService().create(selectedMV);
				Messages.addGlobalInfo("save.successful",
						new Object[] {});
			} else if (!selectedMV.isTransient() && selectedMV != null) {
				getPersistenceService().update(selectedMV);
				Messages.addGlobalInfo("update.successful",
						new Object[] {});
			}
		}

		return null;
	}

	public List<String> getDimension1List() {
		List<String> dim1List = new ArrayList<String>();
		for (MeasurableQuantity mq : mqService.list()) {
			if (!dim1List.contains(mq.getDimension1())) {
				dim1List.add(mq.getDimension1());
			}
		}
		return dim1List;
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

		String columnIndex = event
				.getColumn()
				.getColumnKey()
				.replace(
						"mqTableForm:mqTable:"
								+ String.valueOf(event.getRowIndex()) + ":col",
						"");
		selectedMV = mainMVModel.get(event.getRowIndex()).get(
				Integer.parseInt(columnIndex));

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

		for (List<MeasuredValue> mv : mainMVModel) {
			row = sheet.createRow(j);
			int mvCounter = 0;
			for (MeasuredValue subMV : mv) {
				Cell cell = row.createCell(mvCounter);
				if (mvCounter == 0) {
					cell.setCellValue(sdf1.format(subMV.getDate()));
				} else {
					if (subMV.getValue() != null
							&& subMV.getMeasurementPeriod() == period) {
						cell.setCellValue(subMV.getValue());
					}
				}

				cell.setCellStyle(getCellStyle(workbook));

				sheet.autoSizeColumn(mvCounter, true);
				mvCounter++;
			}
			j++;

		}

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
	

	protected String getDefaultViewName() {
		return "measuredValues";
	}
	

	@Override
	protected String getListViewName() {
		return "measuredValues";
	}

}

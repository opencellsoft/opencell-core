package org.meveo.apiv2.generic.services;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.util.stream.Collectors.toMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.meveo.api.dto.AgedReceivableDto;
import org.meveo.apiv2.generic.GenericFieldDetails;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Stateless
public class GenericFileExportManager {
	
	@Inject
    private ParamBeanFactory paramBeanFactory;
	protected Logger log = LoggerFactory.getLogger(getClass());

    private static final String PATH_STRING_FOLDER = "exports" + File.separator + "generic"+ File.separator;
    private static final String PATH_STRING_FOLDER_NO_GENERIC = "exports" + File.separator;
    private static final String LOCALE_FR = "FR";
    private static final String FR_AGED_BALANCE_FILENAME = "finance_rapports_balance-agee_";
    private static final String FR_DATE_FORMAT = "dd/MM/yyyy";
    private static final String EN_AGED_BALANCE_FILENAME = "Aged_trial_balance_";
    private static final String EN_DATE_FORMAT = "MM/dd/yyyy";
    private String saveDirectory;
    private static Map<String, CellStyle> excelCellStyles = new HashMap<>();

    /**
     * Excel styles enum
     */
    protected enum ExcelStylesEnum {
        BIG_DECIMAL_FORMAT, NUMERIC_FORMAT, STRING_FORMAT, DATE_FORMAT
    }

    public String export(String entityName, List<Map<String, Object>> mapResult, String fileType, Map<String, GenericFieldDetails> fieldDetails, List<String> ordredColumn, String locale){
    	log.debug("Save directory "+paramBeanFactory.getChrootDir());
    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendValue(MONTH_OF_YEAR, 2).appendValue(DAY_OF_MONTH, 2)
        		.appendLiteral('-').appendValue(HOUR_OF_DAY, 2).appendValue(MINUTE_OF_HOUR, 2).appendValue(SECOND_OF_MINUTE, 2).toFormatter();
        String time = LocalDateTime.now().format(formatter);
    	saveDirectory = paramBeanFactory.getChrootDir() + File.separator + PATH_STRING_FOLDER + entityName + File.separator +time.substring(0,8) + File.separator;
        if (mapResult != null && !mapResult.isEmpty()) {        	
            Path filePath = saveAsRecord(entityName, mapResult, fileType, fieldDetails, ordredColumn, locale);
            return filePath == null? null : filePath.toString();
        }
        return null;
    }

    /**
     * 
     * @param fileName
     * @param records
     * @param fileType
     * @param time 
     * @return
     */
    private Path saveAsRecord(String fileName, List<Map<String, Object>> records, String fileType, Map<String, GenericFieldDetails> fieldDetails, List<String> ordredColumn, String locale) {
        String extensionFile = ".csv";
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendValue(DAY_OF_MONTH, 2).appendValue(MONTH_OF_YEAR, 2).appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendLiteral('-').appendValue(HOUR_OF_DAY, 2).appendValue(MINUTE_OF_HOUR, 2).appendValue(SECOND_OF_MINUTE, 2).appendValue(MILLI_OF_SECOND, 3).toFormatter();
        String time = LocalDateTime.now().format(formatter);
        
        try {
        	
        	//CSV
            if(fileType.equals("CSV")) {
                if(!Files.exists(Path.of(saveDirectory))){
                    Files.createDirectories(Path.of(saveDirectory));
                }
                File csvFile = new File(saveDirectory + fileName + time + extensionFile);
                writeCsvFile(records, csvFile, fieldDetails, ordredColumn, locale);
                return Path.of(saveDirectory, fileName + time + extensionFile);
            }
            //EXCEL
            if(fileType.equals("EXCEL")) {
            	if(!Files.exists(Path.of(saveDirectory))){
                    Files.createDirectories(Path.of(saveDirectory));
                }
                extensionFile = ".xlsx";
                File outputExcelFile = new File(saveDirectory + fileName + time + extensionFile);
                writeExcelFile(outputExcelFile, records, fieldDetails, ordredColumn);
                return Path.of(saveDirectory + fileName + time + extensionFile);
            }
            if(fileType.equalsIgnoreCase("pdf")) {
                if(!Files.exists(Path.of(saveDirectory))){
                    Files.createDirectories(Path.of(saveDirectory));
                }
                extensionFile = ".pdf";
                File outputExcelFile = new File(saveDirectory + fileName + time + extensionFile);
                writePdfFile(outputExcelFile, records, fieldDetails, ordredColumn, locale);
                return Path.of(saveDirectory + fileName + time + extensionFile);
            }
        } catch (IOException | DocumentException e) {
            throw new RuntimeException("error during file writing : ", e);
        }
        return null;
    }

    

    /**
     * 
     * @param records
     * @param csvFile
     * @param ordredColumn
     * @throws IOException
     */
	private void writeCsvFile(List<Map<String, Object>> records, File csvFile, Map<String, GenericFieldDetails> fieldDetails, List<String> ordredColumn, String locale) throws IOException {
		CsvBuilder csv = new CsvBuilder();
        ordredColumn.forEach(field -> {
            GenericFieldDetails fieldDetail = fieldDetails.get(field);
            csv.appendValue(extractValue(field, fieldDetail));
        });
        csv.startNewLine();
        for (Map<String, Object> item : records) {
            ordredColumn.forEach(field ->
                    csv.appendValue(applyTransformation(fieldDetails.get(field), item.get(field), locale))
            );
            csv.startNewLine();
        }

		try (FileOutputStream fop = new FileOutputStream(csvFile, true)) {
			fop.write(csv.toString().getBytes());
			fop.flush();
		} catch (IOException ex) {
			throw ex;
		}
	}

	/**
     * 
     * @param file
     * @param CSVLineRecords
     * @param ordredColumn
     * @throws IOException
     */
    private void writeExcelFile(File file, List<Map<String, Object>> CSVLineRecords, Map<String, GenericFieldDetails> fieldDetails, List<String> ordredColumn) throws IOException {
        var wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        int i = 0;
        if (CSVLineRecords != null && !CSVLineRecords.isEmpty()) {
        	var rowHeader = sheet.createRow(i++);
            IntStream.range(0, ordredColumn.size())
                    .forEach(index -> {
                        Cell cell = rowHeader.createCell(index);
                        GenericFieldDetails fieldDetail = fieldDetails.get(ordredColumn.get(index));
                        cell.setCellValue(extractValue(ordredColumn.get(index), fieldDetail));
                    });
		    //Cell
            IntStream.range(0, CSVLineRecords.size())
                .forEach(indexRow -> {
                    var rowCell = sheet.createRow(indexRow+1);
                    IntStream.range(0, ordredColumn.size())
                        .forEach(indexCol -> {
                            Cell cell = rowCell.createCell(indexCol);
                            String key = ordredColumn.get(indexCol);
                            Object value = CSVLineRecords.get(indexRow).get(key);

                            GenericFieldDetails fieldDetail = fieldDetails.get(key);

                            if (fieldDetail == null) {
                                cell.setCellValue(value == null ? StringUtils.EMPTY : value.toString());
                            } else if (StringUtils.isNotBlank(fieldDetail.getTransformation())) {
                                if (value instanceof Long || value instanceof BigDecimal || value instanceof Double || value instanceof Float || value instanceof Integer) {
                                    cell.setCellValue(value instanceof BigDecimal ? ((BigDecimal) value).doubleValue() : (Long) value);
                                } else if (value instanceof Date) {
                                    cell.setCellValue((Date) value);
                                } else if (value instanceof String && (value.toString().startsWith("0.00"))) { // specific case for formula field, wich have a String type with 0.00 value
                                    cell.setCellValue(0.00);
                                } else {
                                    cell.setCellValue(extractStringValue(value));
                                }

                            } else if (MapUtils.isNotEmpty(fieldDetail.getMappings())) {
                                for (Map.Entry<String, String> map : fieldDetail.getMappings().entrySet()) {
                                    if (map.getKey().equals(value.toString())) {
                                        cell.setCellValue(map.getValue());
                                        break;
                                    }
                                }
                            } else {
                                if (value instanceof Date) {
                                    cell.setCellValue((Date) value);
                                } else if (value instanceof Long || value instanceof BigDecimal || value instanceof Double || value instanceof Float || value instanceof Integer) {
                                    cell.setCellValue(value.toString());
                                } else {
                                    cell.setCellValue(extractStringValue(value));
                                }
                            }

                            //cell.setCellValue(applyTransformation(fieldDetails.get(key), value));
                            if (value instanceof Integer || value instanceof BigInteger) {
                                applyNumericFormat(wb, cell);
                            } else if (value instanceof Long || value instanceof BigDecimal || value instanceof Double || value instanceof Float) {
                                applyBigDecimalFormat(wb, cell);
                            } else if (value instanceof Date) {
                                applyDateFormat(wb, cell);
                            } else if (value instanceof String && value.toString().startsWith("0.00")) {
                                applyBigDecimalFormat(wb, cell);
                            } else {
                                applyStringFormat(wb, cell);
                            }
                        });
                });
                try {
                    FileOutputStream fileOut = new FileOutputStream(file);
                    wb.write(fileOut);
                    fileOut.close();
                    wb.close();
                } catch (IOException e) {
                    throw new RuntimeException("error during file writing : ", e);
                }
        }
    }
    
    private void writePdfFile(File file, List<Map<String, Object>> lineRecords, Map<String, GenericFieldDetails> fieldDetails, List<String> ordredColumn, String locale) throws IOException, DocumentException{
        if(!CollectionUtils.isEmpty(lineRecords)) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();
            final PdfPTable table = new PdfPTable(lineRecords.get(0).size());
            table.setWidthPercentage(100);
            addColumns(ordredColumn, table, fieldDetails);
            lineRecords.forEach(map -> addRows(map, table, fieldDetails, ordredColumn, locale));
            doc.add(table);
            doc.close();
        }
    }
    
    private void addColumns(List<String> columns, PdfPTable table, Map<String, GenericFieldDetails> fieldDetails) {
        columns.forEach(column -> {
            PdfPCell header = new PdfPCell();
            GenericFieldDetails fieldDetail = fieldDetails.get(column);
            header.setPhrase(new Phrase(extractValue(column, fieldDetail)));
            table.addCell(header);
        });
    }
    
    private void addRows(Map<String, Object> rows, PdfPTable table, Map<String, GenericFieldDetails> fieldDetails, List<String> ordredColumn, String locale) {
        ordredColumn.forEach(col -> {
            GenericFieldDetails fieldDetail = fieldDetails.get(col);
            table.addCell(applyTransformation(fieldDetail, rows.get(col), locale));
        });
    }

    private String applyTransformation(GenericFieldDetails fieldDetail, Object value, String locale) {
        if (fieldDetail == null) {
            return value == null ? StringUtils.EMPTY : value.toString();
        }

        if (StringUtils.isNotBlank(fieldDetail.getTransformation())) {
            if (value instanceof Long || value instanceof BigDecimal || value instanceof Double || value instanceof Float || value instanceof Integer) {
                DecimalFormatSymbols symbols = "FR".equals(locale) ? new DecimalFormatSymbols(Locale.FRENCH) : new DecimalFormatSymbols(Locale.ENGLISH);
                DecimalFormat formatter = new DecimalFormat(fieldDetail.getTransformation(), symbols);
                formatter.setGroupingUsed(false);
                return formatter.format(value);
            }

            if (value instanceof Date) {
                return new SimpleDateFormat(fieldDetail.getTransformation()).format(value);
            }

            if (value instanceof LocalDate || value instanceof LocalDateTime) {
                return DateTimeFormatter.ofPattern(fieldDetail.getTransformation()).format((TemporalAccessor) value);
            }
        }

        if (MapUtils.isNotEmpty(fieldDetail.getMappings())) {
            for (Map.Entry<String, String> map : fieldDetail.getMappings().entrySet()) {
                if (map.getKey().equals(value.toString())) {
                    return map.getValue();
                }
            }
        }

        return value == null ? null : value.toString();

    }
    
    private String extractValue(String key, GenericFieldDetails fieldDetail) {
		return fieldDetail == null ? key : fieldDetail.getHeader() != null ? fieldDetail.getHeader() : fieldDetail.getName();
	}
    
    private String extractStringValue(Object value) {
    	 return value == null ? StringUtils.EMPTY : value.toString();
	}

    private void applyBigDecimalFormat(Workbook outWorkbook, Cell cell) {
        CellStyle cellStyle = excelCellStyles.get(ExcelStylesEnum.BIG_DECIMAL_FORMAT.name());
        if (cellStyle == null) {
            cellStyle = outWorkbook.createCellStyle();
            DataFormat format = outWorkbook.createDataFormat();
            cellStyle.setDataFormat(format.getFormat("#,##0.00"));
            cellStyle.setAlignment(HorizontalAlignment.RIGHT);
            excelCellStyles.put(ExcelStylesEnum.BIG_DECIMAL_FORMAT.name(), cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    private void applyNumericFormat(Workbook outWorkbook, Cell cell) {
        CellStyle cellStyle = excelCellStyles.get(ExcelStylesEnum.NUMERIC_FORMAT.name());
        if (cellStyle == null) {
            cellStyle = outWorkbook.createCellStyle();
            cellStyle.setDataFormat((short) 1);
            cellStyle.setAlignment(HorizontalAlignment.RIGHT);
            excelCellStyles.put(ExcelStylesEnum.NUMERIC_FORMAT.name(), cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    private void applyStringFormat(Workbook outWorkbook, Cell cell) {
        CellStyle cellStyle = excelCellStyles.get(ExcelStylesEnum.STRING_FORMAT.name());
        if (cellStyle == null) {
            cellStyle = outWorkbook.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            excelCellStyles.put(ExcelStylesEnum.STRING_FORMAT.name(), cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    private void applyDateFormat(Workbook outWorkbook, Cell cell) {
        CellStyle cellStyle = excelCellStyles.get(ExcelStylesEnum.DATE_FORMAT.name());
        if (cellStyle == null) {
            cellStyle = outWorkbook.createCellStyle();
            cellStyle.setDataFormat((short) 14);
            cellStyle.setAlignment(HorizontalAlignment.LEFT);
            excelCellStyles.put(ExcelStylesEnum.DATE_FORMAT.name(), cellStyle);
        }
        cell.setCellStyle(cellStyle);
    }

    /**
     * Export aged trial balance
     * @param entityName Entity Name
     * @param fileType File Type (EXCEL, PDF or CSV)
     * @param genericFieldDetails List of {@link GenericFieldDetails}
     * @param agedReceivablesList List of {@link AgedReceivableDto}
     * @param orderedColumn List of columns
     * @param locale Language
     * @return Stored Path
     */
    public String exportAgedTrialBalance(String entityName, String fileType, List<GenericFieldDetails> genericFieldDetails,
                                         List<AgedReceivableDto> agedReceivablesList, List<String> orderedColumn, String locale){
        log.info("Export Aged Balance - Entity Name: {}, File Type: {}, Locale: {}", entityName, fileType, locale);

        String filename = FR_AGED_BALANCE_FILENAME;;
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendValue(DAY_OF_MONTH, 2).appendValue(MONTH_OF_YEAR, 2).appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendLiteral('-').appendValue(HOUR_OF_DAY, 2).appendValue(MINUTE_OF_HOUR, 2).appendValue(SECOND_OF_MINUTE, 2).toFormatter();
        SimpleDateFormat format = new SimpleDateFormat(FR_DATE_FORMAT);

        // Set Time to create file in the export directory
        String time = LocalDateTime.now().format(formatter);
        saveDirectory = paramBeanFactory.getChrootDir() + File.separator + PATH_STRING_FOLDER_NO_GENERIC + entityName + File.separator + time.substring(0,8) + File.separator;

        // Manage locale language FR or Others (EN)
        if(!LOCALE_FR.equalsIgnoreCase(locale)) {
            filename = EN_AGED_BALANCE_FILENAME;
            format = new SimpleDateFormat(EN_DATE_FORMAT);
        }

        // Manage Columns and convert List of AgedReceivable to a List Of Map
        List<String> columns = new ArrayList<>();
        Objects.requireNonNull(genericFieldDetails).forEach(gfd -> columns.add(gfd.getName()));
        List<Map<String, Object>> map = convertObjectsListToListOfMap(agedReceivablesList, AgedReceivableDto.class, columns);
       
        // Get Fields to Map by name and header
        Map<String, GenericFieldDetails> fieldDetails = getFieldDetailsMap(genericFieldDetails);
        
        // Format Fields
        formatFields(map, format, locale);

        // If the map is not empty then save As Record to export - CSV, EXCEL or PDF
        if (!map.isEmpty()) {
            Path filePath = saveAsRecord(filename, map, fileType, fieldDetails, orderedColumn, locale);
            return filePath == null ? null : filePath.toString();
        }

        return null;
    }

    /**
     * Convert Objects to List of Map
     * @param list List of {@link AgedReceivableDto}
     * @param clazz Class
     * @param columns List of columns
     * @return List of Map
     */
    private List<Map<String, Object>> convertObjectsListToListOfMap(List<AgedReceivableDto> list, Class clazz, List<String> columns) {
        List<Map<String, Object>> listOfMap = new ArrayList<>();
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields()).filter(x -> columns.contains(x.getName())).collect(Collectors.toList());

        if(list != null) {
            for (Object o : list) {
                Map<String, Object> map = new TreeMap<>();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value;
                    try {
                        value = field.get(o);
                        map.put(field.getName(), value);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        log.error("error occurred when converting list to list of Map");
                    }
                }
                listOfMap.add(map);
            }
        }

        return listOfMap;
    }

    /**
     * Format fields (Date)
     * @param mapResult List of Map
     * @param format Date Format
     */
    private static void formatFields(List<Map<String, Object>> mapResult, SimpleDateFormat format, String locale) {
        for (Map<String, Object> item : mapResult) {
            for (Map.Entry<String, Object> entry : item.entrySet()) {
                if(entry.getKey().equals("dueDate")) {
                    entry.setValue(format.format(entry.getValue()));
                }
            }
        }
    }

    /**
     * Get Field Detail Map
     * @param genericFieldDetails List of {@link GenericFieldDetails}
     * @return A Map Generic Field Details
     */
    private Map<String, GenericFieldDetails> getFieldDetailsMap(List<GenericFieldDetails> genericFieldDetails) {
        Map<String, GenericFieldDetails> fieldDetails = new HashMap<>();

        if (CollectionUtils.isNotEmpty(genericFieldDetails)) {
            fieldDetails = genericFieldDetails.stream().collect(toMap(x -> nameOrHeader(x), Function.identity()));
        }

        return fieldDetails;
    }

    /**
     * Get name or header
     * @param genericFieldDetails {@link GenericFieldDetails}
     * @return Nome or Header
     */
    private String nameOrHeader(GenericFieldDetails genericFieldDetails) {
        return Optional.ofNullable(genericFieldDetails.getName()).orElse(genericFieldDetails.getHeader());
    }
}
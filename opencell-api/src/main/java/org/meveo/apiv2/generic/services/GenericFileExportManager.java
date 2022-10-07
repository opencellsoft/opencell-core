package org.meveo.apiv2.generic.services;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.IntStream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bouncycastle.util.Arrays;
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
    private String saveDirectory;

    public String export(String entityName, List<Map<String, Object>> mapResult, String fileType, Map<String, GenericFieldDetails> fieldDetails, List<String> ordredColumn){
    	log.debug("Save directory "+paramBeanFactory.getChrootDir());
    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendValue(MONTH_OF_YEAR, 2).appendValue(DAY_OF_MONTH, 2)
        		.appendLiteral('-').appendValue(HOUR_OF_DAY, 2).appendValue(MINUTE_OF_HOUR, 2).appendValue(SECOND_OF_MINUTE, 2).toFormatter();
        String time = LocalDateTime.now().format(formatter);
    	saveDirectory = paramBeanFactory.getChrootDir() + File.separator + PATH_STRING_FOLDER + entityName + File.separator +time.substring(0,8) + File.separator;
        if (mapResult != null && !mapResult.isEmpty()) {        	
            Path filePath = saveAsRecord(entityName, mapResult, fileType, fieldDetails, time, ordredColumn);
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
    private Path saveAsRecord(String fileName, List<Map<String, Object>> records, String fileType, Map<String, GenericFieldDetails> fieldDetails, String time, List<String> ordredColumn) {
        String extensionFile = ".csv";
        
        try {
        	
        	//CSV
            if(fileType.equals("CSV")) {
                if(!Files.exists(Path.of(saveDirectory))){
                    Files.createDirectories(Path.of(saveDirectory));
                }
                File csvFile = new File(saveDirectory + fileName + time + extensionFile);
                writeCsvFile(records, csvFile, fieldDetails, ordredColumn);
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
                writePdfFile(outputExcelFile, records, fieldDetails, ordredColumn);
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
	private void writeCsvFile(List<Map<String, Object>> records, File csvFile, Map<String, GenericFieldDetails> fieldDetails, List<String> ordredColumn) throws IOException {
		CsvBuilder csv = new CsvBuilder();
        ordredColumn.forEach(field -> {
            GenericFieldDetails fieldDetail = fieldDetails.get(field);
            csv.appendValue(extractValue(field, fieldDetail));
        });
        csv.startNewLine();
        for (Map<String, Object> item : records) {
            ordredColumn.forEach(field ->
                    csv.appendValue(applyTransformation(fieldDetails.get(field), item.get(field)))
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
                            cell.setCellValue(applyTransformation(fieldDetails.get(key), CSVLineRecords.get(indexRow).get(key)));
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
    
    private void writePdfFile(File file, List<Map<String, Object>> lineRecords, Map<String, GenericFieldDetails> fieldDetails, List<String> ordredColumn) throws IOException, DocumentException{
        if(!CollectionUtils.isEmpty(lineRecords)) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();
            final PdfPTable table = new PdfPTable(lineRecords.get(0).size());
            table.setWidthPercentage(100);
            addColumns(ordredColumn, table, fieldDetails);
            lineRecords.forEach(map -> addRows(map, table, fieldDetails, ordredColumn));
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
    
    private void addRows(Map<String, Object> rows, PdfPTable table, Map<String, GenericFieldDetails> fieldDetails, List<String> ordredColumn) {
        ordredColumn.forEach(col -> {
            GenericFieldDetails fieldDetail = fieldDetails.get(col);
            table.addCell(applyTransformation(fieldDetail, rows.get(col)));
        });
    }

    private String applyTransformation(GenericFieldDetails fieldDetail, Object value) {
        if (fieldDetail == null) {
            return value == null ? StringUtils.EMPTY : value.toString();
        }

        if (StringUtils.isNotBlank(fieldDetail.getTransformation())) {
            if (value instanceof BigDecimal || value instanceof Double || value instanceof Float) {
                return new DecimalFormat(fieldDetail.getTransformation()).format(value);
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
    
}

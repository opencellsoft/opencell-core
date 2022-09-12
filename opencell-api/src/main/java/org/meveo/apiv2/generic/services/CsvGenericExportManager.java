package org.meveo.apiv2.generic.services;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

import java.io.File;
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

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
public class CsvGenericExportManager {
	
	@Inject
    private ParamBeanFactory paramBeanFactory;
	protected Logger log = LoggerFactory.getLogger(getClass());

    private static final String PATH_STRING_FOLDER = "exports" + File.separator + "generic"+ File.separator;
    private String saveDirectory;

    public String export(String entityName, List<Map<String, Object>> mapResult, String fileType, Map<String, GenericFieldDetails> fieldDetails){
    	log.debug("Save directory "+paramBeanFactory.getChrootDir());
    	saveDirectory = paramBeanFactory.getChrootDir() + File.separator + PATH_STRING_FOLDER;
        if (mapResult != null && !mapResult.isEmpty()) {        	
            Path filePath = saveAsRecord(entityName, mapResult, fileType, fieldDetails);
            return filePath == null? null : filePath.toString();
        }
        return null;
    }

    /**
     * 
     * @param fileName
     * @param records
     * @param fileType
     * @return
     */
    private Path saveAsRecord(String fileName, List<Map<String, Object>> records, String fileType, Map<String, GenericFieldDetails> fieldDetails) {
        String extensionFile = ".csv";
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendValue(MONTH_OF_YEAR, 2).appendValue(DAY_OF_MONTH, 2)
        		.appendLiteral('-').appendValue(HOUR_OF_DAY, 2).appendValue(MINUTE_OF_HOUR, 2).appendValue(SECOND_OF_MINUTE, 2).toFormatter();
        
        try {
        	String time = LocalDateTime.now().format(formatter);
        	//CSV
            if(fileType.equals("CSV")) {
                if(!Files.exists(Path.of(saveDirectory))){
                    Files.createDirectories(Path.of(saveDirectory));
                }
                File csvFile = new File(saveDirectory + fileName + time + extensionFile);
                writeCsvFile(records, csvFile, fieldDetails);
                return Path.of(saveDirectory, fileName + time + extensionFile);
            }
            //EXCEL
            if(fileType.equals("EXCEL")) {
            	if(!Files.exists(Path.of(saveDirectory))){
                    Files.createDirectories(Path.of(saveDirectory));
                }
                extensionFile = ".xlsx";
                File outputExcelFile = new File(saveDirectory + fileName + time + extensionFile);
                writeExcelFile(outputExcelFile, records, fieldDetails);
                return Path.of(saveDirectory + fileName + time + extensionFile);
            }
            if(fileType.equalsIgnoreCase("pdf")) {
                if(!Files.exists(Path.of(saveDirectory))){
                    Files.createDirectories(Path.of(saveDirectory));
                }
                extensionFile = ".pdf";
                File outputExcelFile = new File(saveDirectory + fileName + time + extensionFile);
                writePdfFile(outputExcelFile, records, fieldDetails);
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
     * @throws IOException
     */
	private void writeCsvFile(List<Map<String, Object>> records, File csvFile, Map<String, GenericFieldDetails> fieldDetails) throws IOException {
		CsvBuilder csv = new CsvBuilder();
		for(Map<String,Object> item : records) {
			for(Entry<String,Object> entry : item.entrySet()) {
				//Header
				//csv.appendValue(translations.getOrDefault(entry.getKey(), entry.getKey()));
                GenericFieldDetails fieldDetail = fieldDetails.get(entry.getKey());
                csv.appendValue(fieldDetail==null ? entry.getKey() : fieldDetail.getHeader());
			}
			break;
		}
		csv.startNewLine();                 		    
		//Cell
		for(Map<String,Object> item : records) {
		    for(Entry<String,Object> entry : item.entrySet()) {
		        csv.appendValue(applyTransformation(fieldDetails.get(entry.getKey()), entry.getValue()));
		        }
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
     * @throws IOException
     */
    private void writeExcelFile(File file, List<Map<String, Object>> CSVLineRecords, Map<String, GenericFieldDetails> fieldDetails) throws IOException {
        var wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        int i = 0;
        
        if (CSVLineRecords != null && !CSVLineRecords.isEmpty()) {
        	Cell cell;
        	var rowHeader = sheet.createRow(i++);
	    	for(Map<String,Object> item : CSVLineRecords) {
	    		int column = 0;
	    		for(Entry<String,Object> entry : item.entrySet()) {
	    			//Header
	    		    cell = rowHeader.createCell(column);
	    		    // cell.setCellValue(translations.getOrDefault(entry.getKey(), entry.getKey()));
                    GenericFieldDetails fieldDetail = fieldDetails.get(entry.getKey());
                    cell.setCellValue(fieldDetail==null ? entry.getKey() : fieldDetail.getHeader());
	    		    column++;
	    		}
	    	}
		    //Cell
	    	for(Map<String,Object> item : CSVLineRecords) {
		        rowHeader = sheet.createRow(i++);
		        int column = 0;
		        for(Entry<String,Object> entry : item.entrySet()) {
		        	
                    cell = rowHeader.createCell(column);
                    cell.setCellValue(applyTransformation(fieldDetails.get(entry.getKey()), entry.getValue()));
                    column++;
		            }
		        }
		        FileOutputStream fileOut = new FileOutputStream(file);
		        wb.write(fileOut);
		        fileOut.close();
		        wb.close();
        }
    }
    
    private void writePdfFile(File file, List<Map<String, Object>> lineRecords, Map<String, GenericFieldDetails> fieldDetails) throws IOException, DocumentException{
        if(!CollectionUtils.isEmpty(lineRecords)) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            doc.open();
            final PdfPTable table = new PdfPTable(lineRecords.get(0).size());
            table.setWidthPercentage(100);
            addColumns(lineRecords.get(0).keySet(), table, fieldDetails);
            lineRecords.forEach(map -> addRows(map, table, fieldDetails));
            doc.add(table);
            doc.close();
        }
    }
    
    private void addColumns(Set<String> columns, PdfPTable table, Map<String, GenericFieldDetails> fieldDetails) {
        columns.forEach(column -> {
            PdfPCell header = new PdfPCell();
            GenericFieldDetails fieldDetail = fieldDetails.get(column);
            header.setPhrase(new Phrase(fieldDetail==null ? column : fieldDetail.getHeader()));
            table.addCell(header);
        });
    }
    
    private void addRows(Map<String, Object> rows, PdfPTable table, Map<String, GenericFieldDetails> fieldDetails) {
        rows.forEach((field, value) -> {
            GenericFieldDetails fieldDetail = fieldDetails.get(field);
            table.addCell(applyTransformation(fieldDetail, value));
        });
        //rows.values().forEach(obj -> table.addCell(obj != null ? obj.toString() : ""));
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

        return value.toString();

    }
}

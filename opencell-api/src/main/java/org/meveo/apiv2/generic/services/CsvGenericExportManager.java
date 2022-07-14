package org.meveo.apiv2.generic.services;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.ParamBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class CsvGenericExportManager {
	
	@Inject
    private ParamBeanFactory paramBeanFactory;
	protected Logger log = LoggerFactory.getLogger(getClass());

    private static final String PATH_STRING_FOLDER = "exports" + File.separator + "generic"+ File.separator;
    private String saveDirectory;

    public String export(String entityName, List<Map<String, Object>> mapResult, String fileType){
    	log.debug("Save directory "+paramBeanFactory.getChrootDir());
    	saveDirectory = paramBeanFactory.getChrootDir() + File.separator + PATH_STRING_FOLDER;
        if (mapResult != null && !mapResult.isEmpty()) {        	
            Path filePath = saveAsRecord(entityName, mapResult, fileType);            
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
    private Path saveAsRecord(String fileName, List<Map<String, Object>> records, String fileType) {        
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
                writeCsvFile(records, csvFile);
                return Path.of(saveDirectory, fileName + time + extensionFile);
            }
            //EXCEL
            if(fileType.equals("EXCEL")) {
            	if(!Files.exists(Path.of(saveDirectory))){
                    Files.createDirectories(Path.of(saveDirectory));
                }
                extensionFile = ".xlsx";
                File outputExcelFile = new File(saveDirectory + fileName + time + extensionFile);
                writeExcelFile(outputExcelFile, records);
                return Path.of(saveDirectory + fileName + time + extensionFile);
            }
        } catch (IOException e) {
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
	private void writeCsvFile(List<Map<String, Object>> records, File csvFile) throws IOException {
		CsvBuilder csv = new CsvBuilder();
		for(Map<String,Object> item : records) {
			for(Entry<String,Object> entry : item.entrySet()) {
				//Header
				csv.appendValue(entry.getKey());
			}
			break;
		}
		csv.startNewLine();                 		    
		//Cell
		for(Map<String,Object> item : records) {
		    for(Entry<String,Object> entry : item.entrySet()) {
		        csv.appendValue(entry.getValue().toString());
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
    private void writeExcelFile(File file, List<Map<String, Object>> CSVLineRecords) throws IOException {
        FileWriter fw = new FileWriter(file, true); 
        BufferedWriter bw = new BufferedWriter(fw);
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
	    		    cell = rowHeader.createCell(column++);
	    		    cell.setCellValue(entry.getKey()); 
	    		    column++;
	    		}
	    	}
		    //Cell
	    	for(Map<String,Object> item : CSVLineRecords) {
		        rowHeader = sheet.createRow(i++);
		        int column = 0;
		        for(Entry<String,Object> entry : item.entrySet()) {
		        	
                    cell = rowHeader.createCell(column++);
                    cell.setCellValue(entry.getValue().toString());
                    column++;
		            }
		        }
		        FileOutputStream fileOut = new FileOutputStream(file);
		        wb.write(fileOut);
		        fileOut.close();
		        wb.close();
        }
    }
}

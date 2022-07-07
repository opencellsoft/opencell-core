package org.meveo.apiv2.generic.services;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Stateless
public class CsvGenericExportManager {
	
	@Inject
    private ParamBeanFactory paramBeanFactory;
	protected Logger log = LoggerFactory.getLogger(getClass());

    private final String PATH_STRING_FOLDER = "exports" + File.separator + "generic_version";
    private String saveDirectory;

    public String export(String entityName, List<Map<String, Object>> mapResult, String fileType){
    	saveDirectory = paramBeanFactory.getChrootDir() + File.separator + PATH_STRING_FOLDER;
        if (mapResult != null && !mapResult.isEmpty()) {
        	for(Map<String,Object> item : mapResult) {
        		for(Entry<String,Object> entry : item.entrySet()) {
        			log.info("baba : " + entry.getKey() + " :  " + entry.getValue());
        		}
        	}
            Path filePath = saveAsRecord(entityName, mapResult, fileType);
            
            return filePath == null? null : filePath.toString();
        }
        return null;
    }


    /**
     * @param file
     * @param CSVLineRecords
     * @param isMatrix
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
    
    /**
     * @param fileName
     * @param ppv
     * @param fileType
     * @return
     */
    private Path saveAsRecord(String fileName, List<Map<String, Object>> records, String fileType) {
        
        String extensionFile = ".csv";
        try {
            if(fileType.equals("CSV")) {
                CsvMapper csvMapper = new CsvMapper();
                CsvSchema invoiceCsvSchema = buildPricePlanVersionCsvSchema();
                csvMapper.enable(CsvParser.Feature.WRAP_AS_ARRAY);
                if(!Files.exists(Path.of(saveDirectory))){
                    Files.createDirectories(Path.of(saveDirectory));
                }
                File csvFile = new File(saveDirectory + fileName + extensionFile);
                OutputStream fileOutputStream = new FileOutputStream(csvFile);
                fileOutputStream.write('\ufeef');
                fileOutputStream.write('\ufebb');
                fileOutputStream.write('\ufebf');
                csvMapper.writer(invoiceCsvSchema).writeValues(fileOutputStream).write(records);
                log.info("PricePlanMatrix version is exported in -> " + saveDirectory + fileName + extensionFile);
                return Path.of(saveDirectory, fileName + extensionFile);
            }
            if(fileType.equals("EXCEL")) {
                extensionFile = ".xlsx";
                File outputExcelFile = new File(saveDirectory + fileName + extensionFile);
                writeExcelFile(outputExcelFile, records);
                return Path.of(saveDirectory + fileName + extensionFile);
            }
        } catch (IOException e) {
            log.error("error exporting entity " + fileName + extensionFile);
            throw new RuntimeException("error during file writing : ", e);
        }
        return null;
    }

    private LinkedHashMap<String, Object> toCSVLineRecords(PricePlanMatrixVersion ppv) {
        LinkedHashMap<String, Object> CSVLineRecords = new LinkedHashMap<>();
        CSVLineRecords.put("id", ppv.getId());
        CSVLineRecords.put("label", ppv.getLabel());
        CSVLineRecords.put("amount", ppv.getAmountWithoutTax());
        return CSVLineRecords;
    }

    private CsvSchema buildPricePlanVersionCsvSchema() {
        return CsvSchema.builder().addColumn("id", CsvSchema.ColumnType.STRING).addColumn("label", CsvSchema.ColumnType.STRING)
            .addColumn("amount", CsvSchema.ColumnType.NUMBER_OR_STRING).build().withColumnSeparator(';').withLineSeparator("\n").withoutQuoteChar().withHeader();
    }


}

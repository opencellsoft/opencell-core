package org.meveo.apiv2.generic.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private final String PATH_STRING_FOLDER = "exports" + File.separator + "generic_version";
    private String saveDirectory;

    public String export(String entityName, List<Map<String, Object>> mapResult, String fileType){
    	saveDirectory = paramBeanFactory.getChrootDir() + File.separator + PATH_STRING_FOLDER;
        if (mapResult != null && !mapResult.isEmpty()) {        	
            Path filePath = saveAsRecord(entityName, mapResult, fileType);            
            return filePath == null? null : filePath.toString();
        }
        return null;
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
    
    /**
     * 
     * @param fileName
     * @param records
     * @param fileType
     * @return
     */
    private Path saveAsRecord(String fileName, List<Map<String, Object>> records, String fileType) {
        
        String extensionFile = ".csv";
        try {
            if(fileType.equals("CSV")) {
                if(!Files.exists(Path.of(saveDirectory))){
                    Files.createDirectories(Path.of(saveDirectory));
                }
                File csvFile = new File(saveDirectory + fileName + extensionFile);
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
                log.info("entity is exported in -> " + saveDirectory + fileName + extensionFile);
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


}

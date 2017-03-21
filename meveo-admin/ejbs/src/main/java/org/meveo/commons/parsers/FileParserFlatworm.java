package org.meveo.commons.parsers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.blackbear.flatworm.ConfigurationReader;
import com.blackbear.flatworm.FileFormat;
import com.blackbear.flatworm.MatchedRecord;

public class FileParserFlatworm implements IFileParser {

	private FileFormat fileFormat = null;
    private ConfigurationReader parser = null;	    
    private File dataFile =null;    
    private MatchedRecord record = null;
    private String mappingDescriptor = null;    
    private String recordName = null;
    private BufferedReader bufferedReader = null;
    private RecordContext recordContext = new RecordContext();
    private RecordRejectedException recordRejectedException=null;
    
    
	public FileParserFlatworm(){
		this.parser = new ConfigurationReader();
	}
	
	@Override
	public void setDataFile(File file) {
		this.dataFile = file;
	}

	@Override
	public void setMappingDescriptor(String mappingDescriptor){
		this.mappingDescriptor = mappingDescriptor;
	}

	@Override
	public void setDataName(String dataName) {
		this.recordName = dataName;
	}

	@Override
	public void parsing() throws Exception {
		fileFormat = parser.loadConfigurationFile( new ByteArrayInputStream(mappingDescriptor.getBytes(StandardCharsets.UTF_8)));
		bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));		
	}
	
	@Override
	public boolean hasNext()throws Exception{
		 if(fileFormat == null || bufferedReader == null ){
			 return false;
		 }
		record = null;
		recordContext.setRecord(null);
		try {
			record =   fileFormat.getNextRecord(bufferedReader);
		} catch ( Exception e) {
			recordRejectedException =  new RecordRejectedException(e.getMessage());	
			return true;
		}
		if(record != null){
			recordContext.setRecord(record.getBean(recordName));	
			recordContext.setLineContent(recordContext.getRecord().toString());
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public RecordContext getNextRecord()  throws RecordRejectedException {	
		if(recordContext.getRecord() == null){
			throw  recordRejectedException;
		}
		return recordContext;   
	}

	
	@Override
	public void close(){
		if(bufferedReader != null){
			try {
				bufferedReader.close();
			} catch (Exception e) {
			}
		}
	}
}

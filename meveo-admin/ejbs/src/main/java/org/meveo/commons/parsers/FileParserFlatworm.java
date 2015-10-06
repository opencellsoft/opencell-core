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
import com.blackbear.flatworm.errors.FlatwormConversionException;
import com.blackbear.flatworm.errors.FlatwormCreatorException;
import com.blackbear.flatworm.errors.FlatwormInputLineLengthException;
import com.blackbear.flatworm.errors.FlatwormInvalidRecordException;
import com.blackbear.flatworm.errors.FlatwormUnsetFieldValueException;

public class FileParserFlatworm implements IFileParser {

	private FileFormat fileFormat = null;
    private ConfigurationReader parser = null;	    
    private File dataFile =null;    
    private MatchedRecord record = null;
    private String mappingDescriptor = null;    
    private String recordName = null;
    private BufferedReader bufferedReader = null;
    
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
	public Object getNextRecord()  throws RecordRejectedException,Exception {		
		try {
			record =   fileFormat.getNextRecord(bufferedReader);
		} catch (FlatwormInvalidRecordException  | FlatwormInputLineLengthException  | FlatwormConversionException   | FlatwormUnsetFieldValueException e) {
		  throw new RecordRejectedException(e.getMessage());		
		} catch ( Exception e) {
			throw e;
		}
		if(record != null){
	    	return record.getBean(recordName);		
		}
	    return null;	    
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

package org.meveo.commons.parsers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

import org.beanio.BeanReader;
import org.beanio.InvalidRecordException;
import org.beanio.MalformedRecordException;
import org.beanio.StreamFactory;
import org.beanio.UnexpectedRecordException;
import org.beanio.UnidentifiedRecordException;

public class FileParserBeanio implements IFileParser {
	
    private StreamFactory factory = null;	    
    private File dataFile =null;    
    private BeanReader beanReader = null;     
    private String mappingDescriptor = null;    
    private String streamName = null;
    
	public FileParserBeanio(){
		this.factory = StreamFactory.newInstance();
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
		this.streamName = dataName;
	}

	@Override
	public Object getNextRecord() throws RecordRejectedException,Exception{
		try{
			return beanReader.read();
		}catch(MalformedRecordException | UnidentifiedRecordException | UnexpectedRecordException | InvalidRecordException e  ){
			throw new RecordRejectedException(e.getMessage());
		}catch(Exception e  ){
			throw e;
		}
	}

	@Override
	public void parsing() throws Exception {
		factory.load( new ByteArrayInputStream(mappingDescriptor.getBytes(StandardCharsets.UTF_8)));
		beanReader = factory.createReader(streamName, dataFile);		
	}

	@Override
	public void close() {
		if(beanReader != null){
			beanReader.close();
		}
	}

}

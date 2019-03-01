package org.meveo.commons.parsers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

import org.beanio.BeanReader;
import org.beanio.StreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Abdellatif BARI
 * @lastModifiedVersion 6.0
 */
public class FileParserBeanio implements IFileParser {

    private StreamFactory factory = null;
    private File dataFile = null;
    private BeanReader beanReader = null;
    private String mappingDescriptor = null;
    private String streamName = null;
    private RecordContext recordContext = new RecordContext();
    private RecordRejectedException recordRejectedException = null;
    private static final Logger log = LoggerFactory.getLogger(FileParserBeanio.class);

    public FileParserBeanio() {
        this.factory = StreamFactory.newInstance();
    }

    @Override
    public void setDataFile(File file) {
        this.dataFile = file;
    }

    @Override
    public void setMappingDescriptor(String mappingDescriptor) {
        this.mappingDescriptor = mappingDescriptor;
    }

    @Override
    public void setDataName(String dataName) {
        this.streamName = dataName;
    }

    @Override
    public void parsing() throws Exception {
        factory.load(new ByteArrayInputStream(mappingDescriptor.getBytes(StandardCharsets.UTF_8)));
        beanReader = factory.createReader(streamName, dataFile);
    }

    @Override
    public boolean hasNext() throws Exception {
        if (beanReader == null) {
            return false;
        }
        recordContext.setRecord(null);
        recordContext.setReason(null);
        log.info("the reason before reading line numbre {} is {}",beanReader.getLineNumber()+1,recordContext.getReason());
        try {
            recordContext.setRecord(beanReader.read());
            recordContext.setLineContent(beanReader.getRecordContext(0).getRecordText());
            recordContext.setLineNumber(beanReader.getLineNumber());

        } catch (Exception e) {
            log.warn("cant parse record:", e.getMessage());
            recordRejectedException = new RecordRejectedException(e.getMessage());
            boolean isEndFile = true;
            try {
                recordContext.setLineContent(beanReader.getRecordContext(0).getRecordText());
            } catch (Exception e2) {
                recordContext.setLineContent("unparseable line");
                isEndFile = false;
            }
            recordContext.setLineNumber(beanReader.getLineNumber());
            recordContext.setReason(e.getMessage()); 
            
            if(isEndFile) {
                return true;
            }else {
                return false;
            }            
        }finally {
            log.info("the reason after reading line numbre {} is {}",beanReader.getLineNumber(),recordContext.getReason());
        }

        if (recordContext.getRecord() != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public RecordContext getNextRecord() throws RecordRejectedException {
        if (recordContext == null) {
            throw recordRejectedException;
        }
        return recordContext;
    }

    @Override
    public void close() {
        if (beanReader != null) {
            beanReader.close();
        }
    }

}

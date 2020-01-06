package org.meveo.commons.parsers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

import org.beanio.BeanReader;
import org.beanio.StreamFactory;

/**
 *
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.3.1
 */
public class FileParserBeanio implements IFileParser {

    private StreamFactory factory = null;
    private File dataFile = null;
    private BeanReader beanReader = null;
    private String mappingDescriptor = null;
    private String streamName = null;

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
    public synchronized RecordContext getNextRecord() {

        if (beanReader == null) {
            return null;
        }

        RecordContext recordContext = new RecordContext();

        try {
            recordContext.setRecord(beanReader.read());
            if (recordContext.getRecord() == null) {
                return null; // end of file reached
            }
            recordContext.setLineContent(getLineContent());
            recordContext.setLineNumber(beanReader.getLineNumber());

        } catch (Exception e) {
            try {
                recordContext.setLineContent(getLineContent());
            } catch (Exception e2) {
                recordContext.setLineContent("unparseable line");
            }
            recordContext.setLineNumber(beanReader.getLineNumber());
            recordContext.setRejectReason(e);
        }

        return recordContext;
    }

    /**
     * Get line content
     *
     * @return line content
     */
    private String getLineContent() {
        StringBuilder recordContent = new StringBuilder();
        for (int i = 0; i < beanReader.getRecordCount(); i++) {
            recordContent.append(beanReader.getRecordContext(i).getRecordText());
            if (i != beanReader.getRecordCount() - 1) {
                recordContent.append("\r\n");
            }
        }
        return recordContent.toString();
    }

    @Override
    public void close() {
        if (beanReader != null) {
            beanReader.close();
        }
    }

}
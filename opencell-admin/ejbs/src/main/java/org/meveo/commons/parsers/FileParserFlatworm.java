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
    private File dataFile = null;
    private String mappingDescriptor = null;
    private String recordName = null;
    private BufferedReader bufferedReader = null;

    public FileParserFlatworm() {
        this.parser = new ConfigurationReader();
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
        this.recordName = dataName;
    }

    @Override
    public void parsing() throws Exception {
        fileFormat = parser.loadConfigurationFile(new ByteArrayInputStream(mappingDescriptor.getBytes(StandardCharsets.UTF_8)));
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
    }

    @Override
    public synchronized RecordContext getNextRecord() {
        if (fileFormat == null || bufferedReader == null) {
            return null;
        }
        RecordContext recordContext = new RecordContext();

        try {
            MatchedRecord record = fileFormat.getNextRecord(bufferedReader);
            if (record == null) {
                return null; // end of file reached
            }
            recordContext.setRecord(record.getBean(recordName));
            recordContext.setLineContent(recordContext.getRecord().toString());

        } catch (Exception e) {
            recordContext.setRejectReason(e);
        }

        return recordContext;
    }

    @Override
    public void close() {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (Exception e) {
            }
        }
    }
}
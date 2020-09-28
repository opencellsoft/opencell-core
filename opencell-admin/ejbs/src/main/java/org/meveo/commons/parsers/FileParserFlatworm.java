/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.commons.parsers;

import com.blackbear.flatworm.ConfigurationReader;
import com.blackbear.flatworm.FileFormat;
import com.blackbear.flatworm.MatchedRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Deprecated
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
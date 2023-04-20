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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

import org.beanio.BeanReader;
import org.beanio.StreamFactory;
import org.meveo.admin.storage.StorageFactory;

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
        beanReader = factory.createReader(streamName, StorageFactory.getBufferedReader(dataFile));
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
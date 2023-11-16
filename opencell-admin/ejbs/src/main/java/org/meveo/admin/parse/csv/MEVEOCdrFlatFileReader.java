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

package org.meveo.admin.parse.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.commons.parsers.FileParserBeanio;
import org.meveo.commons.parsers.RecordContext;
import org.meveo.commons.utils.FileUtils;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.rating.CDR;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService.CDR_ORIGIN_ENUM;
import org.meveo.service.medina.impl.ICdrCsvReader;
import org.meveo.service.medina.impl.ICdrParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default CDR file Reader
 * 
 * @lastModifiedVersion 10.0
 * 
 * @author H.ZNIBAR
 */
@Singleton
@Lock(LockType.READ)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class MEVEOCdrFlatFileReader extends FileParserBeanio implements ICdrCsvReader {

    private static final ThreadLocal<MessageDigest> messageDigest = new ThreadLocal<MessageDigest>() {
        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                Logger log = LoggerFactory.getLogger(MEVEOCdrFlatFileReader.class);
                log.error("No message digest of type MD5", e);
                return null;
            }
        }        
    };

    protected String batchName;
    protected String username;
    protected CDR_ORIGIN_ENUM origin;

    protected Integer totalNumberOfRecords;

    @Override
    public void init(File cdrFile) throws FileNotFoundException {
        batchName = "CDR_" + cdrFile.getName();
        this.origin = CDR_ORIGIN_ENUM.JOB;

        try {
            totalNumberOfRecords = FileUtils.countLines(cdrFile);
        } catch (IOException e) {
        }
    }

    @Override
    public void init(String user, String ip) {
        this.batchName = "API_" + ip;
        this.origin = CDR_ORIGIN_ENUM.API;
        this.username = user;
    }

    public String getBatchName() {
        return batchName;
    }

    public String getUsername() {
        return username;
    }

    public CDR_ORIGIN_ENUM getOrigin() {
        return origin;
    }

    @Override
    public CDR getNextRecord(ICdrParser cdrParser, String originRecordEL) throws IOException {
        RecordContext recordContext = getNextRecord();
        if (recordContext == null) {
            return null;
        }

        return getRecord(cdrParser, recordContext, originRecordEL);
    }

    @Override
    public CDR getRecord(ICdrParser cdrParser, Object cdrData, String originRecordEL) {
        RecordContext recordContext = (RecordContext) cdrData;
        CDR cdr = null;
        try {
            if (recordContext.getRecord() != null) {
                cdr = cdrParser.parse(recordContext.getRecord());

            } else if (recordContext.getRejectReason() != null) {
                cdr = new CDR();
                cdr.setRejectReasonException(recordContext.getRejectReason());
            }

        } catch (CDRParsingException e) {
            cdr = new CDR();
            cdr.setRejectReasonException(e);

        } finally {

            if (recordContext.getRecord() != null) {
// TODO Currently source field is not used when reprocessing a CDR - a line field is used instead
//            try {
//                String source = RecordContext.serializeRecord(recordContext.getRecord());
//                cdr.setSource(source);
//            } catch (Exception e) {
//                log.error("Failed to serialize CDR record", e);
//            }
                cdr.setType(recordContext.getRecord().getClass().getName());
            }
            String line = recordContext.getLineContent();
            cdr.setLine(line);
            cdr.setOriginBatch(batchName);

            if (Strings.isNotBlank(originRecordEL)) {
                try {
                    cdr.setOriginRecord(ValueExpressionWrapper.evaluateExpression(originRecordEL, Map.of("cdr", cdr), String.class));
                } catch (InvalidELException e) {
                    cdr.setRejectReasonException(e);
                }
            } else {
                cdr.setOriginRecord(getOriginRecord(line));
            }
        }

        return cdr;

    }

    /**
     * Build and return a unique identifier from the CDR in order. To avoid importing twice the same CDR.
     * 
     * @param cdr : CDR object parsed
     * @return CDR's unique key
     */
    private String getOriginRecord(String cdr) {

        MessageDigest md = messageDigest.get();
        if (md != null) {
            md.reset();
            md.update(cdr.getBytes(Charset.forName("UTF8")));
            final byte[] resultByte = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < resultByte.length; ++i) {
                sb.append(Integer.toHexString((resultByte[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        }

        return null;
    }

    @Override
    public void init(String originBatch, JobInstance jobInstance) {
        batchName = originBatch;
    }

    @Override
    public Integer getNumberOfRecords() {
        return totalNumberOfRecords;
    }
}
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

import org.apache.logging.log4j.util.Strings;
import org.meveo.commons.encryption.EncryptionFactory;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.rating.CDR;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService.CDR_ORIGIN_ENUM;
import org.meveo.service.medina.impl.ICdrCsvReader;
import org.meveo.service.medina.impl.ICdrParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

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
public class MEVEOCdrReader implements ICdrCsvReader {

    static MessageDigest messageDigest = null;
    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Logger log = LoggerFactory.getLogger(MEVEOCdrReader.class);
            log.error("No message digest of type MD5", e);
        }
    }

    private String batchName;
    private String username;
    private CDR_ORIGIN_ENUM origin;
    private BufferedReader fileReader = null;

    private Integer totalNumberOfRecords;

    @Override
    public void init(File cdrFile) throws FileNotFoundException {
        batchName = "CDR_" + cdrFile.getName();
        this.origin = CDR_ORIGIN_ENUM.JOB;

        try {
            totalNumberOfRecords = FileUtils.countLines(cdrFile);
        } catch (IOException e) {
        }
        fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(cdrFile)));
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
    public CDR getNextRecord(ICdrParser cdrParser) throws IOException {
        if (fileReader == null) {
            return null;
        }
        String line = fileReader.readLine();

        return getRecord(cdrParser, line);
    }

    @Override
    public CDR getRecord(ICdrParser cdrParser, Object cdrData) {

        if (cdrData == null) {
            return null;
        }
        CDR cdr = null;
        try {
            cdr = cdrParser.parse(cdrData);
        } catch (CDRParsingException e) {
            cdr = new CDR();
            cdr.setRejectReasonException(e);

        } finally {
            // TODO Currently source field is not used when reprocessing a CDR - a line field is used instead
            // cdr.setSource(line);
            cdr.setLine((String) cdrData);
            cdr.setOriginBatch(batchName);
            if(Strings.isBlank(cdr.getOriginRecord()))
            	cdr.setOriginRecord(getOriginRecord((String) cdrData));
        }
        return cdr;
    }

    @Override
    public void close() throws IOException {
        if (fileReader != null) {
            fileReader.close();
        }
    }

    @Override
    public void init(String originBatch) {
        batchName = originBatch;

    }

    /**
     * Build and return a unique identifier from the CDR in order. To avoid importing twice the same CDR.
     * 
     * @param cdr : CDR object parsed
     * @return CDR's unique key
     */
    private String getOriginRecord(String cdr) {
System.out.println("getOriginRecord Test Thang");
        if (StringUtils.isBlank(username) || CDR_ORIGIN_ENUM.JOB == origin) {
            final byte[] resultByte = EncryptionFactory.digest(cdr.getBytes(StandardCharsets.UTF_8));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < resultByte.length; ++i) {
                sb.append(Integer.toHexString((resultByte[i] & 0xFF) | 0x100).substring(1, 3));
            }

System.out.println("sb.toString() Test Thang : " + sb.toString());
            return sb.toString();
        } else {
            return username + "_" + new Date().getTime();
        }

    }

    @Override
    public Integer getNumberOfRecords() {
        return totalNumberOfRecords;
    }
}
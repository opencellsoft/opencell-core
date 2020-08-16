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
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.parsers.FileParserBeanio;
import org.meveo.commons.parsers.RecordContext;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.rating.CDR;
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
@Named
public class MEVEOCdrFlatFileReader extends FileParserBeanio implements ICdrCsvReader {

    private static Logger log = LoggerFactory.getLogger(MEVEOCdrFlatFileReader.class);

    static MessageDigest messageDigest = null;
    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("No message digest of type MD5", e);
        }
    }

    private String batchName;
    private String username;
    private CDR_ORIGIN_ENUM origin;
    
    @Inject
    MEVEOCdrParser meveoCdrParser;

    @Override
    public void init(File cdrFile) throws FileNotFoundException {        
        batchName = "CDR_" + cdrFile.getName();
        this.origin = CDR_ORIGIN_ENUM.JOB;       
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
    public synchronized CDR getNextRecord(ICdrParser cdrParser) throws IOException {
        RecordContext recordContext = getNextRecord();
        CDR cdr = cdrParser.parse(recordContext);
        if(cdr == null) {
            return null;
        }
        String line = recordContext.getLineContent();
        cdr.setLine(line);
        cdr.setOriginBatch(batchName);
        cdr.setOriginRecord(getOriginRecord(line));
        return cdr;
    }
    
    
    
    /**
     * Build and return a unique identifier from the CDR in order. To avoid importing twice the same CDR.
     * 
     * @param cdr : CDR object parsed
     * @return CDR's unique key
     */
    private String getOriginRecord(String cdr) {

        if (StringUtils.isBlank(username) || CDR_ORIGIN_ENUM.JOB == origin) {

            if (messageDigest != null) {
                synchronized (messageDigest) {
                    messageDigest.reset();
                    messageDigest.update(cdr.getBytes(Charset.forName("UTF8")));
                    final byte[] resultByte = messageDigest.digest();
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < resultByte.length; ++i) {
                        sb.append(Integer.toHexString((resultByte[i] & 0xFF) | 0x100).substring(1, 3));
                    }
                    return sb.toString();
                }
            }
        } else {
            return username + "_" + new Date().getTime();
        }

        return null;
    }

    @Override
    public void init(String originBatch) {
        batchName = originBatch;
    }

    @Override
    public List<CDR> getRecords(ICdrParser cdrParser, List<String> cdrLines) {
        // TODO Auto-generated method stub
        return null;
    }     
}
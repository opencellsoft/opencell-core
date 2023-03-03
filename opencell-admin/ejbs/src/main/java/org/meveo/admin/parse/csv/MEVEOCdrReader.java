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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.mediation.CDRRejectionCauseEnum;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.service.billing.impl.EdrService;
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
public class MEVEOCdrReader implements ICdrCsvReader {

    private static Logger log = LoggerFactory.getLogger(MEVEOCdrReader.class);

    private EdrService edrService;

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
    private BufferedReader cdrReader = null;

    @Override
    public void init(File cdrFile) throws FileNotFoundException {
        batchName = "CDR_" + cdrFile.getName();
        this.origin = CDR_ORIGIN_ENUM.JOB;
        cdrReader = new BufferedReader(new InputStreamReader(new FileInputStream(cdrFile)));
        edrService = (EdrService) EjbUtils.getServiceInterface(EdrService.class.getSimpleName());
    }

    @Override
    public void init(String user, String ip) {
        this.batchName = "API_" + ip;
        this.origin = CDR_ORIGIN_ENUM.API;
        this.username = user;
        edrService = (EdrService) EjbUtils.getServiceInterface(EdrService.class.getSimpleName());
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
        if(cdrReader == null) {
            return null;
        }
        String line = cdrReader.readLine();
        CDR cdr = cdrParser.parse(line);
        if(cdr == null) {
            return null;
        }
        cdr.setOriginBatch(batchName);
        cdr.setOriginRecord(getOriginRecord(line));
        
        if (edrService.isMemoryDuplicateFound(cdr.getOriginBatch(), cdr.getOriginRecord())) {
            cdr.setStatus(CDRStatusEnum.ERROR);
            cdr.setRejectReason(CDRRejectionCauseEnum.DUPLICATE.toString());
        }
        return cdr;
    }
        
    @Override
    public void close() throws IOException {
        if (cdrReader != null) {
            cdrReader.close();
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
    public List<CDR> getRecords(ICdrParser cdrParser, List<String> cdrLines) {
        List<CDR> cdrs = new ArrayList<CDR>();
        for (String line : cdrLines) {
            CDR cdr = cdrParser.parse(line);
            cdr.setOriginBatch(batchName);
            cdr.setOriginRecord(getOriginRecord(line));
            cdrs.add(cdr);
        }
        return cdrs;
    }
}
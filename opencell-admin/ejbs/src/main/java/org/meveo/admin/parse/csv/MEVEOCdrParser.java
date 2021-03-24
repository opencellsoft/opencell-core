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
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.rating.CDR;
import org.meveo.service.medina.impl.CDRParsingService.CDR_ORIGIN_ENUM;
import org.meveo.service.medina.impl.CSVCDRParser;
import org.meveo.service.medina.impl.InvalidAccessException;
import org.meveo.service.medina.impl.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A default CDR file parser
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 */
@Named
public class MEVEOCdrParser implements CSVCDRParser {

    private static Logger log = LoggerFactory.getLogger(MEVEOCdrParser.class);

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
    private BufferedReader cdrReader = null;
    private CDR_ORIGIN_ENUM origin;

    @Override
    public void init(File cdrFile) throws FileNotFoundException {
        batchName = "CDR_" + cdrFile.getName();
        cdrReader = new BufferedReader(new InputStreamReader(new FileInputStream(cdrFile)));
    }

    @Override
    public void initByApi(String username, String ip) {
        this.batchName = "API_" + ip;
        this.username = username;
    }

    public String getBatchName() {
        return batchName;
    }

    @Override
    public synchronized CDR getNextRecord() throws IOException {

        return parseCDR(cdrReader.readLine());
    }

    @Override
    public CDR parseCDR(String line) {

        if (line == null) {
            return null;
        }

        CDR cdr = new CDR();
        cdr.setLine(line);
        try {
            String[] fields = line.split(";");
            if (fields.length == 0) {
                throw new InvalidFormatException(line, "record empty");

            } else if (fields.length < 4) {
                throw new InvalidFormatException(line, "only " + fields.length + " in the record");

            } else {

                DateTimeFormatter formatter1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                DateTimeFormatter formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

                try {
                    DateTime dt = formatter1.parseDateTime(fields[0]);
                    cdr.setEventDate(new Date(dt.getMillis()));
                } catch (Exception e1) {
                    DateTime dt = formatter2.parseDateTime(fields[0]);
                    cdr.setEventDate(new Date(dt.getMillis()));
                }
                cdr.setQuantity(new BigDecimal(fields[1]));

                cdr.setAccessCode(fields[2]);

                cdr.setParameter1(fields[3]);
                if (fields.length <= 4) {
                    cdr.setParameter2(null);
                } else {
                    cdr.setParameter2(fields[4]);
                }
                if (fields.length <= 5) {
                    cdr.setParameter3(null);
                } else {
                    cdr.setParameter3(fields[5]);
                }
                if (fields.length <= 6) {
                    cdr.setParameter4(null);
                } else {
                    cdr.setParameter4(fields[6]);
                }
                if (fields.length <= 7) {
                    cdr.setParameter5(null);
                } else {
                    cdr.setParameter5(fields[7]);
                }
                if (fields.length <= 8) {
                    cdr.setParameter6(null);
                } else {
                    cdr.setParameter6(fields[8]);
                }
                if (fields.length <= 9) {
                    cdr.setParameter7(null);
                } else {
                    cdr.setParameter7(fields[9]);
                }
                if (fields.length <= 10) {
                    cdr.setParameter8(null);
                } else {
                    cdr.setParameter8(fields[10]);
                }
                if (fields.length <= 11) {
                    cdr.setParameter9(null);
                } else {
                    cdr.setParameter9(fields[11]);
                }

                if (fields.length <= 12 || "".equals(fields[12])) {
                    cdr.setDateParam1(null);
                } else {
                    try {
                        DateTime dt = formatter1.parseDateTime(fields[12]);
                        cdr.setDateParam1(new Date(dt.getMillis()));
                    } catch (Exception e1) {
                        DateTime dt = formatter2.parseDateTime(fields[12]);
                        cdr.setDateParam1(new Date(dt.getMillis()));
                    }
                }
                if (fields.length <= 13 || "".equals(fields[13])) {
                    cdr.setDateParam2(null);
                } else {
                    try {
                        DateTime dt = formatter1.parseDateTime(fields[13]);
                        cdr.setDateParam2(new Date(dt.getMillis()));
                    } catch (Exception e1) {
                        DateTime dt = formatter2.parseDateTime(fields[13]);
                        cdr.setDateParam2(new Date(dt.getMillis()));
                    }
                }
                if (fields.length <= 14 || "".equals(fields[14])) {
                    cdr.setDateParam3(null);
                } else {
                    try {
                        DateTime dt = formatter1.parseDateTime(fields[14]);
                        cdr.setDateParam3(new Date(dt.getMillis()));
                    } catch (Exception e1) {
                        DateTime dt = formatter2.parseDateTime(fields[14]);
                        cdr.setDateParam3(new Date(dt.getMillis()));
                    }
                }
                if (fields.length <= 15 || "".equals(fields[15])) {
                    cdr.setDateParam4(null);
                } else {
                    try {
                        DateTime dt = formatter1.parseDateTime(fields[15]);
                        cdr.setDateParam4(new Date(dt.getMillis()));
                    } catch (Exception e1) {
                        DateTime dt = formatter2.parseDateTime(fields[15]);
                        cdr.setDateParam4(new Date(dt.getMillis()));
                    }
                }
                if (fields.length <= 16 || "".equals(fields[16])) {
                    cdr.setDateParam5(null);
                } else {
                    try {
                        DateTime dt = formatter1.parseDateTime(fields[16]);
                        cdr.setDateParam5(new Date(dt.getMillis()));
                    } catch (Exception e1) {
                        DateTime dt = formatter2.parseDateTime(fields[16]);
                        cdr.setDateParam5(new Date(dt.getMillis()));
                    }
                }
                if (fields.length <= 17 || "".equals(fields[17])) {
                    cdr.setDecimalParam1(null);
                } else {
                    cdr.setDecimalParam1(new BigDecimal(fields[17]));
                }
                if (fields.length <= 18 || "".equals(fields[18])) {
                    cdr.setDecimalParam2(null);
                } else {
                    cdr.setDecimalParam2(new BigDecimal(fields[18]));
                }
                if (fields.length <= 19 || "".equals(fields[19])) {
                    cdr.setDecimalParam3(null);
                } else {
                    cdr.setDecimalParam3(new BigDecimal(fields[19]));
                }
                if (fields.length <= 20 || "".equals(fields[20])) {
                    cdr.setDecimalParam4(null);
                } else {
                    cdr.setDecimalParam4(new BigDecimal(fields[20]));
                }
                if (fields.length <= 21 || "".equals(fields[21])) {
                    cdr.setDecimalParam5(null);
                } else {
                    cdr.setDecimalParam5(new BigDecimal(fields[21]));
                }

                if (fields.length <= 22 || "".equals(fields[22])) {
                    cdr.setExtraParameter(null);
                } else {
                    cdr.setExtraParameter(fields[22]);
                }
            }

            cdr.setOriginBatch(batchName);
            cdr.setOriginRecord(getOriginRecord(line));

            if (cdr.getAccessCode() == null || cdr.getAccessCode().trim().length() == 0) {
                cdr.setRejectReasonException(new InvalidAccessException(line, "userId is empty"));
            }

        } catch (Exception e) {
            cdr.setRejectReasonException(new InvalidFormatException(line, e));
            cdr.setRejectReason(e.getMessage());
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
    public void close() throws IOException {
        if (cdrReader != null) {
            cdrReader.close();
        }
    }
}
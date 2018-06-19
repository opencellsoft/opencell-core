package org.meveo.admin.parse.csv;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.meveo.commons.utils.StringUtils;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.CSVCDRParser;
import org.meveo.service.medina.impl.InvalidAccessException;
import org.meveo.service.medina.impl.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @lastModifiedVersion willBeSetLater
 * @author Edward P. Legaspi To test CdrParserProducer uncomment @CdrParser annotation.
 **/
@Named
public class CustomCdrParser implements CSVCDRParser {

    private static Logger log = LoggerFactory.getLogger(CustomCdrParser.class);

    DateTimeFormatter formatter1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    DateTimeFormatter formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static MessageDigest messageDigest = null;
    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("No message digest of type MD5", e);
        }
    }

    private String batchName;
    private Map<String, String> originBatch;
    private String username;

    @Override
    public void init(File CDRFile) {
        batchName = "CDR_" + CDRFile.getName();
    }

    @Override
    public void initByApi(String username, String ip) {
        if (originBatch == null) {
            originBatch = new HashMap<>();
        }
        originBatch.put(CDRParsingService.CDR_ORIGIN_API, "API_" + ip);
        this.username = username;
    }

    @Override
    public Map<String, String> getOriginBatch() {
        if (StringUtils.isBlank(originBatch.get(CDRParsingService.CDR_ORIGIN_JOB))) {
            originBatch.put(CDRParsingService.CDR_ORIGIN_JOB, batchName == null ? "CDR_CONS_CSV" : batchName);
        }
        return originBatch;
    }

    @Override
    public CDR getCDR(String line, String origin) throws InvalidFormatException {
        CDR cdr = new CDR();
        try {
            String[] fields = line.split(";");
            if (fields.length == 0) {
                throw new InvalidFormatException(line, "record empty");
            } else if (fields.length < 4) {
                throw new InvalidFormatException(line, "only " + fields.length + " in the record");
            } else {
                try {
                    DateTime dt = formatter1.parseDateTime(fields[0]);
                    cdr.setTimestamp(new Date(dt.getMillis()));
                } catch (Exception e1) {
                    DateTime dt = formatter2.parseDateTime(fields[0]);
                    cdr.setTimestamp(new Date(dt.getMillis()));
                }
                cdr.setQuantity(new BigDecimal(fields[1]));

                cdr.setAccess_id(fields[2]);
                if (cdr.getAccess_id() == null) {
                    throw new InvalidAccessException(line, "userId is empty");
                }
                cdr.setParam1(fields[3]);
                if (fields.length <= 4) {
                    cdr.setParam2(null);
                } else {
                    cdr.setParam2(fields[4]);
                }
                if (fields.length <= 5) {
                    cdr.setParam3(null);
                } else {
                    cdr.setParam3(fields[5]);
                }
                if (fields.length <= 6) {
                    cdr.setParam4(null);
                } else {
                    cdr.setParam4(fields[6]);
                }
            }
            
            cdr.setOriginBatch(getOriginBatch().get(origin));
            cdr.setOriginRecord(getOriginRecord(cdr, origin));
            cdr.setLine(line);

        } catch (Exception e) {
            throw new InvalidFormatException(line, e.getMessage());
        }
        return cdr;
    }

    @Override
    public String getOriginRecord(CDR object, String origin) {
        String result = null;
        if (StringUtils.isBlank(username) || origin.equals(CDRParsingService.CDR_ORIGIN_JOB)) {
            CDR cdr = (CDR) object;
            result = cdr.toString();

            if (messageDigest != null) {
                synchronized (messageDigest) {
                    messageDigest.reset();
                    messageDigest.update(result.getBytes(Charset.forName("UTF8")));
                    final byte[] resultByte = messageDigest.digest();
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < resultByte.length; ++i) {
                        sb.append(Integer.toHexString((resultByte[i] & 0xFF) | 0x100).substring(1, 3));
                    }
                    result = sb.toString();
                }
            }
        } else {
            return username + "_" + new Date().getTime();
        }

        return result;
    }

    @Override
    public String getAccessUserId(CDR cdr) throws InvalidAccessException {
        String result = cdr.getAccess_id();
        if (result == null || result.trim().length() == 0) {
            throw new InvalidAccessException(cdr);
        }
        /*
         * if(((CDR)cdr).service_id!=null && (((CDR)cdr).service_id.length()>0) ){ result+="_"+((CDR)cdr).service_id; }
         */
        return result;
    }

    @Override
    public String getCDRLine(CDR cdr, String reason) {
        return ((CDR) cdr).toString() + ";" + reason;
    }
}
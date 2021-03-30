package org.meveo.admin.parse.csv;

import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateful;
import javax.inject.Inject;

import org.meveo.commons.parsers.RecordContext;
import org.meveo.model.rating.CDR;
import org.meveo.service.medina.impl.CDRParsingService.CDR_ORIGIN_ENUM;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.ICdrReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mohammed Amine Tazi
 */
@Stateful
public class CDRReprocessingReader implements ICdrReader, Serializable {

    private static final long serialVersionUID = 6112779360487934487L;

    private static Logger log = LoggerFactory.getLogger(CDRReprocessingReader.class);

    @Inject
    private CDRService cdrService;

    private String batchName;
    private String username;
    private CDR_ORIGIN_ENUM origin;
    private Iterator<CDR> cdrReader;

    private int totalNumberOfRecords = 0;

    static MessageDigest messageDigest = null;
    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.error("No message digest of type MD5", e);
        }
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
        if (cdrReader == null || cdrReader.hasNext() == false) {
            return null;
        }
        CDR cdr = cdrReader.next();
        String originRecord = cdr.getOriginRecord();
        String line = cdr.getLine();
        Integer timesTried = cdr.getTimesTried() == null ? 1 : cdr.getTimesTried() + 1;
        cdr = cdrParser.parse(cdr.getLine());
        cdr.setLine(line);
        cdr.setOriginRecord(originRecord);
        cdr.setTimesTried(timesTried);
        cdr.setOriginBatch(batchName);
        return cdr;
    }

    @Override
    public void init(String originBatch) {
        batchName = originBatch;
        try {
            List<CDR> cdrs = cdrService.getCDRsToReprocess();
            totalNumberOfRecords = cdrs.size();
            cdrReader = cdrs.iterator();
        } catch (Exception ex) {
            log.error("Failed to read cdrs to reprocess from DB", ex);
            throw (ex);
        }
    }

    @Override
    public Integer getNumberOfRecords() {
        return totalNumberOfRecords;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public List<CDR> getRecords(ICdrParser cdrParser, List<String> cdrLines) {
        List<CDR> parsedCdrs = new ArrayList<CDR>();
        List<CDR> cdrs = cdrService.getCDRsToReprocess();
        CDR parsedCdr;
        Object record;
        for (CDR cdr : cdrs) {
            if (cdr.getType() != null && cdr.getSource() != null) {
                record = RecordContext.deserializeRecord(cdr.getType(), cdr.getSource());
            } else { // This case should never happen, but it's added to manage the old data
                record = cdr.getLine();
            }
            parsedCdr = cdrParser.parse(record);
            if (parsedCdr != null) {
                cdr.setTimesTried(cdr.getTimesTried() == null ? 1 : cdr.getTimesTried() + 1);
                cdr.setRejectReason(parsedCdr.getRejectReason());
                cdr.setRejectReasonException(parsedCdr.getRejectReasonException());
                cdr.setStatus(parsedCdr.getStatus());
                cdr.setSource(parsedCdr.getSource());
                cdr.setType(parsedCdr.getType());
                cdr.setOriginBatch(batchName);
                parsedCdrs.add(cdr);
            }

        }
        return parsedCdrs;
    }
}
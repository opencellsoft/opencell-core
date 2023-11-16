package org.meveo.admin.parse.csv;

import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.meveo.admin.async.SynchronizedIterator;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.meveo.service.job.ScopedJob;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService.CDR_ORIGIN_ENUM;
import org.meveo.service.medina.impl.CDRService;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.ICdrReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mohammed Amine Tazi
 */
@Singleton
@Lock(LockType.READ)
public class CDRReprocessingReader implements ICdrReader, Serializable {

    private static final long serialVersionUID = 6112779360487934487L;

    private static Logger log = LoggerFactory.getLogger(CDRReprocessingReader.class);

    @Inject
    private CDRService cdrService;
    @Inject
    private JobInstanceService jobInstanceService;

    private String batchName;
    private String username;
    private CDR_ORIGIN_ENUM origin;
    private Iterator<CDR> cdrIterator;

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
    public synchronized CDR getNextRecord(ICdrParser cdrParser, String originRecordEL) throws IOException {

        CDR cdr = cdrIterator.next();
        if (cdr == null) {
            return null;
        }

        return getRecord(cdrParser, cdr, originRecordEL);
    }

    @Override
    public CDR getRecord(ICdrParser cdrParser, Object cdrData, String originRecordEL) {

        CDR cdr = (CDR) cdrData;
        Integer timesTried = cdr.getTimesTried() == null ? 1 : cdr.getTimesTried() + 1;
        try {
            CDR newCdr = cdrParser.parse(cdr.getLine());
            cdr.fillFrom(newCdr);
            cdr.setTimesTried(timesTried);
            cdr.setOriginBatch(cdr.getOriginBatch() + "_" + batchName);
            cdr.setRejectReasonException(null);
            cdr.setRejectReason(null);
            cdr.setStatus(CDRStatusEnum.OPEN);

        } catch (CDRParsingException e) {
            cdr.setRejectReasonException(e);
        }

        return cdr;
    }

    @Override
    public void init(String originBatch, JobInstance jobInstance) {
        batchName = originBatch;
        try {
            Integer jobItemsLimit = jobInstanceService.getJobItemsLimit(jobInstance);
            List<CDR> cdrs = cdrService.getCDRsToReprocess(jobItemsLimit != null ? jobItemsLimit : 0);
            totalNumberOfRecords = cdrs.size();
            cdrIterator = new SynchronizedIterator<CDR>(cdrs);
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
}
package org.meveo.service.medina.impl;

import java.io.File;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.admin.parse.csv.CdrParserProducer;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.EdrService;

/**
 * Takes care of parsing and converting CDRS to EDR records
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 */
@Singleton
public class CDRParsingService extends PersistenceService<EDR> {

    private CSVCDRParser cdrParser;

    @Inject
    private EdrService edrService;

    @Inject
    private AccessService accessService;

    @Inject
    @RejectedCDR
    private Event<Serializable> rejectededCdrEventProducer;

    @Inject
    private CdrParserProducer cdrParserProducer;

    /**
     * Indicates that CDR came via API
     */
    public static final String CDR_ORIGIN_API = "API";

    /**
     * Indicates that CDR was read from a file
     */
    public static final String CDR_ORIGIN_JOB = "JOB";

    /**
     * Initiate CDR file processing from a file
     * 
     * @param CDRFile CDR file to process
     * @throws BusinessException General business exception
     */
    public void init(File CDRFile) throws BusinessException {
        cdrParser = cdrParserProducer.getParser();
        cdrParser.init(CDRFile);
    }

    /**
     * Initiate CDR file processing from API
     * 
     * @param username Username
     * @param ip Ip address
     * @throws BusinessException General business exception
     */
    public void initByApi(String username, String ip) throws BusinessException {
        cdrParser = cdrParserProducer.getParser();
        cdrParser.initByApi(username, ip);
    }

    /**
     * Get a source of CDR record
     * 
     * @param origin Origin of CDR record
     * @return A source of CDR record
     */
    public String getOriginBatch(String origin) {
        return cdrParser.getOriginBatch().get(origin);
    }

    /*
     * public void resetAccessPointCache(Access access) { List<Access> accesses = null; if (MeveoCacheContainerProvider.getAccessCache().containsKey(access.getAccessUserId())) {
     * accesses = MeveoCacheContainerProvider.getAccessCache().get(access.getAccessUserId()); boolean found = false; for (Access cachedAccess : accesses) { if
     * ((access.getSubscription().getId() != null && access.getSubscription().getId() .equals(cachedAccess.getSubscription().getId())) || (cachedAccess.getSubscription().getCode()
     * != null && cachedAccess.getSubscription() .getCode().equals(access.getSubscription().getCode()))) { cachedAccess.setStartDate(access.getStartDate());
     * cachedAccess.setEndDate(access.getEndDate()); found = true; break; } } if (!found) { accesses.add(access); } } else { accesses = new ArrayList<Access>();
     * accesses.add(access); MeveoCacheContainerProvider.getAccessCache().put(access.getAccessUserId(), accesses); } }
     */

    /**
     * Convert a single CDR line to a list of EDRs
     * 
     * @param line Line to convert
     * @param origin Source of CDR record
     * @return A list of EDRs
     * @throws CDRParsingException Any parsing exception
     */
    public List<EDR> getEDRList(String line, String origin) throws CDRParsingException {
        List<EDR> result = new ArrayList<EDR>();
        CDR cdr = cdrParser.getCDR(line, origin);
        deduplicate(cdr);
        List<Access> accessPoints = accessPointLookup(cdr);

        boolean foundMatchingAccess = false;

        for (Access accessPoint : accessPoints) {
            if ((accessPoint.getStartDate() == null || accessPoint.getStartDate().getTime() <= cdr.getTimestamp().getTime())
                    && (accessPoint.getEndDate() == null || accessPoint.getEndDate().getTime() > cdr.getTimestamp().getTime())) {
                foundMatchingAccess = true;
                EDR edr = cdrToEdr(cdr, accessPoint, null);
                result.add(edr);
            }
        }

        if (!foundMatchingAccess) {
            throw new InvalidAccessException(cdr);
        }

        return result;
    }

    /**
     * Convert a single CDR line to an EDR record, linked to a subscription
     * 
     * @param line CDR line
     * @param origin Source of CDR record
     * @param subscription Subscription to link to
     * @return An EDR record
     * @throws CDRParsingException Any parsing exception
     */
    public EDR getEDRForVirtual(String line, String origin, Subscription subscription) throws CDRParsingException {

        CDR cdr = cdrParser.getCDR(line, origin);
        EDR edr = cdrToEdr(cdr, null, subscription);

        return edr;
    }

    /**
     * Convert CDR to EDR
     * 
     * @param cdr CDR to convert
     * @param accessPoint Access point to bind to
     * @param subscription Subscription to bind to
     * @return EDR
     */
    private EDR cdrToEdr(CDR cdr, Access accessPoint, Subscription subscription) {
        EDR edr = new EDR();
        edr.setCreated(new Date());
        edr.setEventDate(cdr.getTimestamp());
        edr.setOriginBatch(cdr.getOriginBatch());
        edr.setOriginRecord(cdr.getOriginRecord());
        edr.setParameter1(cdr.getParam1());
        edr.setParameter2(cdr.getParam2());
        edr.setParameter3(cdr.getParam3());
        edr.setParameter4(cdr.getParam4());
        edr.setParameter5(cdr.getParam5());
        edr.setParameter6(cdr.getParam6());
        edr.setParameter7(cdr.getParam7());
        edr.setParameter8(cdr.getParam8());
        edr.setParameter9(cdr.getParam9());
        edr.setDateParam1(cdr.getDateParam1());
        edr.setDateParam2(cdr.getDateParam2());
        edr.setDateParam3(cdr.getDateParam3());
        edr.setDateParam4(cdr.getDateParam4());
        edr.setDateParam5(cdr.getDateParam5());
        edr.setDecimalParam1(cdr.getDecimalParam1() != null ? cdr.getDecimalParam1().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : null);
        edr.setDecimalParam2(cdr.getDecimalParam2() != null ? cdr.getDecimalParam2().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : null);
        edr.setDecimalParam3(cdr.getDecimalParam3() != null ? cdr.getDecimalParam3().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : null);
        edr.setDecimalParam4(cdr.getDecimalParam4() != null ? cdr.getDecimalParam4().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : null);
        edr.setDecimalParam5(cdr.getDecimalParam5() != null ? cdr.getDecimalParam5().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP) : null);
        edr.setQuantity(cdr.getQuantity().setScale(BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP));
        edr.setStatus(EDRStatusEnum.OPEN);
        edr.setExtraParameter(cdr.getExtraParam());

        if (accessPoint != null) {
            edr.setSubscription(getEntityManager().getReference(Subscription.class, accessPoint.getSubscription().getId()));
            edr.setAccessCode(accessPoint.getAccessUserId());
        } else if (subscription != null) {
            edr.setSubscription(subscription);
        }

        return edr;
    }

    /**
     * Check if CDR was processed already by comparing Origin record/digest values
     * 
     * @param cdr CDR to check
     * @throws DuplicateException CDR was processed already
     */
    private void deduplicate(CDR cdr) throws DuplicateException {
        if (edrService.isDuplicateFound(cdrParser.getOriginBatch().get(CDR_ORIGIN_JOB), cdr.getOriginRecord())) {
            throw new DuplicateException(cdr);
        }
    }

    /**
     * Get a list of Access points CDR corresponds to
     * 
     * @param cdr CDR
     * @return A list of Access points
     * @throws InvalidAccessException No Access point was matched
     */
    private List<Access> accessPointLookup(CDR cdr) throws InvalidAccessException {
        String accessUserId = cdrParser.getAccessUserId(cdr);
        List<Access> accesses = accessService.getActiveAccessByUserId(accessUserId);
        if (accesses == null || accesses.size() == 0) {
            rejectededCdrEventProducer.fire(cdr);
            throw new InvalidAccessException(cdr);
        }
        return accesses;
    }

    /**
     * Construct a line from an original CDR line plus a failure reason
     * 
     * @param cdr CDR
     * @param reason Failure reason to append
     * @return A CDR line
     */
    public String getCDRLine(CDR cdr, String reason) {
        return cdrParser.getCDRLine(cdr, reason);
    }

    /**
     * Get a CDR parser implementation
     * 
     * @return CDR parser implementation
     */
    public CSVCDRParser getCdrParser() {
        return cdrParser;
    }
}
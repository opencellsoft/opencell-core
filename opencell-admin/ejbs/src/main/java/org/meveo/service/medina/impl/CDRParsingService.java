package org.meveo.service.medina.impl;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.CdrParserProducer;
import org.meveo.cache.CdrEdrProcessingCacheContainerProvider;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.EdrService;

@Singleton
public class CDRParsingService extends PersistenceService<EDR> {

    private CSVCDRParser cdrParser;

    @Inject
    private EdrService edrService;

    @Inject
    @RejectedCDR
    private Event<Serializable> rejectededCdrEventProducer;

    @Inject
    private CdrParserProducer cdrParserProducer;

    @Inject
    private CdrEdrProcessingCacheContainerProvider cdrEdrProcessingCacheContainerProvider;

    public static final String CDR_ORIGIN_API = "API";
    public static final String CDR_ORIGIN_JOB = "JOB";

    public void init(File CDRFile) throws BusinessException {
        cdrParser = cdrParserProducer.getParser();
        cdrParser.init(CDRFile);
    }

    public void initByApi(String username, String ip) throws BusinessException {
        cdrParser = cdrParserProducer.getParser();
        cdrParser.initByApi(username, ip);
    }

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

    public List<EDR> getEDRList(String line, String origin) throws CDRParsingException {
        List<EDR> result = new ArrayList<EDR>();
        Serializable cdr = cdrParser.getCDR(line);
        deduplicate(cdr);
        List<Access> accessPoints = accessPointLookup(cdr);
        
        EDRDAO edrDAO = cdrParser.getEDR(cdr, origin);
        
        boolean foundMatchingAccess = false;

        for (Access accessPoint : accessPoints) {
            if ((accessPoint.getStartDate() == null || accessPoint.getStartDate().getTime() <= edrDAO.getEventDate().getTime())
                    && (accessPoint.getEndDate() == null || accessPoint.getEndDate().getTime() > edrDAO.getEventDate().getTime())) {
                foundMatchingAccess = true;
                EDR edr = edrDaoToEdr(edrDAO, accessPoint, null);
                result.add(edr);
            }
        }

        if (!foundMatchingAccess) {
            throw new InvalidAccessException(cdr);
        }

        return result;
    }

    public EDR getEDRForVirtual(String line, String origin, Subscription subscription) throws CDRParsingException {

        Serializable cdr = cdrParser.getCDR(line);
        EDRDAO edrDAO = cdrParser.getEDR(cdr, origin);
        EDR edr = edrDaoToEdr(edrDAO, null, subscription);

        return edr;
    }

    private EDR edrDaoToEdr(EDRDAO edrDAO, Access accessPoint, Subscription subscription) {
        EDR edr = new EDR();
        edr.setCreated(new Date());
        edr.setEventDate(edrDAO.getEventDate());
        edr.setOriginBatch(edrDAO.getOriginBatch());
        edr.setOriginRecord(edrDAO.getOriginRecord());
        edr.setParameter1(edrDAO.getParameter1());
        edr.setParameter2(edrDAO.getParameter2());
        edr.setParameter3(edrDAO.getParameter3());
        edr.setParameter4(edrDAO.getParameter4());
        edr.setParameter5(edrDAO.getParameter5());
        edr.setParameter6(edrDAO.getParameter6());
        edr.setParameter7(edrDAO.getParameter7());
        edr.setParameter8(edrDAO.getParameter8());
        edr.setParameter9(edrDAO.getParameter9());
        edr.setDateParam1(edrDAO.getDateParam1());
        edr.setDateParam2(edrDAO.getDateParam2());
        edr.setDateParam3(edrDAO.getDateParam3());
        edr.setDateParam4(edrDAO.getDateParam4());
        edr.setDateParam5(edrDAO.getDateParam5());
        edr.setDecimalParam1(edrDAO.getDecimalParam1());
        edr.setDecimalParam2(edrDAO.getDecimalParam2());
        edr.setDecimalParam3(edrDAO.getDecimalParam3());
        edr.setDecimalParam4(edrDAO.getDecimalParam4());
        edr.setDecimalParam5(edrDAO.getDecimalParam5());
        edr.setQuantity(edrDAO.getQuantity());
        edr.setStatus(EDRStatusEnum.OPEN);
        edr.setExtraParameter(edrDAO.getExtraParam());
        if (accessPoint != null) {
            edr.setSubscription(getEntityManager().getReference(Subscription.class, accessPoint.getSubscription().getId()));
            edr.setAccessCode(accessPoint.getAccessUserId());
        } else if (subscription != null) {
            edr.setSubscription(subscription);
        }

        return edr;
    }

    private void deduplicate(Serializable cdr) throws DuplicateException {
        if (edrService.duplicateFound(cdrParser.getOriginBatch().get(CDR_ORIGIN_JOB), cdrParser.getOriginRecord(cdr, CDR_ORIGIN_JOB))) {
            throw new DuplicateException(cdr);
        }
    }

    private List<Access> accessPointLookup(Serializable cdr) throws InvalidAccessException {
        String accessUserId = cdrParser.getAccessUserId(cdr);
        List<Access> accesses = cdrEdrProcessingCacheContainerProvider.getAccessesByAccessUserId(accessUserId);
        if (accesses == null || accesses.size() == 0) {
            rejectededCdrEventProducer.fire(cdr);
            throw new InvalidAccessException(cdr);
        }
        return accesses;
    }

    public String getCDRLine(Serializable cdr, String reason) {
        return cdrParser.getCDRLine(cdr, reason);
    }

    public CSVCDRParser getCdrParser() {
        return cdrParser;
    }

}

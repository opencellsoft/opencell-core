package org.meveo.service.medina.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.CDR;
import org.meveo.admin.parse.csv.CdrParser;
import org.meveo.admin.parse.csv.MEVEOCdrParser;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.mediation.Access;
import org.meveo.model.mediation.CDRRejectionCauseEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.EdrService;

/**
 * Takes care of parsing and converting CDRS to EDR records
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class CDRParsingService extends PersistenceService<EDR> {

    @Inject
    private EdrService edrService;

    @Inject
    private AccessService accessService;

    @Inject
    @RejectedCDR
    private Event<Serializable> rejectededCdrEventProducer;

    @Inject
    private BeanManager beanManager;

    /**
     * The default parser.
     */
    @Inject
    private MEVEOCdrParser meveoCdrParser;

    /**
     * Source of CDR record
     */
    public enum CDR_ORIGIN_ENUM {
        /**
         * Indicates that CDR came via API
         */
        API,
        /**
         * Indicates that CDR was read from a file
         */
        JOB
    }

    /**
     * Initiate CDR file processing from a file
     * 
     * @param cdrFile CDR file to process
     * @return CDR csv file parser
     * @throws BusinessException General business exception
     * @throws FileNotFoundException File was not found exception
     */
    public CSVCDRParser getCDRParser(File cdrFile) throws BusinessException, FileNotFoundException {
        CSVCDRParser cdrParser = getParser();
        cdrParser.init(cdrFile);
        return cdrParser;
    }

    /**
     * Initiate CDR file processing from API
     * 
     * @param username Username
     * @param ip Ip address
     * @return CDR csv file parser
     * @throws BusinessException General business exception
     */
    public CSVCDRParser getCDRParser(String username, String ip) throws BusinessException {
        CSVCDRParser cdrParser = getParser();
        cdrParser.initByApi(username, ip);
        return cdrParser;
    }

    /**
     * Creates the edr.
     *
     * @throws CDRParsingException the CDR parsing exception
     * @throws BusinessException the business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createEdrs(CDR cdr) throws CDRParsingException, BusinessException {
        List<EDR> edrs = getEDRList(cdr);
        if (edrs != null && edrs.size() > 0) {
            for (EDR edr : edrs) {
                createEdr(edr);
            }
        }
    }

    /**
     * Creates the edr.
     *
     * @param edr the edr
     * @throws BusinessException the business exception
     */
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createEdr(EDR edr) throws BusinessException {
        edrService.create(edr);
    }

    /**
     * Convert a single CDR line to a list of EDRs
     * 
     * @param cdr CDR to convert
     * @return A list of EDRs
     * @throws CDRParsingException Any parsing exception
     */
    public List<EDR> getEDRList(CDR cdr) throws CDRParsingException {

        deduplicate(cdr);
        List<EDR> edrs = new ArrayList<EDR>();
        List<Access> accessPoints = accessPointLookup(cdr);

        boolean foundMatchingAccess = false;

        for (Access accessPoint : accessPoints) {
            if ((accessPoint.getStartDate() == null || accessPoint.getStartDate().getTime() <= cdr.getTimestamp().getTime())
                    && (accessPoint.getEndDate() == null || accessPoint.getEndDate().getTime() > cdr.getTimestamp().getTime())) {
                foundMatchingAccess = true;
                EDR edr = cdrToEdr(cdr, accessPoint, null);
                if(edr.getSubscription().getStatus() == SubscriptionStatusEnum.RESILIATED) {
                    throw new InvalidAccessException(cdr, CDRRejectionCauseEnum.SUBSCRIPTION_TERMINATED);
                }else if(edr.getSubscription().getStatus() != SubscriptionStatusEnum.ACTIVE) {
                    throw new InvalidAccessException(cdr, CDRRejectionCauseEnum.SUBSCRIPTION_NOT_ACTIVATED);
                }
                edrs.add(edr);
            }
        }

        if (!foundMatchingAccess) {
            throw new InvalidAccessException(cdr, CDRRejectionCauseEnum.ACCESS_INVALID_DATE);
        }

        return edrs;
    }

    /**
     * Convert a CDR to an EDR record, linked to a subscription
     * 
     * @param cdr CDR record
     * @param subscription Subscription to link to
     * @return An EDR record
     * @throws CDRParsingException Any parsing exception
     */
    public EDR getEDRForVirtual(CDR cdr, Subscription subscription) throws CDRParsingException {

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
        if (edrService.isDuplicateFound(cdr.getOriginBatch(), cdr.getOriginRecord())) {
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

        List<Access> accesses = accessService.getActiveAccessByUserId(cdr.getAccess_id());
        if (accesses == null || accesses.size() == 0) {
            rejectededCdrEventProducer.fire(cdr);
            throw new InvalidAccessException(cdr);
        }
        return accesses;
    }

    @SuppressWarnings({ "unchecked", "serial" })
    private CSVCDRParser getParser() throws BusinessException {
        Set<Bean<?>> parsers = beanManager.getBeans(CSVCDRParser.class, new AnnotationLiteral<CdrParser>() {
        });

        if (parsers.size() > 1) {
            log.error("Multiple custom csv parsers encountered.");
            throw new BusinessException("Multiple custom csv parsers encountered.");

        } else if (parsers.size() == 1) {
            Bean<CSVCDRParser> bean = (Bean<CSVCDRParser>) parsers.toArray()[0];
            log.debug("Found custom cdr parser={}", bean.getBeanClass());
            try {
                CSVCDRParser parser = (CSVCDRParser) bean.getBeanClass().newInstance();
                return parser;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new BusinessException("Cannot instantiate custom cdr parser class=" + bean.getBeanClass().getName() + ".");
            }
        } else {
            log.debug("Use default cdr parser={}", meveoCdrParser.getClass());
            return meveoCdrParser;
        }
    }
}
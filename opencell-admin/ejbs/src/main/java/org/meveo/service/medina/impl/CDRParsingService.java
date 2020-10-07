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

package org.meveo.service.medina.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.MEVEOCdrParser;
import org.meveo.admin.parse.csv.MEVEOCdrReader;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.mediation.Access;
import org.meveo.model.mediation.CDRRejectionCauseEnum;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * Takes care of parsing and converting CDRS to EDR records
 * 
 * @lastModifiedVersion 9.0.1
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class CDRParsingService extends PersistenceService<EDR> {

	@Inject
	private EdrService edrService;

	@Inject
	private CDRService cdrService;

	@Inject
	private AccessService accessService;

	@Inject
	@RejectedCDR
	private Event<Serializable> rejectededCdrEventProducer;
	
	@Inject
	ScriptInstanceService scriptInstance;

	/**
	 * The default parser.
	 */
	@Inject
	private MEVEOCdrParser meveoCdrParser;
	
	/** The default cdr reader. */
	@Inject
    private MEVEOCdrReader meveoCdrReader;

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

	private boolean persistCDR = false;

	@PostConstruct
	private void init() {
		persistCDR = "true".equals(ParamBeanFactory.getAppScopeInstance().getProperty("mediation.persistCDR", "false"));

	}
	
	/**
     * Initiate CDR file processing from a file
     * 
     * @param cdrFile CDR file to process
     * @return CDR csv file parser
     * @throws BusinessException     General business exception
     * @throws FileNotFoundException File was not found exception
     */
    public ICdrCsvReader getCDRReader(File cdrFile) throws BusinessException, FileNotFoundException {
        return getCDRReaderByCode(cdrFile, null);
    }
    
    /**
     * Gets the CDR reader.
     *
     * @param cdrFile the cdr file
     * @param readerCode the reader code
     * @return the CDR reader
     * @throws BusinessException the business exception
     * @throws FileNotFoundException the file not found exception
     */
    public ICdrCsvReader getCDRReaderByCode(File cdrFile,String readerCode) throws BusinessException, FileNotFoundException {
        ICdrCsvReader cdrReader = getReader(readerCode);
        cdrReader.init(cdrFile);
        return cdrReader;
    }
    
    /**
     * Initiate CDR file processing from API
     * 
     * @param username Username
     * @param ip       Ip address
     * @return CDR csv file parser
     * @throws BusinessException General business exception
     */
    public ICdrCsvReader getCDRReader(String username, String ip) throws BusinessException {
        ICdrCsvReader cdrReader = getReader();
        cdrReader.init(username, ip);
        return cdrReader;
    }
    
    public org.meveo.service.medina.impl.ICdrParser getCDRParser(String customParser) throws BusinessException {
        return getParser(customParser);
    }

//    /**
//	 * Initiate CDR file processing from a file
//	 * 
//	 * @param cdrFile CDR file to process
//	 * @return CDR csv file parser
//	 * @throws BusinessException     General business exception
//	 * @throws FileNotFoundException File was not found exception
//	 */
//	public CSVCDRParser getCDRParser(File cdrFile) throws BusinessException, FileNotFoundException {
//		CSVCDRParser cdrParser = getParser();
//		cdrParser.init(cdrFile);
//		return cdrParser;
//	}
//
//	/**
//	 * Initiate CDR file processing from API
//	 * 
//	 * @param username Username
//	 * @param ip       Ip address
//	 * @return CDR csv file parser
//	 * @throws BusinessException General business exception
//	 */
//	public org.meveo.service.medina.impl.CdrParser getCDRParser(String username, String ip) throws BusinessException {
//	    org.meveo.service.medina.impl.CdrParser cdrParser = getParser();
//		cdrParser.initByApi(username, ip);
//		return cdrParser;
//	}

	/**
	 * Creates the edr.
	 *
	 * @param line the line
	 * @throws CDRParsingException the CDR parsing exception
	 * @throws BusinessException   the business exception
	 */
	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createEdrs(CDR cdr) throws CDRParsingException, BusinessException {
		List<EDR> edrs = getEDRList(cdr);
		if (edrs != null && edrs.size() > 0) {
			for (EDR edr : edrs) {
				createEdr(edr, cdr);
			}
		}
	}
	
	@JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createEdrs(List<EDR> edrs, CDR cdr) throws CDRParsingException, BusinessException {
        if (edrs != null && edrs.size() > 0) {
            createEdr(edrs.get(0),cdr);
            for (int i = 1; i < edrs.size(); i++) {
                createEdr(edrs.get(i));
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
		createEdr(edr, null);
	}

	@JpaAmpNewTx
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createEdr(EDR edr, CDR cdr) throws BusinessException {
		edrService.create(edr);
		if (cdr != null && persistCDR) {
			cdr.setHeaderEDR(edr);
			cdr.setStatus(CDRStatusEnum.PROCESSED);
			//once the cdr is processed, we don't need the serialized dto
			cdr.setSource(null);
			cdr.setType(null);
			if(cdr.getId() == null) {
			    cdrService.create(cdr);
			} else {
			    cdrService.update(cdr);
			}			
		}
	}

	/**
	 * Convert a single CDR line to a list of EDRs
	 * 
	 * @param cdr CDR to convert
	 * @return A list of EDRs
	 * @throws CDRParsingException Any parsing exception
	 */
	public List<EDR> getEDRList(CDR cdr) throws CDRParsingException {
		try {
			if (cdr.getRejectReason() != null) {
				throw (CDRParsingException) cdr.getRejectReasonException();
			}

			deduplicate(cdr);
			List<EDR> edrs = new ArrayList<EDR>();
			List<Access> accessPoints = accessPointLookup(cdr);

			boolean foundMatchingAccess = false;

			for (Access accessPoint : accessPoints) {
				if ((accessPoint.getStartDate() == null || accessPoint.getStartDate().getTime() <= cdr.getEventDate().getTime())
						&& (accessPoint.getEndDate() == null || accessPoint.getEndDate().getTime() > cdr.getEventDate().getTime())) {
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

			if (persistCDR) {
				cdrService.create(cdr);
			}

			return edrs;
		} catch (CDRParsingException e) {
			if (persistCDR) {
				cdr.setStatus(CDRStatusEnum.ERROR);
				cdr.setRejectReason(e.getMessage());
				cdrService.create(cdr);
			}
			throw e;
		}
	}

	/**
	 * Convert a CDR to an EDR record, linked to a subscription
	 * 
	 * @param cdr          CDR record
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
	 * @param cdr          CDR to convert
	 * @param accessPoint  Access point to bind to
	 * @param subscription Subscription to bind to
	 * @return EDR
	 */
	private EDR cdrToEdr(CDR cdr, Access accessPoint, Subscription subscription) {
		EDR edr = new EDR();
		edr.setCreated(new Date());
		edr.setEventDate(cdr.getEventDate());
		edr.setOriginBatch(cdr.getOriginBatch());
		edr.setOriginRecord(cdr.getOriginRecord());
		edr.setParameter1(cdr.getParameter1());
		edr.setParameter2(cdr.getParameter2());
		edr.setParameter3(cdr.getParameter3());
		edr.setParameter4(cdr.getParameter4());
		edr.setParameter5(cdr.getParameter5());
		edr.setParameter6(cdr.getParameter6());
		edr.setParameter7(cdr.getParameter7());
		edr.setParameter8(cdr.getParameter8());
		edr.setParameter9(cdr.getParameter9());
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
	public List<Access> accessPointLookup(CDR cdr) throws InvalidAccessException {

		List<Access> accesses = accessService.getActiveAccessByUserId(cdr.getAccessCode());
		if (accesses == null || accesses.size() == 0) {
			rejectededCdrEventProducer.fire(cdr);
			throw new InvalidAccessException(cdr, CDRRejectionCauseEnum.ACCESS_NOT_FOUND);
		}
		return accesses;
	}

	/**
	 * Gets the parser.
	 *
	 * @param customParser the custom parser: the class name of the custom parser
	 * @return the parser
	 * @throws BusinessException the business exception
	 */
	private ICdrParser getParser(String customParser) throws BusinessException {
	    if(customParser != null) {
	        return (ICdrParser) EjbUtils.getServiceInterface(customParser);
	    } else {
	        log.debug("Use default cdr parser={}", meveoCdrParser.getClass());
            return meveoCdrParser;
	    }
	}
		
    /**
     * Gets the reader.
     *
     * @return the reader
     * @throws BusinessException the business exception
     */
    private ICdrCsvReader getReader() throws BusinessException {
        return getReader(null);
    }
	
	
    /**
     * Gets the reader.
     *
     * @param readerCode the reader code
     * @return the reader
     * @throws BusinessException the business exception
     */
    private ICdrCsvReader getReader(String readerCode) throws BusinessException {
        if (readerCode != null) {
            return (ICdrCsvReader) EjbUtils.getServiceInterface(readerCode);
        } else {
            log.debug("Use default cdr reader={}", meveoCdrReader.getClass());
            return meveoCdrReader;
        }
    }
    public void cleanReprocessedCDR(CDR cdr) {
        getEntityManager().createNamedQuery("CDR.cleanReprocessedCDR").setParameter("originRecord", cdr.getOriginRecord()).executeUpdate();                    
    }
}
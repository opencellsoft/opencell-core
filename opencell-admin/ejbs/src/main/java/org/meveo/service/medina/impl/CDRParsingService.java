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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.parse.csv.MEVEOCdrParser;
import org.meveo.admin.parse.csv.MEVEOCdrReader;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.base.PersistenceService;

/**
 * Takes care of parsing and converting CDRS to EDR records
 * 
 * @lastModifiedVersion 9.0.1
 * 
 * @author Andrius Karpavicius
 */

public class CDRParsingService extends PersistenceService<EDR> {

    @Inject
    private CDRService cdrService;
    
    @Inject
    private AccessService accessService;

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
    private void initialize() {
        persistCDR = "true".equals(ParamBeanFactory.getAppScopeInstance().getProperty("mediation.persistCDR", "false"));

    }

    /**
     * Initiate CDR file processing from a file
     * 
     * @param cdrFile CDR file to process
     * @return CDR csv file parser
     * @throws BusinessException General business exception
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
    public ICdrCsvReader getCDRReaderByCode(File cdrFile, String readerCode) throws BusinessException, FileNotFoundException {
        ICdrCsvReader cdrReader = (ICdrCsvReader) getReader(readerCode);
        cdrReader.init(cdrFile);
        return cdrReader;
    }

    /**
     * Initiate CDR file processing from API
     * 
     * @param username Username
     * @param ip Ip address
     * @return CDR csv file parser
     * @throws BusinessException General business exception
     */
    public ICdrCsvReader getCDRReader(String username, String ip) throws BusinessException {
        ICdrCsvReader cdrReader = (ICdrCsvReader) getReader();
        cdrReader.init(username, ip);
        return cdrReader;
    }

    public org.meveo.service.medina.impl.ICdrParser getCDRParser(String customParser) throws BusinessException {
        return getParser(customParser);
    }

    public void createEdrs(List<EDR> edrs, CDR cdr) throws BusinessException {
        if (edrs != null && !edrs.isEmpty()) {
            createEdr(edrs.get(0), cdr);
            for (int i = 1; i < edrs.size(); i++) {
                createEdr(edrs.get(i), null);
            }
        }
    }

    public void createEdr(EDR edr, CDR cdr) throws BusinessException {
        create(edr);
        if (cdr != null && persistCDR) {
            cdr.setHeaderEDR(edr);
            cdr.setStatus(CDRStatusEnum.PROCESSED);
            // once the cdr is processed, we don't need the serialized dto
            cdr.setSource(null);
            cdr.setType(null);
            if (cdr.getId() == null) {
                cdrService.create(cdr);
            } else {
                cdrService.update(cdr);
            }
        }
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
        edr.setSubscription(subscription);

        return edr;
    }

    /**
     * Gets the parser.
     *
     * @param customParser the custom parser: the class name of the custom parser
     * @return the parser
     * @throws BusinessException the business exception
     */
    public ICdrParser getParser(String customParser) throws BusinessException {
        ICdrParser cdrParser;
        if (StringUtils.isNotBlank(customParser)) {
            cdrParser = (ICdrParser) EjbUtils.getServiceInterface(customParser);
            if (cdrParser == null) {
                throw new BusinessException("Failed to find CDR Parser " + cdrParser);
            }
        } else {
            log.debug("Use default cdr parser= MEVEOCdrParser");
            cdrParser = (ICdrParser) EjbUtils.getServiceInterface(MEVEOCdrParser.class.getSimpleName());
        }
        return cdrParser;
    }

    /**
     * Gets the reader.
     *
     * @return the reader
     * @throws BusinessException the business exception
     */
    private ICdrReader getReader() throws BusinessException {
        return getReader(null);
    }

    /**
     * Gets the reader.
     *
     * @param readerCode the reader code
     * @return the reader
     * @throws BusinessException the business exception
     */
    public ICdrReader getReader(String readerCode) throws BusinessException {
        ICdrReader cdrReader;
        if (StringUtils.isNotBlank(readerCode)) {
            cdrReader = (ICdrReader) EjbUtils.getServiceInterface(readerCode);
            if (cdrReader == null) {
                throw new BusinessException("Failed to find CDR Reader " + readerCode);
            }
        } else {
            log.debug("Use default cdr reader=MeveoCdrReader");
            cdrReader = new MEVEOCdrReader();
        }
        return cdrReader;
    }
    
    public List<Access> accessPointLookup(CDR cdr) throws InvalidAccessException {
        List<Access> accesses = accessService.getActiveAccessByUserId(cdr.getAccessCode());
        if (accesses == null || accesses.isEmpty()) {
            throw new InvalidAccessException("No matching access point " + cdr.getAccessCode() + " was found");
        }
        return accesses;
    }

    public List<EDR> convertCdrToEdr(CDR cdr, List<Access> accessPoints) throws CDRParsingException {

        List<EDR> edrs = new ArrayList<EDR>();
        boolean foundMatchingAccess = false;

        for (Access accessPoint : accessPoints) {
            if ((accessPoint.getStartDate() == null || accessPoint.getStartDate().getTime() <= cdr.getEventDate().getTime())
                    && (accessPoint.getEndDate() == null || accessPoint.getEndDate().getTime() > cdr.getEventDate().getTime())) {
                foundMatchingAccess = true;
                EDR edr = cdrToEdr(cdr, accessPoint, accessPoint.getSubscription());
                edrs.add(edr);
            }
        }

        if (!foundMatchingAccess) {
            throw new InvalidAccessException(cdr);
        }
        return edrs;
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
            edr.setAccessCode(accessPoint.getAccessUserId());
        }

        edr.setSubscription(subscription);

        return edr;
    }
}

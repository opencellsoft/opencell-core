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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.meveo.admin.exception.BusinessException;
import org.meveo.event.qualifier.RejectedCDR;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.medina.impl.CDRParsingException;
import org.meveo.service.medina.impl.CDRParsingService;
import org.meveo.service.medina.impl.DuplicateException;
import org.meveo.service.medina.impl.ICdrParser;
import org.meveo.service.medina.impl.InvalidAccessException;
import org.meveo.service.medina.impl.InvalidFormatException;

/**
 * A default CDR file parser
 * 
 * @lastModifiedVersion willBeSetLater
 * 
 * @author Andrius Karpavicius
 * @author h.znibar
 */
@Named
public class MEVEOCdrParser implements ICdrParser {
    
    @Inject
    CDRParsingService cdrParsingService;
    
    @Inject
    private EdrService edrService;

    @Inject
    private AccessService accessService;
    
    @Inject
    @RejectedCDR
    private Event<Serializable> rejectededCdrEventProducer;
    
    @Inject
    private SubscriptionService subscriptionService;

    @Override
    public CDR parse(Object line) {

        if (line == null) {
            return null;
        }

        CDR cdr = new CDR();
        if(line instanceof String) {
            String sLine = (String) line;
            cdr.setLine(sLine);
            try {
                String[] fields = sLine.split(";");
                if (fields.length == 0) {
                    throw new InvalidFormatException(sLine, "record empty");

                } else if (fields.length < 4) {
                    throw new InvalidFormatException(sLine, "only " + fields.length + " in the record");

                } else {

                    DateTimeFormatter formatter1 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    DateTimeFormatter formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

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

                if (cdr.getAccessCode() == null || cdr.getAccessCode().trim().length() == 0) {
                    cdr.setRejectReasonException(new InvalidAccessException(sLine, "userId is empty"));
                }

            } catch (Exception e) {
                cdr.setRejectReasonException(new InvalidFormatException(sLine, e));
                cdr.setRejectReason(e.getMessage());
            }
        }
        
        return cdr;
    }
    
    @Override
    public CDR parseByApi(String line, String userName, String ipAddress) {
        CDR cdr = parse(line);
        cdr.setOriginBatch("API_" + ipAddress);
        cdr.setOriginRecord(userName + "_" + new Date().getTime());
        return cdr;
    }
        
    @Override
    public List<Access> accessPointLookup(CDR cdr) throws InvalidAccessException {
        List<Access> accesses = accessService.getActiveAccessByUserId(cdr.getAccessCode());
        if (accesses == null || accesses.size() == 0) {
            rejectededCdrEventProducer.fire(cdr);
            throw new InvalidAccessException(cdr);
        }
        return accesses;
    }

    @Override
    public List<EDR> convertCdrToEdr(CDR cdr, List<Access> accessPoints) throws CDRParsingException {
        try {            
            if (cdr.getRejectReason() != null) {
                throw (CDRParsingException) cdr.getRejectReasonException();
            }

            deduplicate(cdr);
            List<EDR> edrs = new ArrayList<EDR>();
            boolean foundMatchingAccess = false;
            
            Subscription subscription;
            for (Access accessPoint : accessPoints) {
                if ((accessPoint.getStartDate() == null || accessPoint.getStartDate().getTime() <= cdr.getEventDate().getTime())
                        && (accessPoint.getEndDate() == null || accessPoint.getEndDate().getTime() > cdr.getEventDate().getTime())) {
                    foundMatchingAccess = true; 
                    subscription =  accessPoint.getSubscription() != null ? subscriptionService.findById(accessPoint.getSubscription().getId()) : null;
                    EDR edr = cdrToEdr(cdr, accessPoint, subscription);
                    edrs.add(edr);
                }
            }

            if (!foundMatchingAccess) {
                throw new InvalidAccessException(cdr);
            }
            return edrs;
        } catch (CDRParsingException e) {
            cdr.setStatus(CDRStatusEnum.ERROR);
            cdr.setRejectReason(e.getMessage());
            throw e;
        }
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
            edr.setAccessCode(accessPoint.getAccessUserId());
        }

        edr.setSubscription(subscription);

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

    @Override
    public String getType() {
        return null;
    }

    @Override
    public boolean isApplicable(String type) {
        //TODO Add implementation of this method
        return false;
    }      
}
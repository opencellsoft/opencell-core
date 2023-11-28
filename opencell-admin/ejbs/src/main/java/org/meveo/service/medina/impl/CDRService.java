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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.AccessDeniedException;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.base.PersistenceService;

/**
 * @author anasseh
 * @since 9.3
 */
@Stateless
public class CDRService extends PersistenceService<CDR> {
    
    private boolean persistCDR = false;

    @PostConstruct
    private void init() {
        persistCDR = "true".equals(ParamBeanFactory.getAppScopeInstance().getProperty("mediation.persistCDR", "false"));

    }

	@Override
	public void create(CDR cdr) throws BusinessException {
		cdr.setCreated(new Date());
		super.create(cdr);
	}

	@Override
	public CDR update(CDR cdr) throws BusinessException {
		cdr.setUpdated(new Date());
		return super.update(cdr);
	}
	
    /**
     * Find CDR from an EDR
     * @param edr EDR
     * @return corresponding CDR
     */
    public CDR findByEdr(EDR edr) {
        try {
            return (CDR) getEntityManager()
                    .createNamedQuery("CDR.findByEdr")
                    .setParameter("edr",edr)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    public void reprocess(List<Long> ids) throws BusinessException {
        if (!currentUser.hasRole("cdrRateManager")) {
            throw new AccessDeniedException("CDR Rate Manager permission is required to reprocess CDR");
        }
        CDR cdr;
        for(Long id : ids) {
           cdr = findById(id);
           if(cdr == null) {
               throw new BusinessException("CDR not found with id: " + id);
           }
           cdr.setStatus(CDRStatusEnum.TO_REPROCESS);
           update(cdr);
        }
    }
    
    public void writeOff(List<Long> ids, String writeOffReason) throws BusinessException {
        if (!currentUser.hasRole("cdrManager")) {
            throw new AccessDeniedException("CDR Manager permission is required to write off CDR");
        }
        CDR cdr;
        for(Long id : ids) {
           cdr = findById(id);
           if(cdr == null) {
               throw new BusinessException("CDR not found with id: " + id);
           }
           cdr.setStatus(CDRStatusEnum.DISCARDED);
           cdr.setRejectReason(writeOffReason);
           cdr.setUpdater(currentUser.getUserName());
           update(cdr);
        }
    }
    
    public List<CDR> getCDRFileNames() {
        List<CDR> cdrs = new ArrayList<>();
        String query =  "select distinct origin_batch, first_value (created) over (partition by origin_batch order by id) as created_date from rating_cdr order by created_date desc";
        List<Map<String,Object>> result = executeNativeSelectQuery(query, null);
        result.stream().forEach(record-> {
            CDR cdr = new CDR();
            cdr.setOriginBatch((String)record.get("origin_batch"));
            cdr.setCreated((Date)record.get("created_date"));
            if(StringUtils.isNotBlank(cdr.getOriginBatch())) {
                cdrs.add(cdr);
            }          
        });
        return cdrs;
    }

    public List<CDR> getCDRFileNames(String fileName, String fromCreationDate, String toCreationDate) {
        List<CDR> cdrs = new ArrayList<>();
        String query =  "select distinct origin_batch, first_value (created) over (partition by origin_batch order by id) as created_date from rating_cdr where 1=1 ";
        if(StringUtils.isNotBlank(fileName)) {
            fileName = fileName.replaceAll("\\*","\\%");
            query += " and origin_batch LIKE '" + fileName + "'";
        }
        if(StringUtils.isNotBlank(fromCreationDate)) {
            query += " and created >= '" + fromCreationDate + "'";
        }
        if(StringUtils.isNotBlank(toCreationDate)) {
            query += " and created < '" + toCreationDate + "'";
        }
        query += " order by created_date desc";

        List<Map<String,Object>> result = executeNativeSelectQuery(query, null);
        result.stream().forEach(record-> {
            CDR cdr = new CDR();
            cdr.setOriginBatch((String)record.get("origin_batch"));
            cdr.setCreated((Date)record.get("created_date"));
            if(StringUtils.isNotBlank(cdr.getOriginBatch())) {
                cdrs.add(cdr);
            }          
        });
        return cdrs;
    }

    @SuppressWarnings("unchecked")
    public void backout(String fileName) {
        if (!currentUser.hasRole("cdrManager")) {
            throw new AccessDeniedException("CDR Manager permission is required to write off CDR");
        }
        if(StringUtils.isBlank(fileName)) {
            throw new BusinessException("Please provide a correct file name!");
        }
        List<String> cdrs = getEntityManager().createNamedQuery("CDR.checkFileNameExists").setParameter("fileName", fileName).setMaxResults(1).getResultList();
        if(cdrs == null || cdrs.isEmpty()) {
            throw new BusinessException("File ["+ fileName + "] not found in RATING_CDR Table");
        }
        cdrs = getEntityManager().createNamedQuery("CDR.checkRTBilledExists").setParameter("fileName", fileName).getResultList();
        if(cdrs != null && !cdrs.isEmpty()) {
            throw new BusinessException("Billed Rated Transactions exist related to this file name!");
        }
        getEntityManager().createNamedQuery("CDR.deleteRTs").setParameter("fileName", fileName).executeUpdate();            
        getEntityManager().createNamedQuery("CDR.deleteWOs").setParameter("fileName", fileName).executeUpdate();        
        getEntityManager().createNamedQuery("CDR.deleteEDRs").setParameter("fileName", fileName).executeUpdate();
        getEntityManager().createNamedQuery("CDR.deleteCDRs").setParameter("fileName", fileName).executeUpdate();
    }
    
    @SuppressWarnings("unchecked")
    public List<CDR> getCDRsToReprocess(int nbToRetrieve) {
        TypedQuery<CDR> query = getEntityManager().createNamedQuery("CDR.listCDRsToReprocess", CDR.class);
        if (nbToRetrieve > 0) {
            query = query.setMaxResults(nbToRetrieve);
        }
        return query.getResultList();
    }
    
    /**
     * Get a list of unprocessed CDRs to process
     *
     * @param nbToRetrieve Number of items to retrieve for processing
     * @return List of CDR we can process.
     */
    public List<Long> getCDRsToProcess(int nbToRetrieve) {

        StringBuilder strQuery = new StringBuilder();
        strQuery.append("SELECT cdr.id from CDR cdr where cdr.status='OPEN'");

        TypedQuery<Long> query = getEntityManager().createQuery(strQuery.toString(), Long.class);
        if (nbToRetrieve > 0) {
            query = query.setMaxResults(nbToRetrieve);
        }
        return query.getResultList();
    }
    
    /**
     * Save the cdr if the configuration property mediation.persistCDR is true.
     *
     * @param cdr the cdr
     */
    public void createOrUpdateCdr(CDR cdr) {
        if(cdr != null && persistCDR) {
            if(cdr.getId() == null) {
                create(cdr);
            } else {
                update(cdr);
            }                       
        }
    }

    public boolean isCDRExistByOriginRecord(String originRecord) {
    	return getEntityManager().createNamedQuery("CDR.findByOrginRecord").setParameter("originRecord", originRecord).getResultList().size() > 0;
    }
    
    public boolean checkDuplicateCDR(String originRecord) {
        var cdrs = getEntityManager().createNamedQuery("CDR.checkDuplicateCDR").setParameter("originRecord", originRecord).getResultList();
        return CollectionUtils.isNotEmpty(cdrs);
    }
}
package org.meveo.service.catalog.impl;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.base.PersistenceService;

/**
 * @author Tarik FA.
 * @version 10.0
 */
@Stateless
public class PricePlanMatrixVersionService extends PersistenceService<PricePlanMatrixVersion>{


    public static final String STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED = "status of the price plan matrix version (%d) is %s, it can not be updated nor removed";

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Override
	public void create(PricePlanMatrixVersion entity) throws BusinessException {
        super.create(entity);
    }


    public PricePlanMatrixVersion findByPricePlanAndVersion(String pricePlanMatrixCode, int currentVersion) {

            List<PricePlanMatrixVersion> ppmVersions = this.getEntityManager()
                    .createNamedQuery("PricePlanMatrixVersion.findByPricePlanAndVersionOrderByPmPriority", PricePlanMatrixVersion.class)
                    .setParameter("currentVersion", currentVersion)
                    .setParameter("pricePlanMatrixCode", pricePlanMatrixCode.toLowerCase())
                    .getResultList();
            return ppmVersions.isEmpty() ? null : ppmVersions.get(0);
    }

    public PricePlanMatrixVersion updatePricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        String ppmCode = pricePlanMatrixVersion.getPricePlanMatrix().getCode();
        Integer version = pricePlanMatrixVersion.getVersion();

        log.info("updating pricePlanMatrixVersion with pricePlanMatrix code={} and version={}",ppmCode, version);
        if(!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the pricePlanMatrix with pricePlanMatrix code={} and version={}, it must be DRAFT status.", ppmCode, version);
            throw new BusinessException(String.format(STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED,pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }
        update(pricePlanMatrixVersion);
        return pricePlanMatrixVersion;
    }

    public void removePriceMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        if(!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the status of version of the price plan matrix is not DRAFT, the current version is {}.Can not be deleted", pricePlanMatrixVersion.getStatus().toString());
            throw new BusinessException(String.format(STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED, pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }
        this.remove(pricePlanMatrixVersion);
    }

    public PricePlanMatrixVersion updateProductVersionStatus(PricePlanMatrixVersion pricePlanMatrixVersion, VersionStatusEnum status) {
        if(!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the pricePlanMatrix with pricePlanMatrix code={} and current version={}, it must be DRAFT status.", pricePlanMatrixVersion.getPricePlanMatrix().getCode(),pricePlanMatrixVersion.getVersion());
            throw new BusinessException(String.format(STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED,pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }else {
            pricePlanMatrixVersion.setStatus(status);
            pricePlanMatrixVersion.setStatusDate(Calendar.getInstance().getTime());
        }
        return  update(pricePlanMatrixVersion);
    }

    public PricePlanMatrixVersion duplicate(PricePlanMatrixVersion pricePlanMatrixVersion) {
        PricePlanMatrixVersion duplicate = new PricePlanMatrixVersion(pricePlanMatrixVersion);
        String ppmCode = pricePlanMatrixVersion.getPricePlanMatrix().getCode();
        Integer lastVersion = getLastVersion(ppmCode);
        duplicate.setCurrentVersion(lastVersion + 1);
        try {
            this.create(duplicate);
        }catch(BusinessException e) {
            throw new BusinessException(String.format("Can not duplicate the version of product from version product (%d)", duplicate.getId()), e);
        }
        pricePlanMatrixVersion = (PricePlanMatrixVersion) Hibernate.unproxy(pricePlanMatrixVersion);
        return duplicate;
    }

    @SuppressWarnings("unchecked")
	public Integer getLastVersion(String ppmCode) {
    	List<PricePlanMatrixVersion> pricesVersions = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.lastVersion")
                												.setParameter("pricePlanMatrixCode", ppmCode).getResultList();
        return pricesVersions.isEmpty() ? 0 : pricesVersions.get(0).getCurrentVersion();
    }
    
    public PricePlanMatrixVersionDto load(Long id) {
        PricePlanMatrixVersion pricePlanMatrixVersion = findById(id);
        return new PricePlanMatrixVersionDto(pricePlanMatrixVersion);
    }
    
    @SuppressWarnings("unchecked")
	public PricePlanMatrixVersion getLastPublishedVersion(String ppmCode) {
        List<PricePlanMatrixVersion> result=(List<PricePlanMatrixVersion>) this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.getLastPublishedVersion")
                    .setParameter("pricePlanMatrixCode", ppmCode)
                    .getResultList();
        
        return result.isEmpty()?null:result.get(0);
    }
    
    public PricePlanMatrixLine loadPrices(PricePlanMatrixVersion pricePlanMatrixVersion, ChargeInstance chargeInstance) throws BusinessException {
    	return pricePlanMatrixService.loadPrices(pricePlanMatrixVersion, chargeInstance);
    }
    

    @SuppressWarnings("unchecked")
	public PricePlanMatrixVersion getLasPricePlanMatrixtVersion(String ppmCode) {
    	List<PricePlanMatrixVersion> pricesVersions = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.lastVersion")
                												.setParameter("pricePlanMatrixCode", ppmCode).getResultList();
        return pricesVersions.isEmpty() ? null : pricesVersions.get(0);
    }
    
    @Inject 
	private PricePlanMatrixColumnService pricePlanMatrixColumnService;
    @Inject
    private PricePlanMatrixValueService pricePlanMatrixValueService;
    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;
    
    public PricePlanMatrixVersion duplicateColumns(PricePlanMatrixVersion entity, Set<PricePlanMatrixColumn> columns ) {
    	if(columns != null && !columns.isEmpty()) {
        	columns.forEach(ppmc -> {
        		ppmc.getPricePlanMatrixValues().size();
        		//var pricePlanMatrixValue = ppmc.getPricePlanMatrixValues();
        		pricePlanMatrixColumnService.detach(ppmc);
        		var duplicatePricePlanMatrixColumn = new PricePlanMatrixColumn(ppmc);
        		duplicatePricePlanMatrixColumn.setPricePlanMatrixVersion(entity);
        		pricePlanMatrixColumnService.create(duplicatePricePlanMatrixColumn);
        	});
    	}
    	return entity;
    }
    
    public PricePlanMatrixVersion duplicateLines(PricePlanMatrixVersion entity, Set<PricePlanMatrixLine> lines) {
    	if(lines != null && !lines.isEmpty()) {
    		lines.forEach(ppml -> {
    			ppml.getPricePlanMatrixValues().size();
    			//var pricePlanMatrixValue = ppml.getPricePlanMatrixValues();
    			pricePlanMatrixLineService.detach(ppml);
    			var duplicateLine = new PricePlanMatrixLine(ppml);
    			pricePlanMatrixLineService.create(duplicateLine);
    		});
    	}
    	return entity;
    }
    
    public void duplicatePricePlanValue( PricePlanMatrixColumn column, PricePlanMatrixLine line, Set<PricePlanMatrixValue> pricePlanMatrixValues) {
    	
    	return;
    }
    
    
    
}
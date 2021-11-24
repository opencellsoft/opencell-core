package org.meveo.service.catalog.impl;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.model.DatePeriod;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.ProductService;

/**
 * @author Tarik FA.
 * @version 10.0
 */
@Stateless
public class PricePlanMatrixVersionService extends PersistenceService<PricePlanMatrixVersion>{


    public static final String STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED = "status of the price plan matrix version (%d) is %s, it can not be updated nor removed";

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;
    @Inject 
	private PricePlanMatrixColumnService pricePlanMatrixColumnService;
    @Inject
    private PricePlanMatrixValueService pricePlanMatrixValueService;
    @Inject
    private PricePlanMatrixLineService pricePlanMatrixLineService;
    @Inject
    private ProductService productService;
    @Inject
	private AuditLogService auditLogService;

    @Override
	public void create(PricePlanMatrixVersion entity) throws BusinessException {
        super.create(entity);
        logAction(entity, "CREATE");
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
        Integer version = pricePlanMatrixVersion.getCurrentVersion();

        log.info("updating pricePlanMatrixVersion with pricePlanMatrix code={} and version={}",ppmCode, version);
        if(!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the pricePlanMatrix with pricePlanMatrix code={} and version={}, it must be DRAFT status.", ppmCode, version);
            throw new BusinessException(String.format(STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED,pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }
        update(pricePlanMatrixVersion);
        return pricePlanMatrixVersion;
    }
    
    public PricePlanMatrixVersion updatePublishedPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion, Date endingDate) {
        if(endingDate!=null && endingDate.before(org.meveo.model.shared.DateUtils.setDateToEndOfDay(new Date()))) {
        	throw new ValidationException("ending date must be greater than today");
        }
        pricePlanMatrixVersion.getValidity().setTo(endingDate);
        update(pricePlanMatrixVersion);
        return pricePlanMatrixVersion;
    }

    public void removePriceMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {
        if(!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the status of version of the price plan matrix is not DRAFT, the current version is {}.Can not be deleted", pricePlanMatrixVersion.getStatus().toString());
            throw new BusinessException(String.format(STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED, pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }
        logAction(pricePlanMatrixVersion, "DELETE");
        this.remove(pricePlanMatrixVersion);
    }

    public PricePlanMatrixVersion updateProductVersionStatus(PricePlanMatrixVersion pricePlanMatrixVersion, VersionStatusEnum status) {
        if(!pricePlanMatrixVersion.getStatus().equals(VersionStatusEnum.DRAFT)) {
            log.warn("the pricePlanMatrix with pricePlanMatrix code={} and current version={}, it must be DRAFT status.", pricePlanMatrixVersion.getPricePlanMatrix().getCode(),pricePlanMatrixVersion.getCurrentVersion());
            throw new BusinessException(String.format(STATUS_OF_THE_PRICE_PLAN_MATRIX_VERSION_D_IS_S_IT_CAN_NOT_BE_UPDATED_NOR_REMOVED,pricePlanMatrixVersion.getId(), pricePlanMatrixVersion.getStatus().toString()));
        }else {
            pricePlanMatrixVersion.setStatus(status);
            pricePlanMatrixVersion.setStatusDate(Calendar.getInstance().getTime());
        }
        return  update(pricePlanMatrixVersion, "CHANGE_STATUS");
    }


	@Transactional(value = TxType.REQUIRED)
    public PricePlanMatrixVersion duplicate(PricePlanMatrixVersion pricePlanMatrixVersion, DatePeriod validity, String pricePlanMatrixNewCode) {
    	var columns = new HashSet<>(pricePlanMatrixVersion.getColumns());
    	var lines = new HashSet<>(pricePlanMatrixVersion.getLines());
    	
    	//this.detach(pricePlanMatrixVersion);
    	
        PricePlanMatrixVersion duplicate = new PricePlanMatrixVersion(pricePlanMatrixVersion);
        if(validity!=null) {
         duplicate.setValidity(validity);	
        }
            String ppmCode = pricePlanMatrixVersion.getPricePlanMatrix().getCode();
            Integer lastVersion = getLastVersion(ppmCode);
            duplicate.setCurrentVersion(lastVersion + 1);
        
        try {
            this.create(duplicate);
        }catch(BusinessException e) {
            throw new BusinessException(String.format("Can not duplicate the version of product from version product (%d)", duplicate.getId()), e);
        }
        
        var columnsIds = duplicateColumns(duplicate, columns);
        var lineIds = duplicateLines(duplicate, lines);

        duplicatePricePlanMatrixValue(columnsIds, lineIds);
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
    
    public PricePlanMatrixLine loadPrices(PricePlanMatrixVersion pricePlanMatrixVersion, WalletOperation walletOperation) throws BusinessException {
    	return pricePlanMatrixService.loadPrices(pricePlanMatrixVersion, walletOperation);
    }
    

    @SuppressWarnings("unchecked")
	public PricePlanMatrixVersion getLastPricePlanMatrixtVersion(String ppmCode) {
    	List<PricePlanMatrixVersion> pricesVersions = this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.lastVersion")
                												.setParameter("pricePlanMatrixCode", ppmCode).getResultList();
        return pricesVersions.isEmpty() ? null : pricesVersions.get(0);
    }
    
    
    private Map<Long, PricePlanMatrixColumn> duplicateColumns(PricePlanMatrixVersion entity, Set<PricePlanMatrixColumn> columns ) {
    	var ids = new HashMap<Long, PricePlanMatrixColumn>();
    	if(columns != null && !columns.isEmpty()) {
    		
    		var duplicateColumns = new HashSet<PricePlanMatrixColumn>();
    		
    		for (PricePlanMatrixColumn ppmc : columns) {
				
        		//pricePlanMatrixColumnService.detach(ppmc);
        		
        		var duplicatePricePlanMatrixColumn = new PricePlanMatrixColumn(ppmc);
        		if(ppmc.getProduct() != null) {
        			var product = productService.findById(ppmc.getProduct().getId());
        			duplicatePricePlanMatrixColumn.setProduct(product);
        		}
        		duplicatePricePlanMatrixColumn.setCode(pricePlanMatrixColumnService.findDuplicateCode(ppmc));
        		duplicatePricePlanMatrixColumn.setPricePlanMatrixVersion(entity);
        		pricePlanMatrixColumnService.create(duplicatePricePlanMatrixColumn);
        		
        		ids.put(ppmc.getId(), duplicatePricePlanMatrixColumn);
        		
        		duplicateColumns.add(duplicatePricePlanMatrixColumn);
    		}
    		entity.getColumns().addAll(duplicateColumns);
    	}
    	return ids;
    }
    
    private Map<Long, PricePlanMatrixLine> duplicateLines(PricePlanMatrixVersion entity, Set<PricePlanMatrixLine> lines) {
    	var ids = new HashMap<Long, PricePlanMatrixLine>();
    	if(lines != null && !lines.isEmpty()) {
    		lines.forEach(ppml -> {
    			ppml.getPricePlanMatrixValues().size();

    			//pricePlanMatrixLineService.detach(ppml);
    			
    			var duplicateLine = new PricePlanMatrixLine(ppml);
    			duplicateLine.setPricePlanMatrixVersion(entity);
    			
    			pricePlanMatrixLineService.create(duplicateLine);
    			
    			ids.put(ppml.getId(), duplicateLine);
    			
    			entity.getLines().add(duplicateLine);
    		});
    	}
    	return ids;
    }
    
    private void duplicatePricePlanMatrixValue(Map<Long,PricePlanMatrixColumn> columnsId, Map<Long,PricePlanMatrixLine> lineIds) {
    	var pricePlanMatrixValues = new HashSet<PricePlanMatrixValue>();
    	columnsId.forEach((key, value) -> {
    		var ppmv = pricePlanMatrixValueService.findByPricePlanMatrixColumn(key);
    		ppmv.forEach(tmpValue -> {
        		tmpValue.setPricePlanMatrixColumn(value);
        		pricePlanMatrixValues.add(tmpValue);
    		});
    	});
    	pricePlanMatrixValues.stream()
    			.filter(ppmv -> lineIds.get(ppmv.getPricePlanMatrixLine().getId()) != null)
    			.map(ppmv -> {
    				ppmv.setPricePlanMatrixLine(lineIds.get(ppmv.getPricePlanMatrixLine().getId()));
    				return ppmv;
    			}).forEach(ppmv -> {
    				var pricePlanMatrixValue = new PricePlanMatrixValue(ppmv);
    				pricePlanMatrixValueService.create(pricePlanMatrixValue);
    			});
    	
    	
    	
    	/*if(pricePlanMatrixValues != null && !pricePlanMatrixValues.isEmpty()) {
    		
    		pricePlanMatrixValues.forEach(ppmv -> {
    			
    			pricePlanMatrixValueService.detach(ppmv);
    			
    			var pricePlanMatrixValue = new PricePlanMatrixValue(ppmv);
    			if(pricePlanMatrixColumn != null) {
    				pricePlanMatrixValue.setPricePlanMatrixColumn(pricePlanMatrixColumn);
    				pricePlanMatrixColumn.getPricePlanMatrixValues().add(pricePlanMatrixValue);
    			}
    			if(pricePlanMatrixLine != null) {
    				pricePlanMatrixValue.setPricePlanMatrixLine(pricePlanMatrixLine);
    				pricePlanMatrixLine.getPricePlanMatrixValues().add(pricePlanMatrixValue);
    			}
    			pricePlanMatrixValueService.create(pricePlanMatrixValue);
    			
    		});
    	}*/
    }


	/**
	 * @param pricePlanMatrixCode
	 * @param pricePlanMatrixVersion
	 * @return
	 */
	public Map<String, List<Long>> getUsedEntities(String pricePlanMatrixCode, int version) {
		Map<String, List<Long>> result = new TreeMap<String, List<Long>>();
		PricePlanMatrixVersion pricePlanMatrixVersion = findByPricePlanAndVersion(pricePlanMatrixCode, version);
		if(pricePlanMatrixVersion==null) {
			throw new BusinessException("pricePlanMatrix with code '"+pricePlanMatrixCode+"' and version '"+version+"' not found.");
		}
		
		if(pricePlanMatrixVersion.getValidity().getTo()==null || pricePlanMatrixVersion.getValidity().getTo().after(new Date())) {
			String eventCode = pricePlanMatrixVersion.getPricePlanMatrix().getEventCode();
	
			 List<Long> subscriptionsIds = this.getEntityManager()
	                 .createNamedQuery("Subscription.getSubscriptionIdsUsingProduct", Long.class)
	                 .setParameter("eventCode", eventCode)
	                 .getResultList();
			 result.put("subscriptions", subscriptionsIds);
			 
			 List<Long> quotesIds = this.getEntityManager()
	                 .createNamedQuery("CpqQuote.getQuoteIdsUsingCharge", Long.class)
	                 .setParameter("eventCode", eventCode)
	                 .getResultList();
			 result.put("quotes", quotesIds);
			 
			 List<Long> ordersIds = this.getEntityManager()
	                 .createNamedQuery("CommercialOrder.getOrderIdsUsingCharge", Long.class)
	                 .setParameter("eventCode", eventCode)
	                 .getResultList();
			 result.put("orders", ordersIds);
		}
		return result;
	}
	
	@Override
	public PricePlanMatrixVersion update(PricePlanMatrixVersion ppmv) throws BusinessException {
		return update(ppmv, "UPDATE");
	}
	
	/**
	 * @param pricePlanMatrixVersion
	 * @param auditAction
	 * @return
	 */
	private PricePlanMatrixVersion update(PricePlanMatrixVersion entity, String auditAction) {
		final PricePlanMatrixVersion ppmv = super.update(entity);
		logAction(ppmv, auditAction);
		return ppmv;
	}
	
    /**
	 * @param ppmv 
     * @param action
	 */
	private void logAction(PricePlanMatrixVersion ppmv, String action) {
		AuditLog auditLog = new AuditLog();
		auditLog.setActor(currentUser.getFullNameOrUserName());
		final Date date = new Date();
		auditLog.setCreated(date);
		auditLog.setEntity("PricePlanMatrixVersion");
		final String origin = ppmv.getPricePlanMatrix().getCode()+"."+ppmv.getCurrentVersion();
		auditLog.setOrigin(origin);
		auditLog.setAction(action); 
		auditLog.setParameters("user "+currentUser.getUserName()+" apply "+action+" on "+DateUtils.formatAsDate(date)+" to the price plan version "+origin+". "+ppmv.getStatusChangeLog());
		auditLogService.create(auditLog);
	}
}
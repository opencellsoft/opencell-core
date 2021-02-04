package org.meveo.service.catalog.impl;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.PricePlanMatrixVersionDto;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.catalog.PricePlanMatrixLine;
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
        PricePlanMatrixVersion duplicate = new PricePlanMatrixVersion();
        try {
            BeanUtils.copyProperties(duplicate, pricePlanMatrixVersion);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BusinessException("Failed to clone price plan matrix version", e);
        }
        String ppmCode = pricePlanMatrixVersion.getPricePlanMatrix().getCode();
        Integer lastVersion = getLastVersion(ppmCode);
        duplicate.setId(null);
        duplicate.setColumns(new HashSet<>());
        duplicate.setLines(new HashSet<>());
        duplicate.setVersion(0);
        duplicate.setCurrentVersion(lastVersion + 1);
        duplicate.setStatus(VersionStatusEnum.DRAFT);
        duplicate.setStatusDate(new Date());
        try {
            this.create(duplicate);
        }catch(BusinessException e) {
            throw new BusinessException(String.format("Can not duplicate the version of product from version product (%d)", duplicate.getId()), e);
        }

        return duplicate;
    }

    private Integer getLastVersion(String ppmCode) {
        return this.getEntityManager().createNamedQuery("PricePlanMatrixVersion.lastVersion", Integer.class)
                    .setParameter("pricePlanMatrixCode", ppmCode)
                    .getSingleResult();
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
    
    public PricePlanMatrixLine loadPrices(PricePlanMatrixVersion pricePlanMatrixVersion, ChargeInstance chargeInstance) {
    	return pricePlanMatrixService.loadPrices(pricePlanMatrixVersion, chargeInstance);
    }
    
    
}
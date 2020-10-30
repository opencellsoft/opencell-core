package org.meveo.service.catalog.impl;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.service.base.PersistenceService;
/**
 * @author Tarik FA.
 * @version 10.0
 */
@Stateless
public class PricePlanMatrixVersionService extends PersistenceService<PricePlanMatrixVersion>{
    private static final String PRICE_PLAN_MATRIX_VERSION_ALREADY_EXIST = "Price plan matrix for code %s and version %d, already exist";
    private static final String PRICE_PLAN_MATRIX_VERSION_MISSIN = "Price plan matrix is missing";
    
    @Override
    public void create(PricePlanMatrixVersion entity) throws BusinessException {
        
        if(entity == null)
            throw new BusinessException(PRICE_PLAN_MATRIX_VERSION_MISSIN);        
        if(this.findByCode(entity.getCode(), entity.getPricePlanVersion()) != null)
            throw new BusinessException(String.format(PRICE_PLAN_MATRIX_VERSION_ALREADY_EXIST, entity.getCode(), entity.getPricePlanVersion()));
        super.create(entity);
    }
    
    public PricePlanMatrixVersion findByCode(String code, int priceVersion) {
        try {
            return (PricePlanMatrixVersion) this.getEntityManager()
                                                    .createNamedQuery("PricePlanMatrixVersion.findByCode")
                                                        .setParameter("code", code)
                                                            .setParameter("priceVersion", priceVersion)
                                                                .getSingleResult();
        }catch(NoResultException e) {
            return null;
        }
    }
}
package org.meveo.service.payments.impl;

import javax.ejb.Stateless;

import org.meveo.model.dunning.DunningLevel;
import org.meveo.model.dunning.DunningModeEnum;
import org.meveo.service.base.BusinessService;

/**
 * Service implementation to manage DunningLevel entity. It extends {@link BusinessService} class
 * 
 * @author YIZEM
 * @version 12.0
 *
 */
@Stateless
public class DunningLevelService extends BusinessService<DunningLevel> {

    /**
     * Update dunning levels by setting active = true or false according to the selected dunning settings (INVOICE_LEVEL or CUSTOMER_LEVEL)
     * @param pDunningMode {@link DunningModeEnum}
     */
    public void updateDunningLevelAfterCreatingOrUpdatingDunningSetting(DunningModeEnum pDunningMode) {
        getEntityManager().createNamedQuery("DunningLevel.activateByDunningMode").setParameter("dunningMode", pDunningMode).executeUpdate();
        getEntityManager().createNamedQuery("DunningLevel.deactivateByDunningMode").setParameter("dunningMode", pDunningMode).executeUpdate();
    }
}

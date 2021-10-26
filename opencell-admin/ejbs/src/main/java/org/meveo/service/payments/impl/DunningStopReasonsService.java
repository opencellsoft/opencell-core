package org.meveo.service.payments.impl;
import javax.ejb.Stateless;

import org.meveo.model.dunning.DunningStopReason;
import org.meveo.service.base.PersistenceService;

/**
 * Service implementation to manage DunningStopReasons entity.
 * It extends {@link PersistenceService} class
 *
 * @author Mbarek-Ay
 * @version 11.0
 */
@Stateless
public class DunningStopReasonsService extends PersistenceService<DunningStopReason> {

    /**
     * Search a dunning stop reason based on dunning setting code and stop reason.
     * @param dunningSettingsCode dunning setting code
     * @param stopReason stop reason
     * @return Dunning Stop Reason
     */
    public DunningStopReason findByCodeAndDunningSettingCode(String dunningSettingsCode, String stopReason) {
        return getEntityManager().createNamedQuery("DunningStopReasons.findByCodeAndDunningSettingCode", DunningStopReason.class).setParameter("stopReason", stopReason)
                .setParameter("dunningSettingsCode", dunningSettingsCode).getSingleResult();
    }
}

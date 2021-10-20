package org.meveo.service.payments.impl;
import javax.ejb.Stateless;

import org.meveo.model.dunning.DunningPauseReasons;
import org.meveo.model.dunning.DunningStopReasons;
import org.meveo.service.base.PersistenceService;

/**
 * Service implementation to manage DunningPauseReasons entity.
 * It extends {@link PersistenceService} class
 * 
 * @author Mbarek-Ay
 * @version 11.0
 *
 */
@Stateless
public class DunningPauseReasonsService extends PersistenceService<DunningPauseReasons> {

    /**
     * Search a dunning pause reason based on dunning setting code and pause reason.
     * @param dunningSettingsCode dunning setting code
     * @param pauseReason pause reason
     * @return Dunning pause Reason
     */
    public DunningPauseReasons findByCodeAndDunningSettingCode(String dunningSettingsCode, String pauseReason) {
        return getEntityManager()
                .createNamedQuery("DunningPauseReasons.findByCodeAndDunningSettingCode", DunningPauseReasons.class)
                .setParameter("pauseReason", pauseReason)
                .setParameter("dunningSettingsCode",dunningSettingsCode).getSingleResult();
    }

}

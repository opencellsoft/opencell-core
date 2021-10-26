package org.meveo.service.payments.impl;
import javax.ejb.Stateless;

import org.meveo.model.dunning.DunningPauseReason;
import org.meveo.service.base.PersistenceService;

/**
 * Service implementation to manage DunningPauseReasons entity.
 * It extends {@link PersistenceService} class
 *
 * @author Mbarek-Ay
 * @version 11.0
 */
@Stateless
public class DunningPauseReasonsService extends PersistenceService<DunningPauseReason> {

    /**
     * Search a dunning pause reason based on dunning setting code and pause reason.
     * @param dunningSettingsCode dunning setting code
     * @param pauseReason pause reason
     * @return Dunning pause Reason
     */
    public DunningPauseReason findByCodeAndDunningSettingCode(String dunningSettingsCode, String pauseReason) {
        return getEntityManager().createNamedQuery("DunningPauseReasons.findByCodeAndDunningSettingCode", DunningPauseReason.class).setParameter("pauseReason", pauseReason)
                .setParameter("dunningSettingsCode", dunningSettingsCode).getSingleResult();
    }

}

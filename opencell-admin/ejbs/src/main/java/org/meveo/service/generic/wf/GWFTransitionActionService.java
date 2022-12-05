package org.meveo.service.generic.wf;

import org.meveo.model.generic.wf.GWFTransitionAction;
import org.meveo.service.base.PersistenceService;

import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

@Stateless
public class GWFTransitionActionService extends PersistenceService<GWFTransitionAction> {

    public GWFTransitionAction findWFActionByUUID(String uuid) {
        GWFTransitionAction action;
        try {
            action = (GWFTransitionAction) getEntityManager().createQuery("from " + GWFTransitionAction.class.getSimpleName()
                    + " where uuid=:uuid").setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return action;
    }
}

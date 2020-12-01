package org.meveo.service.generic.wf;

import org.meveo.model.generic.wf.Action;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

@Stateless
public class ActionsService extends PersistenceService<Action> {

    public Action findWFActionByUUID(String uuid) {
        Action action;
        try {
            action = (Action) getEntityManager().createQuery("from " + Action.class.getSimpleName()
                    + " where uuid=:uuid").setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return action;
    }
}

package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;

/**
 * An interface of a script that executes some action on an entity
 * 
 * @author Andrius Karpavicius
 * 
 */
public interface EntityActionInterface {

    /**
     * Execute an action on an entity
     * 
     * @param entity Entity
     * @param parameters Parameters
     * @param currentUser Current user
     * @param currentProvider Current provider
     * @return An optional Value
     * @throws BusinessException
     */
    public Object execute(IEntity entity, Map<String, Object> parameters, User currentUser, Provider currentProvider) throws BusinessException;
}
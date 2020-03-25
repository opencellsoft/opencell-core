/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.service.base.BusinessService;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

@Stateless
public class EntityCustomActionService extends BusinessService<EntityCustomAction> {

    /**
     * Find a list of entity actions/scripts corresponding to a given entity
     * 
     * @param entity Entity that entity actions/scripts apply to

     * @return A map of entity actions/scripts mapped by a action code
     */
    public Map<String, EntityCustomAction> findByAppliesTo(ICustomFieldEntity entity) {
        try {
            return findByAppliesTo(CustomFieldTemplateService.calculateAppliesToValue(entity));

        } catch (CustomFieldException e) {
            // Its ok, handles cases when value that is part of CFT.AppliesTo calculation is not set yet on entity
            return new HashMap<String, EntityCustomAction>();
        }
    }

    /**
     * Find a list of entity actions/scripts corresponding to a given entity
     * 
     * @param appliesTo Entity (CFT appliesTo code) that entity actions/scripts apply to

     * @return A map of entity actions/scripts mapped by a action code
     */
    @SuppressWarnings("unchecked")
    public Map<String, EntityCustomAction> findByAppliesTo(String appliesTo) {

        QueryBuilder qb = new QueryBuilder(EntityCustomAction.class, "s", null);
        qb.addCriterion("s.appliesTo", "=", appliesTo, true);

        List<EntityCustomAction> actions = (List<EntityCustomAction>) qb.getQuery(getEntityManager()).getResultList();

        Map<String, EntityCustomAction> actionMap = new HashMap<String, EntityCustomAction>();
        for (EntityCustomAction action : actions) {
            actionMap.put(action.getCode(), action);
        }
        return actionMap;
    }

    /**
     * Find a specific entity action/script by a code
     * 
     * @param code Entity action/script code. MUST be in a format of &lt;localCode&gt;|&lt;appliesTo&gt;
     * @param entity Entity that entity actions/scripts apply to

     * @return Entity action/script
     * @throws CustomFieldException An exception when AppliesTo value can not be calculated
     */
    public EntityCustomAction findByCodeAndAppliesTo(String code, ICustomFieldEntity entity) throws CustomFieldException {
        return findByCodeAndAppliesTo(code, CustomFieldTemplateService.calculateAppliesToValue(entity));
    }

    /**
     * Find a specific entity action/script by a code
     * 
     * @param code Entity action/script code. MUST be in a format of &lt;localCode&gt;|&lt;appliesTo&gt;
     * @param appliesTo Entity (CFT appliesTo code) that entity actions/scripts apply to

     * @return Entity action/script
     */
    public EntityCustomAction findByCodeAndAppliesTo(String code, String appliesTo) {

        QueryBuilder qb = new QueryBuilder(EntityCustomAction.class, "s", null);
        qb.addCriterion("s.code", "=", code, true);
        qb.addCriterion("s.appliesTo", "=", appliesTo, true);
        try {
            return (EntityCustomAction) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
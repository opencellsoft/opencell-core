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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.service.base.BusinessService;

/**
 * CustomEntityInstance persistence service implementation.
 * @author Adnane Boubia
 */
@Stateless
public class CustomEntityInstanceService extends BusinessService<CustomEntityInstance> {

    public CustomEntityInstance findByCodeByCet(String cetCode, String code) {
        QueryBuilder qb = new QueryBuilder(getEntityClass(), "cei", null);
        qb.addCriterion("cei.cetCode", "=", cetCode, true);
        qb.addCriterion("cei.code", "=", code, true);

        try {
            return (CustomEntityInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            log.warn("No CustomEntityInstance by code {} and cetCode {} found", code, cetCode);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<CustomEntityInstance> findChildEntities(String cetCode, String parentEntityUuid) {

        QueryBuilder qb = new QueryBuilder(getEntityClass(), "cei", null);
        qb.addCriterion("cei.cetCode", "=", cetCode, true);
        qb.addCriterion("cei.parentEntityUuid", "=", parentEntityUuid, true);

        return qb.getQuery(getEntityManager()).getResultList();
    }
    
    /**
     * List of custom entity instances by custom entity template
     * 
     * @param cetCode code of CustomEntityTemplate
     * @return list of CustomEntityInstance
     */
	public List<CustomEntityInstance> listByCet(String cetCode) {
        return listByCet(cetCode, null);
    }
    
	/**
     * List of custom entity instances by custom entity template
     * 
     * @param cetCode code of CustomEntityTemplate
     * @param active Custom entity template's status
     * @return list of CustomEntityInstance
     */
    @SuppressWarnings("unchecked")
	public List<CustomEntityInstance> listByCet(String cetCode, Boolean active) {
        QueryBuilder qb = new QueryBuilder(getEntityClass(), "cei", null);
        qb.addCriterion("cei.cetCode", "=", cetCode, true);
        
        if(active != null) {
        	qb.addBooleanCriterion("disabled", !active);
        }
        try {
            return (List<CustomEntityInstance>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            log.warn("No CustomEntityInstances by cetCode {} found", cetCode);
            return Collections.emptyList();
        }
    }

    public HashMap<String, Object> customEntityInstanceAsMap(CustomEntityInstance c) {
        return new HashMap<String, Object>() {{
            put("id", c.getId());
            put("code", c.getCode());
            put("description", c.getDescription());

        }};
    }

    public HashMap<String, Object> customEntityInstanceAsMapWithCfValues(CustomEntityInstance c) {
        HashMap<String, Object> map = customEntityInstanceAsMap(c);
        map.put("cfValues", c.getCfValuesNullSafe());
        return map;
    }

    public void remove(String cetCode, Long id){
        Query query = getEntityManager().createQuery("delete from CustomEntityInstance c where c.cetCode = :cetCode and c.code = :code");
        query.setParameter("cetCode", cetCode);
        query.setParameter("code", id.toString());
        query.executeUpdate();
    }

    public void remove(String cetCode, Set<Long> ids){
        ids.forEach(id -> remove(cetCode, id));
    }

	/**
	 * @param cetCode
	 * @param cftCode
	 * @param entityCode
	 * @return
	 */
	public List<CustomEntityInstance> listByReferencedEntity(String cetCode, String cftCode, String entityCode) {
        QueryBuilder qb = new QueryBuilder(getEntityClass(), "cei", null);
        qb.addCriterion("cei.cetCode", "=", cetCode, true);
        qb.addCriterion("entityFromJson(cf_values,"+cftCode+",entity)","=",entityCode,true);
        Query query = qb.getQuery(getEntityManager());
        return (List<CustomEntityInstance>) qb.getQuery(getEntityManager()).getResultList();
	}
}
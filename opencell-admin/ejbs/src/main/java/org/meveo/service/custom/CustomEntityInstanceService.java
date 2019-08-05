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
}
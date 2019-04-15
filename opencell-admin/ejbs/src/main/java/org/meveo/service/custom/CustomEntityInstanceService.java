package org.meveo.service.custom;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.api.dto.CustomFieldValueDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.service.base.BusinessService;

/**
 * CustomEntityInstance persistence service implementation.
 * 
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

	public List<CustomEntityInstance> findByCodeByCet(List<CustomFieldValueDto> dtosTo) {

		return getEntityManager()
				.createNamedQuery("CustomEntityInstance.getCEIByCetCodeAndCod", CustomEntityInstance.class)
				.setParameter("codes",
						dtosTo.stream().map(CustomFieldValueDto::getSearchCode).collect(Collectors.toList()))
				.setParameter("cetCodes",
						dtosTo.stream().map(CustomFieldValueDto::getSearchClassnameCode).collect(Collectors.toList()))
				.getResultList();

	}
}
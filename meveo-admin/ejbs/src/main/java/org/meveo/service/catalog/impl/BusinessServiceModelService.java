package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.service.admin.impl.GenericModuleService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessServiceModelService extends GenericModuleService<BusinessServiceModel> {

	public BusinessServiceModel findByBSMAndServiceTemplate(String bsm, String st) {
		QueryBuilder qb = new QueryBuilder(BusinessServiceModel.class, "b");
		qb.addCriterion("b.code", "=", bsm, true);
		qb.addCriterion("b.serviceTemplate.code", "=", st, true);

		try {
			return (BusinessServiceModel) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
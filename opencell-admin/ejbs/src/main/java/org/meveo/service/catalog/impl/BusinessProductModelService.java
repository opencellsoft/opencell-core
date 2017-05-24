package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.catalog.BusinessProductModel;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.service.admin.impl.GenericModuleService;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class BusinessProductModelService extends GenericModuleService<BusinessProductModel> {
	
	public BusinessServiceModel findByBPMAndProductTemplate(String bsm, String productTemplateCode) {
		QueryBuilder qb = new QueryBuilder(BusinessProductModel.class, "b");
		qb.addCriterion("b.code", "=", bsm, true);
		qb.addCriterion("b.productTemplate.code", "=", productTemplateCode, true);

		try {
			return (BusinessServiceModel) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}

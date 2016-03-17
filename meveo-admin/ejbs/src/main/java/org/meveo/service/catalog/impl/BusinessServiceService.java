package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.BusinessServiceModelDto;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.service.base.BusinessService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessServiceService extends BusinessService<BusinessServiceModel> {
//
//	public void create(BusinessServiceModelDto postData, User currentUser) throws BusinessException {
//		BusinessServiceModel bsm = new BusinessServiceModel();
//		bsm.setCode(postData.getCode());
//		bsm.setDescription(postData.getDescription());
//		bsm.setDuplicatePricePlan(postData.isDuplicatePricePlan());
//		bsm.setDuplicateService(postData.isDuplicateService());
//
//		create(bsm, currentUser);
//	}
//
//	public void update(BusinessServiceModel bsm, BusinessServiceModelDto postData, User currentUser) throws BusinessException {
//		bsm.setDescription(postData.getDescription());
//		bsm.setDuplicatePricePlan(postData.isDuplicatePricePlan());
//		bsm.setDuplicateService(postData.isDuplicateService());
//
//		update(bsm, currentUser);
//	}

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

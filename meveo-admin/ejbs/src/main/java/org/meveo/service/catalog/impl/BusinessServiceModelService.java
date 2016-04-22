package org.meveo.service.catalog.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BusinessServiceModel;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.scripts.ServiceModelScript;
import org.meveo.service.base.BusinessService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class BusinessServiceModelService extends BusinessService<BusinessServiceModel> {

	public void create(String code, String description, boolean isDuplicatePricePlan, boolean isDuplicateService, ServiceModelScript serviceModelScript,
			ServiceTemplate serviceTemplate, User currentUser) throws BusinessException {
		BusinessServiceModel bsm = new BusinessServiceModel();
		bsm.setCode(code);
		bsm.setDescription(description);
		bsm.setDuplicatePricePlan(isDuplicatePricePlan);
		bsm.setDuplicateService(isDuplicateService);
		bsm.setScript(serviceModelScript);
		bsm.setServiceTemplate(serviceTemplate);

		create(bsm, currentUser);
	}

	public void update(BusinessServiceModel bsm, String description, boolean isDuplicatePricePlan, boolean isDuplicateService, ServiceModelScript serviceModelScript,
			ServiceTemplate serviceTemplate, User currentUser) throws BusinessException {
		bsm.setDescription(description);
		bsm.setDuplicatePricePlan(isDuplicatePricePlan);
		bsm.setDuplicateService(isDuplicateService);
		bsm.setScript(serviceModelScript);
		bsm.setServiceTemplate(serviceTemplate);

		update(bsm, currentUser);
	}

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

	@SuppressWarnings("unchecked")
	public List<BusinessServiceModel> findByScriptId(Long id) {
		QueryBuilder qb = new QueryBuilder(BusinessServiceModel.class, "b");
		qb.addCriterion("script.id", "=", id, true);

		try {
			return (List<BusinessServiceModel>) qb.getQuery(getEntityManager()).getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

}

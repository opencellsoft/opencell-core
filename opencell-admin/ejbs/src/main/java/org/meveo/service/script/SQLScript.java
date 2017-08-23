package org.meveo.service.script;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.billing.impl.EdrService;



public class SQLScript extends Script {
	private EdrService edrService = (EdrService) getServiceInterface("EdrService");

	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {
		String selectIDQuery = "select id from billing_user_account where billing_account_id in (select id from billing_billing_account where customer_account_id=569544)";
		EntityManager entityManager = edrService.getEntityManager();
		Query query = entityManager.createNativeQuery(selectIDQuery);
		List<Object> rows = query.getResultList();
		String updateSQL = "update billing_subscription set user_account_id= :id where description='JOB_SUB15017862665890_0_0_0_0_0'";
		Query updateQuery = entityManager.createNativeQuery(updateSQL);
		for (Object row : rows) {
			System.out.println((BigInteger)row);
			updateQuery.setParameter("id", (BigInteger)row).executeUpdate();
		}
		
	}
}

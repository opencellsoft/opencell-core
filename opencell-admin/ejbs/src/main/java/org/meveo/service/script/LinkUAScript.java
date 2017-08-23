/**
 * 
 */
package org.meveo.service.script;

import java.util.Map;

import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.billing.impl.EdrService;

/**
 * @author phung
 *
 */
public class LinkUAScript extends Script {

	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {
		EdrService edrService = (EdrService) getServiceInterface("EdrService");
		
		if (edrService != null) {
			EntityManager entityManager = edrService.getEntityManager();
			if (entityManager != null) {
				
				String getIDs = "SELECT ID FROM BILLING_BILLING_ACCOUNT WHERE CUSTOMER_ACCOUNT_ID =";
				String updateIDs = "UPDATE TABLE BILLING_SUBSCRIPTION SET USER_ACCOUNT_ID = ";
				entityManager.createNativeQuery(getIDs);
				
			}
		}
	}
}

package org.meveo.service.script;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.service.billing.impl.BillingRunExtensionService;
import org.meveo.service.billing.impl.BillingRunService;

/**
 * @author phung
 *
 */
public class ValidateBRScript extends Script {

	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {

		BillingRunService billingRunService = (BillingRunService) getServiceInterface("BillingRunService");

		BillingRunExtensionService billingRunExtensionService = (BillingRunExtensionService) getServiceInterface(
				"BillingRunExtensionService");

		if (billingRunService != null) {
			List<BillingRun> billingRuns = billingRunService.getbillingRuns(BillingRunStatusEnum.PREINVOICED,  BillingRunStatusEnum.POSTINVOICED);

			for (BillingRun billingRun : billingRuns) {

				try {
					billingRunExtensionService.updateBillingRun(billingRun, null, null,
							BillingRunStatusEnum.POSTVALIDATED, null);
				} catch (Exception e) {
					System.err.println("Error " + e.getMessage());
				}
			}

		}

	}
}

package org.meveo.api.billing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.meveo.model.billing.ProductInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author melyoussoufi
 *
 */
public class AppliesToValuesCalculator {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	Set<String> allAtvs = new HashSet<String>();

	public AppliesToValuesCalculator(List<Subscription> subscriptions) {

		for (Subscription subscription : subscriptions) {

			try {
				String subscriptionAppliesToValue = CustomFieldTemplateService.calculateAppliesToValue(subscription);
				subscription.setCftAppliesTo(subscriptionAppliesToValue);
				allAtvs.add(subscriptionAppliesToValue);
			} catch (CustomFieldException e) {
				log.error(e.getLocalizedMessage(), e);
			}

			List<Access> accessPoints = subscription.getAccessPoints();
			if (accessPoints != null) {
				for (Access ap : accessPoints) {
					try {
						String atv = CustomFieldTemplateService.calculateAppliesToValue(ap);
						ap.setCftAppliesTo(atv);
						allAtvs.add(atv);
					} catch (CustomFieldException e) {
						log.error(e.getLocalizedMessage(), e);
					}
				}
			}

			List<ServiceInstance> servicesInstances = subscription.getServiceInstances();
			if (servicesInstances != null) {
				for (ServiceInstance si : servicesInstances) {
					try {
						String atv = CustomFieldTemplateService.calculateAppliesToValue(si);
						si.setCftAppliesTo(atv);
						allAtvs.add(atv);
					} catch (CustomFieldException e) {
						log.error(e.getLocalizedMessage(), e);
					}
				}
			}

			List<ProductInstance> productsInstances = subscription.getProductInstances();
			if (productsInstances != null) {
				for (ProductInstance pi : productsInstances) {
					try {
						String atv = CustomFieldTemplateService.calculateAppliesToValue(pi);
						pi.setCftAppliesTo(atv);
						allAtvs.add(atv);
					} catch (CustomFieldException e) {
						log.error(e.getLocalizedMessage(), e);
					}

				}
			}
		}

	}

	public Set<String> getAllAtvs() {
		return allAtvs;
	}

}

package org.meveo.api.billing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.meveo.model.BusinessCFEntity;
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

	Set<String> allAtvs;

	public AppliesToValuesCalculator(List<Subscription> subscriptions) {
		
		allAtvs = new HashSet<String>();

		for (Subscription subscription : subscriptions) {

			try {
				addAppliesToValue(subscription);
			} catch (CustomFieldException e) {
				log.error(e.getLocalizedMessage(), e);
			}

			List<Access> accessPoints = subscription.getAccessPoints();
			if (accessPoints != null) {
				for (Access ap : accessPoints) {
					try {
						addAppliesToValue(ap);
					} catch (CustomFieldException e) {
						log.error(e.getLocalizedMessage(), e);
					}
				}
			}

			List<ServiceInstance> servicesInstances = subscription.getServiceInstances();
			if (servicesInstances != null) {
				for (ServiceInstance si : servicesInstances) {
					try {
						addAppliesToValue(si);
					} catch (CustomFieldException e) {
						log.error(e.getLocalizedMessage(), e);
					}
				}
			}

			List<ProductInstance> productsInstances = subscription.getProductInstances();
			if (productsInstances != null) {
				for (ProductInstance pi : productsInstances) {
					try {
						addAppliesToValue(pi);
					} catch (CustomFieldException e) {
						log.error(e.getLocalizedMessage(), e);
					}

				}
			}
		}

	}

	private void addAppliesToValue(BusinessCFEntity businessCFEntity) throws CustomFieldException {
		String appliesToValue = CustomFieldTemplateService.calculateAppliesToValue(businessCFEntity);
		if (appliesToValue != null) {
			businessCFEntity.setCftAppliesTo(appliesToValue);
			allAtvs.add(appliesToValue);
		} else {
			businessCFEntity.setCftAppliesTo(null);
		}
	}

	private void addAppliesToValue(Access access) throws CustomFieldException {
		String appliesToValue = CustomFieldTemplateService.calculateAppliesToValue(access);
		if (appliesToValue != null) {
			access.setCftAppliesTo(appliesToValue);
			allAtvs.add(appliesToValue);
		} else {
			access.setCftAppliesTo(null);
		}
	}

	public Set<String> getAllAtvs() {
		return allAtvs;
	}

}

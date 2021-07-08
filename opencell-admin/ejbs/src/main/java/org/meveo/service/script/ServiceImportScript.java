package org.meveo.service.script;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.model.billing.ServiceInstance;

public class ServiceImportScript extends Script {

	private static final String DATE_FORMAT_PATTERN = "dd/MM/yy";
	private static final String RECORD_VARIABLE_NAME = "record";

	private ServiceInstanceService serviceInstanceService = (ServiceInstanceService) getServiceInterface(
			"ServiceInstanceService");

	@Override
	public void execute(Map<String, Object> context) throws BusinessException {
		try {
			Map<String, Object> recordMap = (Map<String, Object>) context.get(RECORD_VARIABLE_NAME);
			if (recordMap != null && !recordMap.isEmpty()) {
				String OC_ENTITY = (String) recordMap.get("OC_ENTITY");
				if (!"ServiceInstance".equals(OC_ENTITY)) {
					throw new ValidationException("value of OC_ENTITY is not correct: " + OC_ENTITY);
				}
				String OC_ACTION = (String) recordMap.get("OC_ACTION");
				if (!Stream.of(ServiceInstanceActionEnum.values()).anyMatch(e -> e.toString().equals(OC_ACTION))) {
					throw new ValidationException("value of OC_ACTION is not correct: " + OC_ACTION);
				}
				ServiceInstanceActionEnum action = ServiceInstanceActionEnum.valueOf(OC_ACTION);

				String OC_Subscription_code = (String) recordMap.get("OC_Subscription_code");
				String OC_ServiceInstance_code = (String) recordMap.get("OC_ServiceInstance_code");
				List<ServiceInstance> serviceInstances = serviceInstanceService
						.findByCodeAndCodeSubscription(OC_ServiceInstance_code, OC_Subscription_code);
				if (serviceInstances == null || serviceInstances.isEmpty()) {
					throw new ValidationException("no ServiceInstanceFound for subscriptionCode/serviceInstanceCode: '"
							+ OC_Subscription_code + "'/'" + OC_ServiceInstance_code + "'");
				}
				ServiceInstance serviceInstance = serviceInstances.get(0);

				switch (action) {
				case INSTANTIATE:
					serviceInstanceService.serviceInstanciation(serviceInstance);
					break;
				case ACTIVATE:
					serviceInstanceService.serviceActivation(serviceInstance);
					break;
				case RESUME:
					serviceInstanceService.serviceReactivation(serviceInstance, new Date(), true, false);
					break;
				case SUSPEND:
					serviceInstanceService.serviceSuspension(serviceInstance, new Date());
					break;
				case TERMINATE:
					serviceInstanceService.cancelServiceTermination(serviceInstance);
					break;
				case UPDATE:
					updateService(recordMap, serviceInstance);
					break;
				default:
					break;
				}
			}
		} catch (Exception exception) {
			throw new BusinessException(exception);
		}
	}

	private void updateService(Map<String, Object> recordMap, ServiceInstance serviceInstance) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		String OC_ServiceInstance_subscriptionDate = (String) recordMap.get("OC_ServiceInstance_subscriptionDate");
		String OC_ServiceInstance_rateUntilDate = (String) recordMap.get("OC_ServiceInstance_rateUntilDate");
		String OC_ServiceInstance_endAgreementDate = (String) recordMap.get("OC_ServiceInstance_endAgreementDate");

		Date subscriptionDate = dateFormat.parse(OC_ServiceInstance_subscriptionDate);
		Date rateUntilDate = dateFormat.parse(OC_ServiceInstance_rateUntilDate);
		Date endAgreementDate = dateFormat.parse(OC_ServiceInstance_endAgreementDate);
		String OC_ServiceInstance_description = (String) recordMap.get("OC_ServiceInstance_description");
		String OC_ServiceInstance_quantity = (String) recordMap.get("OC_ServiceInstance_quantity");

		serviceInstance.setSubscriptionDate(subscriptionDate);
		serviceInstance.setRateUntilDate(rateUntilDate);
		serviceInstance.setEndAgreementDate(endAgreementDate);
		serviceInstance.setQuantity(new BigDecimal(OC_ServiceInstance_quantity));
		serviceInstance.setDescription(OC_ServiceInstance_description);
		recordMap.keySet().stream().filter(key -> key.startsWith("CF_"))
				.forEach(key -> serviceInstance.setCfValue(key.substring(3), recordMap.get(key)));
		serviceInstanceService.update(serviceInstance);
	}
	public enum ServiceInstanceActionEnum {
		INSTANTIATE, ACTIVATE, RESUME, SUSPEND, TERMINATE, UPDATE
	}
}
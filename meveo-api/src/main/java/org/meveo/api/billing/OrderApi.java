package org.meveo.api.billing;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.IncorrectServiceInstanceException;
import org.meveo.admin.exception.IncorrectSusbcriptionException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.crm.impl.SubscriptionTerminationReasonService;
import org.slf4j.Logger;
import org.tmf.dsmapi.catalog.resource.order.OrderItem;
import org.tmf.dsmapi.catalog.resource.order.Product;
import org.tmf.dsmapi.catalog.resource.order.ProductCharacteristic;
import org.tmf.dsmapi.catalog.resource.order.ProductOrder;

@Stateless
public class OrderApi {

	@Inject
	private Logger log;

	@Inject
	private SubscriptionService subscriptionService;
	@Inject
	private OfferTemplateService offerTemplateService;
	@Inject
	private UserAccountService userAccountService;
	@Inject
	private ServiceInstanceService serviceInstanceService;
	@Inject
	private SubscriptionTerminationReasonService subscriptionTerminationReasonService;

	public void create(ProductOrder productOrder, User currentUser) throws IncorrectSusbcriptionException,
			IncorrectServiceInstanceException, BusinessException, EntityDoesNotExistsException, Exception {
		List<OrderItem> orders = productOrder.getOrderItem();
		if (orders != null) {
			Provider provider = currentUser.getProvider();
			for (OrderItem orderItem : orders) {
				if (!StringUtils.isBlank(orderItem.getId()) && !StringUtils.isBlank(orderItem.getBillingAccount())
						&& orderItem.getBillingAccount() != null
						&& !StringUtils.isBlank(orderItem.getBillingAccount().getId())
						&& !StringUtils.isBlank(orderItem.getProductOffering())
						&& !StringUtils.isBlank(orderItem.getProductOffering().getId())
						&& !StringUtils.isBlank(productOrder.getOrderDate())
						&& !StringUtils.isBlank(productOrder.getDescription())) {
					UserAccount userAccount = userAccountService.findByCode(orderItem.getBillingAccount().getId(),
							provider);
					if (userAccount == null) {
						throw new EntityDoesNotExistsException(UserAccount.class, "userAccount");
					}
					log.debug("find userAccount by {}", orderItem.getBillingAccount().getId());

					OfferTemplate offerTemplate = offerTemplateService
							.findByCode(orderItem.getProductOffering().getId(), provider);
					if (offerTemplate == null) {
						throw new EntityDoesNotExistsException(OfferTemplate.class, "offerTemplate");
					}
					log.debug("find offerTemplate by {}", orderItem.getProductOffering().getId());
					Subscription subscription = subscriptionService.findByCode(orderItem.getId(), provider);
					log.debug("find subscription {}", subscription);

					if (subscription == null) {// sub is new
						subscription = new Subscription();
						subscription.setCode(orderItem.getId());
						subscription.setDescription(orderItem.getAppointment());
						subscription.setUserAccount(userAccount);
						subscription.setOffer(offerTemplate);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(productOrder.getOrderDate());
						calendar.set(Calendar.HOUR_OF_DAY, 0);
						calendar.set(Calendar.MINUTE, 0);
						calendar.set(Calendar.SECOND, 0);
						calendar.set(Calendar.MILLISECOND, 0);

						subscription.setSubscriptionDate(calendar.getTime());

						subscriptionService.create(subscription, currentUser, provider);
						// instantiate
						// activate

					}
					instanciationAndActiveService(subscription, orderItem, currentUser);
					if (subscription.getUserAccount().getCode().equalsIgnoreCase(orderItem.getBillingAccount().getId())
							&& subscription.getOffer().getCode()
									.equalsIgnoreCase(orderItem.getProductOffering().getId())) {
						List<ServiceInstance> serviceInstances = subscription.getServiceInstances();
						if (serviceInstances != null) {
							for (ServiceInstance serviceInstance : serviceInstances) {
								if (validateServiceInstance(serviceInstance, orderItem)) {
									activateService(serviceInstance, currentUser);
								} else {
									terminateService(serviceInstance, orderItem, currentUser);
								}

							}
						}
					}
				}
				continue;
			}
		}
	}

	private List<ProductCharacteristic> getProductCharacteristic(OrderItem orderItem) {
		List<ProductCharacteristic> productCharacteristic = null;
		Product product = orderItem.getProduct();
		if (!StringUtils.isBlank(product)) {
			productCharacteristic = product.getProductCharacteristic();
		}
		return productCharacteristic;
	}

	private ProductCharacteristic findProductCharacteristic(ServiceInstance serviceInstance, OrderItem orderItem) {
		ProductCharacteristic result = null;
		List<ProductCharacteristic> productCharacteristic = getProductCharacteristic(orderItem);
		if (productCharacteristic != null) {
			for (ProductCharacteristic c : productCharacteristic) {
				if (serviceInstance.getCode().equalsIgnoreCase(c.getValue())) {
					result = c;
					break;
				}
			}
		}
		return result;
	}

	private boolean validateServiceInstance(ServiceInstance serviceInstance, OrderItem orderItem) {
		ProductCharacteristic productCharacteristic = findProductCharacteristic(serviceInstance, orderItem);
		return productCharacteristic != null && "service".equalsIgnoreCase(productCharacteristic.getName());
	}

	private void terminateService(ServiceInstance selectedServiceInstance, OrderItem orderItem, User currentUser)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException, Exception {

		if (selectedServiceInstance.getStatus() == InstanceStatusEnum.ACTIVE) {
			SubscriptionTerminationReason subscriptionTerminationReason = subscriptionTerminationReasonService
					.findByCodeReason("TERM_REASON_1", currentUser.getProvider());
			if (subscriptionTerminationReason != null) {
				serviceInstanceService.terminateService(selectedServiceInstance, new Date(),
						subscriptionTerminationReason, currentUser);
			}
		}

	}

	private void activateService(ServiceInstance selectedServiceInstance, User currentUser)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
		if (selectedServiceInstance.getStatus() != InstanceStatusEnum.ACTIVE
				&& selectedServiceInstance.getStatus() != InstanceStatusEnum.TERMINATED) {
			serviceInstanceService.serviceActivation(selectedServiceInstance, null, null, currentUser);
		}
	}

	private void instanciationAndActiveService(Subscription subscription, OrderItem orderItem, User currentUser)
			throws IncorrectSusbcriptionException, IncorrectServiceInstanceException, BusinessException {
		if (orderItem.getProduct() != null && orderItem.getProduct().getProductCharacteristic() != null) {
			List<ProductCharacteristic> productCharacteristic = orderItem.getProduct().getProductCharacteristic();
			for (ProductCharacteristic c : productCharacteristic) {
				if ("service".equalsIgnoreCase(c.getName()) && !StringUtils.isBlank(c.getValue())) {
					ServiceInstance serviceInstance = serviceInstanceService
							.findActivatedByCodeAndSubscription(c.getValue(), subscription);
					if (serviceInstance == null) {
						ServiceTemplate serviceTemplate = findServiceTemplateByCode(subscription.getOffer(),
								c.getValue());
						if (serviceTemplate != null) {
							serviceInstance = new ServiceInstance();
							serviceInstance.setProvider(serviceTemplate.getProvider());
							serviceInstance.setCode(serviceTemplate.getCode());
							serviceInstance.setDescription(serviceTemplate.getDescription());
							serviceInstance.setServiceTemplate(serviceTemplate);
							serviceInstance.setSubscription(subscription);
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(new Date());
							calendar.set(Calendar.HOUR_OF_DAY, 0);
							calendar.set(Calendar.MINUTE, 0);
							calendar.set(Calendar.SECOND, 0);
							calendar.set(Calendar.MILLISECOND, 0);

							serviceInstance.setSubscriptionDate(calendar.getTime());
							serviceInstance.setQuantity(new BigDecimal(1));
							serviceInstanceService.serviceInstanciation(serviceInstance, currentUser);
						}
					}
					if (serviceInstance != null)
						activateService(serviceInstance, currentUser);
				}
			}
		}
	}

	private ServiceTemplate findServiceTemplateByCode(OfferTemplate offerTemplate, String serviceCode) {
		List<ServiceTemplate> serviceTemplates = offerTemplate.getServiceTemplates();
		if (serviceTemplates != null) {
			for (ServiceTemplate serviceTemplate : serviceTemplates) {
				if (serviceTemplate.getCode().equalsIgnoreCase(serviceCode)) {
					return serviceTemplate;
				}
			}
		}
		return null;
	}
}

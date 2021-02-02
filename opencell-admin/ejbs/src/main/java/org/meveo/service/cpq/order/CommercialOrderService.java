package org.meveo.service.cpq.order;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.order.OrderStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.SubscriptionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Tarik FA.
 * @version 11.0
 * @dateCreation 31/12/2020
 *
 */
@Stateless
public class CommercialOrderService extends PersistenceService<CommercialOrder>{


    @Inject
    private ServiceSingleton serviceSingleton;
    @Inject
    private ServiceInstanceService serviceInstanceService;
    @Inject
	private SubscriptionService subscriptionService;
    
	public CommercialOrder duplicate(CommercialOrder entity) {
		final CommercialOrder duplicate = new CommercialOrder(entity);
		detach(entity);
		duplicate.setStatus(CommercialOrderEnum.DRAFT.toString());
		duplicate.setStatusDate(Calendar.getInstance().getTime());
		create(duplicate);
		return duplicate;
	}
	
	public CommercialOrder validateOrder(CommercialOrder commercialOrder) {
		commercialOrder = serviceSingleton.assignCommercialOrderNumber(commercialOrder);
		
		commercialOrder.setStatus(CommercialOrderEnum.VALIDATED.toString());
		commercialOrder.setStatusDate(Calendar.getInstance().getTime());
		
        update(commercialOrder);
		return commercialOrder;
	}
	
	public CommercialOrder findByOrderNumer(String orderNumber) throws  BusinessException{
		QueryBuilder queryBuilder = new QueryBuilder(CommercialOrder.class, "co");
		queryBuilder.addCriterion("co.orderNumber", "=", orderNumber, false);
		Query query = queryBuilder.getQuery(getEntityManager());
		
		try {
			return (CommercialOrder) query.getSingleResult();
		}catch(NoResultException e) {
			return null;
		}catch(NonUniqueResultException e) {
			throw new BusinessException("Found many order number !!");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<CommercialOrder> findByOrderType(String orderTypeCode) {
		QueryBuilder queryBuilder = new QueryBuilder(CommercialOrder.class, "co", Arrays.asList("orderType"));
		queryBuilder.addCriterion("co.orderType.code", "=", orderTypeCode, false);
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	public CommercialOrder orderValidationProcess(Long orderId) {
		CommercialOrder order = findById(orderId);
		if(order == null)
			throw new EntityDoesNotExistsException(CommercialOrder.class, orderId);


		if (CommercialOrderEnum.COMPLETED.toString().equalsIgnoreCase(order.getStatus())) {
			return order;
		}

		for(OrderOffer offer : order.getOffers().stream().filter(o -> !o.getProducts().isEmpty()).collect(Collectors.toList())){
			Subscription subscription = new Subscription();
			subscription.setCode(UUID.randomUUID().toString());
			subscription.setSeller(order.getBillingAccount().getCustomerAccount().getCustomer().getSeller());

			subscription.setOffer(offer.getOfferTemplate());
			subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
			subscription.setSubscriptionDate(order.getOrderDate());
			subscription.setEndAgreementDate(null);
			subscription.setRenewed(true);
			subscription.setUserAccount(order.getUserAccount());
			subscription.setPaymentMethod(order.getBillingAccount().getCustomerAccount().getPaymentMethods().get(0));

			subscriptionService.create(subscription);

			for (OrderProduct product : offer.getProducts()){
				processProduct(subscription, product);
			}

			subscriptionService.update(subscription);
			subscriptionService.activateInstantiatedService(subscription);
		}

		order.setStatus(CommercialOrderEnum.COMPLETED.toString());
		order.setStatusDate(new Date());

		update(order);

		return order;
	}

	private void processProduct(Subscription subscription, OrderProduct orderProduct) {
		Product product = orderProduct.getProductVersion().getProduct();

		ServiceInstance serviceInstance = new ServiceInstance();
		serviceInstance.setCode(product.getCode());
		serviceInstance.setQuantity(orderProduct.getQuantity());
		serviceInstance.setSubscriptionDate(subscription.getSubscriptionDate());
		serviceInstance.setEndAgreementDate(subscription.getEndAgreementDate());
		serviceInstance.setRateUntilDate(subscription.getEndAgreementDate());
		serviceInstance.setProductVersion(orderProduct.getProductVersion());

		serviceInstance.setSubscription(subscription);

		AttributeInstance attributeInstance = null;
		for (OrderAttribute orderAttribute : orderProduct.getOrderAttributes()) {
				attributeInstance = new AttributeInstance(orderAttribute);
				attributeInstance.updateAudit(currentUser);
				attributeInstance.setServiceInstance(serviceInstance);
				attributeInstance.setSubscription(subscription);
				serviceInstance.addAttributeInstance(attributeInstance);
			}
		serviceInstanceService.cpqServiceInstanciation(serviceInstance, product,null, null, false);

			List<SubscriptionChargeInstance> oneShotCharges = serviceInstance.getSubscriptionChargeInstances();
			for (SubscriptionChargeInstance oneShotChargeInstance : oneShotCharges) {
				oneShotChargeInstance.setQuantity(serviceInstance.getQuantity());
				oneShotChargeInstance.setChargeDate(serviceInstance.getSubscriptionDate());
			}

			List<RecurringChargeInstance> recurringChargeInstances = serviceInstance.getRecurringChargeInstances();
			for (RecurringChargeInstance recurringChargeInstance : recurringChargeInstances) {
				recurringChargeInstance.setSubscriptionDate(serviceInstance.getSubscriptionDate());
				recurringChargeInstance.setQuantity(serviceInstance.getQuantity());
				recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
			}
			subscription.addServiceInstance(serviceInstance);
	}
}

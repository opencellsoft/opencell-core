package org.meveo.service.cpq.order;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.AdvancementRateIncreased;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.DiscountPlanService;

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
    @AdvancementRateIncreased
    protected Event<CommercialOrder> entityAdvancementRateIncreasedEventProducer;
    @Inject
    private ServiceInstanceService serviceInstanceService;
    @Inject
	private SubscriptionService subscriptionService;
    @Inject
    private DiscountPlanService discountPlanService;

	@Override
	public void create(CommercialOrder entity) throws BusinessException {
		if(StringUtils.isBlank(entity.getCode()))
			entity.setCode(UUID.randomUUID().toString());
		super.create(entity);
	}

	public CommercialOrder duplicate(CommercialOrder entity) {
		final CommercialOrder duplicate = new CommercialOrder(entity);
		detach(entity);
		duplicate.setStatus(CommercialOrderEnum.DRAFT.toString());
		duplicate.setStatusDate(Calendar.getInstance().getTime());
		create(duplicate);
		return duplicate;
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

	@Override
	public CommercialOrder update(CommercialOrder entity) throws BusinessException {
		if(StringUtils.isBlank(entity.getCode()))
			entity.setCode(UUID.randomUUID().toString());
		var currentOrder = entity.getOrderProgressTmp() != null ? entity.getOrderProgressTmp() : Integer.MAX_VALUE;
		var nextOrder = entity.getOrderProgress();
		super.update(entity);
		if(currentOrder < nextOrder) {
			entityAdvancementRateIncreasedEventProducer.fire(entity);
		}
		return entity;
	}

    public CommercialOrder updateWithoutProgressCheck(CommercialOrder entity) throws BusinessException {
		if(StringUtils.isBlank(entity.getCode()))
			entity.setCode(UUID.randomUUID().toString());
        return super.update(entity);
    }
    
    @SuppressWarnings("rawtypes")
	public List<OrderOffer> validateOffers(List<OrderOffer> validOffers) {
    	return validOffers.stream().filter(o -> {
			if(o.getProducts().isEmpty()) return false;
			for(OrderProduct quoteProduct: o.getProducts()) {
				if(quoteProduct.getProductVersion() != null) {
					var product = quoteProduct.getProductVersion().getProduct();
					for(ProductChargeTemplateMapping charge: product.getProductCharges()) {
						if(charge.getChargeTemplate() != null) {
							ChargeTemplate templateCharge = (ChargeTemplate) Hibernate.unproxy(charge.getChargeTemplate());
							if(templateCharge instanceof OneShotChargeTemplate) {
								var oneShotCharge = (OneShotChargeTemplate) templateCharge;
								if(oneShotCharge.getOneShotChargeTemplateType() != OneShotChargeTemplateTypeEnum.OTHER)
									return true;
							}else
								return true;
						}else
							return true;
					}
				}   
			}
			return false;
		}).collect(Collectors.toList());

    }
	public CommercialOrder validateOrder(CommercialOrder order, boolean orderCompleted) throws BusinessException {
		if (!(CommercialOrderEnum.DRAFT.toString().equalsIgnoreCase(order.getStatus()) || CommercialOrderEnum.FINALIZED.toString().equalsIgnoreCase(order.getStatus()) || CommercialOrderEnum.COMPLETED.toString().equals(order.getStatus()))) {
			throw new BusinessException("Can not validate order with status different than DRAFT or FINALIZED or COMPLETED, order id: " + order.getId());
		}
		
		List<OrderOffer> validOffers = validateOffers(order.getOffers());
		
		Set<DiscountPlan> discountPlans=new HashSet<DiscountPlan>();
		if(order.getDiscountPlan()!=null) {
			discountPlans.add(order.getDiscountPlan());
		}
		if(order.getOrderNumber() == null)
			order = serviceSingleton.assignCommercialOrderNumber(order);

		for(OrderOffer offer : validOffers){
			Subscription subscription = new Subscription();
			subscription.setSeller(order.getBillingAccount().getCustomerAccount().getCustomer().getSeller());
			subscription.setUserAccount(order.getUserAccount());
            subscription.setCode(subscription.getSeller().getCode() + "_" + subscription.getUserAccount().getCode() + "_" + offer.getId());
			subscription.setOffer(offer.getOfferTemplate());
			subscription.setSubscriptionDate(getSubscriptionDeliveryDate(order, offer));
			if (subscription.getSubscriptionDate().after(new Date())) {
				subscription.setStatus(SubscriptionStatusEnum.PENDING);
			}else {
				subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
			}
			subscription.setEndAgreementDate(null);
			subscription.setRenewed(true);
			subscription.setPaymentMethod(order.getBillingAccount().getCustomerAccount().getPaymentMethods().get(0));
			subscription.setOrder(order);
			subscription.setOrderOffer(offer);
			subscriptionService.create(subscription);
			if(offer.getDiscountPlan()!=null) {
				discountPlans.add(offer.getDiscountPlan());
			}
			
			for (OrderProduct product : offer.getProducts()){
				if(product.getDiscountPlan()!=null) {
					discountPlans.add(product.getDiscountPlan());
				}
				processProduct(subscription, product.getProductVersion().getProduct(), product.getQuantity(), product.getOrderAttributes(), product, null);
			}
			instanciateDiscountPlans(subscription, discountPlans);
			subscriptionService.update(subscription);
			subscriptionService.activateInstantiatedService(subscription);
		}

		order.setStatus(orderCompleted ? CommercialOrderEnum.COMPLETED.toString() : CommercialOrderEnum.VALIDATED.toString());
		order.setStatusDate(new Date());

		updateWithoutProgressCheck(order);

		return order;
	}
	
	private void instanciateDiscountPlans(Subscription subscription,Set<DiscountPlan>  discountPlans) {
	     // instantiate the discounts
            for (DiscountPlan dp : discountPlans) {
                subscriptionService.instantiateDiscountPlan(subscription, dp);
            }
        
	}

	public void processProduct(Subscription subscription, Product product, BigDecimal quantity, List<OrderAttribute> orderAttributes, OrderProduct orderProduct, Date deliveryDate) {

		ServiceInstance serviceInstance = new ServiceInstance();
		serviceInstance.setCode(product.getCode());
		serviceInstance.setQuantity(quantity);
		serviceInstance.setSubscriptionDate(subscription.getSubscriptionDate());
		serviceInstance.setEndAgreementDate(subscription.getEndAgreementDate());
		serviceInstance.setRateUntilDate(subscription.getEndAgreementDate());
		serviceInstance.setProductVersion(product.getCurrentVersion());
		if (deliveryDate != null) {
			serviceInstance.setDeliveryDate(deliveryDate);
		} else {
			serviceInstance.setDeliveryDate(getServiceDeliveryDate(subscription.getOrder(), subscription.getOrderOffer(), orderProduct));
		}

		serviceInstance.setSubscription(subscription);

		for (OrderAttribute orderAttribute : orderAttributes) {
			if(orderAttribute.getAttribute()!=null) {
			AttributeInstance attributeInstance = new AttributeInstance(orderAttribute, currentUser);
			attributeInstance.setServiceInstance(serviceInstance);
			attributeInstance.setSubscription(subscription);
			serviceInstance.addAttributeInstance(attributeInstance);
			}
		}
		serviceInstanceService.cpqServiceInstanciation(serviceInstance, product,null, null, false);

			List<SubscriptionChargeInstance> oneShotCharges = serviceInstance.getSubscriptionChargeInstances()
					.stream()
					.filter(oneShotChargeInstance -> ((OneShotChargeTemplate)oneShotChargeInstance.getChargeTemplate()).getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.SUBSCRIPTION)
					.map(oneShotChargeInstance -> {
						oneShotChargeInstance.setQuantity(serviceInstance.getQuantity());
						oneShotChargeInstance.setChargeDate(serviceInstance.getSubscriptionDate());
						return oneShotChargeInstance;
					}).collect(Collectors.toList());
			serviceInstance.getSubscriptionChargeInstances().clear();
			serviceInstance.getSubscriptionChargeInstances().addAll(oneShotCharges);


			List<RecurringChargeInstance> recurringChargeInstances = serviceInstance.getRecurringChargeInstances();
			for (RecurringChargeInstance recurringChargeInstance : recurringChargeInstances) {
				recurringChargeInstance.setSubscriptionDate(serviceInstance.getSubscriptionDate());
				recurringChargeInstance.setQuantity(serviceInstance.getQuantity());
				recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
			}
			if (serviceInstance.getDeliveryDate().after(new Date())) {
				serviceInstance.setStatus(InstanceStatusEnum.PENDING);
			}
			subscription.addServiceInstance(serviceInstance);
	}

	@Override
	public CommercialOrder findById(Long id) {
		CommercialOrder commercialOrder = super.findById(id);
		if(commercialOrder != null && commercialOrder.getCode() == null) {
			commercialOrder.setCode(UUID.randomUUID().toString());
			commercialOrder = super.update(commercialOrder);
		}
		return commercialOrder;
	}
	
	public Date getSubscriptionDeliveryDate(CommercialOrder order, OrderOffer offer) {
		if (offer.getDeliveryDate() != null) {
			return offer.getDeliveryDate();
		}else if (order.getDeliveryDate() != null) {
			return order.getDeliveryDate();
		}else {
			return order.getOrderDate();
		}
	}
	
	public Date getServiceDeliveryDate(CommercialOrder order, OrderOffer offer, OrderProduct product) {
		if (product != null && product.getDeliveryDate() != null) {
			return product.getDeliveryDate();
		}
		if (offer != null && offer.getDeliveryDate() != null) {
			return offer.getDeliveryDate();
		}else if (order != null && order.getDeliveryDate() != null) {
			return order.getDeliveryDate();
		}else {
			return new Date();
		}
	}
}

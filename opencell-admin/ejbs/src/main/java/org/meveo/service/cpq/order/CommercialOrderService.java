package org.meveo.service.cpq.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.meveo.model.RatingResult;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.OfferTemplateAttribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.model.cpq.commercial.OfferLineTypeEnum;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.cpq.commercial.ProductActionTypeEnum;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.DiscountPlanInstanceService;
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
    @Inject
    private OrderLotService orderLotService;
    @Inject
    private DiscountPlanInstanceService discountPlanInstanceService;

	@Override
	public void create(CommercialOrder entity) throws BusinessException {
		if(StringUtils.isBlank(entity.getCode()))
			entity.setCode(UUID.randomUUID().toString());
		super.create(entity);
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
    		if(o.getOrderLineType() == OfferLineTypeEnum.AMEND  || o.getOrderLineType() == OfferLineTypeEnum.TERMINATE) return true;
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
		UserAccount userAccount = order.getUserAccount() != null ? order.getUserAccount() : order.getOffers().get(0).getUserAccount();
		if(userAccount==null) {
			throw new BusinessException("Can not validate order with empty user account: " + order.getId());
		}
		
		List<OrderOffer> validOffers = validateOffers(order.getOffers());
		
		Set<DiscountPlan> discountPlans=new HashSet<DiscountPlan>();
		if(order.getDiscountPlan()!=null) {
			discountPlans.add(order.getDiscountPlan());
		}
		if(order.getOrderNumber() == null)
			order = serviceSingleton.assignCommercialOrderNumber(order);

		for(OrderOffer offer : validOffers){
			if(offer.getOrderLineType() == OfferLineTypeEnum.CREATE) {
				
				Subscription subscription = new Subscription();
				subscription.setSeller(getSelectedSeller(order));
				if(offer.getUserAccount()==null) {
					subscription.setUserAccount(userAccount);
				}else {
					subscription.setUserAccount(offer.getUserAccount());
				}
				subscription.setCode(subscription.getSeller().getCode() + "_" + userAccount.getCode() + "_" + offer.getId());
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
				subscription.setSubscriptionRenewal(offer.getOfferTemplate() != null ? offer.getOfferTemplate().getSubscriptionRenewal() : null);
				subscriptionService.create(subscription);
				if(offer.getDiscountPlan()!=null) {
					discountPlans.add(offer.getDiscountPlan());
				}
				
				for (OrderProduct product : offer.getProducts()){
					if(product.getDiscountPlan()!=null) {
						discountPlans.add(product.getDiscountPlan());
					}
					processProductWithDiscount(subscription, product);
				}
				instanciateDiscountPlans(subscription, discountPlans);
				subscriptionService.update(subscription);
				RatingResult ratingResult = subscriptionService.activateInstantiatedService(subscription);
				offer.setSubscription(subscription);
				
	            discountPlanService.calculateDiscountplanItems(new ArrayList<>(ratingResult.getEligibleFixedDiscountItems()), subscription.getSeller(), subscription.getUserAccount().getBillingAccount(), new Date(), new BigDecimal(1d), null , 
	            		subscription.getOffer().getCode(), subscription.getUserAccount().getWallet(), subscription.getOffer(), null, subscription, subscription.getOffer().getDescription(), false, null, null,DiscountPlanTypeEnum.OFFER);
				
			}else if(offer.getOrderLineType() == OfferLineTypeEnum.AMEND) {
				for (OrderProduct product : offer.getProducts()){
					//Create Action type
					if(product.getProductActionType() == ProductActionTypeEnum.CREATE) {
						processProduct(offer.getSubscription(), product.getProductVersion().getProduct(), product.getQuantity(), product.getOrderAttributes(), product, null);	
					}else {
						updateProduct(offer, product.getProductVersion().getProduct(), product.getQuantity(), product.getOrderAttributes(), product, null, product.getProductVersion().getProduct().getCode());	
					}
					//Activate Action type
					if(product.getProductActionType() == ProductActionTypeEnum.ACTIVATE) {
						List<ServiceInstance> existingServices = serviceInstanceService.findByCodeSubscriptionAndStatus(product.getProductVersion().getProduct().getCode(), offer.getSubscription());
						if (existingServices.size() < 1) {
							processProduct(offer.getSubscription(), product.getProductVersion().getProduct(), product.getQuantity(), product.getOrderAttributes(), product, null);
							ServiceInstance serviceInstanceToActivate = offer.getSubscription().getServiceInstances().stream().filter(serviceInstance -> product.getProductVersion().getProduct().getCode().equals(serviceInstance.getCode()))
									  .findAny()
									  .orElse(null);
							if(serviceInstanceToActivate != null) {
								serviceInstanceService.serviceActivation(serviceInstanceToActivate);
							}
						} else {
							List<ServiceInstance> services = serviceInstanceService.findByCodeSubscriptionAndStatus(product.getProductVersion().getProduct().getCode(), offer.getSubscription(), InstanceStatusEnum.INACTIVE, InstanceStatusEnum.PENDING, InstanceStatusEnum.SUSPENDED);
				            if (services.size() > 0) {
				            	ServiceInstance serviceInstanceToActivate = services.get(0);
								if (serviceInstanceToActivate.getStatus() == InstanceStatusEnum.SUSPENDED) {
									serviceInstanceService.serviceReactivation(serviceInstanceToActivate, product.getDeliveryDate(), true, false);					
								}else {
									serviceInstanceService.serviceActivation(serviceInstanceToActivate);
								}
				            }
						}
					}
					//Suspend Action type
					if(product.getProductActionType() == ProductActionTypeEnum.SUSPEND) {
						List<ServiceInstance> services = serviceInstanceService.findByCodeSubscriptionAndStatus(product.getProductVersion().getProduct().getCode(), offer.getSubscription(), InstanceStatusEnum.ACTIVE);
			            if (services.size() > 0) {
			            	ServiceInstance serviceInstanceToSuspend = services.get(0);
							serviceInstanceService.serviceSuspension(serviceInstanceToSuspend, product.getDeliveryDate());	
			            }
					}
					//Terminate Action type
					if(product.getProductActionType() == ProductActionTypeEnum.TERMINATE) {
						List<ServiceInstance> services = serviceInstanceService.findByCodeSubscriptionAndStatus(product.getProductVersion().getProduct().getCode(), offer.getSubscription(), InstanceStatusEnum.ACTIVE, InstanceStatusEnum.SUSPENDED);
			            if (services.size() > 0) {
			            	ServiceInstance serviceInstanceToTerminate = services.get(0);
							serviceInstanceService.terminateService(serviceInstanceToTerminate, product.getTerminationDate(), product.getTerminationReason(), order.getOrderNumber());	
				            }
			            }
				}
			}else if (offer.getOrderLineType() == OfferLineTypeEnum.TERMINATE) {
				Subscription subscription = offer.getSubscription();
				subscriptionService.terminateSubscription(subscription, offer.getTerminationDate(), offer.getTerminationReason(), order.getOrderNumber());
			}

		}
		order.setStatus(orderCompleted ? CommercialOrderEnum.COMPLETED.toString() : CommercialOrderEnum.VALIDATED.toString());
		order.setStatusDate(new Date());


		order.getOffers()
				.stream()
				.map(offer->offer.getProducts())
				.flatMap(Collection::stream)
				.filter(orderProduct -> orderProduct.getDeliveryDate() == null)
				.forEach(orderProduct -> {
					orderProduct.setDeliveryDate(new Date());
				});
		updateWithoutProgressCheck(order);

		return order;
	}
	
	private Seller getSelectedSeller(CommercialOrder order) {
    	Seller seller = null;
        if(order.getSeller()!=null) {
        	seller = order.getSeller();
        }
        else if(order.getQuote()!=null) {
        	if( order.getQuote().getSeller()!=null)
        		seller = order.getQuote().getSeller();
        }else {
        	seller = order.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
        }
        return seller;
    }
	
	public void instanciateDiscountPlans(Subscription subscription,Set<DiscountPlan>  discountPlans) {
	     // instantiate the discounts
            for (DiscountPlan dp : discountPlans) {
                subscriptionService.instantiateDiscountPlan(subscription, dp);
            }
        
	}
	
	public ServiceInstance processProductWithDiscount(Subscription subscription, OrderProduct orderProduct) {
		var serviceInstance = processProduct(subscription, orderProduct.getProductVersion().getProduct(), orderProduct.getQuantity(), orderProduct.getOrderAttributes(), orderProduct, null);
		serviceInstance.setQuoteProduct(orderProduct.getQuoteProduct());
		if(orderProduct.getDiscountPlan() != null) {
			DiscountPlanInstance dpi = new DiscountPlanInstance();
			dpi.assignEntityToDiscountPlanInstances(serviceInstance);
			var discountPlan = orderProduct.getDiscountPlan();
			dpi.setDiscountPlan(discountPlan);
			dpi.copyEffectivityDates(discountPlan);
			dpi.setDiscountPlanInstanceStatus(discountPlan);
			dpi.setCfValues(discountPlan.getCfValues());
			dpi.setServiceInstance(serviceInstance);
			dpi.setSubscription(subscription);
			discountPlanInstanceService.create(dpi, discountPlan);
			serviceInstance.getDiscountPlanInstances().add(dpi);
			orderProduct.getDiscountPlan().setStatus(DiscountPlanStatusEnum.IN_USE);
		}
		return serviceInstance;
	}

	public ServiceInstance processProduct(Subscription subscription, Product product, BigDecimal quantity, List<OrderAttribute> orderAttributes, OrderProduct orderProduct, Date deliveryDate) {

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
		
	Map<String,AttributeInstance> instantiatedAttributes=new HashMap<String, AttributeInstance>();
		
		for (OrderAttribute orderAttribute : orderAttributes) {
			if(orderAttribute.getAttribute()!=null  && !AttributeTypeEnum.EXPRESSION_LANGUAGE.equals(orderAttribute.getAttribute().getAttributeType())) {
			AttributeInstance attributeInstance = new AttributeInstance(orderAttribute, currentUser);
			attributeInstance.setServiceInstance(serviceInstance);
			attributeInstance.setSubscription(subscription);
			instantiatedAttributes.put(orderAttribute.getAttribute().getCode(),attributeInstance);
			}
		}
		//add missing attribute instances
		AttributeInstance attributeInstance=null;
		for(ProductVersionAttribute productVersionAttribute:product.getCurrentVersion().getAttributes()) {
			Attribute attribute=productVersionAttribute.getAttribute();
			if(!instantiatedAttributes.containsKey(attribute.getCode())) {
				attributeInstance = new AttributeInstance(currentUser);
				attributeInstance.setAttribute(attribute);
				attributeInstance.setServiceInstance(serviceInstance);
				attributeInstance.setSubscription(subscription);
			
			}else {
				attributeInstance=instantiatedAttributes.get(attribute.getCode());
			}
			if(!StringUtils.isBlank(productVersionAttribute.getDefaultValue())){
				switch (attribute.getAttributeType()) {
					case BOOLEAN:
						if(attributeInstance.getStringValue()==null)
							attributeInstance.setStringValue(productVersionAttribute.getDefaultValue());
						break;
					case TOTAL :
					case COUNT :
					case NUMERIC :
					case INTEGER:
						if(attributeInstance.getDoubleValue()==null)
							attributeInstance.setDoubleValue(Double.valueOf(productVersionAttribute.getDefaultValue()));
						break;
					case LIST_MULTIPLE_TEXT:
					case LIST_TEXT:
					case EXPRESSION_LANGUAGE :
					case TEXT:
						if(attributeInstance.getStringValue()==null)
							attributeInstance.setStringValue(productVersionAttribute.getDefaultValue());
						break;
				default:
					if(attributeInstance.getStringValue()==null)
						attributeInstance.setStringValue(productVersionAttribute.getDefaultValue());
					break;
				}
			}
			serviceInstance.addAttributeInstance(attributeInstance);
			
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
			}
			if (serviceInstance.getDeliveryDate().after(new Date())) {
				serviceInstance.setStatus(InstanceStatusEnum.PENDING);
			}
			subscription.addServiceInstance(serviceInstance);
			return serviceInstance;
	}
	
	public void updateProduct(OrderOffer offer, Product product, BigDecimal quantity, List<OrderAttribute> orderAttributes, OrderProduct orderProduct, Date deliveryDate, String subscriptionCode) {

		List<ServiceInstance> services = serviceInstanceService.findByCodeSubscriptionAndStatus(subscriptionCode, offer.getSubscription());
        if (services.size() > 0) {
	        ServiceInstance serviceInstance = services.get(0);
			serviceInstance.setCode(product.getCode());
			serviceInstance.setQuantity(quantity);
			serviceInstance.setSubscriptionDate(offer.getSubscription().getSubscriptionDate());
			serviceInstance.setEndAgreementDate(offer.getSubscription().getEndAgreementDate());
			serviceInstance.setRateUntilDate(offer.getSubscription().getEndAgreementDate());
			serviceInstance.setProductVersion(product.getCurrentVersion());
			if (deliveryDate != null) {
				serviceInstance.setDeliveryDate(deliveryDate);
			} else {
				serviceInstance.setDeliveryDate(getServiceDeliveryDate(offer.getSubscription().getOrder(), offer.getSubscription().getOrderOffer(), orderProduct));
			}
	
			serviceInstance.setSubscription(offer.getSubscription());
			
			serviceInstance.getAttributeInstances().clear();
			
		Map<String,AttributeInstance> instantiatedAttributes=new HashMap<String, AttributeInstance>();
			
			for (OrderAttribute orderAttribute : orderAttributes) {
				if(orderAttribute.getAttribute()!=null  && !AttributeTypeEnum.EXPRESSION_LANGUAGE.equals(orderAttribute.getAttribute().getAttributeType())) {
				AttributeInstance attributeInstance = new AttributeInstance(orderAttribute, currentUser);
				attributeInstance.setServiceInstance(serviceInstance);
				attributeInstance.setSubscription(offer.getSubscription());
				instantiatedAttributes.put(orderAttribute.getAttribute().getCode(),attributeInstance);
				}
			}
			//add missing attribute instances
			AttributeInstance attributeInstance=null;
			for(ProductVersionAttribute productVersionAttribute:product.getCurrentVersion().getAttributes()) {
				Attribute attribute=productVersionAttribute.getAttribute();
				if(!instantiatedAttributes.containsKey(attribute.getCode())) {
					attributeInstance = new AttributeInstance(currentUser);
					attributeInstance.setAttribute(attribute);
					attributeInstance.setServiceInstance(serviceInstance);
					attributeInstance.setSubscription(offer.getSubscription());
				
				}else {
					attributeInstance=instantiatedAttributes.get(attribute.getCode());
				}
				if(!StringUtils.isBlank(productVersionAttribute.getDefaultValue())){
					switch (attribute.getAttributeType()) {
					case BOOLEAN:
						if(attributeInstance.getBooleanValue()==null)
							attributeInstance.setBooleanValue(Boolean.valueOf(productVersionAttribute.getDefaultValue()));
						break;
					case NUMERIC:
						if(attributeInstance.getDoubleValue()==null)
							attributeInstance.setDoubleValue(Double.valueOf(productVersionAttribute.getDefaultValue()));
						break;
					default:
						if(attributeInstance.getStringValue()==null)
							attributeInstance.setStringValue(productVersionAttribute.getDefaultValue());
						break;
					}
				}
				serviceInstance.addAttributeInstance(attributeInstance);	
			}
		}
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
	
	public void processSubscriptionAttributes(Subscription subscription,OfferTemplate offer,List<OrderAttribute> orderAttributes) {
		Map<String,AttributeInstance> instantiatedAttributes=new HashMap<String, AttributeInstance>();
		
		for (OrderAttribute orderAttribute : orderAttributes) {
			if(orderAttribute.getAttribute()!=null && !AttributeTypeEnum.EXPRESSION_LANGUAGE.equals(orderAttribute.getAttribute().getAttributeType()) ) {
			AttributeInstance attributeInstance = new AttributeInstance(orderAttribute, currentUser); 
			attributeInstance.setSubscription(subscription);
			instantiatedAttributes.put(orderAttribute.getAttribute().getCode(),attributeInstance);
			}
		}
		//add missing attribute instances
		AttributeInstance attributeInstance=null;
		Attribute attribute=null;
		for(OfferTemplateAttribute offerAttribute:offer.getOfferAttributes()) {
		    attribute =offerAttribute.getAttribute();
			if(!instantiatedAttributes.containsKey(attribute.getCode())) {
				attributeInstance = new AttributeInstance(currentUser);
				attributeInstance.setAttribute(attribute);
				attributeInstance.setSubscription(subscription);
			
			}else {
				attributeInstance=instantiatedAttributes.get(attribute.getCode());
			}
			//set default value if value is null
			if(!StringUtils.isBlank(offerAttribute.getDefaultValue())){
				switch (attribute.getAttributeType()) {
				case BOOLEAN:
					if(attributeInstance.getStringValue()==null)
						attributeInstance.setStringValue(offerAttribute.getDefaultValue());
					break;	
				case TOTAL :
				case COUNT :
				case NUMERIC :
				case INTEGER:
					if(attributeInstance.getDoubleValue()==null)
						attributeInstance.setDoubleValue(Double.valueOf(offerAttribute.getDefaultValue()));
					break;
				case LIST_MULTIPLE_TEXT:
				case LIST_TEXT:
				case EXPRESSION_LANGUAGE :
				case TEXT:
					if(attributeInstance.getStringValue()==null)
						attributeInstance.setStringValue(offerAttribute.getDefaultValue());
					break;
				default:
					if(attributeInstance.getStringValue()==null)
						attributeInstance.setStringValue(offerAttribute.getDefaultValue());
					break;
				}
			}
			subscription.addAttributeInstance(attributeInstance);
			
		}
		 
	}
	
}

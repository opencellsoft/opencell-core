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
import javax.persistence.TypedQuery;

import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.cpq.xml.Offer;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.event.qualifier.AdvancementRateIncreased;
import org.meveo.model.RatingResult;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.*;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.cpq.AgreementDateSettingEnum;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.OfferTemplateAttribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.model.cpq.commercial.OfferLineTypeEnum;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.model.cpq.commercial.ProductActionTypeEnum;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.PriceVersionDateSettingEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.billing.impl.DiscountPlanInstanceService;
import org.meveo.service.billing.impl.OneShotChargeInstanceService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.PaymentMethodService;

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
    private DiscountPlanInstanceService discountPlanInstanceService;
    @Inject
    private OneShotChargeInstanceService oneShotChargeInstanceService;
	@Inject
	private AttributeService attributeService;

	@Inject
	private ProductService productService;

	@Inject
	private CustomerAccountService customerAccountService;

	@Inject
	private OfferTemplateService offerTemplateService;

	@Inject
	private OrderProductService orderProductService;
	
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
    		if(o.getOrderLineType() == OfferLineTypeEnum.AMEND  || o.getOrderLineType() == OfferLineTypeEnum.TERMINATE || o.getOrderLineType() == OfferLineTypeEnum.APPLY_ONE_SHOT) return true;
			if(o.getProducts().isEmpty()) return false;
			for(OrderProduct quoteProduct: o.getProducts()) {
				if(quoteProduct.getProductVersion() != null) {
					var product = productService.refreshOrRetrieve(quoteProduct.getProductVersion().getProduct());
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
		
		Set<DiscountPlan> discountPlans=new HashSet<>();
		if(order.getDiscountPlan()!=null) {
			discountPlans.add(order.getDiscountPlan());
		}
		if(order.getOrderNumber() == null)
			order = serviceSingleton.assignCommercialOrderNumber(order);

		CommercialOrderEnum orderStatus = orderCompleted ? CommercialOrderEnum.COMPLETED : CommercialOrderEnum.VALIDATED;

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
				CustomerAccount customerAccount = customerAccountService.refreshOrRetrieve(order.getBillingAccount().getCustomerAccount());
				subscription.setPaymentMethod(customerAccount.getPaymentMethods().get(0));
				subscription.setOrder(order);
				subscription.setOrderOffer(offer);
				subscription.setContract((offer.getContract() != null)? offer.getContract() : order.getContract());
				OfferTemplate offerTemplate = offerTemplateService.refreshOrRetrieve(offer.getOfferTemplate());
				subscription.setSubscriptionRenewal(offerTemplate != null ? offerTemplate.getSubscriptionRenewal() : null);
				subscription.setSalesPersonName(order.getSalesPersonName());
				subscription.setPriceList(order.getPriceList());
				subscriptionService.create(subscription);
				if(offer.getDiscountPlan()!=null) {
					discountPlans.add(offer.getDiscountPlan());
				}
				
				for (OrderProduct product : offer.getProducts()){
					processProductWithDiscount(subscription, product);
				}
				instanciateDiscountPlans(subscription, discountPlans);
				subscriptionService.update(subscription);
				serviceInstanceService.getEntityManager().flush();
				RatingResult ratingResult = subscriptionService.activateInstantiatedService(subscription);
				offer.setSubscription(subscription);
				
	            discountPlanService.calculateDiscountplanItems(new ArrayList<>(ratingResult.getEligibleFixedDiscountItems()), subscription.getSeller(), subscription.getUserAccount().getBillingAccount(), new Date(), new BigDecimal(1d), null , 
	            		subscription.getOffer().getCode(), subscription.getUserAccount().getWallet(), subscription.getOffer(), null, subscription, subscription.getOffer().getDescription(), false, null, null,DiscountPlanTypeEnum.OFFER);
				
			}else if(offer.getOrderLineType() == OfferLineTypeEnum.AMEND) {
				for (OrderProduct product : offer.getProducts()){
					//Create Action type
					if(product.getProductActionType() == ProductActionTypeEnum.CREATE) {
						processProduct(offer.getSubscription(), product.getProductVersion().getProduct(), product.getQuantity(), product.getOrderAttributes(), product, null);	
					}
					//Modify Action type
					if(product.getProductActionType() == ProductActionTypeEnum.MODIFY) {
						updateProduct(offer, product.getProductVersion().getProduct(), product.getQuantity(), product.getOrderAttributes(), product, null, product.getProductVersion().getProduct().getCode());	
					}
					//Activate Action type
					if(product.getProductActionType() == ProductActionTypeEnum.ACTIVATE) {
						serviceInstanceService.getEntityManager().flush();
						ServiceInstance serviceInstance = product.getServiceInstance();
						if (serviceInstance == null || serviceInstance.getStatus() == InstanceStatusEnum.TERMINATED) {
							ServiceInstance si = processProduct(offer.getSubscription(), product.getProductVersion().getProduct(), product.getQuantity(), product.getOrderAttributes(), product, null);
							if (si != null) {
								serviceInstanceService.serviceActivation(si);
							}
						} else if (serviceInstance != null && (serviceInstance.getStatus() == InstanceStatusEnum.INACTIVE
								|| serviceInstance.getStatus() == InstanceStatusEnum.PENDING
								|| serviceInstance.getStatus() == InstanceStatusEnum.SUSPENDED)) {
							updateProduct(offer, product.getProductVersion().getProduct(), product.getQuantity(), product.getOrderAttributes(), product, null, product.getProductVersion().getProduct().getCode());
							if (serviceInstance.getStatus() == InstanceStatusEnum.SUSPENDED) {
								serviceInstanceService.serviceReactivation(serviceInstance, product.getDeliveryDate(), true, false);
							} else {
								serviceInstanceService.serviceActivation(serviceInstance);
							}
						}
					}
					//Suspend Action type
					if(product.getProductActionType() == ProductActionTypeEnum.SUSPEND) {
						ServiceInstance serviceInstance = product.getServiceInstance();
						if (serviceInstance != null) {
			            	updateProduct(offer, product.getProductVersion().getProduct(), product.getQuantity(), product.getOrderAttributes(), product, null, product.getProductVersion().getProduct().getCode());	
			            	serviceInstanceService.serviceSuspension(serviceInstance, product.getDeliveryDate());
			            }
					}
					//Terminate Action type
					if(product.getProductActionType() == ProductActionTypeEnum.TERMINATE) {
						ServiceInstance serviceInstance = product.getServiceInstance();
						if (serviceInstance != null) {
			            	updateProduct(offer, product.getProductVersion().getProduct(), product.getQuantity(), product.getOrderAttributes(), product, null, product.getProductVersion().getProduct().getCode());	
			            	serviceInstanceService.terminateService(serviceInstance, product.getTerminationDate(), product.getTerminationReason(), order.getOrderNumber());
						}
					}
				}
			}else if (offer.getOrderLineType() == OfferLineTypeEnum.TERMINATE) {
				Subscription subscription = offer.getSubscription();
				subscriptionService.terminateSubscription(subscription, offer.getTerminationDate(), offer.getTerminationReason(), order.getOrderNumber());
			}else if (offer.getOrderLineType() == OfferLineTypeEnum.APPLY_ONE_SHOT) {
				Subscription subscription = offer.getSubscription();
				subscription.setOrder(order);
				for(OrderProduct quoteProduct: offer.getProducts()) {
					if(quoteProduct.getProductVersion() != null) {
						Product product = quoteProduct.getProductVersion().getProduct();
						for(ProductChargeTemplateMapping charge: product.getProductCharges()) {
							if(charge.getChargeTemplate() != null) {
								ChargeTemplate templateCharge = (ChargeTemplate) Hibernate.unproxy(charge.getChargeTemplate());
								if(templateCharge instanceof OneShotChargeTemplate) {
									OneShotChargeTemplate oneShotCharge = (OneShotChargeTemplate) templateCharge;
									if (oneShotCharge.getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.OTHER) {
										// Build ServiceInstance
										ServiceInstance serviceInstance = buildServiceInstanceForOSO(quoteProduct.getProductVersion().getProduct().getCode(),
												quoteProduct.getOrderAttributes(), subscription);

										// field to add : serviceInstance, operationDate & quantity = from orderProduct,
										oneShotChargeInstanceService.instantiateAndApplyOneShotCharge(subscription, serviceInstance,
												oneShotCharge, null, quoteProduct.getDeliveryDate() == null ? new Date() : quoteProduct.getDeliveryDate(),
                                                null, null, quoteProduct.getQuantity(),
												null, null, null, null,
												order.getOrderNumber(), null, true, ChargeApplicationModeEnum.SUBSCRIPTION);
									}
								}
							}
						}
					}   
				}
				
			}

		}
		order.setStatus(orderStatus.toString());
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

	/**
	 * Build ServiceInstance from OrderProduct
	 *
	 * @param productCode  productCode
	 * @param attributes list of attributes from orderProducts
	 * @param subscription subscription
	 * @return Virtual or Real ServiceInstance
	 */
	private ServiceInstance buildServiceInstanceForOSO(String productCode, List<OrderAttribute> attributes, Subscription subscription) {
		ServiceInstance serviceInstance = null;
		if (attributes != null) {
			serviceInstance = new ServiceInstance(); // Create a virtual ServiceInstance
			serviceInstance.setSubscription(subscription);
			// Product data
			Product product = productService.findByCode(productCode);
			ProductVersion pVersion = new ProductVersion();
			pVersion.setProduct(product);
			serviceInstance.setCode(product.getCode());
			serviceInstance.setProductVersion(pVersion);
			// add attributes
			for (OrderAttribute orderAttribute : attributes) {
				AttributeInstance attributeInstance = new AttributeInstance();
				attributeInstance.setAttribute(attributeService.findByCode(orderAttribute.getAttribute().getCode()));
				attributeInstance.setServiceInstance(serviceInstance);
				attributeInstance.setSubscription(subscription);
				attributeInstance.setDoubleValue(orderAttribute.getDoubleValue());
				attributeInstance.setStringValue(orderAttribute.getStringValue());
				attributeInstance.setBooleanValue(orderAttribute.getBooleanValue());
				attributeInstance.setDateValue(orderAttribute.getDateValue());
				serviceInstance.addAttributeInstance(attributeInstance);
			}
		} else { // no attributs provided in payload (OSO case for example)
			List<ServiceInstance> alreadyInstantiatedServices = null;
			if (StringUtils.isNotBlank(productCode)) {
				alreadyInstantiatedServices = serviceInstanceService.findByCodeSubscriptionAndStatus(productCode, subscription,
						InstanceStatusEnum.ACTIVE);
				if (alreadyInstantiatedServices == null || alreadyInstantiatedServices.isEmpty()) {
					throw new BusinessException("The product instance " + productCode + " doest not exist for this subscription or is not active");
				}
			} else {
				alreadyInstantiatedServices = subscription.getServiceInstances().stream()
						.filter(si -> si.getStatus() == InstanceStatusEnum.ACTIVE)
						.collect(Collectors.toList());
			}
			if (alreadyInstantiatedServices.size() > 1) {
					throw new BusinessException("More than one Product Instance found for Product '" + productCode
							+ "' and Subscription '" + subscription.getCode() + "'. Please provide productInstanceId field");
			} else {
				serviceInstance = alreadyInstantiatedServices.get(0);
			}
		}
		return serviceInstance;
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
		var serviceInstance = processProduct(subscription, orderProduct.getProductVersion().getProduct(), orderProduct.getQuantity(), orderProductService.refreshOrRetrieve(orderProduct).getOrderAttributes(), orderProduct, null);
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

	public ServiceInstance processProduct(Subscription subscription, Product product, BigDecimal quantity,
										  List<OrderAttribute> orderAttributes, OrderProduct orderProduct, Date deliveryDate) {
		ServiceInstance serviceInstance = new ServiceInstance();
		serviceInstance.setCode(product.getCode());
		serviceInstance.setQuantity(quantity);
		serviceInstance.setSubscriptionDate(subscription.getSubscriptionDate());
		if(!AgreementDateSettingEnum.MANUAL.equals(product.getAgreementDateSetting())) {
			serviceInstance.setEndAgreementDate(subscription.getEndAgreementDate());
		}
		serviceInstance.setRateUntilDate(subscription.getEndAgreementDate());
		ProductVersion productVersion = productService.getCurrentPublishedVersion(serviceInstance.getCode(),
						deliveryDate != null ? deliveryDate : serviceInstance.getSubscriptionDate())
				.orElseThrow(() -> new BusinessException("No product version found for subscription code: " + subscription.getCode()));
		serviceInstance.setProductVersion(productVersion);
		serviceInstance.setOrderNumber(subscription.getOrderNumber());
		if (deliveryDate != null) {
			serviceInstance.setDeliveryDate(deliveryDate);
		} else {
			serviceInstance.setDeliveryDate(getServiceDeliveryDate(subscription.getOrder(), subscription.getOrderOffer(), orderProduct));
		}

		serviceInstance.setSubscription(subscription);
		
		if(subscription.getOrder() != null) {
			if(subscription.getOrder().getQuote() != null) {
				if(PriceVersionDateSettingEnum.QUOTE.equals(product.getPriceVersionDateSetting())) {
					serviceInstance.setPriceVersionDate(subscription.getOrder().getQuote().getQuoteDate());
				}
			}
		}
		
		Map<String,AttributeInstance> instantiatedAttributes = new HashMap<>();
		
		if(orderAttributes != null && orderAttributes.size() > 0) {
			for (OrderAttribute orderAttribute : orderAttributes) {
				if(orderAttribute.getAttribute()!=null  && !AttributeTypeEnum.EXPRESSION_LANGUAGE.equals(orderAttribute.getAttribute().getAttributeType())) {
					AttributeInstance attributeInstance = new AttributeInstance(orderAttribute, currentUser);
					attributeInstance.setServiceInstance(serviceInstance);
					attributeInstance.setSubscription(subscription);
					instantiatedAttributes.put(orderAttribute.getAttribute().getCode(),attributeInstance);
				}
			}
		}
		
		//add missing attribute instances
		AttributeInstance attributeInstance=null;
		for(ProductVersionAttribute productVersionAttribute : productVersion.getAttributes()) {
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
					.filter(
							oneShotChargeInstance ->
									((OneShotChargeTemplate) oneShotChargeInstance.getChargeTemplate()).getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.SUBSCRIPTION
											|| ((OneShotChargeTemplate) oneShotChargeInstance.getChargeTemplate()).getOneShotChargeTemplateType() == OneShotChargeTemplateTypeEnum.INVOICING_PLAN
					)
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
			return serviceInstance;
	}
	
	public void updateProduct(OrderOffer offer, Product product, BigDecimal quantity, List<OrderAttribute> orderAttributes, OrderProduct orderProduct, Date deliveryDate, String subscriptionCode) {

		ServiceInstance serviceInstance = orderProduct.getServiceInstance();
		if (serviceInstance != null) {
	        serviceInstance.setCode(product.getCode());
			serviceInstance.setQuantity(quantity);
			serviceInstance.setSubscriptionDate(offer.getSubscription().getSubscriptionDate());
			if(AgreementDateSettingEnum.INHERIT.equals(orderProduct.getProductVersion().getProduct().getAgreementDateSetting())) {
				serviceInstance.setEndAgreementDate(offer.getSubscription().getEndAgreementDate());
			}
			serviceInstance.setRateUntilDate(offer.getSubscription().getEndAgreementDate());
			ProductVersion productVersion = productService.getCurrentPublishedVersion(serviceInstance.getCode(),
							deliveryDate != null ? deliveryDate : serviceInstance.getSubscriptionDate()).orElse(null);
			serviceInstance.setProductVersion(productVersion);
			if (deliveryDate != null) {
				serviceInstance.setDeliveryDate(deliveryDate);
			} else {
				serviceInstance.setDeliveryDate(getServiceDeliveryDate(offer.getSubscription().getOrder(), offer.getSubscription().getOrderOffer(), orderProduct));
			}

			serviceInstance.setSubscription(offer.getSubscription());

			serviceInstance.getAttributeInstances().clear();
			Map<String,AttributeInstance> instantiatedAttributes=new HashMap<>();
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
			for(ProductVersionAttribute productVersionAttribute : productVersion.getAttributes()) {
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
		Map<String,AttributeInstance> instantiatedAttributes=new HashMap<>();
		
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

	    public List<CommercialOrder> findCommercialOrders(BillingCycle billingCycle) {
        try {
            QueryBuilder qb = new QueryBuilder(CommercialOrder.class, "co", null);
			if(billingCycle.getFilters() != null && !billingCycle.getFilters().isEmpty()) {
				qb.addPaginationConfiguration(new PaginationConfiguration(billingCycle.getFilters()));

			} else {
				qb.addCriterionEntity("co.billingCycle.id", billingCycle.getId());
			}
            return (List<CommercialOrder>) qb.getQuery(getEntityManager()).getResultList();

        } catch (Exception ex) {
            log.error("failed to find billable commercial orders", ex);
        }

        return null;
    }

	public List<CommercialOrder> findByCodeOrExternalId(Collection<String> codeOrExternalId) {

        TypedQuery<CommercialOrder> query = getEntityManager().createNamedQuery("CommercialOrder.listByCodeOrExternalId", CommercialOrder.class).setParameter("code", codeOrExternalId);

        return query.getResultList();
    }

	public CommercialOrder findByCodeOrExternalId(String codeOrExternalId) {

        TypedQuery<CommercialOrder> query = getEntityManager().createNamedQuery("CommercialOrder.findByCodeOrExternalId", CommercialOrder.class).setParameter("code", codeOrExternalId);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            log.debug("No {} of code/externalId {} found", CommercialOrder.class.getSimpleName(), codeOrExternalId);
            return null;
        } catch (NonUniqueResultException e) {
            log.error("More than one entity of type {} with code/externalId {} found", CommercialOrder.class, codeOrExternalId);
            return null;
        }
    }

	public List<CommercialOrder> findWithInvoicingPlanNotNull() {
		TypedQuery<CommercialOrder> query = getEntityManager().createNamedQuery("CommercialOrder.findWithInvoicingPlan", CommercialOrder.class);
		return query.getResultList();
	}
}

package org.meveo.service.script;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.AttributeInstance;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionChargeInstance;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.model.cpq.commercial.OrderAttribute;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.ServiceSingleton;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.cpq.order.CommercialOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderValidationScript extends Script {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8104026721836872604L;

	private static final Logger log = LoggerFactory.getLogger(OrderValidationScript.class);

    private CommercialOrderService commercialOrderService = (CommercialOrderService) getServiceInterface("CommercialOrderService");
    private SubscriptionService subscriptionService = (SubscriptionService) getServiceInterface("SubscriptionService");
    private ServiceInstanceService serviceInstanceService = (ServiceInstanceService) getServiceInterface("ServiceInstanceService");
    private ServiceSingleton serviceSingleton = (ServiceSingleton) getServiceInterface("ServiceSingleton");

    @Override
    public void execute(Map<String, Object> context) {
        log.info(">>> Method context >>>");
        context.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            log.info("{}={}", entry.getKey(), entry.getValue());
        });
        CommercialOrder order = (CommercialOrder) context.get("commercialOrder");
        MeveoUser currentUser = (MeveoUser) context.get(Script.CONTEXT_CURRENT_USER);

        if (!CommercialOrderEnum.DRAFT.toString().equalsIgnoreCase(order.getStatus())) {
            throw new BusinessException("Can not validate order with status different then DRAFT, order id: " + order.getId());
        }

        List<OrderOffer> validOffers = commercialOrderService.validateOffers(order.getOffers());

        if(order.getOrderNumber() == null)
            order = serviceSingleton.assignCommercialOrderNumber(order);

        for(OrderOffer offer : validOffers){
            Subscription subscription = new Subscription();
            subscription.setSeller(getSelectedSeller(order));

            subscription.setOffer(offer.getOfferTemplate());
            subscription.setStatus(SubscriptionStatusEnum.ACTIVE);
            subscription.setSubscriptionDate(order.getOrderDate());
            subscription.setEndAgreementDate(null);
            subscription.setRenewed(true);
            subscription.setUserAccount(order.getUserAccount());
            subscription.setPaymentMethod(order.getBillingAccount().getCustomerAccount().getPaymentMethods().get(0));
            subscription.setCode(subscription.getSeller().getCode() + "_" + subscription.getUserAccount().getCode() + "_" + offer.getId());
            subscription.setOrder(order);
            subscriptionService.create(subscription);

            for (OrderProduct product : offer.getProducts()){
                processProduct(subscription, product, currentUser);
            }

            subscriptionService.update(subscription);
            subscriptionService.activateInstantiatedService(subscription);
        }

        order.setStatus(CommercialOrderEnum.VALIDATED.toString());
        order.setStatusDate(new Date());
        order = commercialOrderService.update(order);
        context.put(Script.RESULT_VALUE, order);
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

    private void processProduct(Subscription subscription, OrderProduct orderProduct, MeveoUser currentUser) {
        Product product = orderProduct.getProductVersion().getProduct();

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setCode(product.getCode());
        serviceInstance.setQuantity(orderProduct.getQuantity());
        serviceInstance.setSubscriptionDate(subscription.getSubscriptionDate());
        serviceInstance.setEndAgreementDate(subscription.getEndAgreementDate());
        serviceInstance.setRateUntilDate(subscription.getEndAgreementDate());
        serviceInstance.setProductVersion(orderProduct.getProductVersion());

        serviceInstance.setSubscription(subscription);
        serviceInstance.setQuoteProduct(orderProduct.getQuoteProduct());

        AttributeInstance attributeInstance = null;
        for (OrderAttribute orderAttribute : orderProduct.getOrderAttributes()) {
            attributeInstance = new AttributeInstance(orderAttribute, currentUser);
            attributeInstance.updateAudit(currentUser);
            attributeInstance.setServiceInstance(serviceInstance);
            attributeInstance.setSubscription(subscription);
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
            recurringChargeInstance.setStatus(InstanceStatusEnum.ACTIVE);
        }
        subscription.addServiceInstance(serviceInstance);
    }
}
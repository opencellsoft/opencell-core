package org.meveo.service.script;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.SubscriptionStatusEnum;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.commercial.CommercialOrder;
import org.meveo.model.cpq.commercial.CommercialOrderEnum;
import org.meveo.model.cpq.commercial.OrderOffer;
import org.meveo.model.cpq.commercial.OrderProduct;
import org.meveo.security.MeveoUser;
import org.meveo.service.billing.impl.DiscountPlanInstanceService;
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
    private DiscountPlanInstanceService discountPlanInstanceService = (DiscountPlanInstanceService) getServiceInterface("DiscountPlanInstanceService");

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
        Set<DiscountPlan> discountPlans=new HashSet<DiscountPlan>();
		if(order.getDiscountPlan()!=null) {
			discountPlans.add(order.getDiscountPlan());
		}
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
            commercialOrderService.processSubscriptionAttributes(subscription, offer.getOfferTemplate(), offer.getOrderAttributes());
            subscriptionService.create(subscription);

            if(offer.getDiscountPlan()!=null) {
				discountPlans.add(offer.getDiscountPlan());
			}

			for (OrderProduct product : offer.getProducts()){
				commercialOrderService.processProductWithDiscount(subscription, product);
			}
			commercialOrderService.instanciateDiscountPlans(subscription, discountPlans);
			subscriptionService.update(subscription);
			subscriptionService.activateInstantiatedService(subscription);
        }

        order.setStatus(CommercialOrderEnum.VALIDATED.toString());
        order.setStatusDate(new Date());
        order.setOrderProgressTmp(order.getOrderProgress());
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

}
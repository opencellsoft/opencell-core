package org.meveo.service.crm.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.BusinessEntity;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.billing.impl.UserAccountService;
import org.meveo.service.billing.impl.WalletTemplateService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.communication.impl.EmailTemplateService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.slf4j.Logger;

@Stateless
public class CustomEntitySearchService {

	@Inject
	private Logger log;
	
	@Inject
	private TaxService taxService;
	@Inject
	private SellerService sellerService;
	@Inject
	private OfferTemplateService offerTemplateService;
	@Inject
	private UserAccountService userAccountService;
	@Inject
	private PricePlanMatrixService pricePlanMatrixService;
	@Inject
	private BillingAccountService billingAccountService;
	@Inject
	private CustomerAccountService customerAccountService;
	@Inject
	private OneShotChargeTemplateService oneShotChargeTemplateService;
	@Inject
	private ServiceTemplateService serviceTemplateService;
	@Inject
	private WalletTemplateService walletTemplateService;
	@Inject
	private SubscriptionService subscriptionService;
	@Inject
	private RecurringChargeTemplateService recurringChargeTemplateService;
	@Inject
	private ServiceInstanceService serviceInstanceService;
	@Inject
	private CustomerService customerService;
	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;
	@Inject
	private TriggeredEDRTemplateService triggeredEDRTemplateService;
	@SuppressWarnings("rawtypes")
	@Inject
	private CounterTemplateService counterTemplateService;
	@Inject
	private CalendarService calendarService;
	@Inject
	private ProviderContactService providerContactService;
	@Inject
	private DiscountPlanService discountPlanService;
	@Inject
	private EmailTemplateService emailTemplateService;
	
	public BusinessEntity findCustomEntity(String clazzName,String ids){
		log.debug("find customEntity by class {} - id {}",clazzName,ids);
		Long id=null;
		try {
			id = Long.parseLong(ids);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		if(clazzName.indexOf("Tax")>=0){
			return taxService.findById(id);
		}else if(clazzName.indexOf("Seller")>=0){
			return sellerService.findById(id);
		}else if(clazzName.indexOf("OfferTemplate")>=0){
			return offerTemplateService.findById(id);
		}else if(clazzName.indexOf("UserAccount")>=0){
			return userAccountService.findById(id);
		}else if(clazzName.indexOf("BillingAccount")>=0){
			return billingAccountService.findById(id);
		}else if(clazzName.indexOf("PricePlanMatrix")>=0){
			return pricePlanMatrixService.findById(id);
		}else if(clazzName.indexOf("CustomerAccount")>=0){
			return customerAccountService.findById(id);
		}else if(clazzName.indexOf("OneShotChargeTemplate")>=0){
			return oneShotChargeTemplateService.findById(id);
		}else if(clazzName.indexOf("ServiceTemplate")>=0){
			return serviceTemplateService.findById(id);
		}else if(clazzName.indexOf("WalletTemplate")>=0){
			return walletTemplateService.findById(id);
		}else if(clazzName.indexOf("Subscription")>=0){
			return subscriptionService.findById(id);
		}else if(clazzName.indexOf("RecurringChargeTemplate")>=0){
			return recurringChargeTemplateService.findById(id);
		}else if(clazzName.indexOf("ServiceInstance")>=0){
			return serviceInstanceService.findById(id);
		}else if(clazzName.indexOf("Customer")>=0){
			return customerService.findById(id);
		}else if(clazzName.indexOf("UsageChargeTemplate")>=0){
			return usageChargeTemplateService.findById(id);
		}else if(clazzName.indexOf("TriggeredEDRTemplate")>=0){
			return triggeredEDRTemplateService.findById(id);
		}else if(clazzName.indexOf("CounterTemplate")>=0){
			return (BusinessEntity) counterTemplateService.findById(id);
		}else if(clazzName.indexOf("Calendar")>=0){
			return calendarService.findById(id);
		}else if(clazzName.indexOf("ProviderContact")>=0){
			return providerContactService.findById(id);
		}else if(clazzName.indexOf("DiscountPlan")>=0){
			return discountPlanService.findById(id);
		}else if(clazzName.indexOf("EmailTemplate")>=0){
			return emailTemplateService.findById(id);
		}
		log.debug("No found a business entity!");
		return null;
	}
}

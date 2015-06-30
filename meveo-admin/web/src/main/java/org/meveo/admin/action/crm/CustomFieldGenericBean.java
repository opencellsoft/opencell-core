package org.meveo.admin.action.crm;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.Calendar;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.catalog.WalletTemplate;
import org.meveo.model.communication.email.EmailTemplate;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.crm.ProviderContact;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
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
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.crm.impl.ProviderContactService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.cdi.ViewScoped;
import org.slf4j.Logger;

@Named
@ViewScoped
public class CustomFieldGenericBean extends BaseBean<BusinessEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private Logger log;

	@Inject
	@CurrentProvider
	protected Provider currentProvider;

	@Inject
	private SellerService sellerService;
	@Inject
	private TaxService taxService;
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
//	@Inject
//	private ServiceInstanceService serviceInstanceService;
	@Inject
	private CustomerService customerService;
	@Inject
	private UsageChargeTemplateService usageChargeTemplateService;
	@Inject
	private TriggeredEDRTemplateService triggeredEDRTemplateService;
	@Inject
	private CounterTemplateService<CounterTemplate> counterTemplateService;
	@Inject
	private CalendarService calendarService;
	@Inject
	private ProviderContactService providerContactService;
	@Inject
	private DiscountPlanService discountPlanService;
	@Inject
	private EmailTemplateService emailTemplateService;
	
	
	public List<Tax> autocompleteCustomEntityTax(String wildcode) {
		return taxService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<Seller> autocompleteCustomEntitySeller(String wildcode) {
		return sellerService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<OfferTemplate> autocompleteCustomEntityOfferTemplate(String wildcode) {
		return offerTemplateService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<UserAccount> autocompleteCustomEntityUserAccount(String wildcode) {
		return userAccountService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<PricePlanMatrix> autocompleteCustomEntityPricePlanMatrix(String wildcode) {
		return pricePlanMatrixService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<BillingAccount> autocompleteCustomEntityBillingAccount(String wildcode) {
		return billingAccountService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<CustomerAccount> autocompleteCustomEntityCustomerAccount(String wildcode) {
		return customerAccountService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<OneShotChargeTemplate> autocompleteCustomEntityOneShotChargeTemplate(String wildcode) {
		return oneShotChargeTemplateService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<ServiceTemplate> autocompleteCustomEntityServiceTemplate(String wildcode) {
		return serviceTemplateService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<WalletTemplate> autocompleteCustomEntityWalletTemplate(String wildcode) {
		return walletTemplateService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<Subscription> autocompleteCustomEntitySubscription(String wildcode) {
		return subscriptionService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<RecurringChargeTemplate> autocompleteCustomEntityRecurringChargeTemplate(String wildcode) {
		return recurringChargeTemplateService.findByCodeLike(wildcode, this.currentProvider);
	}
//	public List<ServiceInstance> autocompleteCustomEntityServiceInstance(String wildcode) {
//		return serviceInstanceService.findByCodeLike(wildcode, this.currentProvider);
//	}
	public List<Customer> autocompleteCustomEntityCustomer(String wildcode) {
		return customerService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<UsageChargeTemplate> autocompleteCustomEntityUsageChargeTemplate(String wildcode) {
		return usageChargeTemplateService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<TriggeredEDRTemplate> autocompleteCustomEntityTriggeredEDRTemplate(String wildcode) {
		return triggeredEDRTemplateService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<CounterTemplate> autocompleteCustomEntityCounterTemplate(String wildcode) {
		return counterTemplateService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<Calendar> autocompleteCustomEntityCalendar(String wildcode) {
		return calendarService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<ProviderContact> autocompleteCustomEntityProviderContact(String wildcode) {
		return providerContactService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<DiscountPlan> autocompleteCustomEntityDiscountPlan(String wildcode) {
		return discountPlanService.findByCodeLike(wildcode, this.currentProvider);
	}
	public List<EmailTemplate> autocompleteCustomEntityEmailTemplate(String wildcode) {
		return emailTemplateService.findByCodeLike(wildcode, this.currentProvider);
	}

	@Override
	protected IPersistenceService<BusinessEntity> getPersistenceService() {
		// TODO Auto-generated method stub
		return null;
	}

}
package org.meveo.admin.action.crm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class CustomFieldTemplateBean extends CustomFieldBean<CustomFieldTemplate> {

    private static final long serialVersionUID = 9099292371182275568L;

    @Inject
    private CustomFieldTemplateService cftService;

    public CustomFieldTemplateBean() {
        super(CustomFieldTemplate.class);
    }

    @Override
    public CustomFieldTemplate initEntity() {
        CustomFieldTemplate customFieldTemplate = super.initEntity();

        extractMapTypeFieldFromEntity(customFieldTemplate.getListValues(), "listValues");

        return customFieldTemplate;
    }
    
    @Override
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        updateMapTypeFieldInEntity(entity.getListValues(), "listValues");

        CustomFieldTemplate cfDuplicate = customFieldTemplateService.findByCodeAndAccountLevel(entity.getCode(), entity.getAccountLevel(), getCurrentProvider());
        if (cfDuplicate != null && !cfDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "customFieldTemplate.alreadyExists"));
            return null;
        }

        return super.saveOrUpdate(killConversation);
    }

    @Override
    protected IPersistenceService<CustomFieldTemplate> getPersistenceService() {
        return cftService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("provider");
    }
    
    /**
     * Get a list of account levels that can be created
     * @return
     */
    public List<AccountLevelEnum> getAccountLevelsNoTimer(){
        
        List<AccountLevelEnum> enumValues = new ArrayList<AccountLevelEnum>();
        for (AccountLevelEnum enumValue : AccountLevelEnum.values()) {
            if (enumValue != AccountLevelEnum.TIMER){
                enumValues.add(enumValue);
            }
        }
        return enumValues;
    }
    
    @Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        if (!searchCriteria.containsKey("accountLevel")){
            searchCriteria.put("ne accountLevel", AccountLevelEnum.TIMER);
        }
        return super.supplementSearchCriteria(searchCriteria);
    }
    
    public List<String> autocompleteClassNames(String query) {
    	String qLower=query.toLowerCase();
    	 List<String> clazzNames = new ArrayList<String>();
         for (String clazz : clazzes) {
             if (clazz.toLowerCase().contains(qLower)) {
                 clazzNames.add(clazz);
             }
         }
         return clazzNames;
    }
    // business and observable
    public static final List<String> clazzes= Arrays.asList("org.meveo.model.billing.Tax",
			"org.meveo.model.admin.Seller",
			"org.meveo.model.catalog.OfferTemplate",
			"org.meveo.model.billing.UserAccount",
			"org.meveo.model.catalog.PricePlanMatrix",
			"org.meveo.model.billing.BillingAccount",
			"org.meveo.model.payments.CustomerAccount",
			"org.meveo.model.catalog.OneShotChargeTemplate",
			"org.meveo.model.catalog.ServiceTemplate",
			"org.meveo.model.catalog.WalletTemplate",
			"org.meveo.model.billing.Subscription",
			"org.meveo.model.catalog.RecurringChargeTemplate",
//			"org.meveo.model.billing.ServiceInstance",
			"org.meveo.model.crm.Customer",
			"org.meveo.model.catalog.UsageChargeTemplate",
			"org.meveo.model.catalog.TriggeredEDRTemplate",
			"org.meveo.model.catalog.CounterTemplate",
			"org.meveo.model.catalog.Calendar",
			"org.meveo.model.crm.ProviderContact",
			"org.meveo.model.catalog.DiscountPlan",
			"org.meveo.model.communication.email.EmailTemplate");

    /**
     * get storage types for storage list and map
     */
    public List<CustomFieldStorageTypeEnum> getListEnum(){
    	return Arrays.asList(CustomFieldStorageTypeEnum.LIST,CustomFieldStorageTypeEnum.MAP);
    }
}
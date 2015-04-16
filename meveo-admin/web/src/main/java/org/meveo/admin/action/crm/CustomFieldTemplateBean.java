package org.meveo.admin.action.crm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class CustomFieldTemplateBean extends BaseBean<CustomFieldTemplate> {

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
            searchCriteria.put("not-accountLevel", AccountLevelEnum.TIMER);
        }
        return super.supplementSearchCriteria(searchCriteria);
    }
}
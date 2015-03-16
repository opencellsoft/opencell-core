package org.meveo.admin.action.crm;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.Subscription;
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
        
    	if (customFieldTemplateService.findByCodeAndAccountLevel(entity.getCode(),entity.getAccountLevel(),getCurrentProvider())!=null) {
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
}
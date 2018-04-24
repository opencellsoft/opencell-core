package org.meveo.admin.action;

import javax.inject.Inject;

import org.meveo.admin.action.admin.custom.CustomFieldDataEntryBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

/**
 * Backing bean for support custom field instances value data entry
 * 
 * @param <T> the type of the value
 */
public abstract class CustomFieldBean<T extends IEntity> extends BaseBean<T> {

    private static final long serialVersionUID = 1L;
    //
    // private CustomFieldTemplate customFieldSelectedTemplate;
    //
    // private CustomFieldInstance customFieldSelectedPeriod;
    //
    // private String customFieldSelectedPeriodId;
    //
    // private boolean customFieldPeriodMatched;


    @Inject
    protected CustomFieldDataEntryBean customFieldDataEntryBean;
    
    @Inject
    protected CustomFieldTemplateService customFieldTemplateService;

    public CustomFieldBean() {
    }

    public CustomFieldBean(Class<T> clazz) {
        super(clazz);
    }

    // @Override
    // public T initEntity() {
    // T result = super.initEntity();
    // return result;
    // }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        boolean isNew = entity.isTransient();
        
        customFieldDataEntryBean.saveCustomFieldsToEntity((ICustomFieldEntity) entity, isNew);
        String outcome = super.saveOrUpdate(killConversation);
        
        return outcome;
    }    
}
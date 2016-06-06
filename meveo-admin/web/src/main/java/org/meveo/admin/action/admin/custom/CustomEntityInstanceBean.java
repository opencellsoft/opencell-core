package org.meveo.admin.action.admin.custom;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.CustomFieldSearchBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.customEntities.CustomEntityInstance;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.custom.CustomEntityInstanceService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.omnifaces.cdi.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

@Named
@ViewScoped
public class CustomEntityInstanceBean extends CustomFieldSearchBean<CustomEntityInstance> {

    private static final long serialVersionUID = -459772193950603406L;

    private String customEntityTemplateCode;

    private CustomEntityTemplate customEntityTemplate;

    public CustomEntityInstanceBean() {
        super(CustomEntityInstance.class);
    }

    @Inject
    private CustomEntityInstanceService customEntityInstanceService;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Override
    protected IPersistenceService<CustomEntityInstance> getPersistenceService() {
        return customEntityInstanceService;
    }

    public void setCustomEntityTemplateCode(String customEntityTemplateCode) {
        this.customEntityTemplateCode = customEntityTemplateCode;
    }

    public String getCustomEntityTemplateCode() {
        return customEntityTemplateCode;
    }

    @Override
    public CustomEntityInstance initEntity() {

        CustomEntityInstance initResult = super.initEntity();

        // If it is a new entity and does not have yet the CET code set yet, set it from request parameter and initialize custom fields
        if (initResult.getCetCode() == null && customEntityTemplateCode != null) {
            initResult.setCetCode(customEntityTemplateCode);
        }
        return initResult;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        if (StringUtils.isBlank(entity.getCetCode())) {
            messages.error(new BundleKey("messages", "customEntityInstance.noCetCodeSet"));
            return null;
        }

        // Check for unicity of code
        CustomEntityInstance ceiSameCode = customEntityInstanceService.findByCodeByCet(entity.getCetCode(), entity.getCode(), entity.getProvider());
        if ((entity.isTransient() && ceiSameCode != null) || (!entity.isTransient() && entity.getId().longValue() != ceiSameCode.getId().longValue())) {
            messages.error(new BundleKey("messages", "commons.uniqueField.code"));
            return null;
        }
        return super.saveOrUpdate(killConversation);
    }

    @Override
    public String getEditViewName() {
        return "customEntity";
    }

    @Override
    public String getListViewName() {
        return "customEntities";
    }

    public CustomEntityTemplate getCustomEntityTemplate() {
        if (customEntityTemplate == null && customEntityTemplateCode != null) {
            customEntityTemplate = customEntityTemplateService.findByCode(customEntityTemplateCode, getCurrentProvider());
        }
        return customEntityTemplate;

    }

    @Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        searchCriteria.put("cetCode", customEntityTemplateCode);
        return searchCriteria;
    }
}
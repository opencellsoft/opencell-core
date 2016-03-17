package org.meveo.admin.action.admin.custom;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.scripts.EntityActionScript;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.script.EntityActionScriptService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class EntityActionScriptBean extends BaseBean<EntityActionScript> {

    private static final long serialVersionUID = 5401687428382698718L;

    @Inject
    private EntityActionScriptService entityActionScriptService;

    public EntityActionScriptBean() {
        super(EntityActionScript.class);
    }

    @Override
    public EntityActionScript initEntity(Long id) {
        super.initEntity(id);

        if (entity.isError()) {
            entityActionScriptService.compileScript(entity, true);
        }
        entity.getLocalCodeForRead();

        return entity;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        if (entity.isTransient()) {
            entity.setCode(entity.getLocalCode(), entity.getAppliesTo());
        }
        EntityActionScript actionDuplicate = entityActionScriptService.findByCodeAndAppliesTo(entity.getCode(), entity.getAppliesTo(), getCurrentProvider());
        if (actionDuplicate != null && !actionDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "customizedEntities.actionAlreadyExists"));
            return null;
        }

        try {
            String result = super.saveOrUpdate(killConversation);
            if (entity.isError()) {
                return null;
            }
            return result;

        } catch (Exception e) {
            messages.error(e.getMessage());
            return null;
        }
    }

    @Override
    protected IPersistenceService<EntityActionScript> getPersistenceService() {
        return entityActionScriptService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("provider");
    }

    public void testCompilation() {
        entityActionScriptService.compileScript(entity, true);
        if (!entity.isError()) {
            messages.info(new BundleKey("messages", "scriptInstance.compilationSuccessfull"));
        }
    }
}
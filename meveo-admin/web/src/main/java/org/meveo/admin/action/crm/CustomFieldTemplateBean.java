package org.meveo.admin.action.crm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.UpdateMapTypeFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CalendarService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class CustomFieldTemplateBean extends UpdateMapTypeFieldBean<CustomFieldTemplate> {

    private static final long serialVersionUID = 9099292371182275568L;

    @Inject
    private CustomFieldTemplateService cftService;

    @Inject
    private CalendarService calendarService;

    @Inject
    private CustomizedEntityService customizedEntityService;

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
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        updateMapTypeFieldInEntity(entity.getListValues(), "listValues");

        CustomFieldTemplate cfDuplicate = customFieldTemplateService.findByCodeAndAppliesTo(entity.getCode(), entity.getAppliesTo(), getCurrentProvider());
        if (cfDuplicate != null && !cfDuplicate.getId().equals(entity.getId())) {
            messages.error(new BundleKey("messages", "customFieldTemplate.alreadyExists"));
            return null;
        }

        if (entity.getCalendar() != null) {
            entity.setCalendar(calendarService.refreshOrRetrieve(entity.getCalendar()));
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
     * Autocomplete method for selecting a class/custom entity template for entity reference type Custom field template
     * 
     * @param query Partial value entered
     * @return A list of matching values
     */
    public List<String> autocompleteClassNames(String query) {
        List<String> clazzNames = new ArrayList<String>();

        List<CustomizedEntity> entities = customizedEntityService.getCustomizedEntities(query, false, null, null, getCurrentProvider());

        for (CustomizedEntity customizedEntity : entities) {
            clazzNames.add(customizedEntity.getClassnameToDisplay());
        }

        return clazzNames;
    }

    public List<String> autocompleteClassNamesHuman(String query) {
        List<String> clazzNames = new ArrayList<String>();

        List<CustomizedEntity> entities = customizedEntityService.getCustomizedEntities(query, false, null, null, getCurrentProvider());

        for (CustomizedEntity customizedEntity : entities) {
            clazzNames.add(customizedEntity.getClassnameToDisplayHuman());
        }

        return clazzNames;
    }
}
package org.meveo.admin.action.admin.custom;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.LazyDataModelWSize;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.primefaces.model.SortOrder;
import org.reflections.Reflections;

@Named
@ConversationScoped
public class CustomEntityTemplateListBean extends CustomEntityTemplateBean {

    private static final long serialVersionUID = 7731570832396817056L;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private JobInstanceService jobInstanceService;

    private LazyDataModelWSize<CustomizedEntity> customizedEntityDM = null;

    public LazyDataModelWSize<CustomizedEntity> getCustomizedEntities() {

        if (customizedEntityDM != null) {
            return customizedEntityDM;
        }

        customizedEntityDM = new LazyDataModelWSize<CustomizedEntity>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<CustomizedEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> loadingFilters) {

                final String sortFieldFinal = sortField;
                final SortOrder sortOrderFinal = sortOrder;

                List<CustomizedEntity> entities = new ArrayList<CustomizedEntity>();

                String filterByNameValue = null;
                if (filters.get("entityName") != null) {
                    filterByNameValue = ((String) filters.get("entityName")).toLowerCase();
                }

                if (filters.get("customEntity") == null || !(boolean) filters.get("customEntity")) {

                    // Find standard entities that implement ICustomFieldEntity interface except JobInstance
                    Reflections reflections = new Reflections("org.meveo.model");
                    Set<Class<? extends ICustomFieldEntity>> cfClasses = reflections.getSubTypesOf(ICustomFieldEntity.class);

                    for (Class<? extends ICustomFieldEntity> cfClass : cfClasses) {

                        if (JobInstance.class.isAssignableFrom(cfClass) || Modifier.isAbstract(cfClass.getModifiers())) {
                            continue;

                            // Filter by name
                        } else if (filterByNameValue != null && !cfClass.getSimpleName().toLowerCase().contains(filterByNameValue)) {
                            continue;
                        }

                        entities.add(new CustomizedEntity(cfClass.getSimpleName(), cfClass, null, null));

                    }

                    // Find Jobs
                    for (Job job : jobInstanceService.getJobs()) {

                        if (job.getCustomFields() != null
                                && (filterByNameValue == null || (filterByNameValue != null && job.getClass().getSimpleName().toLowerCase().contains(filterByNameValue)))) {

                            String classname = job.getClass().getSimpleName();
                            int pos = classname.indexOf("$$");
                            if (pos > 0) {
                                classname = classname.substring(0, pos);
                            }

                            entities.add(new CustomizedEntity(classname, job.getClass(), null, null));
                        }
                    }
                }

                List<CustomEntityTemplate> cets = null;
                if (filterByNameValue == null) {
                    cets = customEntityTemplateService.list();
                } else {
                    cets = customEntityTemplateService.findByCodeLike(filterByNameValue, getCurrentProvider());
                }

                for (CustomEntityTemplate cet : cets) {
                    entities.add(new CustomizedEntity(cet.getCode(), CustomEntityTemplate.class, cet.getId(), cet.getDescription()));
                }

                setRowCount(entities.size());

                Collections.sort(entities, new Comparator<CustomizedEntity>() {

                    @Override
                    public int compare(CustomizedEntity o1, CustomizedEntity o2) {
                        int sortMultiplicator = 1;
                        if (sortOrderFinal == SortOrder.DESCENDING) {
                            sortMultiplicator = -1;
                        }
                        if ("description".equals(sortFieldFinal)) {
                            return StringUtils.compare(o1.getDescription(), o2.getDescription()) * sortMultiplicator;

                        } else {
                            return StringUtils.compare(o1.getEntityName(), o2.getEntityName()) * sortMultiplicator;
                        }
                    }

                });

                return entities.subList(first, (first + pageSize) > entities.size() ? entities.size() : (first + pageSize));
            }
        };

        return customizedEntityDM;
    }
}
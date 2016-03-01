package org.meveo.service.custom;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.Provider;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobInstanceService;
import org.reflections.Reflections;

public class CustomizedEntityService implements Serializable {

    private static final long serialVersionUID = 4108034108745598588L;

    @Inject
    private JobInstanceService jobInstanceService;

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    /**
     * Get a list of customized/customizable entities optionally filtering by a name and custom entities only
     * 
     * @param entityName Optional filter by a name
     * @param customEntityOnly Return custom entity templates only
     * @param sortBy Sort by. Valid values are: "description" or null to sort by entity name
     * @param sortOrder Sort order. Valid values are "DESCENDING" or "ASCENDING". By default will sort in Ascending order.
     * @param currentProvider Current provider
     * @return A list of customized/customizable entities
     */
    public List<CustomizedEntity> getCustomizedEntities(String entityName, boolean customEntityOnly, final String sortBy, final String sortOrder, Provider currentProvider) {
        List<CustomizedEntity> entities = new ArrayList<CustomizedEntity>();

        if (entityName != null) {
            entityName = entityName.toLowerCase();
        }

        if (!customEntityOnly) {

            // Find standard entities that implement ICustomFieldEntity interface except JobInstance
            Reflections reflections = new Reflections("org.meveo.model");
            Set<Class<? extends ICustomFieldEntity>> cfClasses = reflections.getSubTypesOf(ICustomFieldEntity.class);

            for (Class<? extends ICustomFieldEntity> cfClass : cfClasses) {

                if (JobInstance.class.isAssignableFrom(cfClass) || Modifier.isAbstract(cfClass.getModifiers())) {
                    continue;

                    // Filter by name
                } else if (entityName != null && !cfClass.getSimpleName().toLowerCase().contains(entityName)) {
                    continue;
                }

                entities.add(new CustomizedEntity(cfClass));
            }

            // Find Jobs
            for (Job job : jobInstanceService.getJobs()) {
                if (job.getCustomFields() != null && (entityName == null || (entityName != null && job.getClass().getSimpleName().toLowerCase().contains(entityName)))) {
                    entities.add(new CustomizedEntity(job.getClass()));
                }
            }
        }

        List<CustomEntityTemplate> cets = null;
        if (entityName == null || CustomEntityTemplate.class.getSimpleName().toLowerCase().contains(entityName)) {
            cets = customEntityTemplateService.list(currentProvider);

        } else if (entityName != null) {
            cets = customEntityTemplateService.findByCodeLike(entityName, currentProvider);
        }

        for (CustomEntityTemplate cet : cets) {
            entities.add(new CustomizedEntity(cet.getCode(), CustomEntityTemplate.class, cet.getId(), cet.getDescription()));
        }

        Collections.sort(entities, new Comparator<CustomizedEntity>() {

            @Override
            public int compare(CustomizedEntity o1, CustomizedEntity o2) {
                int sortMultiplicator = 1;
                if ("DESCENDING".equalsIgnoreCase(sortOrder)) {
                    sortMultiplicator = -1;
                }
                if ("description".equals(sortBy)) {
                    return StringUtils.compare(o1.getDescription(), o2.getDescription()) * sortMultiplicator;

                } else {
                    return StringUtils.compare(o1.getEntityName(), o2.getEntityName()) * sortMultiplicator;
                }
            }

        });

        return entities;
    }
}
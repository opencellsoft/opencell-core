package org.meveo.admin.action.admin.custom;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.service.custom.CustomizedEntity;
import org.meveo.service.custom.CustomizedEntityService;
import org.meveo.util.view.LazyDataModelWSize;
import org.primefaces.model.SortOrder;

@Named
@ConversationScoped
public class CustomEntityTemplateListBean extends CustomEntityTemplateBean {

    private static final long serialVersionUID = 7731570832396817056L;

    @Inject
    private CustomizedEntityService customizedEntityService;

    private LazyDataModelWSize<CustomizedEntity> customizedEntityDM = null;

    public LazyDataModelWSize<CustomizedEntity> getCustomizedEntities() {

        if (customizedEntityDM != null) {
            return customizedEntityDM;
        }

        customizedEntityDM = new LazyDataModelWSize<CustomizedEntity>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<CustomizedEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> loadingFilters) {

                List<CustomizedEntity> entities = null;
                String query = (String) filters.get("entityName");
                boolean isCustomEntityOnly = filters.get("customEntity") != null && (boolean) filters.get("customEntity");
                String sortBy = sortOrder != null ? sortOrder.name() : null;

                entities = customizedEntityService.getCustomizedEntities(query, isCustomEntityOnly, false, false, sortField, sortBy);
                setRowCount(entities.size());

                return entities.subList(first, (first + pageSize) > entities.size() ? entities.size() : (first + pageSize));
            }
        };

        return customizedEntityDM;
    }
}
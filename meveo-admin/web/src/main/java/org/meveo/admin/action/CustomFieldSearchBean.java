package org.meveo.admin.action;

import org.meveo.admin.action.admin.custom.GroupedCustomField;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.custom.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.custom.CustomFieldValueHolder;
import org.meveo.model.filter.Filter;
import org.meveo.service.base.ValueExpressionWrapper;
import org.primefaces.component.datatable.DataTable;

import java.util.Map;

/**
 * Created by Tony Alejandro on 03/06/2016.
 */
public abstract class CustomFieldSearchBean<T extends IEntity> extends CustomFieldBean<T> {

    public CustomFieldSearchBean() {
    }

    public CustomFieldSearchBean(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public DataTable search() {
        if (filters != null && filters.containsKey("$FILTER")) {
            Filter entity = (Filter)filters.get("$FILTER");
            try {
                Map<CustomFieldTemplate, Object> parameterMap = customFieldDataEntryBean.loadCustomFieldsFromGUI(entity);
                filters.put("$FILTER_PARAMETERS", parameterMap);
            } catch (BusinessException e) {
                log.error("Failed to load search parameters from custom fields.", e);
                messages.error(e.getMessage());
            }
        }
        return super.search();
    }

    public void saveOrUpdateFilter(Filter filter) throws BusinessException {
        boolean isNew = filter.isTransient();
        customFieldDataEntryBean.saveCustomFieldsToEntity((ICustomFieldEntity) filter, isNew);
    }
}

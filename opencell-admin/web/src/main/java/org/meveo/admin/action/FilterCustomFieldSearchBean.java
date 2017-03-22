package org.meveo.admin.action;

import java.io.Serializable;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.meveo.admin.action.admin.custom.CustomFieldDataEntryBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tony Alejandro on 03/06/2016.
 */
@Named
@ViewScoped
public class FilterCustomFieldSearchBean implements Serializable {

	private static final long serialVersionUID = 4300150745614341095L;

	@Inject
	private CustomFieldDataEntryBean customFieldDataEntryBean;
	
	@Inject
    protected Messages messages;
	
	private static final Logger log = LoggerFactory.getLogger(FilterCustomFieldSearchBean.class);
	
    public void buildFilterParameters(Map<String, Object> filters) {
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
    }

    public void saveOrUpdateFilter(Filter filter) throws BusinessException {
        boolean isNew = filter.isTransient();
        customFieldDataEntryBean.saveCustomFieldsToEntity((ICustomFieldEntity) filter, isNew);
    }
}

package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.base.PersistenceService;

@Named
@ConversationScoped
public class ProductAndBundleTemplateListBean extends ProductTemplateListBean {

    private static final long serialVersionUID = 7690305402189246824L;

    @Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        // Show product and bundle templates
        @SuppressWarnings("rawtypes")
        List<Class> types = new ArrayList<>();
        types.add(ProductTemplate.class);
        types.add(BundleTemplate.class);
        searchCriteria.put(PersistenceService.SEARCH_ATTR_TYPE_CLASS, types);

        return super.supplementSearchCriteria(searchCriteria);
    }
}
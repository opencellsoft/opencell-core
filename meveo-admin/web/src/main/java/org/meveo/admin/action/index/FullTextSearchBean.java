package org.meveo.admin.action.index;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.CurrentUser;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.BundleTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.model.hierarchy.UserHierarchyLevel;
import org.meveo.service.index.ElasticClient;
import org.meveo.service.index.ElasticSearchClassInfo;
import org.meveo.service.base.BusinessEntityService;
import org.meveo.util.view.ESBasedDataModel;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;

@Named
@ConversationScoped
public class FullTextSearchBean implements Serializable {

    private static final long serialVersionUID = -2748591950645172132L;

    @Inject
    private ElasticClient elasticClient;

    @Inject
    @CurrentUser
    protected User currentUser;

    @Inject
    private BusinessEntityService businessEntityService;

    @Inject
    protected Conversation conversation;

    @Inject
    private Logger log;

    private LazyDataModel<Map<String, Object>> esDataModel;

    /** Search filters. */
    protected Map<String, Object> filters = new HashMap<String, Object>();

    /**
     * DataModel for primefaces lazy loading datatable component.
     * 
     * @return LazyDataModel implementation.
     */
    public LazyDataModel<Map<String, Object>> getEsDataModel() {
        return getEsDataModel(filters);
    }

    public LazyDataModel<Map<String, Object>> getEsDataModel(Map<String, Object> inputFilters) {
        if (esDataModel == null) {

            final Map<String, Object> filters = inputFilters;

            esDataModel = new ESBasedDataModel() {

                private static final long serialVersionUID = -1514374110345615089L;

                @Override
                protected String getFullTextSearchValue(Map<String, Object> loadingFilters) {
                    return (String) filters.get(ESBasedDataModel.FILTER_FULL_TEXT);
                }

                @Override
                protected ElasticClient getElasticClientImpl() {
                    return elasticClient;
                }

                @Override
                public User getCurrentUser() {
                    return FullTextSearchBean.this.getCurrentUser();
                }

                @Override
                public String[] getSearchScope() {

                    // Limit search scope to offers, product, offer template categories, user groups for marketing manager application
                    if (FullTextSearchBean.this.getCurrentUser().hasPermission("marketing", "marketingCatalogManager") || FullTextSearchBean.this.getCurrentUser().hasPermission("marketing", "marketingCatalogVisualization")) {
                        return new String[] { OfferTemplate.class.getName(), ProductTemplate.class.getName(), BundleTemplate.class.getName(),
                                OfferTemplateCategory.class.getName(), UserHierarchyLevel.class.getName() };
                    }
                    return null;
                }
            };
        }

        return esDataModel;
    }

    protected User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clean search fields in datatable.
     */
    public void clean() {
        esDataModel = null;
        filters = new HashMap<String, Object>();
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public void preRenderView() {
        beginConversation();
    }

    protected void beginConversation() {
        if (conversation.isTransient()) {
            conversation.begin();
        }
    }

    /**
     * Get navigation link and identifier
     * 
     * @param esType
     * @param code
     * @return
     */
    @SuppressWarnings("unchecked")
    public String[] getViewAndId(String esType, String code) {

        String[] viewInfo = new String[2];

        ElasticSearchClassInfo scopeInfo = elasticClient.getSearchScopeInfo(esType, getCurrentUser());

        QueryBuilder qb = new QueryBuilder(scopeInfo.getClazz(), "be", null, getCurrentUser().getProvider());
        qb.addCriterion("be.code", "=", code, true);

        List<? extends BusinessEntity> results = qb.getQuery(businessEntityService.getEntityManager()).getResultList();
        if (!results.isEmpty()) {
            BusinessEntity entity = results.get(0);
            viewInfo[0] = BaseBean.getEditViewName(entity.getClass());
            viewInfo[1] = entity.getId().toString();

            if (getCurrentUser().hasPermission("marketing", "marketingCatalogManager") || getCurrentUser().hasPermission("marketing", "marketingCatalogVisualization")) {
                viewInfo[0] = "mm_" + viewInfo[0];
            }

        } else {
            log.warn("Could not resolve view and ID for {} {}", esType, code);
            viewInfo[0] = "fullTextSearch";
        }

        return viewInfo;
    }
}
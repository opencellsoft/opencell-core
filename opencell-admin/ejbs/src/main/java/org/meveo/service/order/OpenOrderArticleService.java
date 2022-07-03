package org.meveo.service.order;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.model.ordering.OpenOrderArticle;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class OpenOrderArticleService extends PersistenceService<OpenOrderArticle> {

    public OpenOrderArticle findByArticleCodeAndTemplate(String code, Long idTemplate) {
        List<OpenOrderArticle> ooas = getEntityManager().createNamedQuery("OpenOrderArticle.findByCodeAndTemplate")
                .setParameter("TEMPLATE_ID", idTemplate)
                .setParameter("ARTICLE_CODE", code)
                .getResultList();

        return CollectionUtils.isEmpty(ooas) ? null : ooas.get(0);
    }
}
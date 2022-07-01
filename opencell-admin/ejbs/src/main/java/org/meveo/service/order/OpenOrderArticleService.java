package org.meveo.service.order;

import org.meveo.model.ordering.OpenOrderArticle;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;

@Stateless
public class OpenOrderArticleService extends PersistenceService<OpenOrderArticle> {

    public OpenOrderArticle findByArticleCodeAndTemplate(String code, Long idTemplate){
        return (OpenOrderArticle) getEntityManager().createNamedQuery("OpenOrderArticle.findByCodeAndTemplate")
                .setParameter("TEMPLATE_ID", idTemplate)
                .setParameter("ARTICLE_CODE", code)
                .getSingleResult();
    }
}
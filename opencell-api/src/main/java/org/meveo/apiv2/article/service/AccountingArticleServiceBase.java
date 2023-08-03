package org.meveo.apiv2.article.service;

import java.util.Map;
import java.util.Optional;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.AccountingArticle;

public interface AccountingArticleServiceBase extends ApiService<AccountingArticle> {
	 
		public Optional<AccountingArticle> findByCode(String code);
		
		public Optional<AccountingArticle> delete(String code);
		
	    public Optional<AccountingArticle> getAccountingArticles(String productCode, Map<String, Object> attributes);
	    
}
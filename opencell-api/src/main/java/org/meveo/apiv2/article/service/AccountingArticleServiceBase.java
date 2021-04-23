package org.meveo.apiv2.article.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.model.article.AccountingArticle;

public interface AccountingArticleServiceBase extends ApiService<AccountingArticle> {
	 
		public Optional<AccountingArticle> findByCode(String code);
		
		public Optional<AccountingArticle> delete(String code);
		
	    public List<AccountingArticle> list(Long offset, Long limit, String sort, String orderBy, Map<String, Object> filter);
	    
	    public Long getCount(Map<String, Object> filter);
	    
	    public Optional<AccountingArticle> getAccountingArticles(String productCode, Map<String, Object> attributes);
	    
		default List<AccountingArticle> list(Long offset, Long limit, String sort, String orderBy, String filter) {
			return Collections.emptyList();
		}

	    default Optional<AccountingArticle> patch(Long id, AccountingArticle baseEntity) {
	        return Optional.empty();
	    }
}

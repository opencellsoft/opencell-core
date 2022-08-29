package org.meveo.apiv2.article.service;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.meveo.model.article.ArticleMappingLine;
import org.meveo.service.billing.impl.article.ArticleMappingLineService;

@Stateless
public class ArticleMappingLineApiService{

    @Inject
    private ArticleMappingLineService articleMappingLineService;

    private List<String> fields =
            asList("accountingArticle", "articleMapping", "offerTemplate", "product", "chargeTemplate");
    
    public Optional<ArticleMappingLine> findById(Long id) {
        return ofNullable(articleMappingLineService.findById(id, fields, true));
    }

    @Transactional
    public ArticleMappingLine create(ArticleMappingLine articleMappingLine) {
        return articleMappingLineService.validateAndCreate(articleMappingLine);
    }

    public Optional<ArticleMappingLine> update(Long id, ArticleMappingLine articleMappingLine) {
    	return articleMappingLineService.update(id, articleMappingLine);
       
    }

    public Optional<ArticleMappingLine> delete(Long id) {
    	Optional<ArticleMappingLine> articleMappingLine = findById(id);
    	if(articleMappingLine.isPresent()) {
    		ArticleMappingLine current = articleMappingLine.get();
    		articleMappingLineService.remove(current);
    		return Optional.of(current);
    	}
         return Optional.empty();
    }

    public Optional<ArticleMappingLine> findByCode(String code) {
        return ofNullable(articleMappingLineService.findByCode(code, fields));
    }
}

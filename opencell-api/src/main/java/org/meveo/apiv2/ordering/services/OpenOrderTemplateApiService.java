package org.meveo.apiv2.ordering.services;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.ordering.resource.openOrderTemplate.OpenOrderTemplateMapper;
import org.meveo.apiv2.ordering.resource.openOrderTemplate.ThresholdMapper;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.ordering.OpenOrderTemplate;
import org.meveo.model.ordering.OpenOrderTemplateStatusEnum;
import org.meveo.model.ordering.OpenOrderTypeEnum;
import org.meveo.model.ordering.Threshold;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.order.OpenOrderTemplateService;
import org.meveo.service.order.ThresholdService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.meveo.admin.util.CollectionUtil.isNullOrEmpty;
@Stateless
public class OpenOrderTemplateApiService {

    @Inject
    private ProductService productService;
    @Inject
    private AccountingArticleService accountingArticleService;
    @Inject
    private OpenOrderTemplateService openOrderTemplateService;
    @Inject
    private ThresholdService thresholdService;

    private OpenOrderTemplateMapper openOrderTemplateMapper = new OpenOrderTemplateMapper();
     private ThresholdMapper thresholdMapper = new ThresholdMapper();

    public void create(OpenOrderTemplateInput  input)
    {

        OpenOrderTemplate openOrderTemplate = openOrderTemplateMapper.toEntity(input);
         if(null != input.getThresholds() ) openOrderTemplate.setThresholds(input.getThresholds().stream().map(thresholdMapper::toEntity).collect(Collectors.toList()));
        if (null != input.getArticles()) openOrderTemplate.setArticles(fetchArticles(input.getArticles()));
        if (null != input.getProducts())  openOrderTemplate.setProducts(fetchProducts(input.getProducts()));
         checkParameters(openOrderTemplate);

        openOrderTemplateService.create(openOrderTemplate);
    }



    public void update(String code, OpenOrderTemplateInput input)
    {
        OpenOrderTemplate openOrderTemplate = openOrderTemplateService.findByCode(code);
        if(null == openOrderTemplate)
        {
            throw new BusinessApiException(String.format("open order template with code %s doesn't exist", code));
        }
        openOrderTemplateMapper.fillEntity(openOrderTemplate, input);
        thresholdService.deleteThresholdsByOpenOrderTemplateId(openOrderTemplate.getId());
         openOrderTemplate.setThresholds(input.getThresholds().stream().map(thresholdMapper::toEntity).collect(Collectors.toList()));
        if (null != input.getArticles()) openOrderTemplate.setArticles(fetchArticles(input.getArticles()));
        if (null != input.getProducts())  openOrderTemplate.setProducts(fetchProducts(input.getProducts()));
         checkParameters(openOrderTemplate);

        openOrderTemplateService.update(openOrderTemplate);


    }

    public void disableOpenOrderTemplate(String code)
    {
        OpenOrderTemplate openOrderTemplate = openOrderTemplateService.findByCode(code);
        if(null == openOrderTemplate)
        {
            throw new BusinessApiException(String.format("open order template with code %s doesn't exist", code));
        }
        openOrderTemplate.setStatus(OpenOrderTemplateStatusEnum.Archived);
        openOrderTemplateService.update(openOrderTemplate);

    }



    private void checkParameters(OpenOrderTemplate openOrderTemplate) {
        checkOpenOrderType(openOrderTemplate);
        checkThresholds(openOrderTemplate.getThresholds());


    }

    private void checkThresholds(List<Threshold> thresholds) {
        if(!isNullOrEmpty(thresholds)){
            thresholds
                    .stream()
                    .filter(threshold -> threshold.getPercentage() < 1 || threshold.getPercentage() > 100)
                    .findAny()
                    .ifPresent(threshold -> { throw new BusinessApiException("Threshold should be between 1 and 100");});

            List<Threshold> sortedThresholds = thresholds.stream().sorted(Comparator.comparingInt(Threshold::getSequence)).collect(Collectors.toList());

            for(int i=1; i < sortedThresholds.size(); i ++)
            {
                if(thresholds.get(i).getPercentage() < thresholds.get(i-1).getPercentage())
                {
                    throw new BusinessApiException("Threshold sequence and percentage dosn’t match, threshold with high sequence number should contain the highest percentage");
                }
            }
        }

    }

    private void checkOpenOrderType(OpenOrderTemplate openOrderTemplate){
        if(openOrderTemplate.getOpenOrderType() == OpenOrderTypeEnum.ARTICLES && !isNullOrEmpty(openOrderTemplate.getProducts()))
        {
            throw new BusinessApiException("Open order template of type ARTICLE can not be applied on products");
        }

        if(openOrderTemplate.getOpenOrderType() == OpenOrderTypeEnum.PRODUCTS && !isNullOrEmpty(openOrderTemplate.getArticles()))
        {
            throw new BusinessApiException("Open order template of type PRODUCT can not be applied on articles");
        }
    }



    private List<Product> fetchProducts(List<String> productsCodes) {

        List<Product> products= new ArrayList<>();
        for(String productCode : productsCodes)
        {
            Product product = productService.findByCode(productCode);
            if( null == product)
            {
                throw new BusinessApiException(String.format("Product with code %s doesn't exist", productCode));

            }
            products.add(product);
        }
        return products;
    }

    private List<AccountingArticle> fetchArticles(List<String> articlesCodes) {
        List<AccountingArticle> articles= new ArrayList<>();
        for(String articleCode : articlesCodes)
        {
            AccountingArticle article = accountingArticleService.findByCode(articleCode);
            if( null == article)
            {
                throw new BusinessApiException(String.format("Article with code %s doesn't exist", articleCode));

            }
            articles.add(article);
        }
        return articles;
    }

}

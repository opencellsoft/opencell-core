package org.meveo.apiv2.ordering.services;

import static org.meveo.admin.util.CollectionUtil.isNullOrEmpty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.ordering.resource.openOrderTemplate.OpenOrderTemplateMapper;
import org.meveo.apiv2.ordering.resource.openOrderTemplate.ThresholdMapper;
import org.meveo.apiv2.ordering.resource.order.OpenOrderTemplateInput;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.ordering.OpenOrderArticle;
import org.meveo.model.ordering.OpenOrderProduct;
import org.meveo.model.ordering.OpenOrderTemplate;
import org.meveo.model.ordering.OpenOrderTemplateStatusEnum;
import org.meveo.model.ordering.OpenOrderTypeEnum;
import org.meveo.model.ordering.Threshold;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.billing.impl.article.OpenOrderArticleService;
import org.meveo.service.billing.impl.article.OpenOrderProductService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.order.OpenOrderTemplateService;
import org.meveo.service.order.ThresholdService;

@Stateless
public class OpenOrderTemplateApiService {

    @Inject
    private OpenOrderProductService openOrderProductService;
    @Inject
    private OpenOrderArticleService openOrderArticleService;
    @Inject
    private OpenOrderTemplateService openOrderTemplateService;
    @Inject
    private ThresholdService thresholdService;
    @Inject
    private TagService tagService;

    @Inject
    private AuditLogService auditLogService;

    private OpenOrderTemplateMapper openOrderTemplateMapper = new OpenOrderTemplateMapper();
     private ThresholdMapper thresholdMapper = new ThresholdMapper();

    public OpenOrderTemplateInput create(OpenOrderTemplateInput input) {
        checkParameters(input);
        if (openOrderTemplateService.findByCode(input.getTemplateName()) != null)
            throw new InvalidParameterException(String.format("Template name %s already exists", input.getTemplateName()));


        OpenOrderTemplate openOrderTemplate = openOrderTemplateMapper.toEntity(input);
        openOrderTemplate.setCode(openOrderTemplate.getTemplateName());
        if (null != input.getThresholds())
            openOrderTemplate.setThresholds(input.getThresholds().stream().map(thresholdMapper::toEntity).collect(Collectors.toList()));
        if (null != input.getArticles()) openOrderTemplate.setArticles(fetchArticles(input.getArticles()));
        if (null != input.getProducts()) openOrderTemplate.setProducts(fetchProducts(input.getProducts()));
        if (null != input.getTags()) openOrderTemplate.setTags(fetchTags(input.getTags()));
        checkParameters(openOrderTemplate);

        openOrderTemplate.setStatus(OpenOrderTemplateStatusEnum.DRAFT);
        openOrderTemplateService.create(openOrderTemplate);
        auditLogService.trackOperation("CREATE", new Date(), openOrderTemplate, openOrderTemplate.getCode());
        return openOrderTemplateMapper.toResource(openOrderTemplate);
    }

    public OpenOrderTemplateInput update(String code, OpenOrderTemplateInput input) {
        checkParameters(input);
        OpenOrderTemplate openOrderTemplate = openOrderTemplateService.findByCode(code);
        if (null == openOrderTemplate) {
            throw new BusinessApiException(String.format("open order template with code %s doesn't exist", code));
        }
        if (!code.equals(input.getTemplateName())) {
            if (openOrderTemplateService.findByCode(input.getTemplateName()) != null)
                throw new InvalidParameterException(String.format("Template name %s already exists", input.getTemplateName()));
            openOrderTemplate.setCode(input.getTemplateName());
        }

        openOrderTemplateMapper.fillEntity(openOrderTemplate, input);
        thresholdService.deleteThresholdsByOpenOrderTemplateId(openOrderTemplate.getId());
        openOrderTemplate.setThresholds(input.getThresholds().stream().map(thresholdMapper::toEntity).collect(Collectors.toList()));
        if (null != input.getArticles()) openOrderTemplate.setArticles(fetchArticles(input.getArticles()));
        if (null != input.getProducts()) openOrderTemplate.setProducts(fetchProducts(input.getProducts()));
        if (null != input.getTags()) openOrderTemplate.setTags(fetchTags(input.getTags()));
        checkParameters(openOrderTemplate);
        openOrderTemplate = openOrderTemplateService.update(openOrderTemplate);
        auditLogService.trackOperation("UPDATE", new Date(), openOrderTemplate, openOrderTemplate.getCode());
        return openOrderTemplateMapper.toResource(openOrderTemplate);
    }

    private void checkParameters(OpenOrderTemplateInput openOrderTemplateInput) {
        if(openOrderTemplateInput.getTemplateName() == null || openOrderTemplateInput.getTemplateName().isEmpty()
                || openOrderTemplateInput.getOpenOrderType() == null )
            throw new InvalidParameterException("The following fields are required: Template name, Open order type");
    }

    public void disableOpenOrderTemplate(String code) {
        OpenOrderTemplate openOrderTemplate = openOrderTemplateService.findByCode(code);
        if (null == openOrderTemplate) {
            throw new BusinessApiException(String.format("open order template with code %s doesn't exist", code));
        }
        openOrderTemplate.setStatus(OpenOrderTemplateStatusEnum.ARCHIVED);
        openOrderTemplateService.update(openOrderTemplate);
    }

    private void checkParameters(OpenOrderTemplate openOrderTemplate) {
        checkOpenOrderType(openOrderTemplate);
        checkThresholds(openOrderTemplate.getThresholds());
    }

    private void checkThresholds(List<Threshold> thresholds) {
        if (!isNullOrEmpty(thresholds)) {
            thresholds
                    .stream()
                    .filter(threshold -> threshold.getPercentage() == null
                            || threshold.getPercentage() < 1 || threshold.getPercentage() > 100)
                    .findAny()
                    .ifPresent(threshold -> {
                        throw new BusinessApiException("Threshold should be between 1 and 100");
                    });

            List<Threshold> sortedThresholds = thresholds.stream().sorted(Comparator.comparingInt(Threshold::getSequence)).collect(Collectors.toList());

            for (int i = 1; i < sortedThresholds.size(); i++) {
                if (thresholds.get(i).getPercentage() < thresholds.get(i - 1).getPercentage()) {
                    throw new BusinessApiException("Threshold sequence and percentage dosn’t match, threshold with high sequence number should contain the highest percentage");
                }
            }
        }

    }

    private void checkOpenOrderType(OpenOrderTemplate openOrderTemplate) {
        if (openOrderTemplate.getOpenOrderType() == OpenOrderTypeEnum.ARTICLES && !isNullOrEmpty(openOrderTemplate.getProducts())) {
            throw new BusinessApiException("Open order template of type ARTICLE can not be applied on products");
        }

        if (openOrderTemplate.getOpenOrderType() == OpenOrderTypeEnum.PRODUCTS && !isNullOrEmpty(openOrderTemplate.getArticles())) {
            throw new BusinessApiException("Open order template of type PRODUCT can not be applied on articles");
        }
    }


    private List<OpenOrderProduct> fetchProducts(List<String> productsCodes) {
        List<OpenOrderProduct> products = new ArrayList<>();
        for (String productCode : productsCodes) {
        	OpenOrderProduct product = openOrderProductService.findByCode(productCode);
            if (null == product) {
                throw new BusinessApiException(String.format("Product with code %s doesn't exist", productCode));

            }
            products.add(product);
        }
        return products;
    }

    private List<OpenOrderArticle> fetchArticles(List<String> articlesCodes) {
        List<OpenOrderArticle> articles = new ArrayList<>();
        for (String articleCode : articlesCodes) {
        	OpenOrderArticle article = openOrderArticleService.findByCode(articleCode);
            if (null == article) {
                throw new BusinessApiException(String.format("Article with code %s doesn't exist", articleCode));

            }
            articles.add(article);
        }
        return articles;
    }

    private List<Tag> fetchTags(List<String> tagsCodes) {
        List<Tag> tags = new ArrayList<>();
        for (String tagCode : tagsCodes) {
            Tag tag = tagService.findByCode(tagCode);
            if (null == tag) {
                throw new BusinessApiException(String.format("Tag with code %s doesn't exist", tagCode));
            }
            tags.add(tag);
        }
        return tags;
    }

}
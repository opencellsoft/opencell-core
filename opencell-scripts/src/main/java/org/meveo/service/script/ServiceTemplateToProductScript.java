package org.meveo.service.script;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.catalog.ServiceChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.billing.impl.article.ArticleMappingLineService;
import org.meveo.service.billing.impl.article.ArticleMappingService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.tax.TaxClassService;

public class ServiceTemplateToProductScript extends Script {
    private static final long serialVersionUID = -6513133027379352381L;

    // Script parameters
    public static final String DEFAULT_TAX_CLASS_CODE = "NORMAL";
    public static final long ARTICLE_MAPPING_ID = 1L;
    private static final Integer PAGE_COUNT = 100;

    private ServiceInstanceService serviceInstanceService = (ServiceInstanceService) getServiceInterface(ServiceInstanceService.class.getSimpleName());
    private ProductService productService = (ProductService) getServiceInterface(ProductService.class.getSimpleName());
    private ProductVersionService productVersionService = (ProductVersionService) getServiceInterface(ProductVersionService.class.getSimpleName());
    private OfferTemplateService offerTemplateService = (OfferTemplateService) getServiceInterface(OfferTemplateService.class.getSimpleName());
    private AccountingArticleService accountingArticleService = (AccountingArticleService) getServiceInterface(AccountingArticleService.class.getSimpleName());
    private ArticleMappingService articleMappingService = (ArticleMappingService) getServiceInterface(ArticleMappingService.class.getSimpleName());
    private ArticleMappingLineService articleMappingLineService = (ArticleMappingLineService) getServiceInterface(ArticleMappingLineService.class.getSimpleName());
    private ServiceTemplateService serviceTemplateService = (ServiceTemplateService) getServiceInterface(ServiceTemplateService.class.getSimpleName());
    private TaxClassService taxClassService = (TaxClassService) getServiceInterface(TaxClassService.class.getSimpleName());

    private TaxClass defaultTaxClass;
    private ArticleMapping defaultArticleMapping;

    @Override
    public void init(Map<String, Object> methodContext) throws BusinessException {
        defaultTaxClass = taxClassService.findByCode(DEFAULT_TAX_CLASS_CODE);
        if (defaultTaxClass == null) {
            throw new EntityDoesNotExistsException(TaxClass.class, DEFAULT_TAX_CLASS_CODE);
        }

        defaultArticleMapping = articleMappingService.findById(ARTICLE_MAPPING_ID);
        if (defaultArticleMapping == null) {
            throw new EntityDoesNotExistsException(ArticleMapping.class, ARTICLE_MAPPING_ID);
        }
    }

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
        log.info("Migration started at {}", new Date());

        long count = 0;
        List<ServiceTemplate> serviceTemplates = (List<ServiceTemplate>) methodContext.get("SERVICE_TEMPLATE");

        if (serviceTemplates != null && !serviceTemplates.isEmpty()) {
            count = serviceTemplates.size();
            serviceTemplates.forEach(this::map);
        } else {

            count = serviceTemplateService.count(new PaginationConfiguration("id", SortOrder.ASCENDING));

            if (PAGE_COUNT < count) {
                serviceTemplateService.list(new PaginationConfiguration(null, null)).forEach(this::map);
            } else {
                for (int index = 0; index < count; index = index + PAGE_COUNT) {
                    serviceTemplateService.list(new PaginationConfiguration(index, PAGE_COUNT, null, null, null, "id", SortOrder.ASCENDING)).forEach(this::map);
                }
            }
        }

        log.info("Number of services migrated={}", count);
        log.info("Migration ended at {}", new Date());
    }

    public Product map(ServiceTemplate serviceTemplate) {
        Product product = createProduct(serviceTemplate);
        createArticle(product);

        ProductVersion productVersion = createProductVersion(product);

        List<ServiceInstance> serviceInstances = serviceInstanceService.findByServiceTemplate(serviceTemplate);

        serviceInstances.forEach(serviceInstance -> {

            serviceInstance.setProductVersion(productVersion);
            serviceInstance.setServiceTemplate(null);
            serviceInstanceService.update(serviceInstance);

            OfferTemplate offer = serviceInstance.getSubscription().getOffer();
            OfferComponent offerComponent = new OfferComponent();
            offerComponent.setProduct(product);
            offerComponent.setOfferTemplate(offer);
            offer.getOfferComponents().add(offerComponent);
            offer.getOfferServiceTemplates().removeIf(o -> o.getServiceTemplate().getId().equals(serviceTemplate.getId()));
            offerTemplateService.update(offer);
        });

        return product;
    }

    private void createArticle(Product product) {
        product.getProductCharges().stream().map(pc -> pc.getChargeTemplate()).forEach(chargeTemplate -> {
            AccountingArticle accountingArticle;
            List<AccountingArticle> accountingArticles = accountingArticleService.findByTaxClassAndSubCategory(chargeTemplate.getTaxClass(), chargeTemplate.getInvoiceSubCategory());
            if (accountingArticles.isEmpty()) {
                accountingArticle = new AccountingArticle(UUID.randomUUID().toString(), "Migration Accounting article", defaultTaxClass, chargeTemplate.getInvoiceSubCategory());
                accountingArticleService.create(accountingArticle);
            } else {
                accountingArticle = accountingArticles.get(0);
            }

            ArticleMappingLine articleMappingLine = new ArticleMappingLine();
            articleMappingLine.setChargeTemplate(chargeTemplate);
            articleMappingLine.setArticleMapping(defaultArticleMapping);
            articleMappingLine.setProduct(product);
            articleMappingLine.setAccountingArticle(accountingArticle);
            articleMappingLineService.create(articleMappingLine);
        });

    }

    private Product createProduct(ServiceTemplate serviceTemplate) {
        Product product = new Product();
        product.setCode(serviceTemplate.getCode());
        product.setDescription(serviceTemplate.getDescription());
        List<ProductChargeTemplateMapping> productCharges = getProductCharges(serviceTemplate);
        product.setProductCharges(productCharges);
        product.setCfValues(serviceTemplate.getCfValues());
        product.setCfAccumulatedValues(serviceTemplate.getCfAccumulatedValues());
        product.setDisabled(serviceTemplate.isDisabled());
        productService.create(product);
        return product;
    }

    private ProductVersion createProductVersion(Product product) {
        ProductVersion productVersion = new ProductVersion();
        productVersion.setProduct(product);
        productVersion.setShortDescription(product.getDescription());
        productVersion.setStatus(VersionStatusEnum.PUBLISHED);
        productVersion.setStatusDate(Calendar.getInstance().getTime());
        productVersionService.create(productVersion);
        return productVersion;
    }

    private ProductChargeTemplateMapping mapToProductChargeTemplate(ServiceChargeTemplate serviceCharge) {
        ProductChargeTemplateMapping productChargeTemplateMapping = new ProductChargeTemplateMapping();
        productChargeTemplateMapping.setChargeTemplate(serviceCharge.getChargeTemplate());
        return productChargeTemplateMapping;
    }

    private List<ProductChargeTemplateMapping> getProductCharges(ServiceTemplate serviceTemplate) {
        List<ProductChargeTemplateMapping> productCharges = serviceTemplate.getServiceSubscriptionCharges().stream().map(this::mapToProductChargeTemplate).collect(Collectors.toList());

        productCharges.addAll(serviceTemplate.getServiceRecurringCharges().stream().map(this::mapToProductChargeTemplate).collect(Collectors.toList()));

        productCharges.addAll(serviceTemplate.getServiceTerminationCharges().stream().map(this::mapToProductChargeTemplate).collect(Collectors.toList()));

        productCharges.addAll(serviceTemplate.getServiceUsageCharges().stream().map(this::mapToProductChargeTemplate).collect(Collectors.toList()));
        return productCharges;
    }
}
package org.meveo.service.script.catalog;

import java.util.*;
import java.util.Calendar;
import java.util.stream.Collectors;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.article.*;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.catalog.*;
import org.meveo.model.cpq.*;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.tax.TaxClass;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.article.*;
import org.meveo.service.catalog.impl.*;
import org.meveo.service.cpq.*;
import org.meveo.service.script.Script;
import org.meveo.service.tax.TaxClassService;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;

public class V11MigrationScript extends Script {
	public static final String DEFAULT_TAX_CLASS_CODE = "CMP_DATA";
	public static final long ARTICLE_MAPPING_ID = 1L;
	private static final Integer PAGE_COUNT = 100;
	private ServiceInstanceService serviceInstanceService = (ServiceInstanceService) getServiceInterface(
			ServiceInstanceService.class.getSimpleName());
	private ProductService productService = (ProductService) getServiceInterface(ProductService.class.getSimpleName());
	private ProductVersionService productVersionService = (ProductVersionService) getServiceInterface(
			ProductVersionService.class.getSimpleName());
	private OfferTemplateService offerTemplateService = (OfferTemplateService) getServiceInterface(
			OfferTemplateService.class.getSimpleName());
	private AccountingArticleService accountingArticleService = (AccountingArticleService) getServiceInterface(
			AccountingArticleService.class.getSimpleName());
	private ArticleMappingService articleMappingService = (ArticleMappingService) getServiceInterface(
			ArticleMappingService.class.getSimpleName());
	private ArticleMappingLineService articleMappingLineService = (ArticleMappingLineService) getServiceInterface(
			ArticleMappingLineService.class.getSimpleName());
	private ServiceTemplateService serviceTemplateService = (ServiceTemplateService) getServiceInterface(
			ServiceTemplateService.class.getSimpleName());
	private TaxClassService taxClassService = (TaxClassService) getServiceInterface(
			TaxClassService.class.getSimpleName());

	@Override
	public void execute(Map<String, Object> methodContext) throws BusinessException {
		List<ServiceTemplate> serviceTemplates = (List<ServiceTemplate>) methodContext.get("SERVICE_TEMPLATE");
		if (serviceTemplates != null && !serviceTemplates.isEmpty()) {
			serviceTemplates.forEach(this::map);
		} else {
			long count = serviceTemplateService.count(new PaginationConfiguration(null, null, null, null, null, "id", SortOrder.ASCENDING));
			if (PAGE_COUNT < count) {
				serviceTemplateService.list(new PaginationConfiguration(null, null)).forEach(this::map);
			} else {
				for (int index = 0; index < count; index = index + PAGE_COUNT) {
					serviceTemplateService.list(
							new PaginationConfiguration(index, PAGE_COUNT, null, null, null, "id", SortOrder.ASCENDING))
							.forEach(this::map);
				}
			}
		}
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
			offer.getOfferServiceTemplates()
					.removeIf(o -> o.getServiceTemplate().getId().equals(serviceTemplate.getId()));
			offerTemplateService.update(offer);
		});
		return product;
	}

	private void createArticle(Product product) {
		product.getProductCharges().stream().map(pc -> pc.getChargeTemplate()).forEach(chargeTemplate -> {
			AccountingArticle accountingArticle;
			List<AccountingArticle> accountingArticles = accountingArticleService
					.findByTaxClassAndSubCategory(chargeTemplate.getTaxClass(), chargeTemplate.getInvoiceSubCategory());
			if (accountingArticles.isEmpty()) {
				TaxClass taxClass = taxClassService.findByCode(DEFAULT_TAX_CLASS_CODE);
				accountingArticle = new AccountingArticle(UUID.randomUUID().toString(), "Migration Accounting article",
						taxClass, chargeTemplate.getInvoiceSubCategory());
				accountingArticleService.create(accountingArticle);
			} else {
				accountingArticle = accountingArticles.get(0);
			}
			ArticleMapping defaultArticleMapping = articleMappingService.findById(ARTICLE_MAPPING_ID);
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

	private ProductChargeTemplateMapping<OneShotChargeTemplate> mapToProductChargeTemplateSubscription(ServiceChargeTemplateSubscription serviceCharge) {
		ProductChargeTemplateMapping<OneShotChargeTemplate> productChargeTemplateMapping = new ProductChargeTemplateMapping<OneShotChargeTemplate>();
		productChargeTemplateMapping.setChargeTemplate(serviceCharge.getChargeTemplate());
		productChargeTemplateMapping.setCounterTemplate(serviceCharge.getCounterTemplate());
		if(serviceCharge.getAccumulatorCounterTemplates()!=null && !serviceCharge.getAccumulatorCounterTemplates().isEmpty())
			productChargeTemplateMapping.setAccumulatorCounterTemplates(serviceCharge.getAccumulatorCounterTemplates());
		productChargeTemplateMapping.setWalletTemplates(serviceCharge.getWalletTemplates());
		return productChargeTemplateMapping;
	}

	private ProductChargeTemplateMapping<RecurringChargeTemplate> mapToProductChargeTemplateRecurring(ServiceChargeTemplateRecurring serviceCharge) {
		ProductChargeTemplateMapping<RecurringChargeTemplate> productChargeTemplateMapping = new ProductChargeTemplateMapping<RecurringChargeTemplate>();
		productChargeTemplateMapping.setChargeTemplate(serviceCharge.getChargeTemplate());
		productChargeTemplateMapping.setCounterTemplate(serviceCharge.getCounterTemplate());
		if(serviceCharge.getAccumulatorCounterTemplates()!=null && !serviceCharge.getAccumulatorCounterTemplates().isEmpty())
		productChargeTemplateMapping.setAccumulatorCounterTemplates(serviceCharge.getAccumulatorCounterTemplates());
		productChargeTemplateMapping.setWalletTemplates(serviceCharge.getWalletTemplates());
		return productChargeTemplateMapping;
	}
	
	private ProductChargeTemplateMapping<UsageChargeTemplate> mapToProductChargeTemplateUsage(ServiceChargeTemplateUsage serviceCharge) {
		ProductChargeTemplateMapping<UsageChargeTemplate> productChargeTemplateMapping = new ProductChargeTemplateMapping<UsageChargeTemplate>();
		productChargeTemplateMapping.setChargeTemplate(serviceCharge.getChargeTemplate());
		productChargeTemplateMapping.setCounterTemplate(serviceCharge.getCounterTemplate());
		if(serviceCharge.getAccumulatorCounterTemplates()!=null && !serviceCharge.getAccumulatorCounterTemplates().isEmpty())
		productChargeTemplateMapping.setAccumulatorCounterTemplates(serviceCharge.getAccumulatorCounterTemplates());
		productChargeTemplateMapping.setWalletTemplates(serviceCharge.getWalletTemplates());
		return productChargeTemplateMapping;
	}
	private ProductChargeTemplateMapping<OneShotChargeTemplate> mapToProductTemplateTermination(ServiceChargeTemplateTermination serviceCharge) {
		ProductChargeTemplateMapping<OneShotChargeTemplate> productChargeTemplateMapping = new ProductChargeTemplateMapping<OneShotChargeTemplate>();
		productChargeTemplateMapping.setChargeTemplate(serviceCharge.getChargeTemplate());
		productChargeTemplateMapping.setCounterTemplate(serviceCharge.getCounterTemplate());
		if(serviceCharge.getAccumulatorCounterTemplates()!=null && !serviceCharge.getAccumulatorCounterTemplates().isEmpty())
		productChargeTemplateMapping.setAccumulatorCounterTemplates(serviceCharge.getAccumulatorCounterTemplates());
		productChargeTemplateMapping.setWalletTemplates(serviceCharge.getWalletTemplates());
		return productChargeTemplateMapping;
	}
	private List<ProductChargeTemplateMapping> getProductCharges(ServiceTemplate serviceTemplate) {
		List<ProductChargeTemplateMapping> productCharges = serviceTemplate.getServiceSubscriptionCharges().stream()
				.map(this::mapToProductChargeTemplateSubscription).collect(Collectors.toList());
		productCharges.addAll(serviceTemplate.getServiceRecurringCharges().stream()
				.map(this::mapToProductChargeTemplateRecurring).collect(Collectors.toList()));
		productCharges.addAll(serviceTemplate.getServiceTerminationCharges().stream()
				.map(this::mapToProductTemplateTermination).collect(Collectors.toList()));
		productCharges.addAll(serviceTemplate.getServiceUsageCharges().stream().map(this::mapToProductChargeTemplateUsage)
				.collect(Collectors.toList()));
		return productCharges;
	}
}
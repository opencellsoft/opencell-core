package org.meveo.service.script.catalog;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.model.DatePeriod;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.article.ArticleMapping;
import org.meveo.model.article.ArticleMappingLine;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferServiceTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.catalog.ServiceChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.billing.impl.article.ArticleMappingLineService;
import org.meveo.service.billing.impl.article.ArticleMappingService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.script.Script;
import org.meveo.service.tax.TaxClassService;

public class V11MigrationScript extends Script {
	public static final String ARTICLE_MAPPING_ID = "mainArticleMapping";
	public static final String SERVICE_TEMPLATE_SUFFIX = "_OLD";
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
	private PricePlanMatrixVersionService pricePlanMatrixVersionService = (PricePlanMatrixVersionService) getServiceInterface(
			PricePlanMatrixVersionService.class.getSimpleName());
	private ChargeTemplateService<ChargeTemplate> chargeTemplateService = (ChargeTemplateService) getServiceInterface(
			ChargeTemplateService.class.getSimpleName());
	private PricePlanMatrixService pricePlanMatrixService = (PricePlanMatrixService) getServiceInterface(
			PricePlanMatrixService.class.getSimpleName());

    private CustomFieldTemplateService cftService = (CustomFieldTemplateService) getServiceInterface(
            CustomFieldTemplateService.class.getSimpleName());

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
		
		long count = offerTemplateService.count(new PaginationConfiguration(null, null, null, null, null, "id", SortOrder.ASCENDING));
		if (PAGE_COUNT < count) {
			offerTemplateService.list(new PaginationConfiguration(null, null)).forEach(this::map);
		} else {
			for (int index = 0; index < count; index = index + PAGE_COUNT) {
				offerTemplateService.list(
						new PaginationConfiguration(index, PAGE_COUNT, null, null, null, "id", SortOrder.ASCENDING))
						.forEach(this::map);
			}
		}
		
		 count = chargeTemplateService.count(new PaginationConfiguration(null, null, null, null, null, "id", SortOrder.ASCENDING));
		if (PAGE_COUNT < count) {
			chargeTemplateService.list(new PaginationConfiguration(null, null)).forEach(this::map);
		} else {
			for (int index = 0; index < count; index = index + PAGE_COUNT) {
				chargeTemplateService.list(
						new PaginationConfiguration(index, PAGE_COUNT, null, null, null, "id", SortOrder.ASCENDING))
						.forEach(this::map);
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
		});
		
		return product;
	}
	
	
	public OfferTemplate map(OfferTemplate offerTemplate) {
		Product product=null;
		for(OfferServiceTemplate offerServiceTemplate:offerTemplate.getOfferServiceTemplates()) {
			OfferComponent offerComponent = new OfferComponent();
			String productCode=offerServiceTemplate.getServiceTemplate().getCode().replaceAll("_OLD$", "");
			product=productService.findByCode(productCode);
			if(product!=null) {
				offerComponent.setProduct(product);
				offerComponent.setOfferTemplate(offerTemplate);
				offerTemplate.getOfferComponents().add(offerComponent);
			}
			
		}
			offerTemplate.getOfferServiceTemplates().clear();
			offerTemplateService.update(offerTemplate);
		
		return offerTemplate;
	}
	public ChargeTemplate map(ChargeTemplate chargeTemplate) {
		List<PricePlanMatrix> ppmList=pricePlanMatrixService.listByChargeCode(chargeTemplate.getCode());
		for(PricePlanMatrix ppm:ppmList) {
			createPPMVersion(ppm);
		}
		return chargeTemplate;
	}

	private void createArticle(Product product) {
		product.getProductCharges().stream().map(pc -> pc.getChargeTemplate()).forEach(chargeTemplate -> {
			AccountingArticle accountingArticle;
			List<AccountingArticle> accountingArticles = accountingArticleService
					.findByTaxClassAndSubCategory(chargeTemplate.getTaxClass(), chargeTemplate.getInvoiceSubCategory());
			if (accountingArticles.isEmpty()) {
				accountingArticle = new AccountingArticle(UUID.randomUUID().toString(), "Migration Accounting article",
						chargeTemplate.getTaxClass(), chargeTemplate.getInvoiceSubCategory());
				accountingArticleService.create(accountingArticle);
			} else {
				accountingArticle = accountingArticles.get(0);
			}
			ArticleMapping defaultArticleMapping = articleMappingService.findByCode(ARTICLE_MAPPING_ID);
			if(defaultArticleMapping==null) {
				defaultArticleMapping=new ArticleMapping();
				defaultArticleMapping.setCode(ARTICLE_MAPPING_ID);
				defaultArticleMapping.setDescription("default ArticleMapping");
				articleMappingService.create(defaultArticleMapping);
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
		Product product=productService.findByCode(serviceTemplate.getCode());
		if(product==null) {
			product = new Product();
			product.setCode(serviceTemplate.getCode());
			product.setDescription(serviceTemplate.getDescription());
			product.setAuditable(serviceTemplate.getAuditable());
			List<ProductChargeTemplateMapping> productCharges = getProductCharges(product,serviceTemplate);
			product.setProductCharges(productCharges);
            copyCFTemplates(serviceTemplate, product);
			product.setCfValues(serviceTemplate.getCfValues());
			product.setCfAccumulatedValues(serviceTemplate.getCfAccumulatedValues());
			product.setDisabled(serviceTemplate.isDisabled());
			productService.create(product);
			product.setStatus(ProductStatusEnum.ACTIVE);
			productService.update(product);
			
			serviceTemplate.setCode(serviceTemplate.getCode()+"_OLD");
			
			serviceTemplateService.update(serviceTemplate);
		}else {
			log.warn("Product with code {} already exists",serviceTemplate.getCode());
		}
		
		return product;
	}
    private void copyCFTemplates(ICustomFieldEntity source, ICustomFieldEntity target) {
        try {
            String appliesToSource = CustomFieldTemplateService.calculateAppliesToValue(source);
            String appliesToTarget = CustomFieldTemplateService.calculateAppliesToValue(target);

            Map<String, CustomFieldTemplate> byAppliesTo = cftService.findByAppliesToNoCache(appliesToSource);
            if (byAppliesTo == null || byAppliesTo.isEmpty()) {
                return;
            }
            for (CustomFieldTemplate cft : byAppliesTo.values()) {
                cftService.copyCustomFieldTemplate(cft, appliesToTarget);
            }
        } catch (Exception e) {
            log.error("Can't copy CFs Templates between entities {} and {}.Error: {}", source, target, e.getMessage());
        }
    }

    private ProductVersion createProductVersion(Product product) {
		ProductVersion productVersion = new ProductVersion();
		productVersion.setProduct(product);
		productVersion.setShortDescription(product.getDescription());
		productVersion.setStatus(VersionStatusEnum.PUBLISHED);
		DatePeriod validity=new DatePeriod(product.getAuditable().getCreated(),null);
		productVersion.setValidity(validity);
		productVersion.setStatusDate(Calendar.getInstance().getTime());
		productVersionService.create(productVersion);
		
		product.setCurrentVersion(productVersion);
		productService.update(product);
		
		return productVersion;
	}
	
	private PricePlanMatrixVersion createPPMVersion(PricePlanMatrix ppm) {
		PricePlanMatrixVersion ppmv = new PricePlanMatrixVersion();
		ppmv.setPricePlanMatrix(ppm);
		ppmv.setAmountWithoutTax(ppm.getAmountWithoutTax());
		ppmv.setAmountWithoutTaxEL(ppm.getAmountWithoutTaxEL());
		ppmv.setAmountWithTax(ppm.getAmountWithTax());
		ppmv.setAmountWithTaxEL(ppm.getAmountWithTaxEL());
		ppmv.setLabel(ppm.getDescription());
		ppmv.setMatrix(false);
		ppmv.setStatus(VersionStatusEnum.PUBLISHED);
		ppmv.setStatusDate(Calendar.getInstance().getTime());
		ppmv.setVersion(1);
		DatePeriod validity=new DatePeriod(DateUtils.truncateTime(ppm.getAuditable().getCreated()),null);
		ppmv.setValidity(validity);
		pricePlanMatrixVersionService.create(ppmv);
		return ppmv;
	}

	private ProductChargeTemplateMapping<ChargeTemplate> mapToProductChargeTemplate(Product product,ServiceChargeTemplate serviceCharge) {
		ChargeTemplate chargeTemplate=serviceCharge.getChargeTemplate();
		ProductChargeTemplateMapping<ChargeTemplate> productChargeTemplateMapping = new ProductChargeTemplateMapping<ChargeTemplate>();
		productChargeTemplateMapping.setChargeTemplate(chargeTemplate);
		productChargeTemplateMapping.setCounterTemplate(serviceCharge.getCounterTemplate());
		productChargeTemplateMapping.setProduct(product);
		//productChargeTemplateMapping.setAccumulatorCounterTemplates(serviceCharge.getAccumulatorCounterTemplates());
	    switch (chargeTemplate.getChargeMainType()) {
	    case RECURRING :
	    	chargeTemplateService.removeServiceLinkChargeRecurring(chargeTemplate.getId());
	    	break;
	    case ONESHOT :
	    	if(((OneShotChargeTemplate)chargeTemplate).getOneShotChargeTemplateType()==OneShotChargeTemplateTypeEnum.TERMINATION) {
	    		chargeTemplateService.removeServiceLinkChargeTermination(chargeTemplate.getId());
	    	}else {
	    		chargeTemplateService.removeServiceLinkChargeSubscription(chargeTemplate.getId());
	    	}
	    	break;
	    case USAGE :
	    	chargeTemplateService.removeServiceLinkChargeUsage(chargeTemplate.getId());
		default:
			break;	
	    }
	   
	    
		
		
		return productChargeTemplateMapping;
	}

	private List<ProductChargeTemplateMapping> getProductCharges(Product product,ServiceTemplate serviceTemplate) {
		List<ProductChargeTemplateMapping> productCharges = serviceTemplate.getServiceSubscriptionCharges().stream()
				.map(key ->mapToProductChargeTemplate(product,key)).collect(Collectors.toList());
		productCharges.addAll(serviceTemplate.getServiceRecurringCharges().stream()
				.map(key ->mapToProductChargeTemplate(product,key)).collect(Collectors.toList()));
		productCharges.addAll(serviceTemplate.getServiceTerminationCharges().stream()
				.map(key ->mapToProductChargeTemplate(product,key)).collect(Collectors.toList()));
		productCharges.addAll(serviceTemplate.getServiceUsageCharges().stream().map(key ->mapToProductChargeTemplate(product,key))
				.collect(Collectors.toList()));
	
		
		return productCharges;
	}
}
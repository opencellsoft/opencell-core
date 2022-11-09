package org.meveo.api.cpq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import liquibase.pro.packaged.D;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.util.Strings;
import org.assertj.core.util.DateUtil;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.catalog.DiscountPlanApi;
import org.meveo.api.catalog.DiscountPlanItemApi;
import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.dto.catalog.ChargeTemplateDto;
import org.meveo.api.dto.catalog.CpqOfferDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.cpq.CommercialRuleHeaderDTO;
import org.meveo.api.dto.cpq.GroupedAttributeDto;
import org.meveo.api.dto.cpq.OfferContextConfigDTO;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.cpq.OfferProductsDto;
import org.meveo.api.dto.cpq.OfferTemplateAttributeDTO;
import org.meveo.api.dto.cpq.ProductChargeTemplateMappingDto;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductVersionAttributeDTO;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import  org.meveo.api.dto.response.PagingAndFiltering.SortOrder;
import org.meveo.api.dto.response.catalog.GetCpqOfferResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductVersionsResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductsResponseDto;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductVersionResponse;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.DatePeriod;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ProductChargeTemplateMapping;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.GroupedAttributes;
import org.meveo.model.cpq.Media;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ProductChargeTemplateMappingService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.CommercialRuleHeaderService;
import org.meveo.service.cpq.CommercialRuleLineService;
import org.meveo.service.cpq.GroupedAttributeService;
import org.meveo.service.cpq.MediaService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.cpq.rule.ReplacementRulesExecutor;
import org.meveo.service.cpq.rule.SelectedAttributes;
import org.meveo.service.crm.impl.CustomerBrandService;

/**
 * @author Tarik FAKHOURI
 * @author Mbarek-Ay
 * @version 10.0
 */
@Stateless
public class ProductApi extends BaseApi {


	private static final String PRODUCT_STATUS_NOT_FOUND = "Status (%d) not found!!";

	@Inject
	private ProductService productService;
	@Inject
	private ProductLineService productLineService;
	@Inject
	private CustomerBrandService customerBrandService;
	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private ProductVersionService productVersionService;

	@Inject
	private TagService tagService;

	@Inject
	private OfferTemplateService offerTemplateService;


	@Inject
	private AttributeService  attributeService;

	@Inject
	private OfferTemplateApi  offerTemplateApi;

	@Inject
	private ChargeTemplateService<ChargeTemplate> chargeTemplateService;

	@Inject
	private CommercialRuleHeaderService commercialRuleHeaderService;

	@Inject
	private DiscountPlanService discountPlanService;

	@Inject
	private DiscountPlanApi discountPlanApi;

	@Inject
	private DiscountPlanItemApi discountPlanItemApi;

	@Inject
	private GroupedAttributeService  groupedAttributeService;

	 @Inject
	 private MediaService mediaService;

	 @Inject
    private CommercialRuleLineService commercialRuleLineService;

	 @Inject
	 private CounterTemplateService counterTemplateService;

	@Inject
	private ProductChargeTemplateMappingService productChargeTemplateMappingService;

	private static final String DEFAULT_SORT_ORDER_ID = "id";

	/**
	 * @return ProductDto
	 * @throws ProductException
	 */
	public ProductDto create(ProductDto productDto){
		if(Strings.isEmpty(productDto.getCode())) {
			missingParameters.add("code");
		}
		if(Strings.isEmpty(productDto.getLabel())){
			missingParameters.add("label");
		}
		handleMissingParameters();
		try {
			productDto.setCode(productDto.getCode().trim());
			Product product = populateProduct(productDto, true);
			productService.create(product);
			ProductVersionDto currentProductVersion=productDto.getCurrentProductVersion();
			ProductDto response = new ProductDto(product);
			ProductVersion productVersion;
			if(currentProductVersion != null) {
				productVersion = createProductVersion(product, productDto.getCurrentProductVersion());
				response.setCurrentProductVersion(new GetProductVersionResponse(productVersion));
			} else {
				productVersion= new ProductVersion();
				productVersion.setProduct(product);
				productVersion.setShortDescription(productDto.getLabel());
				productVersion.setStatus(VersionStatusEnum.DRAFT);
				productVersion.setStatusDate(Calendar.getInstance().getTime());
				productVersion.getValidity().setFrom(new Date());
				productVersionService.create(productVersion);
				response.setCurrentProductVersion(new ProductVersionDto(productVersion));
			}
			product.setCurrentVersion(productVersion);
			processMedias(productDto, product);
			return response;
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
	}

	/**
	 * @param productDto
	 * @throws ProductException
	 */
	public void updateProduct(String productCode, ProductDto productDto){
		if(Strings.isEmpty(productCode)){
			missingParameters.add("productCode");
		}
		handleMissingParameters();
		try {
			Product product = productService.findByCode(productCode);
			if (product == null) {
				throw new EntityDoesNotExistsException(Product.class, productCode);
			}

			if(!productCode.equalsIgnoreCase(productDto.getCode()) &&  productService.findByCode(productDto.getCode()) != null)
				throw new EntityAlreadyExistsException(Product.class,productDto.getCode());

			//set current product version
			var versions = productVersionService.findLastVersionByCode(productCode);
			if(!Strings.isEmpty(productDto.getCode())){
				product.setCode(productDto.getCode());
			}
			if(!Strings.isEmpty(productDto.getLabel())){
				product.setDescription(productDto.getLabel());
			}
			if(!StringUtils.isBlank(productDto.getProductLineCode())) {
				ProductLine productLine=productLineService.findByCode(productDto.getProductLineCode());
				if (productLine == null) {
					throw new EntityDoesNotExistsException(ProductLine.class,productDto.getProductLineCode());
				}
				product.setProductLine(productLine);
			}
			if(!StringUtils.isBlank(productDto.getBrandCode())) {
				CustomerBrand customerBrand=customerBrandService.findByCode(productDto.getBrandCode());
				if (customerBrand == null) {
					throw new EntityDoesNotExistsException(CustomerBrand.class,productDto.getBrandCode());
				}
				product.setBrand(customerBrand);
			}

			if(productDto.getCurrentProductVersion() != null){
				checkMandatoryFields(productDto.getCurrentProductVersion());
				ProductVersion existingProductVersion = productVersionService.findByProductAndVersion(productCode, productDto.getCurrentProductVersion().getCurrentVersion());
				if(existingProductVersion != null)
					updateProductVersion(productDto.getCurrentProductVersion(), existingProductVersion);
				else
					createProductVersion(null, productDto.getCurrentProductVersion());
			}
			Set<DiscountPlan> discountList = new HashSet<>();
			if(productDto.getDiscountList() != null && !productDto.getDiscountList().isEmpty()){
				Set<DiscountPlan> discountListUsingObjects = productDto.getDiscountList().stream()
						.map(discount -> {
							DiscountPlan discountPlan = discountPlanService.findByCode(discount.getCode());
							if (discountPlan == null)
								createDiscountPlan(discount);
							return discountPlan;
						})
						.collect(Collectors.toSet());
				discountList.addAll(discountListUsingObjects);
			}

	    	if(productDto.getDiscountListCodes() != null && !productDto.getDiscountListCodes().isEmpty()){
				Set<DiscountPlan> discountListUsingCodes = productDto.getDiscountListCodes().stream()
						.map(discountCode -> {
							DiscountPlan discountPlan = discountPlanService.findByCode(discountCode);
							if (discountPlan == null)
								throw new EntityDoesNotExistsException(DiscountPlan.class, discountCode);
							return discountPlan;
						})
						.collect(Collectors.toSet());
				discountList.addAll(discountListUsingCodes);
			}

	    	if(productDto.getPriceVersionDateSetting() != null){
	    		product.setPriceVersionDateSetting(productDto.getPriceVersionDateSetting());
			}
	    	
	    	product.getDiscountList().clear();
	    	if(!discountList.isEmpty()){
	    		product.getDiscountList().addAll(discountList);
			}

	    	if(productDto.getReference() != null){
				product.setReference(productDto.getReference());
			}
	    	if(productDto.getModel() != null){
				product.setModel(productDto.getModel());
			}
	    	if(productDto.getModelChildren() != null){
				product.setModelChildren(productDto.getModelChildren());
			}
			if(productDto.isDiscountFlag() != null){
				product.setDiscountFlag(productDto.isDiscountFlag());
			}
			if(productDto.isPackageFlag() != null){
				product.setPackageFlag(productDto.isPackageFlag());
			}
			if(productDto.getProductChargeTemplateMappingDto() != null){
				createProductChargeTemplateMappings(product, productDto.getProductChargeTemplateMappingDto());
			}

			var publishedVersion = versions.stream()
											.filter(pv -> pv.getStatus().equals(VersionStatusEnum.PUBLISHED))
												.sorted( (pv1, pv2) -> pv2.getValidity().compareFieldTo(pv1.getValidity())).collect(Collectors.toList());
			if(publishedVersion.size() >= 1 ) {
				product.setCurrentVersion(publishedVersion.get(0));
			}else {
				versions.stream()
						.filter(pv -> pv.getStatus().equals(VersionStatusEnum.DRAFT))
						.sorted( (pv1, pv2) -> pv2.getAuditable().compareByUpdated(pv1.getAuditable()))
						.findFirst()
						.ifPresent(productVersion -> product.setCurrentVersion(productVersion));
			}

			Boolean isModel = productDto.getIsModel();
		       if (isModel != null) {
		    	   product.setIsModel(isModel);
		       }

			if(productDto.getCustomFields() != null) {
				populateCustomFields(productDto.getCustomFields(), product, false);
			}
			if(!StringUtils.isBlank(productDto.getProductModelCode())) {
	    		product.setProductModel(loadEntityByCode(productService, productDto.getProductModelCode(), Product.class));
	    	}
			if(productDto.getCommercialRuleCodes() != null){
				var commercialRuleheaderAdded = new ArrayList<CommercialRuleHeader>();
				for (String commercialRuleCode : productDto.getCommercialRuleCodes()) {
					commercialRuleheaderAdded.add(loadEntityByCode(commercialRuleHeaderService, commercialRuleCode, CommercialRuleHeader.class));
				}
				product.getCommercialRuleHeader().clear();
				product.getCommercialRuleHeader().addAll(commercialRuleheaderAdded);
			}
			processMedias(productDto, product);
			productService.updateProduct(product);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
	}

	/**
	 * update status of a product
	 * @param codeProduct
	 * @param status
	 * @return
	 * @throws MeveoApiException when the status is unknown and the status
	 */
	public void updateStatus(String codeProduct, ProductStatusEnum status){
		if(Strings.isEmpty(codeProduct)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		if(status == null)
			throw new MeveoApiException(String.format(PRODUCT_STATUS_NOT_FOUND, status));

		try {
			productService.updateStatus(codeProduct, status);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
	}

	/**
	 * @param code
	 * @return
	 * @throws ProductException
	 */

	public GetProductDtoResponse findByCode(String code) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
			handleMissingParameters();
		}
		Product product = productService.findByCode(code);
		if (product == null) {
			throw new EntityDoesNotExistsException(Product.class,code);
		}
		ChargeTemplateDto chargeTemplateDto=null;
		Set<ChargeTemplateDto> chargeTemplateDtos=new HashSet<ChargeTemplateDto>();
		for(ProductChargeTemplateMapping prodcutCharge : product.getProductCharges()) {
			chargeTemplateDto=new ChargeTemplateDto(prodcutCharge.getChargeTemplate(),entityToDtoConverter.getCustomFieldsDTO(prodcutCharge.getChargeTemplate()));
			chargeTemplateDtos.add(chargeTemplateDto);
		}
			GetProductDtoResponse  result = new GetProductDtoResponse(product,chargeTemplateDtos,true);
			result.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(product));
			return result;
		}

	public ProductVersion createProductVersion(Product product, ProductVersionDto postData) throws MeveoApiException, BusinessException {
		checkMandatoryFields(postData);
		handleMissingParameters();
		if(product == null) {
			product = checkProductExiste(postData);
		}
		ProductVersion  productVersion= new ProductVersion();
		populateProduct(postData, product, productVersion);
		productVersionService.create(productVersion);
        return productVersion;
    }

	private void populateProduct(ProductVersionDto postData, Product product, ProductVersion productVersion) {
		productVersion.setProduct(product);
		productVersion.setShortDescription(postData.getShortDescription());
		productVersion.setLongDescription(postData.getLongDescription());
		productVersion.setCurrentVersion(postData.getCurrentVersion());
		if(postData.getValidity() == null) {
			var datePeriod = new DatePeriod();
			datePeriod.setFrom(Calendar.getInstance().getTime());
		}else if(postData.getValidity() != null && postData.getValidity().getFrom() == null)
			postData.getValidity().setFrom(Calendar.getInstance().getTime());
		
		productVersion.setValidity(postData.getValidity());
		productVersion.setStatus(VersionStatusEnum.DRAFT);
		productVersion.setStatusDate(Calendar.getInstance().getTime());
		processProductVersionAttributes(postData,productVersion);
		processTags(postData, productVersion);
		processGroupedAttribute(postData, productVersion);
	}

	private void checkMandatoryFields(ProductVersionDto postData) {
		if (StringUtils.isBlank(postData.getProductCode())) {
            missingParameters.add("productCode");
        }
		if (StringUtils.isBlank(postData.getCurrentVersion())) {
			missingParameters.add("currentVersion");
		}
		handleMissingParametersAndValidate(postData);
	}

	private Product checkProductExiste(ProductVersionDto postData) {
		Product product = productService.findByCode(postData.getProductCode());
		if (product == null) {
			throw new EntityDoesNotExistsException(Product.class, postData.getProductCode(),"productCode");
		}
		return product;
	}

	/**
     * Updates a product version Entity
     *
     * @param postData posted data to API
     *
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws ProductException
     * @throws ProductVersionException
     */
    public ProductVersion updateProductVersion(ProductVersionDto postData) throws MeveoApiException, BusinessException {
        String productCode = postData.getProductCode();
        int currentVersion = postData.getCurrentVersion();
        if (StringUtils.isBlank(productCode)) {
            missingParameters.add("productCode");
        }
        if (StringUtils.isBlank(currentVersion)) {
            missingParameters.add("currentVersion");
        }
       ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
       if(productVersion==null) {
           throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
       }
		return updateProductVersion(postData, productVersion);
	}

	private ProductVersion updateProductVersion(ProductVersionDto postData, ProductVersion productVersion) {
		productVersion.setShortDescription(postData.getShortDescription());
		productVersion.setLongDescription(postData.getLongDescription());
		if(postData.getValidity() == null) {
			var datePeriod = new DatePeriod();
			datePeriod.setFrom(Calendar.getInstance().getTime());
			productVersion.setValidity(datePeriod);
		}else if(postData.getValidity() != null && postData.getValidity().getFrom() == null) {
			postData.getValidity().setFrom(Calendar.getInstance().getTime());
			productVersion.setValidity(postData.getValidity());
		}
		Date today = DateUtils.setTimeToZero(new Date());
		if(postData.getValidity() != null
				&& DateUtils.setTimeToZero(postData.getValidity().getTo()).compareTo(today) <= 0) {
			throw new MeveoApiException("End date must be greater than today");
		}
		if(productVersion.getStatus() == VersionStatusEnum.CLOSED
				&& postData.getValidity() != null && postData.getValidity().getTo() != null) {
			throw new MeveoApiException("Can not update endDate for closed version");
		}
		productVersion.setValidity(postData.getValidity());
		productVersion.setStatus(postData.getStatus() == null ? VersionStatusEnum.DRAFT : postData.getStatus());
		productVersion.setStatusDate(Calendar.getInstance().getTime());
		processProductVersionAttributes(postData,productVersion);
		processTags(postData, productVersion);
		processGroupedAttribute(postData, productVersion);
		try {
			productVersionService.updateProductVersion(productVersion);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
		return productVersion;
	}

	/**
     * Delete a product version Entity
     *
     * @param productCode and currentVersion
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws ProductException
     * @throws ProductVersionException
     */
    public void removeProductVersion(String productCode,int currentVersion){
    	  if (StringUtils.isBlank(productCode)) {
              missingParameters.add("productCode");
          }
          if (StringUtils.isBlank(currentVersion)) {
              missingParameters.add("currentVersion");
          }
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if(productVersion==null) {
	            throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
	        }
	        productVersionService.removeProductVersion(productVersion);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
    }
    /**
     * Duplicate a product version Entity
     *
     * @param postData posted data to API
     *
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws ProductException
     * @throws ProductVersionException
     */
    public GetProductVersionResponse duplicateProductVersion(String productCode, int currentVersion)  throws MeveoApiException, BusinessException  {
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if(productVersion==null) {
	            throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
	        }
	        return new GetProductVersionResponse(productVersionService.duplicate(productVersion, true),true,true);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
    }

    public GetProductVersionResponse findProductVersion(String productCode, int currentVersion)  throws MeveoApiException, BusinessException  {

		try {
			ProductVersion productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if(productVersion==null) {
	            throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
	        }
	        return new GetProductVersionResponse(productVersion,true,true);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
    }


    public Product duplicateProduct(String codeProduct, boolean duplicateHierarchy, boolean preserveCode) throws MeveoApiException, BusinessException {

    	Product product = null;
    	try {
    		product = productService.findByCode(codeProduct, Arrays.asList("productLine"));
    		if(product == null) {
    			throw new EntityDoesNotExistsException(Product.class, codeProduct);
    		}
    		return productService.duplicateProduct(product, duplicateHierarchy, preserveCode);
    	}catch (BusinessException e) {
			throw new MeveoApiException(e);
		}

    }
    /**
     * Change status product version Entity
     *
     * @param postData posted data to API
     *
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws ProductException
     * @throws ProductVersionException
     */
    public GetProductVersionResponse UpdateProductVersionStatus (String productCode, int currentVersion,VersionStatusEnum status)  throws MeveoApiException, BusinessException {
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if(productVersion==null) {
	            throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
	        }
	        if(VersionStatusEnum.PUBLISHED == status) {
	        	var productVersions = productVersionService.findByProduct(productCode);
	        	var overloppingExist = productVersions
	        									.stream()
	        									.filter(pv -> {
	        										return pv.getId() != productVersion.getId() && pv.getStatus() == VersionStatusEnum.PUBLISHED;
	        									})
	        									.filter(pv -> {
	        										return productVersion.getValidity().isCorrespondsToPeriod(pv.getValidity().getFrom(), pv.getValidity().getTo(), false);
	        									})
	        									.collect(Collectors.toList()).size() > 0;
	        	if(overloppingExist)
	        		throw new MeveoApiException("An overlap of validity dates has been detected");
	        }
	        productVersionService.updateProductVersionStatus(productVersion,status);
	        return new GetProductVersionResponse(productVersion,true,true);
		} catch (BusinessException e) {
			throw new MeveoApiException(e.getMessage());
		}
    }


    /**
     * Creates or updates product version based on the product code and current version.
     *
     * @param postData posted data.
     *
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws MeveoApiException
     * @throws BusinessException
     */
    public ProductVersion createOrUpdateProductVersion(ProductVersionDto postData) throws MeveoApiException, BusinessException {
    	String productCode = postData.getProductCode();
        int currentVersion = postData.getCurrentVersion();
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if (productVersion == null) {
	            return createProductVersion(null, postData);
	        } else {
	            return updateProductVersion(postData);
	        }
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}

    }

    public Product populateProduct(ProductDto productDto, boolean isNewEntity) {
    	Product product=new Product();
    	product.setCode(productDto.getCode());
    	product.setDescription(productDto.getLabel());

    	if(!StringUtils.isBlank(productDto.getProductModelCode())) {
    		product.setProductModel(loadEntityByCode(productService, productDto.getProductModelCode(), Product.class));
    	}
    	if(!StringUtils.isBlank(productDto.getProductLineCode())) {
    		ProductLine productLine=productLineService.findByCode(productDto.getProductLineCode());
    		if (productLine == null) {
    			throw new EntityDoesNotExistsException(ProductLine.class,productDto.getProductLineCode());
    		}
    		product.setProductLine(productLine);
    	}
    	if(!StringUtils.isBlank(productDto.getBrandCode())) {
    		CustomerBrand customerBrand=customerBrandService.findByCode(productDto.getBrandCode());
    		if (customerBrand == null) {
    			throw new EntityDoesNotExistsException(CustomerBrand.class,productDto.getBrandCode());
    		}
			product.setBrand(customerBrand);
		}

		   Boolean isModel = productDto.getIsModel();
	       if (isModel != null) {
	    	   product.setIsModel(isModel);
	       }

    	if(productDto.getDiscountList() != null && !productDto.getDiscountList().isEmpty()){
    		product.setDiscountList(productDto.getDiscountList().stream()
					.map(discount -> createDiscountPlan(discount))
					.collect(Collectors.toSet()));
		}

    	if(productDto.getDiscountListCodes() != null && !productDto.getDiscountListCodes().isEmpty()){
    		product.getDiscountList().addAll(productDto.getDiscountListCodes().stream()
					.map(discountCode -> {
						DiscountPlan discountPlan = discountPlanService.findByCode(discountCode);
						if(discountPlan == null)
							throw new EntityDoesNotExistsException(DiscountPlan.class, discountCode);
						return discountPlan;
					})
					.collect(Collectors.toSet())
			);
		}

		product.setReference(productDto.getReference());
		product.setModel(productDto.getModel());
		product.setModelChildren(productDto.getModelChildren());
		product.setDiscountFlag(productDto.isDiscountFlag());
		
		if(productDto.getPriceVersionDateSetting() != null) {
			product.setPriceVersionDateSetting(productDto.getPriceVersionDateSetting());
		}
		createProductChargeTemplateMappings(product, productDto.getProductChargeTemplateMappingDto());
		/***@TODO : update product chargeTemplates
		 * Use this method to get them by code : chargeTemplateService.getChargeTemplatesByCodes(productDto.getChargeTemplateCodes())***/
		populateCustomFields(productDto.getCustomFields(), product, isNewEntity);


		return product;
    }

	private DiscountPlan createDiscountPlan(DiscountPlanDto discount) {
		DiscountPlan discountPlan = discountPlanApi.create(discount);
		if(discount.getDiscountPlanItems() == null || discount.getDiscountPlanItems().isEmpty()) return discountPlan;
		discount.getDiscountPlanItems().stream()
				.map(discountItem -> {
					discountPlanItemApi.create(discountItem);
					return discountItem;
				}).collect(Collectors.toList());
		return discountPlan;
	}

	@SuppressWarnings("unchecked")
    private void createProductChargeTemplateMappings(Product product, List<ProductChargeTemplateMappingDto> productChargeTemplateMappingDtos) {
		List<String> persistedChargesCodes = product.getProductCharges().stream().map(x->x.getChargeTemplate().getCode()).collect(Collectors.toList());
		List<String> dtoChargesCodes = productChargeTemplateMappingDtos.stream().map(x->x.getChargeCode()).collect(Collectors.toList());
		List<String> removedChargesCodes = persistedChargesCodes.stream().filter(x->!dtoChargesCodes.contains(x)).collect(Collectors.toList());
		product.getProductCharges().removeIf(x->removedChargesCodes.contains(x.getChargeTemplate().getCode()));
		//Set<ChargeTemplate> chargeTemplates = chargeTemplateService.getChargeTemplatesByCodes(chargeTemplateCodes);
		product.getProductCharges().addAll(productChargeTemplateMappingDtos.stream().map(pctm -> {
					ProductChargeTemplateMapping<ChargeTemplate> chargeTemplateProductChargeTemplateMapping = productChargeTemplateMappingService.findByProductAndOfferTemplate(pctm.getProductCode(), pctm.getChargeCode());
					if(chargeTemplateProductChargeTemplateMapping == null)
					    chargeTemplateProductChargeTemplateMapping = new ProductChargeTemplateMapping<ChargeTemplate>();
					chargeTemplateProductChargeTemplateMapping.setProduct(product);
					if(!Strings.isEmpty(pctm.getChargeCode()))
						chargeTemplateProductChargeTemplateMapping.setChargeTemplate(loadEntityByCode(chargeTemplateService, pctm.getChargeCode(), ChargeTemplate.class));
					if(!Strings.isEmpty(pctm.getCounterCode()))
						chargeTemplateProductChargeTemplateMapping.setCounterTemplate(loadEntityByCode(counterTemplateService, pctm.getCounterCode(), CounterTemplate.class));
					if(pctm.getAccumulatorCounterCodes() != null && !pctm.getAccumulatorCounterCodes().isEmpty()) {
						var accumulator = pctm.getAccumulatorCounterCodes().stream()
											.map(counterCode -> loadEntityByCode(counterTemplateService, counterCode, CounterTemplate.class))
												.collect(Collectors.toList());
						chargeTemplateProductChargeTemplateMapping.setAccumulatorCounterTemplates(accumulator);
					}
					return chargeTemplateProductChargeTemplateMapping;

				}).collect(Collectors.toList()));
	}

	public void removeProduct(String codeProduct) {
		try {
			productService.removeProduct(codeProduct);
		} catch (Exception e) {
			if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
				throw new DeleteReferencedEntityException(Product.class, codeProduct);
			}
			throw new MeveoApiException(e.getMessage());
		}
	}


	public GetListProductsResponseDto list(OfferContextDTO offerContextDTO) {
		GetListProductsResponseDto result = new GetListProductsResponseDto();
		String billingAccountCode=offerContextDTO.getCustomerContextDTO().getBillingAccountCode();
		String offerCode=offerContextDTO.getOfferCode();
		if(Strings.isEmpty(billingAccountCode)) {
			missingParameters.add("billingAccountCode");
		}
		if(Strings.isEmpty(offerCode)) {
			missingParameters.add("offerCode");
		}
		handleMissingParameters();
		List<String> tagCodes=new ArrayList<String>();
		BillingAccount ba=billingAccountService.findByCode(billingAccountCode);
		if(ba!=null) {
			List<Tag> entityTags=tagService.getTagsByBA(ba);
			if(!entityTags.isEmpty()) {
				for(Tag tag:entityTags) {
					tagCodes.add(tag.getCode());
				}
			}}
		List<String> sellerTags=offerContextDTO.getCustomerContextDTO().getSellerTags();
		List<String> customerTags=offerContextDTO.getCustomerContextDTO().getCustomerTags();
		HashSet<String> resultBaTags = new HashSet<String>();
		resultBaTags.addAll(tagCodes);
		resultBaTags.addAll(sellerTags);
		resultBaTags.addAll(customerTags);

		OfferTemplate offerTemplate=offerTemplateService.findByCode(offerCode);
		if(offerTemplate!=null) {
			List<OfferComponent> offerComponents=offerTemplate.getOfferComponents();

			List<ProductVersion> prdVersionsByTags=productVersionService.findByTags(resultBaTags);
			Set<Product> products=new HashSet<Product>();
			for(ProductVersion pdVersion:prdVersionsByTags) {
				products.add(pdVersion.getProduct());
			}
			ProductDto prodDto=null;
			Product product=null;
			if(!offerComponents.isEmpty() && !prdVersionsByTags.isEmpty()) {
				for(OfferComponent oc:offerComponents) {
					product=oc.getProduct();
					if(products.contains(product)) {
						prodDto=new ProductDto(product);
						prodDto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(product));
						result.addProduct(prodDto);
				}}
			}
		}
		return result;
	}
	private void processGroupedAttribute(ProductVersionDto postData, ProductVersion productVersion) {
		Set<String> groupedAttributesCodes = postData.getGroupedAttributeCodes();
		if(groupedAttributesCodes != null && !groupedAttributesCodes.isEmpty()) {
			List<GroupedAttributes> groupedAttributes = new ArrayList<GroupedAttributes>();
			for (String groupedCode : groupedAttributesCodes) {
				GroupedAttributes attributes = loadEntityByCode(groupedAttributeService, groupedCode, GroupedAttributes.class);
				groupedAttributes.add(attributes);
			}
			productVersion.setGroupedAttributes(groupedAttributes);
		}else {
			productVersion.setGroupedAttributes(null);
		}
	}
	private void processProductVersionAttributes(ProductVersionDto postData, ProductVersion productVersion) {
		Set<ProductVersionAttributeDTO> attributeCodes = postData.getProductAttributes(); 
		productVersion.getAttributes().clear();
		if(attributeCodes != null && !attributeCodes.isEmpty()){
            List<ProductVersionAttribute> attributes = new ArrayList<>();
			for(ProductVersionAttributeDTO attr:attributeCodes) {
                var currentSequence = attr.getSequence();
				Attribute attribute = attributeService.findByCode(attr.getAttributeCode());
				if(attribute == null) { 
                    throw new EntityDoesNotExistsException(Attribute.class, attr.getAttributeCode());
				}
				ProductVersionAttribute productAttribute = new ProductVersionAttribute();
				productAttribute.setProductVersion(productVersion);
				productAttribute.setAttribute(attribute);
				productAttribute.setSequence(currentSequence);
				productAttribute.setMandatoryWithEl(attr.getMandatoryWithEl());
				productAttribute.setDefaultValue(attr.getDefaultValue());
				productAttribute.setDisplay(attr.isDisplay());
				productAttribute.setMandatory(attr.isMandatory());
				productAttribute.setReadOnly(attr.isReadOnly());
				productAttribute.setValidationLabel(attr.getValidationLabel());
				productAttribute.setValidationPattern(attr.getValidationPattern());
				productAttribute.setValidationType(attr.getValidationType());
				//productVersionAttributeService.checkValidationPattern(productAttribute);
                attributes.add(productAttribute);
			}
            productVersion.getAttributes().addAll(attributes);
		}
	}

	private void processTags(ProductVersionDto postData, ProductVersion productVersion) {
		Set<String> tagCodes = postData.getTagCodes();
		if(tagCodes != null && !tagCodes.isEmpty()){
			Set<Tag> tags = new HashSet<>();
			for(String code:tagCodes) {
				Tag tag=tagService.findByCode(code);
				if(tag == null) {
					throw new EntityDoesNotExistsException(Tag.class,code);
				}
				tags.add(tag);
			}
			productVersion.setTags(tags);
		}else {
			productVersion.setTags(null);
		}
	}

	private void processMedias(ProductDto postData, Product product) {
		Set<String> mediaCodes = postData.getMediaCodes();
		if(mediaCodes != null && !mediaCodes.isEmpty()){
			List<Media> medias = new ArrayList<>();
			for(String code:mediaCodes) {
				Media media = mediaService.findByCode(code);
				if(media == null) {
					throw new EntityDoesNotExistsException(Media.class,code);
				}
				medias.add(media);
			}
			product.setMedias(medias);
		}else {
			product.setMedias(null);
		}
	}


	public List<GetProductVersionResponse> findProductVersionByProduct(String productCode)  throws MeveoApiException, BusinessException  {
		try {
			List<GetProductVersionResponse> GetProductVersionResponses=new ArrayList<GetProductVersionResponse>();
			List<ProductVersion> productVersions = productVersionService.findByProduct(productCode);

			GetProductVersionResponse getProductVersionResponse=null;
			if(!productVersions.isEmpty()) {

				for(ProductVersion prodversion:productVersions) {
					getProductVersionResponse=new GetProductVersionResponse(prodversion,true,true);
					GetProductVersionResponses.add(getProductVersionResponse);
				}
			}
			return GetProductVersionResponses;
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
	}


	 public GetListProductVersionsResponseDto listProductVersions (PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
		 if (pagingAndFiltering == null) {
			 pagingAndFiltering = new PagingAndFiltering();
		 }
		 String sortBy = DEFAULT_SORT_ORDER_ID;
		 if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
			 sortBy = pagingAndFiltering.getSortBy();
		 }
		 var filters = new HashedMap<String, Object>();
		 pagingAndFiltering.getFilters().forEach( (key, value) -> {
			 String newKey = key.replace("productCode", "product.code");
			 filters.put(key.replace(key, newKey), value);
		 });
		 pagingAndFiltering.getFilters().clear();
		 pagingAndFiltering.getFilters().putAll(filters);
		 List<String> fields = Arrays.asList("productLine", "brand");
		 PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, SortOrder.ASCENDING, fields, pagingAndFiltering, ProductVersion.class);
		 Long totalCount = productVersionService.count(paginationConfiguration);
		 GetListProductVersionsResponseDto result = new GetListProductVersionsResponseDto();
		 result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
		 result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

		 if(totalCount > 0) {
			 productVersionService.list(paginationConfiguration).stream().forEach(p -> {
				 result.getProductVersions().add(new ProductVersionDto(p));
			 });
		 }
		 return result;
	 }


	 public GetListProductsResponseDto listProducts (PagingAndFiltering pagingAndFiltering) throws MeveoApiException {
		 if (pagingAndFiltering == null) {
			 pagingAndFiltering = new PagingAndFiltering();
		 }
		 String sortBy = DEFAULT_SORT_ORDER_ID;
		 if (!StringUtils.isBlank(pagingAndFiltering.getSortBy())) {
			 sortBy = pagingAndFiltering.getSortBy();
		 }
		 var filters = new HashedMap<String, Object>();
		 pagingAndFiltering.getFilters().forEach( (key, value) -> {
			 String newKey = key.replace("productLineCode", "productLine.code")
					 .replace("brandCode", "brand.code");
			 filters.put(key.replace(key, newKey), value);
		 });
		 pagingAndFiltering.getFilters().clear();
		 pagingAndFiltering.getFilters().putAll(filters);
		 List<String> fields = Arrays.asList("productLine", "brand", "currentVersion");
		 PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, SortOrder.ASCENDING, fields, pagingAndFiltering, Product.class);
		 Long totalCount = productService.count(paginationConfiguration);
		 GetListProductsResponseDto result = new GetListProductsResponseDto();
		 result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
		 result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

		 if(totalCount > 0) {
			 productService.list(paginationConfiguration).stream().forEach(p -> {
				 ProductDto dto = new ProductDto(p);
				 dto.setCustomFields(entityToDtoConverter.getCustomFieldsDTO(p));
				 result.getProducts().add(dto);
			 });
		 }
		 return result;
	 }
	  
	 public GetCpqOfferResponseDto listPost(OfferContextDTO offerContextDTO) {
	        GetCpqOfferResponseDto result = new GetCpqOfferResponseDto();
	        List<String> requestedTagTypes = offerContextDTO.getCustomerContextDTO().getRequestedTagTypes();
	        String offerCode = offerContextDTO.getOfferCode();
	        if (Strings.isEmpty(offerCode)) {
	            missingParameters.add("offerCode");
	        }
	        OfferTemplate offerTemplate = offerTemplateService.findByCode(offerCode);
	        if (offerTemplate == null) {
	            throw new EntityDoesNotExistsException(OfferTemplate.class, offerCode, "offerCode");
	        }
			ReplacementRulesExecutor replacementRulesExecutor = new ReplacementRulesExecutor(false);

			SelectedAttributes offerSourceSelectedAttributes = new SelectedAttributes(offerContextDTO.getOfferCode(), null, offerContextDTO.getSelectedOfferAttributes());
			List<SelectedAttributes> productSourceSelectedAttributes = offerContextDTO.getSelectedProducts()
					.stream()
					.map(product -> new SelectedAttributes(offerContextDTO.getOfferCode(), product.getProductCode(), product.getSelectedAttributes()))
					.collect(Collectors.toList());
			productSourceSelectedAttributes.add(offerSourceSelectedAttributes);
			log.info("OfferTemplateApi requestedTagTypes={}", requestedTagTypes);

			OfferContextConfigDTO config = offerContextDTO.getConfig();

			if(config==null){
				// init config with default value set for each boolean field
				config = new OfferContextConfigDTO();
			}
	        GetOfferTemplateResponseDto offertemplateDTO = offerTemplateApi.fromOfferTemplate(offerTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE, true, false, false, false, false, false, true, true, requestedTagTypes, config);
			for (OfferProductsDto offerProduct : offertemplateDTO.getOfferProducts()) {
	            if (offerProduct.getProduct() != null && offerProduct.getProduct().getCurrentProductVersion() != null) {

	               // List<CommercialRuleHeader> commercialRules = commercialRulesContainerProvider.getForOfferAndProduct(offerCode + "-"+ offerProduct.getProduct().getCode());
	                 List<CommercialRuleHeader> commercialRules = commercialRuleHeaderService.getProductRules(offerCode, offerProduct.getProduct().getCode(), offerProduct.getProduct().getCurrentProductVersion().getCurrentVersion());
	                 log.info("productCommercialRules productCode={},size={}",offerProduct.getProduct().getCode(),commercialRules!=null?commercialRules.size():null);
	                if (commercialRules != null && !commercialRules.isEmpty()) {
	                    List<CommercialRuleHeaderDTO> commercialRuleDtoList = new ArrayList<CommercialRuleHeaderDTO>();
	                    for (CommercialRuleHeader rule : commercialRules) {
	                        CommercialRuleHeaderDTO commercialRuleDto = new CommercialRuleHeaderDTO(rule);
	                        commercialRuleDtoList.add(commercialRuleDto);
	                    }
	                    offerProduct.setCommercialRules(commercialRuleDtoList);
	                    boolean isSelectable = commercialRuleHeaderService.isElementSelectable(offerCode, commercialRules, offerContextDTO.getSelectedProducts(),offerContextDTO.getSelectedOfferAttributes(), (rule) -> rule.getTargetAttribute() == null);
	                    //processReplacementRules(commercialRules, offerContextDTO.getSelectedProducts(), null);
	                    offerProduct.setSelectable(isSelectable);
	                }

	                List<Long> sourceProductRules = commercialRuleLineService.getSourceProductRules(offerCode, offerProduct.getProduct().getCode(), offerProduct.getProduct().getCurrentProductVersion().getCurrentVersion());
	                if (sourceProductRules != null && !sourceProductRules.isEmpty()) {
	                    offerProduct.setRuled(true);
	                }


	                GetProductVersionResponse productVersionResponse = (GetProductVersionResponse) offerProduct.getProduct().getCurrentProductVersion();
	                for (ProductVersionAttributeDTO attributeDto : productVersionResponse.getProductAttributes()) {
						//List<CommercialRuleHeader> attributeCommercialRules = commercialRulesContainerProvider.getForProductAndAtttribute(attributeDto.getCode() + "-"+ offerProduct.getProduct().getCode());
	                   List<CommercialRuleHeader> attributeCommercialRules = commercialRuleHeaderService.getProductAttributeRules(attributeDto.getAttributeCode(), offerProduct.getProduct().getCode());
	                log.info("attributeCommercialRules attributeCode={}, productCode={},size={}",attributeDto.getAttributeCode(),offerProduct.getProduct().getCode(),attributeCommercialRules!=null?attributeCommercialRules.size():null);

						if (attributeCommercialRules != null && !attributeCommercialRules.isEmpty()) {
	                        List<String> commercialRuleCodes = new ArrayList<>();
	                        for (CommercialRuleHeader rule : attributeCommercialRules) {
	                            commercialRuleCodes.add(rule.getCode());
	                        }
	                        attributeDto.setCommercialRuleCodes(commercialRuleCodes);
	                        boolean isSelectable = commercialRuleHeaderService.isElementSelectable(offerCode, attributeCommercialRules, offerContextDTO.getSelectedProducts(),offerContextDTO.getSelectedOfferAttributes(), (rule) -> true);
	                        attributeDto.setSelectable(isSelectable);
							Optional<SelectedAttributes> selectedAttribute = productSourceSelectedAttributes.stream()
									.filter(selectedAttributes -> selectedAttributes.match(offerProduct.getOfferTemplateCode(), offerProduct.getProduct().getCode()))
									.findFirst();
							if(selectedAttribute.isPresent() && selectedAttribute.get().getSelectedAttributesMap() != null && selectedAttribute.get().getSelectedAttributesMap().get(attributeDto.getAttributeCode()) != null) {
								replacementRulesExecutor.executeReplacements(selectedAttribute.get(), productSourceSelectedAttributes,attributeCommercialRules);
								if(selectedAttribute.get().isCanReplace()) {
									attributeDto.setAssignedValue(selectedAttribute.get().getSelectedAttributesMap().get(attributeDto.getAttributeCode()));
								}
								
							}
	                    }
	                    List<Long> sourceRules = commercialRuleLineService.getSourceProductAttributeRules(attributeDto.getAttributeCode(), offerProduct.getProduct().getCode());
	                    if (sourceRules != null && !sourceRules.isEmpty()) {
	                        attributeDto.setRuled(true);
	                    }
	                }


	                for (GroupedAttributeDto groupedAttributeDTO : productVersionResponse.getGroupedAttributes()) {
	                    List<CommercialRuleHeader> groupedAttributeCommercialRules = commercialRuleHeaderService.getGroupedAttributesRules(groupedAttributeDTO.getCode(), offerProduct.getProduct().getCode());
	                    if (groupedAttributeCommercialRules != null && !groupedAttributeCommercialRules.isEmpty()) {
	                        List<String> commercialRuleCodes = new ArrayList<String>();
	                        for (CommercialRuleHeader rule : groupedAttributeCommercialRules) {
	                            commercialRuleCodes.add(rule.getCode());
	                        }
	                        groupedAttributeDTO.setCommercialRuleCodes(commercialRuleCodes);
	                        boolean isSelectable = commercialRuleHeaderService.isElementSelectable(offerCode, groupedAttributeCommercialRules, offerContextDTO.getSelectedProducts(),offerContextDTO.getSelectedOfferAttributes(), (rule) -> true);
	                        groupedAttributeDTO.setSelectable(isSelectable);
	                    }
	                    List<Long> sourceGroupedAttributeRules = commercialRuleLineService.getSourceGroupedAttributesRules(groupedAttributeDTO.getCode(), offerProduct.getProduct().getCode());
	                    if (sourceGroupedAttributeRules != null && !sourceGroupedAttributeRules.isEmpty()) {
	                        groupedAttributeDTO.setRuled(true);
	                    }
	                }
	                offerProduct.getProduct().setCurrentProductVersion(productVersionResponse);
	            }
	        }
	        for (OfferTemplateAttributeDTO attributeDto : offertemplateDTO.getOfferAttributes()) {
	            List<CommercialRuleHeader> commercialRules = commercialRuleHeaderService.getOfferAttributeRules(attributeDto.getAttributeCode(), offertemplateDTO.getCode());
	            if (commercialRules != null && !commercialRules.isEmpty()) {
	                List<String> commercialRuleCodes = new ArrayList<String>();
	                for (CommercialRuleHeader rule : commercialRules) {
	                    commercialRuleCodes.add(rule.getCode());
	                }
	                attributeDto.setCommercialRuleCodes(commercialRuleCodes);
	                boolean isSelectable = commercialRuleHeaderService.isElementSelectable(offerCode, commercialRules, offerContextDTO.getSelectedProducts(),offerContextDTO.getSelectedOfferAttributes(), rule -> true);
	                attributeDto.setSelectable(isSelectable);
					replacementRulesExecutor.executeReplacements(offerSourceSelectedAttributes, productSourceSelectedAttributes, commercialRules);
	                if(offerSourceSelectedAttributes.getSelectedAttributesMap() != null){
						attributeDto.setAssignedValue(offerSourceSelectedAttributes.getSelectedAttributesMap().get(attributeDto.getAttributeCode()));
					}
	            }
	            List<Long> sourceRules = commercialRuleLineService.getSourceOfferAttributeRules(attributeDto.getAttributeCode(), offerCode);
	            if (sourceRules != null && !sourceRules.isEmpty()) {
	                attributeDto.setRuled(true);
	            }
	        }

	        result.setCpqOfferDto(new CpqOfferDto(offertemplateDTO));
	        return result;
	    }
    
    public GetProductDtoResponse addCharges(String productCode, List<ProductChargeTemplateMappingDto> productChargeTemplateMappings) {
    	if (StringUtils.isBlank(productCode)) {
			missingParameters.add("productCode");
		}
    	if (productChargeTemplateMappings == null || productChargeTemplateMappings.isEmpty()) {
			missingParameters.add("productChargeTemplateMapping");
		}
		handleMissingParameters();
		Product product = productService.findByCode(productCode);
		if (product == null) {
			throw new EntityDoesNotExistsException(Product.class,productCode);
		}
		productChargeTemplateMappings.forEach(pctm -> {
			if(StringUtils.isBlank(pctm.getChargeCode())) {
				missingParameters.add("chargeCode");
				handleMissingParameters();
			}
			var charge = loadEntityByCode(chargeTemplateService, pctm.getChargeCode(), ChargeTemplate.class);
			ProductChargeTemplateMapping<ChargeTemplate> chargeTemplateProductChargeTemplateMapping = new ProductChargeTemplateMapping<>();
			chargeTemplateProductChargeTemplateMapping.setProduct(product);
			chargeTemplateProductChargeTemplateMapping.setChargeTemplate(charge);
			if(!Strings.isEmpty(pctm.getCounterCode()))
				chargeTemplateProductChargeTemplateMapping.setCounterTemplate(loadEntityByCode(counterTemplateService, pctm.getCounterCode(), CounterTemplate.class));
			if(pctm.getAccumulatorCounterCodes() != null && !pctm.getAccumulatorCounterCodes().isEmpty()) {
				var accumulator = pctm.getAccumulatorCounterCodes().stream()
									.map(counterCode -> loadEntityByCode(counterTemplateService, counterCode, CounterTemplate.class))
										.collect(Collectors.toList());
				chargeTemplateProductChargeTemplateMapping.setAccumulatorCounterTemplates(accumulator);
			}
			product.getProductCharges().removeIf(ptm -> ptm.getChargeTemplate() != null && ptm.getChargeTemplate().getCode().equalsIgnoreCase(pctm.getChargeCode()));
			product.getProductCharges().add(chargeTemplateProductChargeTemplateMapping);
			
		});
		productService.updateProduct(product);
	    return findByCode(productCode);
    }
    
    public GetProductDtoResponse removeCharges(String productCode, List<String> chargeCodes) {
    	if (StringUtils.isBlank(productCode)) {
			missingParameters.add("productCode");
			handleMissingParameters();
		}
		Product product = productService.findByCode(productCode);
		if (product == null) {
			throw new EntityDoesNotExistsException(Product.class,productCode);
		}
		chargeCodes.forEach(charge -> {
			product.getProductCharges().removeIf(ptm -> ptm.getChargeTemplate() != null && ptm.getChargeTemplate().getCode().equalsIgnoreCase(charge));
		});
    	return findByCode(productCode);
    }
    
    
}

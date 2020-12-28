package org.meveo.api.cpq;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.catalog.OfferTemplateApi;
import org.meveo.api.dto.catalog.CpqOfferDto;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetCpqOfferResponseDto;
import org.meveo.api.dto.response.catalog.GetOfferTemplateResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductVersionsResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductsResponseDto;
import org.meveo.api.dto.response.cpq.GetProductVersionResponse;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.cpq.AttributeService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.primefaces.model.SortOrder;

/**
 * @author Tarik FAKHOURI
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
	
	private static final String DEFAULT_SORT_ORDER_ID = "id";
	
	/**
	 * @return ProductDto
	 * @throws ProductException
	 */
	public Long addNewProduct(ProductDto productDto){
		if(Strings.isEmpty(productDto.getCode())) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		try {
			Product product=populateProduct(productDto);
			productService.create(product);
			ProductVersionDto currentProductVersion=productDto.getCurrentProductVersion();
			if(currentProductVersion!=null) {
				createProductVersion(productDto.getCurrentProductVersion());
			}
			
			return product.getId();
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
	}
	
	/**
	 * @param productDto
	 * @throws ProductException
	 */
	public void updateProduct(ProductDto productDto){

		try {
			Product product = productService.findByCode(productDto.getCode());
			if (product == null) {
				throw new EntityDoesNotExistsException(Product.class,productDto.getCode());
			}   
			product.setDescription(productDto.getLabel());
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
			product.setReference(productDto.getReference());
			product.setModel(productDto.getModel());
			product.setModelChlidren(productDto.getModelChildren());
			product.setDiscountFlag(productDto.isDiscountFlag());
			product.setPackageFlag(productDto.isPackageFlag());
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
	 * @throws ProductException when the status is unknown and the status 
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
	public ProductDto findByCode(String code){
		if(Strings.isEmpty(code)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		Product product = productService.findByCode(code);
		if (product == null) {
			throw new EntityDoesNotExistsException(Product.class,code);
		} 
		
		return new ProductDto(product);
	}
	
 
 
	public ProductVersion createProductVersion(ProductVersionDto postData) throws MeveoApiException, BusinessException {
        String description = postData.getShortDescription();
        String productCode = postData.getProductCode();
        int currentVersion = postData.getCurrentVersion();
        if (StringUtils.isBlank(productCode)) {
            missingParameters.add("productCode");
        }
        if (StringUtils.isBlank(currentVersion)) {
            missingParameters.add("currentVersion");
        }
        handleMissingParametersAndValidate(postData);
        Product product = productService.findByCode(productCode);
        if (product == null) {
            throw new EntityDoesNotExistsException(Product.class,productCode,"productCode");
        }
        ProductVersion  productVersion= new ProductVersion();
        productVersion.setProduct(product);
        productVersion.setShortDescription(description);
        productVersion.setLongDescription(postData.getLongDescription());
        productVersion.setCurrentVersion(currentVersion);
        productVersion.setValidity(postData.getValidity());
        productVersion.setStatus(VersionStatusEnum.DRAFT);
        productVersion.setStatusDate(Calendar.getInstance().getTime());
        processAttributes(postData,productVersion);
        processTags(postData, productVersion); 
        productVersionService.create(productVersion);
        return productVersion;
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
        productVersion.setShortDescription(postData.getShortDescription());
        productVersion.setLongDescription(postData.getLongDescription());
        productVersion.setValidity(postData.getValidity());
        productVersion.setStatus(postData.getStatus());
        processAttributes(postData,productVersion);
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
	        return new GetProductVersionResponse(productVersionService.duplicate(productVersion, true));
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
	        return new GetProductVersionResponse(productVersion);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
    }
     
    
    public Product duplicateProduct(String codeProduct, boolean duplicateHierarchy, boolean preserveCode) throws MeveoApiException, BusinessException {
    	
    	Product product = null; 
    	try {
    		product = productService.findByCode(codeProduct);
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
	        productVersionService.updateProductVersionStatus(productVersion,status);
	        return new GetProductVersionResponse(productVersion);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
    }
    
    
    /**
     * Creates or updates product version based on the product code and current version.
     * 
     * @param postData posted data.
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     * @throws ProductException 
     * @throws ProductVersionException 
     */
    public ProductVersion createOrUpdateProductVersion(ProductVersionDto postData) throws MeveoApiException, BusinessException {
    	String productCode = postData.getProductCode();
        int currentVersion = postData.getCurrentVersion();
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if (productVersion == null) {
	            return createProductVersion(postData);
	        } else {
	            return updateProductVersion(postData);
	        }
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		} 

    }
    
    public Product populateProduct(ProductDto productDto) {
    	Product product=new Product();
    	product.setCode(productDto.getCode());
    	product.setDescription(productDto.getLabel());
    	
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
		product.setReference(productDto.getReference());
		product.setModel(productDto.getModel());
		product.setModelChlidren(productDto.getModelChildren());
		product.setDiscountFlag(productDto.isDiscountFlag());
		
		
		return product;
    }
    
	public void removeProduct(String codeProduct) {
		try { 
			productService.removeProduct(codeProduct);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
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
						result.addProduct(prodDto);
				}}
			}  
		} 
		return result;
	} 
	private void processTags(ProductVersionDto postData, ProductVersion productVersion) {
		Set<String> tagCodes = postData.getTagCodes(); 
		if(tagCodes != null && !tagCodes.isEmpty()){
			Set<Tag> tags=new HashSet<Tag>();
			for(String code:tagCodes) {
				Tag tag=tagService.findByCode(code);
				if(tag == null) { 
					throw new EntityDoesNotExistsException(Tag.class,code);
				}
				tags.add(tag);
			}
			productVersion.setTags(tags);
		}
	} 
	
	private void processAttributes(ProductVersionDto postData, ProductVersion productVersion) {
		Set<String> attributeCodes = postData.getAttributeCodes(); 
		if(attributeCodes != null && !attributeCodes.isEmpty()){
			List<Attribute> attributes=new ArrayList<Attribute>();
			for(String code:attributeCodes) {
				Attribute attribute=attributeService.findByCode(code);
				if(attribute == null) { 
					throw new EntityDoesNotExistsException(Attribute.class,code);
				}
				attributes.add(attribute);
			}
			productVersion.setAttributes(attributes);
		}
	} 
	
	
	public List<GetProductVersionResponse> findProductVersionByProduct(String productCode)  throws MeveoApiException, BusinessException  { 
		try {
			List<GetProductVersionResponse> GetProductVersionResponses=new ArrayList<GetProductVersionResponse>();
			List<ProductVersion> productVersions = productVersionService.findByProduct(productCode); 

			GetProductVersionResponse getProductVersionResponse=null;
			if(!productVersions.isEmpty()) {

				for(ProductVersion prodversion:productVersions) {
					getProductVersionResponse=new GetProductVersionResponse(prodversion);
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
		 PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, SortOrder.ASCENDING, null, pagingAndFiltering, ProductVersion.class);
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
		 PaginationConfiguration paginationConfiguration = toPaginationConfiguration(sortBy, SortOrder.ASCENDING, null, pagingAndFiltering, Product.class);
		 Long totalCount = productService.count(paginationConfiguration);
		 GetListProductsResponseDto result = new GetListProductsResponseDto();
		 result.setPaging(pagingAndFiltering != null ? pagingAndFiltering : new PagingAndFiltering());
		 result.getPaging().setTotalNumberOfRecords(totalCount.intValue());

		 if(totalCount > 0) {
			 productService.list(paginationConfiguration).stream().forEach(p -> {
				 result.getProducts().add(new ProductDto(p));
			 });
		 }
		 return result;
	 }
	 
	 
	 public GetCpqOfferResponseDto listPost(OfferContextDTO  offerContextDTO ) {
		 GetCpqOfferResponseDto result=new GetCpqOfferResponseDto();
		 List<String> requestedTagTypes=offerContextDTO.getCustomerContextDTO().getRequestedTagTypes();
		 String offerCode=offerContextDTO.getOfferCode();
		 if(Strings.isEmpty(offerCode)) {
			 missingParameters.add("offerCode");
		 }
		 OfferTemplate offerTemplate=offerTemplateService.findByCode(offerCode);
		 if (offerTemplate == null) {
			 throw new EntityDoesNotExistsException(OfferTemplate.class,offerCode,"offerCode");
		 }   
		 log.info("OfferTemplateApi requestedTagTypes={}",requestedTagTypes);   
		 GetOfferTemplateResponseDto offertemplateDTO=offerTemplateApi.fromOfferTemplate(offerTemplate, CustomFieldInheritanceEnum.INHERIT_NO_MERGE,true,false,false, false,false,false,true,true,requestedTagTypes);
		 result.setCpqOfferDto(new CpqOfferDto(offertemplateDTO));
		 return result;
	 }
	
}

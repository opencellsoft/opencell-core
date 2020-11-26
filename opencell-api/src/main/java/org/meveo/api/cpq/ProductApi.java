package org.meveo.api.cpq;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.cpq.ServiceDTO;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.response.cpq.GetListProductsResponseDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.offer.OfferComponent;
import org.meveo.model.cpq.tags.Tag;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.catalog.impl.OfferTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.ProductVersionService;
import org.meveo.service.cpq.TagService;
import org.meveo.service.crm.impl.CustomerBrandService;

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
	private CustomerBrandService brandService;
	@Inject
	private BillingAccountService billingAccountService;
	@Inject
	private ProductLineApi productLineApi;
	
	@Inject
	private ProductVersionService productVersionService;
	
	@Inject
	private TagService tagService;
	
	@Inject
	private OfferTemplateService offerTemplateService;
	
	@Inject
	private ServiceTemplateService serviceTemplateService;
	
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
			product.setDescription(productDto.getLabel());
			if(productDto.getProductLineCode() != null) {
				product.setProductLine(productLineService.findByCode(productDto.getProductLineCode()));
			}
			if(productDto.getBrandCode() != null) {
				product.setBrand(brandService.findByCode(productDto.getBrandCode()));
			}
			product.setReference(productDto.getReference());
			product.setModel(productDto.getModel());
			product.setModelChlidren(productDto.getModelChildren());
			product.setDiscountFlag(productDto.isDiscountFlag());
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
		return new ProductDto(productService.findByCode(code));
	}
	
	

	/**
	 * @param dto
	 * @return
	 */
	public Long createProductLine(ProductLineDto dto){
		return productLineApi.createProductLine(dto);
	}

	/**
	 * @param dto
	 * @return
	 */
	public ProductLineDto updateProductLine(ProductLineDto dto){
			return productLineApi.updateProductLine(dto);
	}

	/**
	 * @param code
	 * @return
	 */
	public ProductLineDto findProductLineByCode(String code) {
		return new ProductLineDto(productLineService.findByCode(code));
	}
	
	
	public ProductVersion createProductVersion(ProductVersionDto postData) throws MeveoApiException, BusinessException {
        String description = postData.getShortDescription();
        String productCode = postData.getProductCode();
        int currentVersion = postData.getCurrentVersion();
        if (StringUtils.isBlank(description)) {
            missingParameters.add("description");
        }
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
        productVersion.setEndDate(postData.getEndDate());
        productVersion.setStartDate(postData.getStartDate());
        productVersion.setStatus(VersionStatusEnum.DRAFT);
        productVersion.setStatusDate(Calendar.getInstance().getTime());
        processServices(postData,productVersion);
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
        productVersion.setStartDate(postData.getStartDate());
        productVersion.setEndDate(postData.getEndDate());
        productVersion.setStartDate(postData.getStartDate());
        productVersion.setStatus(postData.getStatus());
        processServices(postData,productVersion);
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
    public ProductVersion duplicateProductVersion(String productCode, int currentVersion)  throws MeveoApiException, BusinessException  { 
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if(productVersion==null) {
	            throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
	        }
	        return productVersionService.duplicate(productVersion, true);
		} catch (BusinessException e) {
			throw new MeveoApiException(e);
		}
    }
    
    public ProductVersion findProductVersion(String productCode, int currentVersion)  throws MeveoApiException, BusinessException  { 
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if(productVersion==null) {
	            throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
	        }
	        return productVersion;
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
    public ProductVersionDto UpdateProductVersionStatus (String productCode, int currentVersion,VersionStatusEnum status)  throws MeveoApiException, BusinessException { 
        ProductVersion productVersion;
		try {
			productVersion = productVersionService.findByProductAndVersion(productCode,currentVersion);
	        if(productVersion==null) {
	            throw new EntityDoesNotExistsException(ProductVersion.class,productCode,"productCode",""+currentVersion,"currentVersion");
	        }
	        productVersionService.updateProductVersionStatus(productVersion,status);
	        return new ProductVersionDto(productVersion);
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
    	product.setId(productDto.getId());
    	product.setCode(productDto.getCode());
    	product.setDescription(productDto.getLabel());
		if(productDto.getProductLineCode() != null) {
			product.setProductLine(productLineService.findByCode(productDto.getProductLineCode()));
		}
		if(productDto.getBrandCode() != null) {
			product.setBrand(brandService.findByCode(productDto.getBrandCode()));
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
			List<Product> prodByTags=productService.getProductsByTags(resultBaTags); 
			ProductDto prodDto=null;
			Product product=null;
			if(!offerComponents.isEmpty() && !prodByTags.isEmpty()) {
				for(OfferComponent oc:offerComponents) {
					product=oc.getProduct();
					if(prodByTags.contains(product)) {
						prodDto=new ProductDto(product);
						result.addProduct(prodDto);
				}}
			}  
		} 
		return result;
	}
	
	
	private void processTags(ProductVersionDto postData, ProductVersion product) {
		Set<TagDto> tags = postData.getTagList();
		if(tags != null && !tags.isEmpty()){
			product.setTags(tags
					.stream()
					.map(tagDto -> tagService.findByCode(tagDto.getCode()))
					.collect(Collectors.toSet()));
		}
	}

	private void processServices(ProductVersionDto postData, ProductVersion productVersion) {
		Set<ServiceDTO> services = postData.getServices();
		if(services != null && !services.isEmpty()){
			productVersion.setServices(services
					.stream()
					.map(serviceDto -> serviceTemplateService.findByCode(serviceDto.getCode()))
					.collect(Collectors.toList()));
		}
	}
	
}

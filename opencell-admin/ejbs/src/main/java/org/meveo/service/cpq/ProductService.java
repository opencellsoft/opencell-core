package org.meveo.service.cpq;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.billing.impl.article.ArticleMappingLineService;
import org.meveo.service.catalog.impl.CatalogHierarchyBuilderService;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mbarek-Ay
 * @author Tarik FAKHOURI.
 * @version 10.0
 * 
 * Product service implementation.
 */

/**
 * @author Khairi
 *
 */
@Stateless
public class ProductService extends BusinessService<Product> {

	private final static Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
	private final static String PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE = "status of the product (%s) is %s, it can not be updated or removed";
	private final static String PRODUCT_UNKWON = "product (%s) unknwon!";
	private final static String PRODUCT_CAN_NOT_CHANGE_THE_STATUS = "product (%s) can not change the status beacause it not draft";
	private static final String PRODUCT_CODE_EXIST = "code %s of the product already exist!";
	
	/*@Inject
	private CustomerBrandService customerBrandService;
	@Inject
	private DiscountPlanService discountPlanService;
	@Inject
	private ProductLineService productLineService;*/
	
	@Inject
	private CatalogHierarchyBuilderService catalogHierarchyBuilderService;
	@Inject private PricePlanMatrixColumnService pricePlanMatrixColumnService;
	@Inject private ArticleMappingLineService articleMappingLineService;
    /**
     * check if the product has any product line 
     * @param idProductLine
     * @return
     */
    @SuppressWarnings("unchecked")
	public boolean checkIfProductLineExist(Long idProductLine) {
		Query query = getEntityManager().createNamedQuery("Product.getProductLine").setParameter("id", idProductLine);
		List<Product> listProducts = query.getResultList();
		if(listProducts != null && !listProducts.isEmpty()) {
			LOGGER.info("Product exist for Product line ({}), number of the product found are : {}", idProductLine, listProducts.size());
			return true;
		}
		LOGGER.info("No product found for the product line ({})", idProductLine);
		return false;
	}
    

	/**
	 * check the status of the product before it update, only product with the status DRAFT can be updated
	 * @param product
	 * @return product updated
	 * @throws ProductException <br/> when: <ul><li>the status is ACTIVE</li><li>the status is CLOSED</li>
	 */
	public Product updateProduct(Product product) throws BusinessException{
		LOGGER.info("updating product {}", product.getCode());
		
//		if(product.getStatus().equals(ProductStatusEnum.ACTIVE)) {
//			LOGGER.warn("the product {} can not be updated, because of its status => {}", product.getCode(), product.getStatus().toString());
//			throw new BusinessException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, product.getCode(), product.getStatus().toString()));
//		}
		if(product.getStatus().equals(ProductStatusEnum.CLOSED)) {
			LOGGER.warn("the product {} can not be updated, because of its status => {}", product.getCode(), product.getStatus().toString());
			throw new BusinessException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, product.getCode(), product.getStatus().toString()));
		}
		
		update(product);
		
		LOGGER.info("the product ({}) updated successfully", product.getCode());
		return product;
	}
	
	public Product duplicateProduct(Product product, boolean duplicateHierarchy, boolean preserveCode) throws BusinessException {
		
		product.getProductVersions().size();
		product.getDiscountList().size();
		product.getModelChildren().size();
		product.getOfferComponents().size();
		product.getMedias().size();
		product.getProductCharges().size();
		product.getServiceRecurringCharges().size();
		product.getServiceSubscriptionCharges().size();
		product.getServiceTerminationCharges().size();
		product.getServiceUsageCharges().size();

		product.getOfferComponents().forEach(oc -> oc.getTagsList().size());
		product.getProductCharges().forEach(pct -> {
			pct.getAccumulatorCounterTemplates().size();
			pct.getWalletTemplates().size();
		});
		product.getCommercialRuleHeader().size();
		product.getCommercialRuleHeader().forEach(crh -> {
			crh.getCommercialRuleItems().size();
		});
		product.getCommercialRuleLines().size();
		
		var productVersions = product.getProductVersions();
		var discountPlans = new HashSet<>(product.getDiscountList());
		var modelChildren = new HashSet<>(product.getModelChildren());
		var offerComponents = new ArrayList<>(product.getOfferComponents());
		var medias = new ArrayList<>(product.getMedias());
		var productCharge = new ArrayList<>(product.getProductCharges());
		var commercialRuleHeader = new ArrayList<>(product.getCommercialRuleHeader());
		var commercialRuleLine = new ArrayList<>(product.getCommercialRuleLines());
		
		detach(product);

		ProductVersion productVersion = null;
		if(productVersions != null && !productVersions.isEmpty()) {
    		Collections.sort(productVersions, new Comparator<ProductVersion>() {
				@Override
				public int compare(ProductVersion o1, ProductVersion o2) {
					DatePeriod o1Period = o1.getValidity();
					DatePeriod o2Period = o2.getValidity();
					if(o1Period == null && o2Period == null) {
						return 0;
					}else if (o1Period != null && o2Period == null) 
						return 1;
					else if (o1Period == null && o2Period != null) 
						return -1;
					else{
						Date o1from = o1Period.getFrom();
						Date o2from = o2Period.getFrom();
						if(o1from == null && o2from == null) {
							return 0;
						}else if (o1from != null && o2from == null) 
							return 1;
						else if(o1from == null && o2from != null)
							return -1;
						else 
							return o2from.compareTo(o1from);
					}
					
				}
			});
    		for (ProductVersion pv : productVersions) {
				if(VersionStatusEnum.PUBLISHED == pv.getStatus()) {
					productVersion = pv;
					break;
				}
			}
    		if(productVersion == null) {
        		productVersion = productVersions.get(0);
    		}
    	}
		
		
		Product duplicate = new Product();
   	 	try{
            BeanUtils.copyProperties(duplicate, product);
	       } catch (IllegalAccessException | InvocationTargetException e) {
	           throw new BusinessException("Failed to clone Product", e);
	       }
   	 	
	   	 duplicate.setId(null);
	   	 duplicate.setBrand(null);
	   	 duplicate.setCurrentVersion(null);
	   	 duplicate.setModelChildren(new HashSet<>());
	   	 duplicate.setProductVersions(new ArrayList<>());
	   	 duplicate.setStatus(ProductStatusEnum.DRAFT);
	   	 duplicate.setStatusDate(Calendar.getInstance().getTime());
	   	 duplicate.setProductLine(product.getProductLine());
	   	 duplicate.setModel(product.getModel());
	   	 duplicate.setReference(product.getReference());
	   	 duplicate.setDiscountFlag(product.isDiscountFlag());
   		 duplicate.setPackageFlag(product.isPackageFlag());
   		 duplicate.setDiscountList(new HashSet<>());
   		 duplicate.setOfferComponents(new ArrayList<>());
   		 duplicate.setMedias(new ArrayList<>());
   		 duplicate.setUuid(UUID.randomUUID().toString());
   		 duplicate.setProductCharges(new ArrayList<>());
   		 duplicate.setCommercialRuleHeader(new ArrayList<>());
   		 duplicate.setCommercialRuleLines(new ArrayList<>());
   		 
   		 duplicate.setProductModel(product.getIsModel() != null && product.getIsModel() == Boolean.TRUE ? product : null);
	   	 
	   	 if(!preserveCode) {
	         String code = findDuplicateCode(duplicate);
	   	   	duplicate.setCode(code);
	   	 }

	   	 try {
		   	 	super.create(duplicate);
	   	 }catch(BusinessException e) {
	   		 throw new MeveoApiException(e);
	   	 }
	   	 
	   	 if(duplicateHierarchy) {
	   		catalogHierarchyBuilderService.duplicateProduct(duplicate, productVersion, discountPlans, modelChildren, 
	   														offerComponents, medias, productCharge, duplicate.getId() + "_", commercialRuleHeader, commercialRuleLine);
	   	 }
	   	 return duplicate;
	}
	

	/**
	 * delete product by its id
	 * @param id
	 * @throws ProductException <br /> when : 
	 * 	<ul>
	 * 		<li>can't find any contract by id</li>
	 * 		<li>the status of contract is active</li>
	 * 	</ul>
	 */
	public void deleteContractById(Long id) throws BusinessException{
		LOGGER.info("product ({}) to be deleted", id);
		
		final Product deleteProduct = findById(id);
		if(deleteProduct == null) {
			throw new BusinessException(String.format(PRODUCT_UNKWON, id));
		}
		if(deleteProduct.getStatus().equals(ProductStatusEnum.ACTIVE)) {
			LOGGER.warn("product ({}) can not be removed, because its status is active", deleteProduct.getCode());
			throw new BusinessException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, deleteProduct.getCode(), deleteProduct.getStatus().toString()));
		}
		getEntityManager().remove(deleteProduct);
		LOGGER.info("product ({}) is deleted successfully", deleteProduct.getCode());
	}
	

	/**
	 * change the status of product.
	 * <p>the product with status DRAFT can be change to ACTIVE or CLOSED.<br />
	 * if the product status is ACTIVE then the only value possible is CLOSED otherwise it will throw exception.</p>
	 * @param product
	 * @param status
	 * @return
	 * @throws ProductException
	 */
	public Product updateStatus(String productCode, ProductStatusEnum status) throws BusinessException{
		Product product =findByCode(productCode);
		if(product == null)
			throw new EntityDoesNotExistsException(Product.class, productCode);
		if(product.getStatus().equals(ProductStatusEnum.DRAFT)) {
			product.setStatus(status);
			product.setStatusDate(Calendar.getInstance().getTime());
			return  update(product);
		}else if (ProductStatusEnum.ACTIVE.equals(product.getStatus()) && ProductStatusEnum.CLOSED.equals(status)) {
			product.setStatus(status);
			product.setStatusDate(Calendar.getInstance().getTime());
			return  update(product);
		}
		throw new BusinessException(String.format(PRODUCT_CAN_NOT_CHANGE_THE_STATUS, product.getCode()));
	}
	
	/**
	 *  create new product with DRAFT status
	 * @param product
	 * @return
	 * @throws ProductException
	 */
	public void create(Product product) throws BusinessException {
		
		if(!this.findByCodeLike(product.getCode()).isEmpty()) {
			throw new EntityAlreadyExistsException(String.format(PRODUCT_CODE_EXIST, product.getCode()));
		}
		product.setStatus(ProductStatusEnum.DRAFT);
		product.setStatusDate(Calendar.getInstance().getTime());
		super.create(product);
	}
	
	/**
	 * delete a product line
	 * @param id
	 * @throws ProductException when 
	 * 	<ul>
	 * 		<li>there is no Product line found</li>
	 * 		<li>if product line attached to any product</li>
	 *	</ul>
	 */
	public void removeProduct(String codeProduct) throws BusinessException { 
		final Product product = this.findByCode(codeProduct); 
		if (product == null) {
			throw new EntityDoesNotExistsException(Product.class,codeProduct);
		} 
		
		if(!product.getCommercialRuleHeader().isEmpty() || !product.getCommercialRuleLines().isEmpty()) {
			throw new MeveoApiException("Product ("+codeProduct+") can not be deleted. There are rules applied to this product");
		}

		pricePlanMatrixColumnService.findByProduct(product).stream().map(PricePlanMatrixColumn::getCode).forEach(code -> pricePlanMatrixColumnService.removePricePlanColumn(code));
		
//		articleMappingLineService.findByProductCode(product).forEach(aml -> {
//			articleMappingLineService.remove(aml);
//		});
//		articleMappingLineService.deleteByProductCode(product);
		remove(product); 
	}

	public Optional<ProductVersion> getCurrentPublishedVersion(String code, Date date) {
		Product product = findByCode(code);
		if(product == null)
			return Optional.empty();
		return product.getProductVersions()
				.stream()
				.filter(pv -> VersionStatusEnum.PUBLISHED.equals(pv.getStatus()))
				.filter(pv -> pv.getValidity().isCorrespondsToPeriod(date))
				.findFirst();
	}
}
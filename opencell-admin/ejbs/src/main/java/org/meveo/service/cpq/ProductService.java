package org.meveo.service.cpq;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.cpq.exception.ProductException;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mbarek-Ay
 * @author Tarik FAKHOURI.
 * @version 10.0
 * 
 * Product service implementation.
 */

@Stateless
public class ProductService extends
		PersistenceService<Product> {

	private final static Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
	private final static String PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE = "status of the product (%s) is %s, it can not be updated nor removed";
	private final static String PRODUCT_UNKWON = "product (%s) unkwon!";
	private final static String PRODUCT_CAN_NOT_CHANGE_THE_STATUS = "product (%s) can not change the status beacause it not draft";

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
	public Product updateProduct(Product product) throws ProductException{
		LOGGER.info("updating product {}", product.getCode());
		
		if(product.getStatus().equals(ProductStatusEnum.ACTIVE)) {
			LOGGER.warn("the product {} can not be updated, because of its status => {}", product.getCode(), product.getStatus().toString());
			throw new ProductException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, product.getCode(), product.getStatus().toString()));
		}
		if(product.getStatus().equals(ProductStatusEnum.CLOSED)) {
			LOGGER.warn("the product {} can not be updated, because of its status => {}", product.getCode(), product.getStatus().toString());
			throw new ProductException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, product.getCode(), product.getStatus().toString()));
		}
		
		update(product);
		
		LOGGER.info("the product ({}) updated successfully", product.getCode());
		return product;
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
	public void deleteContractById(Long id) throws ProductException{
		LOGGER.info("product ({}) to be deleted", id);
		
		final Product deleteProduct = findById(id);
		if(deleteProduct == null) {
			throw new ProductException(String.format(PRODUCT_UNKWON, id));
		}
		if(deleteProduct.getStatus().equals(ProductStatusEnum.ACTIVE)) {
			LOGGER.warn("product ({}) can not be removed, because its status is active", deleteProduct.getCode());
			throw new ProductException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, deleteProduct.getCode(), deleteProduct.getStatus().toString()));
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
	public Product updateStatus(Product product, ProductStatusEnum status) throws ProductException{
		if(product.getStatus().equals(ProductStatusEnum.DRAFT)) {
			product.setStatus(status);
			return  update(product);
		}else if (ProductStatusEnum.ACTIVE.equals(product.getStatus())) {
			product.setStatus(ProductStatusEnum.CLOSED);
			return  update(product);
		}
		throw new ProductException(String.format(PRODUCT_CAN_NOT_CHANGE_THE_STATUS, product.getCode()));
	}
	
	private static final String PRODUCT_CODE_EXIST = "code %s of the product already exist!";
	
	@Inject
	private CustomerBrandService customerBrandService;
	@Inject
	private DiscountPlanService discountPlanService;
	
	public Product create(String codeProduct, String label, Long idProductLine,
								String codeBrand, String reference, String model, 
								Set<String> modelChildren, Set<String> discountPlanCode, boolean discountFlag) throws ProductException {
		
		if(!this.findByCodeLike(codeProduct).isEmpty()) {
			throw new ProductException(String.format(PRODUCT_CODE_EXIST, codeProduct));
		}
		final Product product = new Product();
		product.setStatus(ProductStatusEnum.DRAFT);
		product.setStatusDate(Calendar.getInstance().getTime());
		product.setCode(codeProduct);
		product.setDescription(label);
		
		final CustomerBrand brand = customerBrandService.findByCode(codeBrand);
		product.setBrand(brand);
		product.setReference(reference);
		product.setModel(model);
		product.setModelChlidren(modelChildren);
		product.setDiscountFlag(discountFlag);
		
		var discountPlans  = new HashSet<DiscountPlan>(discountPlanCode.stream().map(codeDiscount -> {
			final DiscountPlan discount = discountPlanService.findByCode(codeDiscount);
			return discount;
		}).collect(Collectors.toSet()));
		
		product.setDiscountList(discountPlans);
		this.create(product);
		return  product;
	}
}
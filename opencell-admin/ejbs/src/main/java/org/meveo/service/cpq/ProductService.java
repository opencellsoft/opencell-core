package org.meveo.service.cpq;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.service.base.BusinessService;
import org.meveo.service.catalog.impl.DiscountPlanService;
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

/**
 * @author Khairi
 *
 */
@Stateless
public class ProductService extends BusinessService<Product> {

	private final static Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
	private final static String PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE = "status of the product (%s) is %s, it can not be updated nor removed";
	private final static String PRODUCT_UNKWON = "product (%s) unknwon!";
	private final static String PRODUCT_CAN_NOT_CHANGE_THE_STATUS = "product (%s) can not change the status beacause it not draft";
	private static final String PRODUCT_CODE_EXIST = "code %s of the product already exist!";
	
	@Inject
	private CustomerBrandService customerBrandService;
	@Inject
	private DiscountPlanService discountPlanService;
	@Inject
	private ProductLineService productLineService;
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
		
		if(product.getStatus().equals(ProductStatusEnum.ACTIVE)) {
			LOGGER.warn("the product {} can not be updated, because of its status => {}", product.getCode(), product.getStatus().toString());
			throw new BusinessException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, product.getCode(), product.getStatus().toString()));
		}
		if(product.getStatus().equals(ProductStatusEnum.CLOSED)) {
			LOGGER.warn("the product {} can not be updated, because of its status => {}", product.getCode(), product.getStatus().toString());
			throw new BusinessException(String.format(PRODUCT_ACTIVE_CAN_NOT_REMOVED_OR_UPDATE, product.getCode(), product.getStatus().toString()));
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
		this.create(product);
	}

}
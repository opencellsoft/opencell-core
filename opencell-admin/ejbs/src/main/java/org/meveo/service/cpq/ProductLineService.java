package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.model.cpq.ProductLine;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mbarek-Ay
 * @version 10.0
 * 
 * ProductLine service implementation.
 */

@Stateless
public class ProductLineService extends
		BusinessService<ProductLine> {
	

	private final static Logger LOGGER = LoggerFactory.getLogger(ProductLineService.class);

	public final static String PRODUCT_LINE_UNKNOWN = "product line (%s) is missing!";
	public final static String PRODUCT_LINE_UNKNOWN_ID = "product line (%d) is missing!";
	private final static String PRODUCT_LINE_HAS_PRODUCTS = "product line (%d) has product, it can be deleted!";
	private final static String PRODUCT_LINE_CODE_EXIST = "Duplicate of the product line code (%s)";
	public final static String PRODUCT_LINE_CODE_UNKNOWN = "product line (%s) is missing!";
	
	@Inject
	private ProductService productService;
	@Inject
	private SellerService sellerService;
	

	/**
	 * delete a product line
	 * @param id
	 * @ when 
	 * 	<ul>
	 * 		<li>there is no Product line found</li>
	 * 		<li>if product line attached to any product</li>
	 *	</ul>
	 */
	public void removeProductLine(Long id) {
		LOGGER.info("deleting product line ({})", id);
		
		final ProductLine line = this.findById(id);
		if(line == null || line.getId() == null) {
			LOGGER.warn("unknown product line with id: ({})", id);
			throw new EntityDoesNotExistsException(String.format(PRODUCT_LINE_UNKNOWN_ID, id));
		}
		
		boolean isProductExist = productService.checkIfProductLineExist(id);
		
		if(isProductExist) {
			LOGGER.warn("this product line ({}) can not be delete, it attached to product", id);
			throw new BusinessException(String.format(PRODUCT_LINE_HAS_PRODUCTS, id));
		}
		
		this.remove(line);
		LOGGER.info("product line ({}) is deleted successfully", id);
	}
	/**
	 * delete a product line
	 * @param id
	 * @ when 
	 * 	<ul>
	 * 		<li>there is no Product line found</li>
	 * 		<li>if product line attached to any product</li>
	 *	</ul>
	 */
	public void removeProductLine(String codeProductLine)  {
		LOGGER.info("deleting product line ({})", codeProductLine);
		
		final ProductLine line = this.findByCode(codeProductLine);
		if(line == null || line.getId() == null) {
			LOGGER.warn("unknown product line with id: ({})", codeProductLine);
			throw new EntityDoesNotExistsException(String.format(PRODUCT_LINE_UNKNOWN, codeProductLine));
		}
		
		boolean isProductExist = productService.checkIfProductLineExist(line.getId());
		
		if(isProductExist) {
			LOGGER.warn("this product line ({}) can not be delete, it attached to product", codeProductLine);
			throw new BusinessException(String.format(PRODUCT_LINE_HAS_PRODUCTS, codeProductLine));
		}
		
		this.remove(line);
		LOGGER.info("product line ({}) is deleted successfully", codeProductLine);
	}
	
	
	/**
	 * @param productLine
	 * @return
	 */
	public ProductLine createNew(ProductLine productLine)  {
		LOGGER.info("creation of the product line ({})", productLine.getCode());
		if(productLine.getParentEntity() != null) {
			final boolean isCodeProductExist = this.findByCodeLike(productLine.getParentLine().getCode()).isEmpty();
			if(!isCodeProductExist) {
				throw new BusinessException(String.format(PRODUCT_LINE_CODE_EXIST, productLine.getCode()));
			}
		}
		this.create(productLine);
		LOGGER.info("product line ({}) created with successfully", productLine.getCode());
		return productLine;
	}
	
	public ProductLine findByCode(String code){
		try {
			return (ProductLine) getEntityManager().createNamedQuery("ProductLine.findByCode").setParameter("code", code).getSingleResult();
		}catch(NoResultException e) {
			throw new EntityDoesNotExistsException(String.format(PRODUCT_LINE_UNKNOWN, code));
		}
		
	}
	
}
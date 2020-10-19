package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.model.admin.Seller;
import org.meveo.model.cpq.ProductLine;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.cpq.exception.ProductLineException;
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
		PersistenceService<ProductLine> {

	private final static Logger LOGGER = LoggerFactory.getLogger(ProductLineService.class);
	
	private final static String PRODUCT_LINE_UNKNOWN = "product line (%d) is missing!";
	private final static String PRODUCT_LINE_HAS_PRODUCTS = "product line (%d) has product, it can be deleted!";
	private final static String PRODUCT_LINE_CODE_EXIST = "Duplicate of the product line code (%s)";
	
	@Inject
	private ProductService productService;
	@Inject
	private SellerService sellerService;
	
	
	/**
	 * delete a product line
	 * @param id
	 * @throws ProductLineException when 
	 * 	<ul>
	 * 		<li>there is no Product line found</li>
	 * 		<li>if product line attached to any product</li>
	 *	</ul>
	 */
	public void removeProductLine(Long id) throws ProductLineException {
		LOGGER.info("deleting product line ({})", id);
		
		final ProductLine line = this.findById(id);
		if(line == null || line.getId() == null) {
			LOGGER.warn("unknown product line with id: ({})", id);
			throw new ProductLineException(String.format(PRODUCT_LINE_UNKNOWN, id));
		}
		
		boolean isProductExist = productService.checkIfProductLineExist(id);
		
		if(isProductExist) {
			LOGGER.warn("this product line ({}) can not be delete, it attached to product", id);
			throw new ProductLineException(String.format(PRODUCT_LINE_HAS_PRODUCTS, id));
		}
		
		this.remove(line);
		LOGGER.info("product line ({}) is deleted successfully", id);
	}
	
	
	/**
	 * create a new 
	 * @param codeProductLine
	 * @param label
	 * @param codeSeller
	 * @param longDescription
	 * @param idCodeParentLine
	 * @return
	 * @throws ProductLineException
	 */
	public ProductLine createNew(String codeProductLine, String label, String codeSeller, String longDescription, Long idCodeParentLine) throws ProductLineException {
		LOGGER.info("creation of the product line ({})", codeProductLine);
		
		final boolean isCodeProductExist = this.findByCodeLike(codeProductLine).isEmpty();
		if(!isCodeProductExist) {
			throw new ProductLineException(String.format(PRODUCT_LINE_CODE_EXIST, codeProductLine));
		}
		final ProductLine line = new ProductLine();
		line.setCode(codeProductLine);
		line.setDescription(label);
		line.setLongDescription(longDescription);
		
		final ProductLine parent = this.findById(idCodeParentLine);
		line.setParentLine(parent);
		
		final Seller seller = sellerService.findByCode(codeSeller);
		line.setSeller(seller);
		
		this.create(line);
		LOGGER.info("product line ({}) created with successfully", codeProductLine);
		return line;
	}
	
}
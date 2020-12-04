package org.meveo.service.cpq;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.cpq.ProductLine;
import org.meveo.service.base.BusinessService;

/**
 * @author Mbarek-Ay
 * @version 10.0
 * 
 * ProductLine service implementation.
 */

@Stateless
public class ProductLineService extends
		BusinessService<ProductLine> {
	
	@Inject
	ProductService productService;
	
	/**
	 * delete a product line
	 * @param productLine
	 * @ when 
	 * 	<ul>
	 * 		<li>there is no Product line found</li>
	 * 		<li>if product line attached to any product</li>
	 *	</ul>
	 */
	@Override
	public void remove(ProductLine productLine)  {  
		boolean isProductExist = productService.checkIfProductLineExist(productLine.getId());
		if(isProductExist) {
			log.warn("this product line ({}) can not be delete, it attached to product", productLine.getCode());
			throw new BusinessException(productLine.getCode());
		}
		super.remove(productLine);
	}
 
}
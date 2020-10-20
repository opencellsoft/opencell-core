package org.meveo.api.cpq;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.api.BaseApi;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.exception.ProductException;
import org.meveo.service.crm.impl.CustomerBrandService;

/**
 * @author Khairi
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
	private DiscountPlanService discountPlanService;
	
	/**
	 * @param codeProduct
	 * @param label
	 * @param idProductLine
	 * @param codeBrand
	 * @param reference
	 * @param model
	 * @param modelChildren
	 * @param discountPlanCode
	 * @param discountFlag
	 * @return
	 * @throws ProductException
	 */
	public Product addNewProduct(String codeProduct, String label, Long idProductLine,
			String codeBrand, String reference, String model, 
			Set<String> modelChildren, Set<String> discountPlanCode, boolean discountFlag) throws ProductException{
		if(Strings.isEmpty(codeProduct)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		
		return productService.create(codeProduct, label, idProductLine, codeBrand, reference, model, modelChildren, discountPlanCode, discountFlag);
	}
	
	/**
	 * @param codeProduct
	 * @param label
	 * @param idProductLine
	 * @param codeBrand
	 * @param reference
	 * @param model
	 * @param modelChildren
	 * @param discountPlanCode
	 * @param discountFlag
	 * @return
	 * @throws ProductException
	 */
	public Product updateProduct(String codeProduct, String label, Long idProductLine, 
									String codeBrand, String reference, String model,
									Set<String> modelChildren, Set<String> discountPlanCode, boolean discountFlag) throws ProductException {
		
		Product product = productService.findByCode(codeBrand);
		product.setDescription(label);
		final ProductLine productLine = product.getProductLine();
		if(productLine != null && idProductLine != null && product.getId() != idProductLine ) {
			product.setProductLine(productLineService.findById(idProductLine));
		}
		final CustomerBrand customerBrand = product.getBrand();
		if(customerBrand != null && Strings.isNotBlank(codeBrand) && codeBrand.equals(customerBrand.getCode())) {
			product.setBrand(brandService.findByCode(codeBrand));
		}
		product.setReference(reference);
		product.setModel(model);
		product.setModelChlidren(modelChildren);
		
		if(discountPlanCode != null && !discountPlanCode.isEmpty()) {
			Set<DiscountPlan> discountPlans  = new HashSet<DiscountPlan>(discountPlanCode.stream().map(codeDiscount -> {
				final DiscountPlan discount = discountPlanService.findByCode(codeDiscount);
				return discount;
			}).collect(Collectors.toSet()));
			product.setDiscountList(discountPlans);
		}
		product.setDiscountFlag(discountFlag);
		return productService.updateProduct(product);
	}
	
	/**
	 * update status of a product
	 * @param codeProduct
	 * @param status
	 * @return
	 * @throws ProductException when the status is unknown and the status 
	 */
	public Product updateState(String codeProduct, int status) throws ProductException {
		if(Strings.isEmpty(codeProduct)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		ProductStatusEnum productStatus = ProductStatusEnum.getCurrentStatus(status).get();
		if(productStatus == null)
			throw new ProductException(String.format(PRODUCT_STATUS_NOT_FOUND, status));
		final Product product = productService.findByCode(codeProduct);
		return productService.updateStatus(product, productStatus);
	}
	
	/**
	 * @param code
	 * @return
	 * @throws ProductException
	 */
	public Product findByCode(String code) throws ProductException {
		if(Strings.isEmpty(code)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		return productService.findByCode(code);
	}
}

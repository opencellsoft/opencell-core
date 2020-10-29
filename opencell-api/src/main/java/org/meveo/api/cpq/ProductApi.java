package org.meveo.api.cpq;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductLine;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.ProductService;
import org.meveo.service.cpq.exception.ProductException;
import org.meveo.service.cpq.exception.ProductLineException;
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
	@Inject
	private ProductLineApi productLineApi;
	
	/**
	 * @return ProductDto
	 * @throws ProductException
	 */
	public ProductDto addNewProduct(ProductDto productDto){
		if(Strings.isEmpty(productDto.getCode())) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		Long idLineProdcut = productDto.getProductLine() != null ? idLineProdcut = productDto.getProductLine().getId() : null;
		String codeBrand = productDto.getBrand() != null ? productDto.getBrand().getCode() : null;
		var discountList = productDto.getDiscountList().stream().map(DiscountPlanDto::getCode).collect(Collectors.toSet());
		try {
			return new ProductDto(productService.create(productDto.getCode(), productDto.getLabel(), idLineProdcut, codeBrand,
															productDto.getReference(), productDto.getModel(), productDto.getModelChlidren(),
															discountList, productDto.isDiscountFlag()));
		} catch (ProductException e) {
			throw new BusinessException(e);
		}
	}
	
	/**
	 * @param productDto
	 * @throws ProductException
	 */
	public ProductDto updateProduct(ProductDto productDto) throws ProductException {
		
		Product product = productService.findByCode(productDto.getCode());
		product.setDescription(productDto.getLabel());
		product.setProductLine(productLineService.findById(productDto.getProductLine() != null ? productDto.getProductLine().getId() : null));
		product.setBrand(brandService.findByCode(productDto.getBrand() != null ? productDto.getBrand().getCode() : null));
		product.setReference(productDto.getReference());
		product.setModel(productDto.getModel());
		product.setModelChlidren(productDto.getModelChlidren());
		
		if(productDto.getDiscountList() != null && !productDto.getDiscountList().isEmpty()) {
			Set<DiscountPlan> discountPlans  = new HashSet<DiscountPlan>(productDto.getDiscountList().stream().map(d -> {
				final DiscountPlan discount = discountPlanService.findByCode(d.getCode());
				return discount;
			}).collect(Collectors.toSet()));
			product.setDiscountList(discountPlans);
		}
		product.setDiscountFlag(productDto.isDiscountFlag());
		return new ProductDto(productService.updateProduct(product));
	}
	
	/**
	 * update status of a product
	 * @param codeProduct
	 * @param status
	 * @return
	 * @throws ProductException when the status is unknown and the status 
	 */
	public ProductDto updateStatus(String codeProduct, int status) throws ProductException {
		if(Strings.isEmpty(codeProduct)) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		ProductStatusEnum productStatus = ProductStatusEnum.getCurrentStatus(status).get();
		if(productStatus == null)
			throw new ProductException(String.format(PRODUCT_STATUS_NOT_FOUND, status));
		final Product product = productService.findByCode(codeProduct);
		return new ProductDto(productService.updateStatus(product, productStatus));
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
		try {
			return new ProductDto(productService.findByCode(code));
		} catch (ProductException e) {
			throw new BusinessException(e);
		}
	}
	
	/**
	 * @param id
	 */
	public void removeProductLine(Long id){
		try {
			productLineApi.removeProductLine(id);
		} catch (ProductLineException e) {
			throw new BusinessApiException(e);
		}
	}
	

	/**
	 * @param dto
	 * @return
	 */
	public ProductLineDto createProductLine(ProductLineDto dto){
		try {
			return new ProductLineDto(productLineApi.createProductLine(dto));
		} catch (ProductLineException e) {
			throw new BusinessApiException(e);

		}
	}

	/**
	 * @param dto
	 * @return
	 */
	public ProductLineDto updateProductLine(ProductLineDto dto){
		try {
			return new ProductLineDto(productLineApi.updateProductLine(dto));
		} catch (ProductLineException e) {
			throw new BusinessApiException(e);

		}
	}

	/**
	 * @param code
	 * @return
	 */
	public ProductLineDto findProductLineByCode(String code) {
		try {
			return new ProductLineDto(productLineApi.findOne(code));
		} catch (ProductLineException e) {
			throw new BusinessApiException(e);

		}
	}
	
}

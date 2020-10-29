package org.meveo.api.cpq;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.model.cpq.ProductLine;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.cpq.ProductLineService;
import org.meveo.service.cpq.exception.ProductLineException;

@Stateless
public class ProductLineApi extends BaseApi {


	private static final String PRODUCT_LINE_EMPTY = "The product line must not be null";
	
	@Inject
	private ProductLineService productLineService;
	@Inject
	private SellerService sellerService;
	
	public void removeProductLine(Long id) throws ProductLineException {
		productLineService.removeProductLine(id);
	}
	
	public ProductLine createProductLine(ProductLineDto dto) throws ProductLineException{
		if(dto == null)
			throw new ProductLineException(PRODUCT_LINE_EMPTY);
		if(dto.getCodeProductLine() == null) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		return productLineService.createNew(dto.getCodeProductLine(), dto.getLabel(), dto.getCodeSeller(), dto.getLongDescription(), dto.getIdCodeParentLine());
	}
	
	public ProductLine updateProductLine(ProductLineDto dto) throws ProductLineException {
		if(dto == null)
			throw new ProductLineException(PRODUCT_LINE_EMPTY);
		if(dto.getCodeProductLine() == null) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		final ProductLine line = productLineService.findById(dto.getId());
		if(line == null)
			throw new ProductLineException(String.format(ProductLineService.PRODUCT_LINE_UNKNOWN, dto.getId()));
		
		line.setLongDescription(dto.getLongDescription());
		line.setDescription(dto.getLabel());
		if(dto.getIdCodeParentLine() != null) {
			line.setParentLine(productLineService.findById(dto.getIdCodeParentLine()));
		}
		if(dto.getCodeSeller() != null && !dto.getCodeSeller().strip().equals("")) {
			line.setSeller(sellerService.findByCode(dto.getCodeSeller()));
		}
		return productLineService.update(line);
	}
	
	public List<ProductLine> findByCode(String code) {
		return productLineService.findByCodeLike(code);
	}
	
	public ProductLineDto findOne(String code) {
		if(code == null || code.strip().equals("") ) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		try {
			return new ProductLineDto(productLineService.findByCode(code));
		} catch (ProductLineException e) {
			throw new BusinessException(e);
		}
	}
	public ProductLine findOne(Long id) {
		if(id == null) {
			missingParameters.add("id");
		}
		handleMissingParameters();
		return productLineService.findById(id);
	}
}

package org.meveo.api.cpq;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.cpq.ProductLine;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.cpq.ProductLineService;

@Stateless
public class ProductLineApi extends BaseApi {


	private static final String PRODUCT_LINE_EMPTY = "The product line must not be null";
	
	@Inject
	private ProductLineService productLineService;
	@Inject
	private SellerService sellerService;
	
	public void removeProductLine(Long id) {
		productLineService.removeProductLine(id);
	}
	
	public void removeProductLine(String codeProductLine) {
		productLineService.removeProductLine(codeProductLine);
	}
	
	public Long createProductLine(ProductLineDto dto){
		if(dto == null)
			throw new MeveoApiException(PRODUCT_LINE_EMPTY);
		if(dto.getCodeProductLine() == null) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		/**String codeProductLine, String label, String codeSeller, String longDescription, Long idCodeParentLine**/

		ProductLine productLine = new ProductLine();
		productLine.setCode(dto.getCodeProductLine());
		productLine.setDescription(dto.getLabel());
		productLine.setLongDescription(dto.getLongDescription());
		if(dto.getIdCodeParentLine() != null) {
			productLine.setParentLine(productLineService.findById(dto.getIdCodeParentLine()));
		}
		if(Strings.isNotEmpty(dto.getCodeSeller())) {
			productLine.setSeller(sellerService.findByCode(dto.getCodeSeller()));
		}
		
		productLine = productLineService.createNew(productLine);
		return productLine.getId();
	}
	
	public ProductLineDto updateProductLine(ProductLineDto dto){
		if(dto == null)
			throw new MeveoApiException(PRODUCT_LINE_EMPTY);
		if(dto.getCodeProductLine() == null) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		final ProductLine line = productLineService.findById(dto.getId());
		if(line == null)
			throw new MeveoApiException(String.format(ProductLineService.PRODUCT_LINE_UNKNOWN, dto.getId()));
		
		line.setLongDescription(dto.getLongDescription());
		line.setDescription(dto.getLabel());
		if(dto.getIdCodeParentLine() != null) {
			line.setParentLine(productLineService.findById(dto.getIdCodeParentLine()));
		}
		if(dto.getCodeSeller() != null && !dto.getCodeSeller().strip().equals("")) {
			line.setSeller(sellerService.findByCode(dto.getCodeSeller()));
		}
		return new ProductLineDto(productLineService.update(line));
	}
	
	public List<ProductLine> findByCodeLike(String code) {
		return productLineService.findByCodeLike(code);
	}
	
	public ProductLineDto findProductLineByCode(String code) {		
		
		if(code == null || code.strip().equals("") ) {
			missingParameters.add("code");
		}
		handleMissingParameters();
		return new ProductLineDto(productLineService.findByCode(code));
	}

}

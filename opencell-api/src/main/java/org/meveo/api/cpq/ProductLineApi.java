package org.meveo.api.cpq;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.logging.log4j.util.Strings;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.cpq.ProductLine;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.cpq.ProductLineService;


/**
 * @author Mbarek-Ay
 **/

@Stateless
public class ProductLineApi extends BaseCrudApi<ProductLine, ProductLineDto> {


	private static final String PRODUCT_LINE_EMPTY = "The product line must not be null";
	
	@Inject
	private ProductLineService productLineService;
	@Inject
	private SellerService sellerService;
	
	
	 @Override
    public ProductLine createOrUpdate(ProductLineDto postData) throws MeveoApiException, BusinessException {
        ProductLine productLine = productLineService.findByCode(postData.getCode());
        if (productLine == null) {
        	productLine = create(postData);
        } else {
        	productLine = update(postData);
        }
        return productLine;
    }
	
	
	@Override
	public void remove(String code) throws MeveoApiException, BusinessException {

		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
			handleMissingParameters();
		}
		ProductLine productLine = productLineService.findByCode(code);
		if (productLine == null) {
			throw new EntityDoesNotExistsException(ProductLine.class, code);
		}

		productLineService.remove(productLine);

	}
	 
	@Override
	public ProductLine create(ProductLineDto dto){
		if(dto == null)
			throw new MeveoApiException(PRODUCT_LINE_EMPTY);
		if(StringUtils.isBlank(dto.getCode())) {
			missingParameters.add("code");
		}
		handleMissingParameters();
	     if (productLineService.findByCode(dto.getCode()) != null) {
	            throw new EntityAlreadyExistsException(ProductLine.class, dto.getCode());
	        } 
		ProductLine productLine = new ProductLine();
		productLine.setCode(dto.getCode());
		productLine.setDescription(dto.getDescription());
		productLine.setLongDescription(dto.getLongDescription());
		updateProductLineParent(productLine,dto.getParentLineCode());
		
		if(!StringUtils.isBlank(dto.getSellerCode())) {
			productLine.setSeller(sellerService.findByCode(dto.getSellerCode()));
		} 
		 productLineService.create(productLine);
		return productLine;
	}
	
	@Override
	public ProductLine update(ProductLineDto dto){
		if(dto == null)
			throw new MeveoApiException(PRODUCT_LINE_EMPTY);
		if(dto.getCode()== null) {
			missingParameters.add("code");
		}
		handleMissingParameters();
	     ProductLine productLine = productLineService.findByCode(dto.getCode());
		if(productLine == null)
			throw new MeveoApiException(dto.getCode());
		productLine.setCode(StringUtils.isBlank(dto.getUpdatedCode()) ? dto.getCode() : dto.getUpdatedCode());
		productLine.setLongDescription(dto.getLongDescription());
		productLine.setDescription(dto.getDescription());
		updateProductLineParent(productLine,dto.getParentLineCode());
		
		if(!StringUtils.isBlank(dto.getSellerCode())) {
			productLine.setSeller(sellerService.findByCode(dto.getSellerCode()));
		}
		productLineService.update(productLine);
		return productLine;
	}

	 private void updateProductLineParent(ProductLine productLine, final String prodLineParentCode) throws EntityDoesNotExistsException {
	        if (prodLineParentCode != null) {
	            if (isNotBlank(prodLineParentCode)) {
	                ProductLine parentProductLine =productLineService.findByCode(prodLineParentCode);
	                if (parentProductLine == null) {
	                    throw new EntityDoesNotExistsException(ProductLine.class, prodLineParentCode);
	                }

	                productLine.setParentLine(parentProductLine);
	            } else {
	            	productLine.setParentLine(null);
	            }

	        }
	    }
	 
	 /**
		 * @param code
		 * @return ProductLineDto
		 */
	 public ProductLineDto findByCode(String code){
		 if(Strings.isEmpty(code)) {
			 missingParameters.add("code");
		 }
		 handleMissingParameters();
		 ProductLine prodcutLine=productLineService.findByCode(code);

		 if (prodcutLine == null) {
			 throw new EntityDoesNotExistsException(ProductLine.class, code);
		 }
		 ProductLineDto productLineDto= new ProductLineDto(prodcutLine);
		 return productLineDto;
	 }
		
		
 

}

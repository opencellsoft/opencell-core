package org.meveo.api.rest.catalog.impl;

import javax.inject.Inject;

import org.meveo.api.cpq.ProductApi;
import org.meveo.api.cpq.ProductLineApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductLineDtoResponse;
import org.meveo.api.rest.catalog.ProductRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.cpq.enums.ProductStatusEnum;

public class ProductRsImpl extends BaseRs implements ProductRs {

	@Inject
	private ProductApi productApi;
	@Inject
	private ProductLineApi productLineApi; 
	
	@Override
	public ActionStatus addNewProduct(ProductDto productDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
        	productApi.addNewProduct(productDto);
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
	}

	@Override
	public ActionStatus updateProduct(ProductDto productDto) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	productApi.updateProduct(productDto);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}

	@Override
	public ActionStatus updateStatus(String codeProduct, ProductStatusEnum status) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	productApi.updateStatus(codeProduct, status);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}

	@Override
	public GetProductDtoResponse findByCode(String codeProduct) {
		GetProductDtoResponse result = new GetProductDtoResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
        	result.setProductDto(productApi.findByCode(codeProduct));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
	}

	@Override
	public ActionStatus removeProductLine(Long id) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	productLineApi.removeProductLine(id);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}

	@Override
	public ActionStatus createOrUpdateProductLine(ProductLineDto dto) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	if(dto.getId() == null)
	        		productLineApi.createProductLine(dto);
	        	else
	        		productLineApi.updateProductLine(dto);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}

	@Override
	public GetProductLineDtoResponse findProductLineByCode(String code) {
		GetProductLineDtoResponse result = new GetProductLineDtoResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
        	result.setProductLineDto(productLineApi.findProductLineByCode(code));        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
	}

}

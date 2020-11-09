package org.meveo.api.rest.catalog.impl;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.ProductApi;
import org.meveo.api.cpq.ProductLineApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetListProductsResponseDto;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductLineDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductVersionResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.catalog.ProductRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;

public class ProductRsImpl extends BaseRs implements ProductRs {

	@Inject
	private ProductApi productApi;
	@Inject
	private ProductLineApi productLineApi; 
	
	
	
	@Override
	public Response addNewProduct(ProductDto productDto) {
		GetProductDtoResponse result = new GetProductDtoResponse();
        try {
        	result.setProductDto(productApi.addNewProduct(productDto));
            return Response.ok(result).build();
        } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}

	@Override
	public Response updateProduct(ProductDto productDto) {
		GetProductDtoResponse result = new GetProductDtoResponse();
        try {
        	result.setProductDto(productApi.updateProduct(productDto));
            return Response.ok(result).build();
        } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}

	@Override
	public Response updateStatus(String codeProduct, ProductStatusEnum status) {
		GetProductDtoResponse result = new GetProductDtoResponse();
	        try {
	        	result.setProductDto(productApi.updateStatus(codeProduct, status));
	            return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response findByCode(String codeProduct) {
		GetProductDtoResponse result = new GetProductDtoResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
        	result.setProductDto(productApi.findByCode(codeProduct));
            return Response.ok(result).build();
        } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}
	@Override
	public Response listPost(PagingAndFiltering pagingAndFiltering) {
		 GetListProductsResponseDto result = new GetListProductsResponseDto();

	        try {
	        	/*****@TODO RAY : create a new method in ProductAPI that get products matching given criteria**/
	        	
	        } catch (Exception e) {
	            processException(e, result.getActionStatus());
	        }

	        return Response.ok().entity(result).build();
	}

	@Override
	public Response listPost(String billingAccountCode, String offerCode, List<String> selectedProducts,
			PagingAndFiltering pagingAndFiltering) {
		 GetListProductsResponseDto result = new GetListProductsResponseDto();

	        try {
	        	/*****@TODO RAY : create a new method in ProductAPI that get products matching given criteria**/
	        	
	        } catch (Exception e) {
	            processException(e, result.getActionStatus());
	        }

	        return Response.ok().entity(result).build();
	}
	
	@Override
	public Response removeProductLine(String codeProductLine) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	productLineApi.removeProductLine(codeProductLine);
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result);
	        }
	}

	@Override
	public Response createOrUpdateProductLine(ProductLineDto dto) {
		GetProductLineDtoResponse result = new GetProductLineDtoResponse();
	        try {
	        	ProductLineDto line = null;
	        	if(dto.getId() == null)
	        		line = productLineApi.createProductLine(dto);
	        	else
	        		line = productLineApi.updateProductLine(dto);
	        	result.setProductLineDto(line);
	            return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response findProductLineByCode(String code) {
		GetProductLineDtoResponse result = new GetProductLineDtoResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
        	result.setProductLineDto(productLineApi.findProductLineByCode(code));  
            return Response.ok(result).build();      
        } catch(MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}
	
	
	@Override
	public Response createOrUpdateProductVersion(ProductVersionDto postData) {
		GetProductVersionResponse result = new GetProductVersionResponse();
        try {
			productApi.createOrUpdateProductVersion(postData);
			return Response.ok(result).build();
        } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }

	}
	
	public Response removeProductVersion(String productCode,int currentVersion) { 
		ActionStatus result = new ActionStatus();
		try {
			productApi.removeProductVersion(productCode, currentVersion);
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
		       return errorResponse(e, result);
		}
	}

	@Override
	public Response updateProductVersionStatus(String productCode, int currentVersion,VersionStatusEnum status) {
		GetProductVersionResponse result = new GetProductVersionResponse();
		try {
			result.setProductVersionDto(productApi.UpdateProductVersionStatus(productCode, currentVersion, status));
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
		}
	}
	
	
	public Response duplicateProductVersion(String productCode,int currentVersion) { 
		GetProductVersionResponse result = new GetProductVersionResponse();
		try {
			result.setProductVersionDto(productApi.duplicateProductVersion(productCode, currentVersion));
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
		}
	}

	private Response errorResponse(MeveoApiException e, ActionStatus result) {
		result.setStatus(ActionStatusEnum.FAIL);
		result.setMessage(e.getMessage());
		 return createResponseFromMeveoApiException(e, result).build();
	}




}

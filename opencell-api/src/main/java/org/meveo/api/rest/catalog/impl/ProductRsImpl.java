package org.meveo.api.rest.catalog.impl;

import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.ProductApi;
import org.meveo.api.cpq.ProductLineApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.cpq.ProductChargeTemplateMappingDto;
import org.meveo.api.dto.cpq.ProductDto;
import org.meveo.api.dto.cpq.ProductLineDto;
import org.meveo.api.dto.cpq.ProductVersionDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetCpqOfferResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductVersionsResponseDto;
import org.meveo.api.dto.response.cpq.GetListProductsResponseDto;
import org.meveo.api.dto.response.cpq.GetProductDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductLineDtoResponse;
import org.meveo.api.dto.response.cpq.GetProductVersionResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.ProductRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;

@Interceptors({ WsRestApiInterceptor.class })
public class ProductRsImpl extends BaseRs implements ProductRs {

	@Inject
	private ProductApi productApi;
	@Inject
	private ProductLineApi productLineApi;  
 
	@Override
	public Response createProduct(ProductDto productDto) {
		GetProductDtoResponse result = new GetProductDtoResponse();
        try {
			ProductDto response = productApi.create(productDto);
			result.setId(response.getId());
			if(response.getCurrentProductVersion() != null)
				result.setCurrentProductVersion(response.getCurrentProductVersion());
			result.setDiscountList(response.getDiscountList());
			result.setAgreementDateSetting(response.getAgreementDateSetting());
			return Response.ok(result).build();
        } catch(MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}

	@Override
	public Response updateProduct(String productCode, ProductDto productDto) {
		GetProductDtoResponse result = new GetProductDtoResponse();
        try {
        	productApi.updateProduct(productCode, productDto);
        	return Response.ok(result).build();
        } catch(MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}

	@Override
	public Response updateProductStatus(String codeProduct, ProductStatusEnum status) {
		GetProductDtoResponse result = new GetProductDtoResponse();
        try {
        	productApi.updateStatus(codeProduct,status);
        	return Response.ok(result).build();
        } catch(MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}

	
	@Override
	public Response findProductByCode(String code) {
		GetProductDtoResponse result = new GetProductDtoResponse();
	    try {
	    	result=productApi.findByCode(code);
	        return Response.ok(result).build();
	    } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
	    }
	}
	
	
	@Override
	public Response listProducts(PagingAndFiltering pagingAndFiltering) {
		 GetListProductsResponseDto result = new GetListProductsResponseDto();

	        try {  
	    			result = productApi.listProducts(pagingAndFiltering);
	    			return Response.ok(result).build(); 
	        	
	        } catch (MeveoApiException e) { 
	            return errorResponse(e, result.getActionStatus());
	        } 
	}
	        

	
	@Override
	public Response removeProductLine(String codeProductLine) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	productLineApi.remove(codeProductLine);
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result);
	        }
	}

	@Override
	public Response createOrUpdateProductLine(ProductLineDto dto) {
		GetProductLineDtoResponse result = new GetProductLineDtoResponse();
	        try { 
	        	var productLine = productLineApi.createOrUpdate(dto);
	        	result.getActionStatus().setEntityId(productLine.getId());
	        	result.getActionStatus().setEntityCode(productLine.getCode());
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	        return Response.ok(result).build();
	}

 

	@Override
	public Response findProductLineByCode(String code) {
		GetProductLineDtoResponse result = new GetProductLineDtoResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
        try {
        	result.setProductLineDto(productLineApi.findByCode(code));  
            return Response.ok(result).build();      
        } catch(MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}
	
	
	@Override
	public Response createOrUpdateProductVersion(ProductVersionDto postData) {
		GetProductVersionResponse result = new GetProductVersionResponse();
        try {
        	ProductVersion pv =  productApi.createOrUpdateProductVersion(postData);
        	result.setCurrentVersion(pv.getCurrentVersion());
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
			result=productApi.UpdateProductVersionStatus(productCode, currentVersion, status);
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
		}
	}
	
	
	public Response duplicateProductVersion(String productCode,int currentVersion) { 
		GetProductVersionResponse result = new GetProductVersionResponse();
		try {
			result =productApi.duplicateProductVersion(productCode, currentVersion);
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
		}
	}

	/*private Response errorResponse(MeveoApiException e, ActionStatus result) {
		if(result==null) {
			result = new ActionStatus();
		}
		result.setStatus(ActionStatusEnum.FAIL);
		result.setMessage(e.getMessage());
		 return createResponseFromMeveoApiException(e, result).build();
	}*/

	@Override
	public Response removeProduct(String productCode) {
		 ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	productApi.removeProduct(productCode);
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result);
	        }
	}

	@Transactional
	public Response duplicateProduct(String productCode, boolean duplicateHierarchy, boolean preserveCode) { 
		GetProductDtoResponse result = new GetProductDtoResponse();
		try {
			Product p=productApi.duplicateProduct(productCode, duplicateHierarchy, preserveCode);  
			result = new GetProductDtoResponse(p);
		} catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
		}catch (Exception e) {
			  log.error("Failed to duplicate product:", e);
		}
		 return Response.ok(result).build();
	}

	@Override
	public Response findProductVersion(String productCode, int productVersion) {
		GetProductVersionResponse result = new GetProductVersionResponse();
		try { 
			result=productApi.findProductVersion(productCode, productVersion);
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
		}
	}

	@Override
	public Response findProductVersions(String productCode) {
		GetListProductVersionsResponseDto result = new GetListProductVersionsResponseDto();
		try { 
			result.getProductVersions().addAll(productApi.findProductVersionByProduct(productCode));
			return Response.ok(result).build();
		} catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
		}
	}
 
	
	@Override
	public Response listPost(OfferContextDTO offerContextDto) {
		GetCpqOfferResponseDto result = new GetCpqOfferResponseDto();
		try {  
			result = productApi.listPost(offerContextDto);
			return Response.ok(result).build(); 

		} catch (MeveoApiException e) { 
			return errorResponse(e, result.getActionStatus());
		} 
	}

	@Override
	public Response addCharges(String productCode, List<ProductChargeTemplateMappingDto> productChargeTemplateMapping) {
		GetProductDtoResponse result = new GetProductDtoResponse();
		try {  
			result = productApi.addCharges(productCode, productChargeTemplateMapping);
			return Response.ok(result).build(); 

		} catch (MeveoApiException e) { 
			return errorResponse(e, result.getActionStatus());
		} 
	}

	@Override
	public Response removeCharge(String productCode, List<String> chargeCodes) {
		GetProductDtoResponse result = new GetProductDtoResponse();
		try {  
			result = productApi.removeCharges(productCode, chargeCodes);
			return Response.ok(result).build(); 

		} catch (MeveoApiException e) { 
			return errorResponse(e, result.getActionStatus());
		} 
	}



}

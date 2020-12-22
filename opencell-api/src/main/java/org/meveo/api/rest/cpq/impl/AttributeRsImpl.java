package org.meveo.api.rest.cpq.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.AttributeApi;
import org.meveo.api.dto.cpq.AttributeDTO;
import org.meveo.api.dto.cpq.OfferContextDTO;
import org.meveo.api.dto.response.cpq.GetAttributeDtoResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.cpq.AttributeRs;
import org.meveo.api.rest.impl.BaseRs;

public class AttributeRsImpl extends BaseRs implements AttributeRs {

	@Inject
	private AttributeApi attributeApi;
	
	@Override
	public Response create(AttributeDTO attributeDto) {
      
        GetAttributeDtoResponse result = new GetAttributeDtoResponse();
        try {
        	attributeApi.create(attributeDto);
        	return Response.ok(result).build();
        } catch(MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}

	@Override
	public Response update(AttributeDTO attributeDto) {
		GetAttributeDtoResponse result = new GetAttributeDtoResponse();
	        try {
	        	attributeApi.update(attributeDto);
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}
	@Override
	public Response delete(String code) {
		GetAttributeDtoResponse result = new GetAttributeDtoResponse();
	        try {
	        	attributeApi.remove(code);
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response findByCode(String code) {
		GetAttributeDtoResponse result = new GetAttributeDtoResponse();
	    try {
	    	result.setAttributeDto(attributeApi.findByCode(code));
	        return Response.ok(result).build();
	    } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
	    }
	}
 
	
	/*@Override
	public Response createOrUpdateAttribute(AttributeDTO postData) {
		GetAttributeDtoResponse result = new GetAttributeDtoResponse();
        try {
        	attributeApi.createOrUpdate(postData);
			return Response.ok(result).build();
        } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}*/

//	@Override
//	public Response listPost(OfferContextDTO quoteContext) {
//		// TODO Auto-generated method stub
//		return null;
//	}
 




	 

	 
}

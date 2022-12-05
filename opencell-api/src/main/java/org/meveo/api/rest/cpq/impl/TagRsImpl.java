package org.meveo.api.rest.cpq.impl;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.meveo.api.cpq.TagApi;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.cpq.TagTypeDto;
import org.meveo.api.dto.response.cpq.GetTagDtoResponse;
import org.meveo.api.dto.response.cpq.GetTagTypeDtoResponse;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.cpq.TagRs;
import org.meveo.api.rest.impl.BaseRs;

public class TagRsImpl extends BaseRs implements TagRs {

	@Inject
	private TagApi tagApi;
	
	public static final String TAG_TYPE_CREATED = "the Tag type successfully created";
	public static final String TAG_CREATED = "the Tag successfully created";
	
	@Override
	public Response createTag(TagDto tagDto) {
      
        GetTagDtoResponse result = new GetTagDtoResponse();
        try {
        	Long id=tagApi.create(tagDto);
        	result.getActionStatus().setMessage(TAG_CREATED);
        	result.setId(id);
        	return Response.ok(result).build();
        } catch(MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
        }
	}

	@Override
	public Response updateTag(TagDto tagDto) {
	     GetTagDtoResponse result = new GetTagDtoResponse();
	        try {
	        	tagApi.update(tagDto);
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response deleteTag(String codeTag) {
	     GetTagDtoResponse result = new GetTagDtoResponse();
	        try {
	        	tagApi.removeTag(codeTag);
	        	return Response.ok(result).build();
	        } catch(Exception e) {
				MeveoApiException exp = null;
				if (e instanceof MeveoApiException) {
					exp = (MeveoApiException) e;
				} else {
					exp = new BusinessApiException("The tag can not be deleted because attached to other entities");
				}
				return errorResponse(exp, result.getActionStatus());
	        }
	}


	@Override
	public Response findByCode(String codeTag) {
		GetTagDtoResponse result = new GetTagDtoResponse();
	    try {
	    	result.setTagDto(tagApi.findTagByCode(codeTag));
	        return Response.ok(result).build();
	    } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
	    }
	}

	@Override
	public Response createTagType(TagTypeDto tagTypeDto) {
		 GetTagTypeDtoResponse result = new GetTagTypeDtoResponse();
	        try {
	        	Long id=tagApi.create(tagTypeDto);
	        	result.getActionStatus().setMessage(TAG_TYPE_CREATED);
	        	result.setId(id);
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response updateTagType(TagTypeDto tagTypeDto) {
		 GetTagTypeDtoResponse result = new GetTagTypeDtoResponse();
	        try {
	        	var tagType=tagApi.update(tagTypeDto);
	        	result.getActionStatus().setEntityId(tagType.getId());
	        	result.getActionStatus().setEntityCode(tagType.getCode());
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

	@Override
	public Response findTagTypeBycode(String codeTagType) {
		GetTagTypeDtoResponse result = new GetTagTypeDtoResponse();
	    try {
	    	result.setTagTypeDto(tagApi.findTagTypeByCode(codeTagType));
	        return Response.ok(result).build();
	    } catch (MeveoApiException e) {
		       return errorResponse(e, result.getActionStatus());
	    }
	}
	
	@Override
	public Response deleteTagType(String codeTag) {
		 GetTagTypeDtoResponse result = new GetTagTypeDtoResponse();
	        try {
	        	tagApi.deleteTagType(codeTag);
	        	return Response.ok(result).build();
	        } catch(MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}
}

package org.meveo.api.rest.cpq.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.TagApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.cpq.TagTypeDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.cpq.TagRs;
import org.meveo.api.rest.impl.BaseRs;

public class TagRsImpl extends BaseRs implements TagRs {

	@Inject
	private TagApi tagApi;
	
	@Override
	public Response createTag(TagDto tagDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            tagApi.create(tagDto);
            responseBuilder = Response.status(Response.Status.CREATED).entity(result);
            return responseBuilder.build();
        } catch (MeveoApiException e) {
 	       return createResponseFromMeveoApiException(e, result).build();
        }
	}

	@Override
	public Response updateTag(TagDto tagDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        Response.ResponseBuilder responseBuilder = null;
        try {
            tagApi.update(tagDto);
            responseBuilder = Response.ok(result);
            return responseBuilder.build();
        } catch (MeveoApiException e) {
 	       return createResponseFromMeveoApiException(e, result).build();
        }
	}

	@Override
	public Response deleteTag(String codeTag) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    Response.ResponseBuilder responseBuilder = null;
	    try {
	        tagApi.removeTag(codeTag);
	        responseBuilder = Response.ok(result);
	        return responseBuilder.build();
	    } catch (MeveoApiException e) {
		       return createResponseFromMeveoApiException(e, result).build();
	    }
	}

	@Override
	public Response deleteTag(Long id) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    Response.ResponseBuilder responseBuilder = null;
	    try {
	        tagApi.removeTag(id);
	        responseBuilder = Response.ok(result);
	        return responseBuilder.build();
	    } catch (MeveoApiException e) {
		       return createResponseFromMeveoApiException(e, result).build();
	    }
	}

	@Override
	public Response findByCode(String codeTag) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
	        return Response.ok(tagApi.findTagByCode(codeTag)).build();
	    } catch (MeveoApiException e) {
		       return createResponseFromMeveoApiException(e, result).build();
	    }
	}

	@Override
	public Response createTagType(TagTypeDto tagTypeDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    Response.ResponseBuilder responseBuilder = null;
	    try {
	        tagApi.create(tagTypeDto);
	        responseBuilder = Response.ok(result);
	        return responseBuilder.build();
	    } catch (MeveoApiException e) {
		       return createResponseFromMeveoApiException(e, result).build();
	    }
	}

	@Override
	public Response updateTagType(TagTypeDto tagTypeDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    Response.ResponseBuilder responseBuilder = null;
	    try {
	        tagApi.update(tagTypeDto);
	        responseBuilder = Response.ok(result);
	        return responseBuilder.build();
	    } catch (MeveoApiException e) {
		       return createResponseFromMeveoApiException(e, result).build();
	    }
	}

	@Override
	public Response findBycode(String codeTagType) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
	        return Response.ok(tagApi.findTagTypeByCode(codeTagType)).build();
	    } catch (MeveoApiException e) {
		       return createResponseFromMeveoApiException(e, result).build();
	    }
	}

	@Override
	public Response findById(Long idTagType) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	    try {
	        return Response.ok(tagApi.findTagTypeById(idTagType)).build();
	    } catch (MeveoApiException e) {
	       return createResponseFromMeveoApiException(e, result).build();
	    }
	}
	



}

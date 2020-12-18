package org.meveo.api.rest.cpq.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.MediaApi;
import org.meveo.api.dto.cpq.MediaDto;
import org.meveo.api.dto.response.cpq.GetMediaDtoResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.cpq.MediaRs;
import org.meveo.api.rest.impl.BaseRs;

public class MediaRsImpl extends BaseRs implements MediaRs{

	@Inject
	private MediaApi mediaApi;
	
	@Override
	public Response createMedai(MediaDto mediaDto) {
		 GetMediaDtoResponse result = new GetMediaDtoResponse();
		 try {
			 result.setMediaDto(mediaApi.createMedia(mediaDto));
	            return Response.ok(result).build();
	        } catch (MeveoApiException e) {
			       return errorResponse(e, result.getActionStatus());
	        }
	}

}

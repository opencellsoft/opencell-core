package org.meveo.api.rest.cpq.impl;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.meveo.api.cpq.GroupedAttributesApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.GroupedAttributeDto;
import org.meveo.api.dto.response.cpq.GetGroupedAttributesResponse;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.rest.cpq.GroupedAttributesRs;
import org.meveo.api.rest.impl.BaseRs;

public class GroupedAttributeRsImpl  extends BaseRs implements GroupedAttributesRs {

	@Inject
	private GroupedAttributesApi groupedServiceApi;
	
	@Override
	public Response create(GroupedAttributeDto groupedAttributeDto) { 
		  GetGroupedAttributesResponse result = new GetGroupedAttributesResponse();
	        try {
	        	var groupeAttribute = groupedServiceApi.createGroupedAttribute(groupedAttributeDto);
	        	result.setGroupedAttributeDto(groupeAttribute);
	        	result.getActionStatus().setEntityId(groupeAttribute.getId());
	        	return Response.ok(result).build();
	        } catch (Exception e) {
	        	return errorResponse(new MeveoApiException(e), result.getActionStatus());
	        }
	}

	@Override
	public ActionStatus update(GroupedAttributeDto groupedAttributeDto) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	groupedServiceApi.updateGroupedAttribute(groupedAttributeDto);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}

	@Override
	public ActionStatus remove(String groupedServiceCode) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	groupedServiceApi.removeGroupedAttribute(groupedServiceCode);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}

	@Override
	public Response find(String groupedServiceCode) {
		  GetGroupedAttributesResponse result = new GetGroupedAttributesResponse();
	        try {
	        	result.setGroupedAttributeDto(groupedServiceApi.findGroupedAttributeByCode(groupedServiceCode));
	        	return Response.ok(result).build();
	        } catch (Exception e) {
	        	return errorResponse(new MeveoApiException(e), result.getActionStatus());
	        }
	        
	}


}

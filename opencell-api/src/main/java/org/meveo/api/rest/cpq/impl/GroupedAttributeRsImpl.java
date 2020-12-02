package org.meveo.api.rest.cpq.impl;

import javax.inject.Inject;

import org.meveo.api.cpq.GroupedAttributesApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.GroupedAttributeDto;
import org.meveo.api.rest.cpq.GroupedAttributesRs;
import org.meveo.api.rest.impl.BaseRs;

public class GroupedAttributeRsImpl  extends BaseRs implements GroupedAttributesRs {

	@Inject
	private GroupedAttributesApi groupedServiceApi;
	
	@Override
	public ActionStatus create(GroupedAttributeDto groupedAttributeDto) { 
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	groupedServiceApi.createGroupedAttribute(groupedAttributeDto);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
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
	public ActionStatus find(String groupedServiceCode) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	groupedServiceApi.findGroupedAttributeByCode(groupedServiceCode);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}


}

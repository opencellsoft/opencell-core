package org.meveo.api.rest.cpq.impl;

import javax.inject.Inject;

import org.meveo.api.cpq.GroupedServiceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.GroupedServiceDto;
import org.meveo.api.rest.cpq.GroupedServiceRs;
import org.meveo.api.rest.impl.BaseRs;

public class GroupedServiceRsImpl  extends BaseRs implements GroupedServiceRs {

	@Inject
	private GroupedServiceApi groupedServiceApi;
	
	@Override
	public ActionStatus create(GroupedServiceDto groupedServiceDto) { 
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	groupedServiceApi.createGroupedService(groupedServiceDto);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}

	@Override
	public ActionStatus update(GroupedServiceDto groupedServiceDto) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	groupedServiceApi.updateGroupedService(groupedServiceDto);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}

	@Override
	public ActionStatus remove(String groupedServiceCode) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	groupedServiceApi.removeGroupedService(groupedServiceCode);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}

	@Override
	public ActionStatus find(String groupedServiceCode) {
		  ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
	        try {
	        	groupedServiceApi.findGroupedServiceByCode(groupedServiceCode);
	        } catch (Exception e) {
	            processException(e, result);
	        }
	        return result;
	}

}

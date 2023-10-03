package org.meveo.api.rest.billing.impl;

import org.meveo.api.billing.EinvoiceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.billing.PdpStatusDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.billing.EinvoicingRs;
import org.meveo.api.rest.impl.BaseRs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class EinvoicingRsImpl extends BaseRs implements EinvoicingRs {
	
	@Inject
	private EinvoiceApi einvoiceApi;
	
	@Override
	public Response creatOrUpdatePdpStatus(PdpStatusDto pdpStatusDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, null);
		try {
			einvoiceApi.assignPdpStatus(pdpStatusDto);
		} catch (Exception e) {
			processException(e, result);
		}
		return Response.ok(result).build();
	}
}

package org.meveo.apiv2.billing.impl;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.meveo.apiv2.billing.CdrListInput;
import org.meveo.apiv2.billing.CdrListResult;
import org.meveo.apiv2.billing.ChargeCdrListInput;
import org.meveo.apiv2.billing.ChargeCdrListResult;
import org.meveo.apiv2.billing.resource.MediationResource;
import org.meveo.apiv2.billing.service.MediationApiService;
import org.meveo.commons.utils.StringUtils;

public class MediationResourceImpl implements MediationResource {

    @Context
    protected HttpServletRequest httpServletRequest;

    @Inject
    private MediationApiService mediationApiService;

    @Override
    public CdrListResult registerCdrList(CdrListInput cdrListInput) {
        String ipAddress = StringUtils.isBlank(httpServletRequest.getHeader("x-forwarded-for")) ? httpServletRequest.getRemoteAddr() : httpServletRequest.getHeader("x-forwarded-for");
        return mediationApiService.registerCdrList(cdrListInput, ipAddress);
    }

    @Override
    public ChargeCdrListResult chargeCdrList(ChargeCdrListInput cdrListInput) {
        String ipAddress = StringUtils.isBlank(httpServletRequest.getHeader("x-forwarded-for")) ? httpServletRequest.getRemoteAddr() : httpServletRequest.getHeader("x-forwarded-for");
        return mediationApiService.chargeCdrList(cdrListInput, ipAddress);
    }
}

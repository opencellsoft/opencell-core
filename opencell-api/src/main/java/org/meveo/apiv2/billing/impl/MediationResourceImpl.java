package org.meveo.apiv2.billing.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.billing.CdrDtoInput;
import org.meveo.apiv2.billing.CdrDtoResponse;
import org.meveo.apiv2.billing.CdrListDtoDeletedInput;
import org.meveo.apiv2.billing.CdrListDtoInput;
import org.meveo.apiv2.billing.CdrListInput;
import org.meveo.apiv2.billing.ChargeCdrListInput;
import org.meveo.apiv2.billing.ProcessCdrListResult;
import org.meveo.apiv2.billing.resource.MediationResource;
import org.meveo.apiv2.billing.service.MediationApiService;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.rating.CDR;

@Interceptors({ WsRestApiInterceptor.class })
public class MediationResourceImpl implements MediationResource {

    @Context
    protected HttpServletRequest httpServletRequest;

    @Inject
    private MediationApiService mediationApiService;
    private CdrMapper mapper = new CdrMapper();

    @Override
    public ProcessCdrListResult registerCdrList(CdrListInput cdrListInput) {
        String ipAddress = StringUtils.isBlank(httpServletRequest.getHeader("x-forwarded-for")) ? httpServletRequest.getRemoteAddr() : httpServletRequest.getHeader("x-forwarded-for");
        return mediationApiService.registerCdrList(cdrListInput, ipAddress);
    }

    @Override
    public ProcessCdrListResult reserveCdrList(CdrListInput cdrListInput) {
        String ipAddress = StringUtils.isBlank(httpServletRequest.getHeader("x-forwarded-for")) ? httpServletRequest.getRemoteAddr() : httpServletRequest.getHeader("x-forwarded-for");
        return mediationApiService.reserveCdrList(cdrListInput, ipAddress);
    }

    @Override
    public ProcessCdrListResult chargeCdrList(ChargeCdrListInput cdrListInput) {
        String ipAddress = StringUtils.isBlank(httpServletRequest.getHeader("x-forwarded-for")) ? httpServletRequest.getRemoteAddr() : httpServletRequest.getHeader("x-forwarded-for");
        return mediationApiService.chargeCdrList(cdrListInput, ipAddress);
    }

    @Override
    public CdrDtoResponse createCDR(CdrListDtoInput dtoInput) {
        String ipAddress = StringUtils.isBlank(httpServletRequest.getHeader("x-forwarded-for")) ? httpServletRequest.getRemoteAddr() : httpServletRequest.getHeader("x-forwarded-for");
        List<CDR> cdrs = toEntities(dtoInput.getCdrs(), ipAddress);
       return mediationApiService.createCdr(cdrs, dtoInput.getMode(), dtoInput.getReturnCDRs(), dtoInput.getReturnCDRs());
    }

    @Override
    public CdrDtoResponse updateCDR(Long cdrId, CdrDtoInput cdrDto) {
        CDR toBeUpdated = mapper.toEntity(cdrDto);
         return mediationApiService.updateCDR(cdrId, toBeUpdated);
    }

    @Override
    public CdrDtoResponse updateCDRs(CdrListDtoInput dtoInput) {
        String ipAddress = StringUtils.isBlank(httpServletRequest.getHeader("x-forwarded-for")) ? httpServletRequest.getRemoteAddr() : httpServletRequest.getHeader("x-forwarded-for");
        List<CDR> listTobeUpdated = toEntities(dtoInput.getCdrs(), ipAddress);
        return mediationApiService.updateCDRs(listTobeUpdated, dtoInput.getMode(), dtoInput.getReturnCDRs(), dtoInput.getReturnCDRs());
    }
    
    private List<CDR> toEntities(List<CdrDtoInput> cdrsInput, String ipAddress){
        List<CDR> cdrs = new ArrayList<CDR>();
        for(CdrDtoInput resource: cdrsInput) {
            CDR cdr = mapper.toEntity(resource);
            cdr.setOriginBatch(ipAddress);
            cdr.setOriginRecord(cdr.toCsv().hashCode() + "");
            cdrs.add(cdr);
        }
        return cdrs;
    }

    @Override
    public ActionStatus deletCDR(Long id) {
        mediationApiService.deleteCdr(id);
        return new ActionStatus(ActionStatusEnum.SUCCESS, "");
    }

    @Override
    public CdrDtoResponse deletCDR(CdrListDtoDeletedInput cdrs) {
        return   mediationApiService.deleteCdrs(cdrs.getCdrs(), cdrs.getMode(), cdrs.getReturnCDRs(), cdrs.getReturnErrors());
    }
}

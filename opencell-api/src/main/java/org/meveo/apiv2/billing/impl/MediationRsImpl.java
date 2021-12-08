package org.meveo.apiv2.billing.impl;

import org.meveo.api.billing.MediationApi;
import org.meveo.api.dto.billing.ChargeCDRDto;
import org.meveo.api.dto.billing.ChargeCDRListResponseDto;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.apiv2.billing.resource.MediationRs;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.List;

public class MediationRsImpl extends BaseRs implements MediationRs {

    @Context
    protected HttpServletRequest httpServletRequest;

    @Inject
    private MediationApi mediationApi;

    @Override
    public ChargeCDRListResponseDto chargeCdrList(List<String> cdrs, boolean isVirtual, boolean rateTriggeredEdr, boolean returnWalletOperationDetails, Integer maxDepth,
                                                  boolean returnEDRs, boolean returnWalletOperations, boolean returnCounters) {

        try {
            ChargeCDRDto chargeCDRDto = new ChargeCDRDto(cdrs, httpServletRequest.getRemoteAddr(), isVirtual,
                    rateTriggeredEdr, returnWalletOperationDetails, maxDepth, returnEDRs, returnWalletOperations, returnCounters);
            return mediationApi.chargeCdrList(chargeCDRDto);
        } catch (Exception e) {
            ChargeCDRListResponseDto result = new ChargeCDRListResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }
}

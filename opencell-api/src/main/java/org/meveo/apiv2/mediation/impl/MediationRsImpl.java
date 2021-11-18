package org.meveo.apiv2.mediation.impl;

import org.meveo.api.billing.MediationApi;
import org.meveo.api.dto.billing.ChargeCDRDto;
import org.meveo.api.dto.billing.ChargeCDRListResponseDto;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.apiv2.mediation.MediationRs;

import javax.inject.Inject;
import java.util.List;

public class MediationRsImpl extends BaseRs implements MediationRs {

    @Inject
    private MediationApi mediationApi;

    @Override
    public ChargeCDRListResponseDto chargeCdrList(List<String> cdrs, boolean isVirtual, boolean rateTriggeredEdr, boolean returnWalletOperations, Integer maxDepth) {

        try {
            ChargeCDRDto chargeCDRDto = new ChargeCDRDto(cdrs, httpServletRequest.getRemoteAddr(), isVirtual, rateTriggeredEdr, returnWalletOperations, maxDepth);
            return mediationApi.chargeCdrList(chargeCDRDto);
        } catch (Exception e) {
            ChargeCDRListResponseDto result = new ChargeCDRListResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }
}

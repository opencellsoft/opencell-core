package org.meveo.api.rest.catalog.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.catalog.ChannelApi;
import org.meveo.api.catalog.OfferTemplateCategoryApi;
import org.meveo.api.dto.response.catalog.GetListChannelsAndSegmentsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.catalog.ChannelsAndSegmentsRs;
import org.meveo.api.rest.impl.BaseRs;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class ChannelsAndSegmentsRsImpl extends BaseRs implements ChannelsAndSegmentsRs {

    @Inject
    private ChannelApi channelApi;

    @Inject
    private OfferTemplateCategoryApi offerTemplateCategoryApi;

    @Override
    public GetListChannelsAndSegmentsResponseDto list(Boolean active) {
        GetListChannelsAndSegmentsResponseDto result = new GetListChannelsAndSegmentsResponseDto();

        try {
            result.setChannels(channelApi.list(active));
            result.setSegments(offerTemplateCategoryApi.list(active));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}
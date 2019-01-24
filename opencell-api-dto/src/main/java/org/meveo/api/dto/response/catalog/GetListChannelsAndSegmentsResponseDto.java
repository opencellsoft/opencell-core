package org.meveo.api.dto.response.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.catalog.OfferTemplateCategoryDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Youssef IZEM
 * @lastModifiedVersion 5.4
 */
@XmlRootElement(name = "GetChannelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListChannelsAndSegmentsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7907466519449995575L;

    /** The channel list. */
    private List<ChannelDto> channels;

    /** The segment list. */
    private List<OfferTemplateCategoryDto> segments;

    /**
     * @return channel list
     */
    public List<ChannelDto> getChannels() {
        return channels;
    }

    /**
     * @param channel list
     */
    public void setChannels(List<ChannelDto> channels) {
        this.channels = channels;
    }

    /**
     * @return segment list
     */
    public List<OfferTemplateCategoryDto> getSegments() {
        return segments;
    }

    /**
     * @param segment list
     */
    public void setSegments(List<OfferTemplateCategoryDto> segments) {
        this.segments = segments;
    }

}
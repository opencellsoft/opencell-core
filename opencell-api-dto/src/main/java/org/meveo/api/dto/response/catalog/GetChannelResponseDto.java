package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetChannelResponseDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "GetChannelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetChannelResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7907466519449995575L;

    /** The channel. */
    private ChannelDto channel;

    /**
     * Gets the channel.
     *
     * @return the channel
     */
    public ChannelDto getChannel() {
        return channel;
    }

    /**
     * Sets the channel.
     *
     * @param channel the new channel
     */
    public void setChannel(ChannelDto channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "GetChannelResponseDto [channel=" + channel + ", toString()=" + super.toString() + "]";
    }
}
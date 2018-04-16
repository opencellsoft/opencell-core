package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.catalog.Channel;

/**
 * The Class ChannelDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "Channel")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChannelDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new channel dto.
     */
    public ChannelDto() {
    }

    /**
     * Instantiates a new channel dto.
     *
     * @param channel the channel
     */
    public ChannelDto(Channel channel) {
        super(channel);
    }

    @Override
    public String toString() {
        return "ChannelDto [id=" + id + ", code=" + code + ", description=" + description + ", updatedCode=" + updatedCode + "]";
    }
}
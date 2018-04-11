package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.catalog.Channel;

@XmlRootElement(name = "Channel")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChannelDto extends EnableBusinessDto {

    private static final long serialVersionUID = 1L;

    public ChannelDto() {
    }

    public ChannelDto(Channel channel) {
        super(channel);
    }
}
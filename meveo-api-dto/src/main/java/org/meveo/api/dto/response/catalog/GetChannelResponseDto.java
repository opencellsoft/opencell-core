package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.ChannelDto;
import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "GetChannelResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetChannelResponseDto extends BaseResponse {

	private static final long serialVersionUID = -7907466519449995575L;

	private ChannelDto channel;

	public ChannelDto getChannel() {
		return channel;
	}

	public void setChannel(ChannelDto channel) {
		this.channel = channel;
	}

	@Override
	public String toString() {
		return "GetChannelResponseDto [channel=" + channel + ", toString()=" + super.toString() + "]";
	}

}

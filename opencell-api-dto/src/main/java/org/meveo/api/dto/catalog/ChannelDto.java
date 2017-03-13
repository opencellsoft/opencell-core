package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.catalog.Channel;

@XmlRootElement(name = "Channel")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChannelDto extends BusinessDto {

		private static final long serialVersionUID = 1L;
		
		@XmlAttribute(required = true)
		private String code;
		
		@XmlAttribute()
		private String description;

		public ChannelDto() {
		}
		
		public ChannelDto(Channel channel) {
			if(channel!=null){
				this.code=channel.getCode();
				this.description=channel.getDescription();
			}
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
}

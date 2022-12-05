package org.meveo.api.dto.response.cpq;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.cpq.MediaDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author TARIK FA.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetMediaDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetMediaDtoResponse extends BaseResponse{

	/**
	 * Media data
	 */
	private MediaDto mediaDto;

	/**
	 * @return the mediaDto
	 */
	public MediaDto getMediaDto() {
		return mediaDto;
	}

	/**
	 * @param mediaDto the mediaDto to set
	 */
	public void setMediaDto(MediaDto mediaDto) {
		this.mediaDto = mediaDto;
	}
	

	
	
	
}

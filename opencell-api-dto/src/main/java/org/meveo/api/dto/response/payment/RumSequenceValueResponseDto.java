package org.meveo.api.dto.response.payment;

import org.meveo.api.dto.payment.RumSequenceDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * RUM Sequence value response DTO representation.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
public class RumSequenceValueResponseDto extends BaseResponse {

	private static final long serialVersionUID = -8029811611595876971L;

	private RumSequenceDto rumSequenceDto;
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public RumSequenceDto getRumSequenceDto() {
		return rumSequenceDto;
	}

	public void setRumSequenceDto(RumSequenceDto rumSequenceDto) {
		this.rumSequenceDto = rumSequenceDto;
	}

}

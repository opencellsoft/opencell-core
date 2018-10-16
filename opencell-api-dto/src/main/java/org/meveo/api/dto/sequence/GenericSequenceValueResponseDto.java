package org.meveo.api.dto.sequence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * Sequence value response DTO representation.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@XmlRootElement(name = "GenericSequenceValueResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericSequenceValueResponseDto extends BaseResponse {

	private static final long serialVersionUID = -8029811611595876971L;

	private GenericSequenceDto sequence;
	private String value;
	private String seller;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public GenericSequenceDto getSequence() {
		return sequence;
	}

	public void setSequence(GenericSequenceDto sequence) {
		this.sequence = sequence;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

}

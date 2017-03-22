package org.meveo.api.dto.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.GenerateInvoiceResultDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GenerateInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceResponseDto extends BaseResponse {

	private static final long serialVersionUID = 5811304676103235597L;
	
	private GenerateInvoiceResultDto generateInvoiceResultDto;
	
	public GenerateInvoiceResponseDto(){
		
	}

	/**
	 * @return the generateInvoiceResultDto
	 */
	public GenerateInvoiceResultDto getGenerateInvoiceResultDto() {
		return generateInvoiceResultDto;
	}

	/**
	 * @param generateInvoiceResultDto the generateInvoiceResultDto to set
	 */
	public void setGenerateInvoiceResultDto(GenerateInvoiceResultDto generateInvoiceResultDto) {
		this.generateInvoiceResultDto = generateInvoiceResultDto;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GenerateInvoiceResponseDto [generateInvoiceResultDto=" + generateInvoiceResultDto + "]";
	}
}

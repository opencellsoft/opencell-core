package org.meveo.api.dto.invoice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.billing.GenerateInvoiceResultDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GenerateInvoiceResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GenerateInvoiceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5811304676103235597L;

    /** The generate invoice result dto. */
    private GenerateInvoiceResultDto generateInvoiceResultDto;

    /**
     * Instantiates a new generate invoice response dto.
     */
    public GenerateInvoiceResponseDto() {

    }

    /**
     * Gets the generate invoice result dto.
     *
     * @return the generateInvoiceResultDto
     */
    public GenerateInvoiceResultDto getGenerateInvoiceResultDto() {
        return generateInvoiceResultDto;
    }

    /**
     * Sets the generate invoice result dto.
     *
     * @param generateInvoiceResultDto the generateInvoiceResultDto to set
     */
    public void setGenerateInvoiceResultDto(GenerateInvoiceResultDto generateInvoiceResultDto) {
        this.generateInvoiceResultDto = generateInvoiceResultDto;
    }

    /**
     * Gets the serialversionuid.
     *
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "GenerateInvoiceResponseDto [generateInvoiceResultDto=" + generateInvoiceResultDto + "]";
    }
}
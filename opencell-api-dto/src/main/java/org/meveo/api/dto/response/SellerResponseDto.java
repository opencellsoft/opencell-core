package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.SellersDto;

/**
 * The Class SellerResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "SellerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class SellerResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6134470575443721802L;

    /** The sellers. */
    private SellersDto sellers = new SellersDto();

    /**
     * Gets the sellers.
     *
     * @return the sellers
     */
    public SellersDto getSellers() {
        return sellers;
    }

    /**
     * Sets the sellers.
     *
     * @param sellers the new sellers
     */
    public void setSellers(SellersDto sellers) {
        this.sellers = sellers;
    }

    @Override
    public String toString() {
        return "ListSellerResponseDto [sellers=" + sellers + ", toString()=" + super.toString() + "]";
    }
}
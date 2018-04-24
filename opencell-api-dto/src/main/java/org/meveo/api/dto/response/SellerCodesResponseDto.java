package org.meveo.api.dto.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class SellerCodesResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "SellerCodesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class SellerCodesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 386494190197359162L;

    /** The seller codes. */
    private List<String> sellerCodes;

    /**
     * Gets the seller codes.
     *
     * @return the seller codes
     */
    public List<String> getSellerCodes() {
        if (sellerCodes == null) {
            sellerCodes = new ArrayList<String>();
        }
        return sellerCodes;
    }

    /**
     * Sets the seller codes.
     *
     * @param sellerCodes the new seller codes
     */
    public void setSellerCodes(List<String> sellerCodes) {
        this.sellerCodes = sellerCodes;
    }

    @Override
    public String toString() {
        return "ListSellerCodesResponseDto [sellerCodes=" + sellerCodes + "]";
    }
}
package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.SellerDto;

/**
 * The Class GetSellerResponse.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetSellerResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetSellerResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1927118061401041786L;

    /** The seller. */
    private SellerDto seller;

    /**
     * Instantiates a new gets the seller response.
     */
    public GetSellerResponse() {
        super();
    }

    /**
     * Gets the seller.
     *
     * @return the seller
     */
    public SellerDto getSeller() {
        return seller;
    }

    /**
     * Sets the seller.
     *
     * @param seller the new seller
     */
    public void setSeller(SellerDto seller) {
        this.seller = seller;
    }

    @Override
    public String toString() {
        return "GetSellerResponse [seller=" + seller + ", toString()=" + super.toString() + "]";
    }
}
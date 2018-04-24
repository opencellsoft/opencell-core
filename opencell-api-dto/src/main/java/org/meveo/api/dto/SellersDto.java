package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class SellersDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SellersDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2924035308389476982L;

    /** The seller. */
    private List<SellerDto> seller;

    /**
     * Gets the seller.
     *
     * @return the seller
     */
    public List<SellerDto> getSeller() {
        if (seller == null) {
            seller = new ArrayList<SellerDto>();
        }

        return seller;
    }

    /**
     * Sets the seller.
     *
     * @param seller the new seller
     */
    public void setSeller(List<SellerDto> seller) {
        this.seller = seller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SellersDto [seller=" + seller + "]";
    }

}

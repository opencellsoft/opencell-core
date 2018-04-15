package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class PricePlansDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlansDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4354099345909112263L;

    /** The price plan matrix. */
    private List<PricePlanMatrixDto> pricePlanMatrix;

    /**
     * Gets the price plan matrix.
     *
     * @return the price plan matrix
     */
    public List<PricePlanMatrixDto> getPricePlanMatrix() {
        return pricePlanMatrix;
    }

    /**
     * Sets the price plan matrix.
     *
     * @param pricePlanMatrix the new price plan matrix
     */
    public void setPricePlanMatrix(List<PricePlanMatrixDto> pricePlanMatrix) {
        this.pricePlanMatrix = pricePlanMatrix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PricePlansDto [pricePlanMatrix=" + pricePlanMatrix + "]";
    }

}

package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class TaxesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Taxes")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2521572799898861284L;

    /** The tax. */
    private List<TaxDto> tax;

    /**
     * Gets the tax.
     *
     * @return the tax
     */
    public List<TaxDto> getTax() {
        if (tax == null)
            tax = new ArrayList<TaxDto>();
        return tax;
    }

    /**
     * Sets the tax.
     *
     * @param tax the new tax
     */
    public void setTax(List<TaxDto> tax) {
        this.tax = tax;
    }

    @Override
    public String toString() {
        return "TaxesDto [tax=" + tax + "]";
    }
}
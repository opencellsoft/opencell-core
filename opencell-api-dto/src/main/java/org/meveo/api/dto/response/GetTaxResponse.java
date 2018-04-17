package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.TaxDto;

/**
 * The Class GetTaxResponse.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetTaxResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTaxResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1336652304727158329L;

    /** The tax. */
    private TaxDto tax;

    /**
     * Gets the tax.
     *
     * @return the tax
     */
    public TaxDto getTax() {
        return tax;
    }

    /**
     * Sets the tax.
     *
     * @param tax the new tax
     */
    public void setTax(TaxDto tax) {
        this.tax = tax;
    }

    @Override
    public String toString() {
        return "GetTaxResponse [tax=" + tax + ", toString()=" + super.toString() + "]";
    }
}

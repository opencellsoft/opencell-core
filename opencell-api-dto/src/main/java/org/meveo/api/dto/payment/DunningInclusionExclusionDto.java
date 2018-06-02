package org.meveo.api.dto.payment;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class DunningInclusionExclusionDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "dunningInclusionExclusion")
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningInclusionExclusionDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4329241417200680028L;

    /** The invoice references. */
    private List<String> invoiceReferences;
    
    /** The exclude. */
    private Boolean exclude;

    /**
     * Gets the invoice references.
     *
     * @return the invoice references
     */
    public List<String> getInvoiceReferences() {
        return invoiceReferences;
    }

    /**
     * Sets the invoice references.
     *
     * @param invoiceReferences the new invoice references
     */
    public void setInvoiceReferences(List<String> invoiceReferences) {
        this.invoiceReferences = invoiceReferences;
    }

    /**
     * Gets the exclude.
     *
     * @return the exclude
     */
    public Boolean getExclude() {
        return exclude;
    }

    /**
     * Sets the exclude.
     *
     * @param exclude the new exclude
     */
    public void setExclude(Boolean exclude) {
        this.exclude = exclude;
    }

}
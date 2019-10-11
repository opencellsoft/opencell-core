package org.meveo.admin.async;

import java.io.Serializable;
import java.math.BigDecimal;

import org.meveo.model.billing.Amounts;

/**
 * Amounts to invoice for a given billable entity
 * 
 * @author Andrius Karpavicius
 */
public class AmountsToInvoice implements Serializable {

    private static final long serialVersionUID = 3109687834951882877L;

    /**
     * ID of an entity to invoice
     */
    private Long entityToInvoiceId;

    /**
     * Amounts to invoice
     */
    private Amounts amountsToInvoice;

    /**
     * Constructor
     */
    public AmountsToInvoice() {
    }

    /**
     * Constructor
     * 
     * @param entityToInvoiceId ID of an entity to invoice
     * @param amountsToInvoice Amounts to invoice
     */
    public AmountsToInvoice(Long entityToInvoiceId, BigDecimal amountWithoutTax, BigDecimal amountWithTax, BigDecimal amountTax) {
        this.entityToInvoiceId = entityToInvoiceId;
        this.amountsToInvoice = new Amounts(amountWithoutTax, amountWithTax, amountTax);
    }

    /**
     * Constructor
     * 
     * @param entityToInvoiceId ID of an entity to invoice
     * @param amountsToInvoice Amounts to invoice
     */
    public AmountsToInvoice(Long entityToInvoiceId, Amounts amountsToInvoice) {
        this.entityToInvoiceId = entityToInvoiceId;
        this.amountsToInvoice = amountsToInvoice;
    }

    /**
     * @return ID of an entity to invoice
     */
    public Long getEntityToInvoiceId() {
        return entityToInvoiceId;
    }

    /**
     * @param entityToInvoiceId ID of an entity to invoice
     */
    public void setEntityToInvoiceId(Long entityToInvoiceId) {
        this.entityToInvoiceId = entityToInvoiceId;
    }

    /**
     * @return Amounts to invoice
     */
    public Amounts getAmountsToInvoice() {
        return amountsToInvoice;
    }

    /**
     * @param amountsToInvoice Amounts to invoice
     */
    public void setAmountsToInvoice(Amounts amountsToInvoice) {
        this.amountsToInvoice = amountsToInvoice;
    }
}
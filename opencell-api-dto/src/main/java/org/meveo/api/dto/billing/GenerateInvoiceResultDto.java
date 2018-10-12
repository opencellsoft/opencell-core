package org.meveo.api.dto.billing;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.invoice.InvoiceDto;
import org.meveo.model.billing.Invoice;

/**
 * Invoice generate request DTO
 */
@XmlRootElement(name = "GenerateInvoiceResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceResultDto extends InvoiceDto {

    private static final long serialVersionUID = 3593774505433626017L;

    /** The temporary invoice number. */
    private String temporaryInvoiceNumber;

    /** The invoice type code. */
    private String invoiceTypeCode;

    /** The amount. */
    private BigDecimal amount;

    /** The account operation id. */
    private Long accountOperationId;

    /**
     * Instantiates a new generate invoice response dto
     */
    public GenerateInvoiceResultDto() {
        super();
    }

    /**
     * Instantiates a new generate invoice response dto. Note: does not fill in XML and PDF information
     * 
     * @param invoice Invoice
     * @param includeTransactions Should Rated transactions be detailed in subcategory aggregate level
     */
    public GenerateInvoiceResultDto(Invoice invoice, boolean includeTransactions) {
        super(invoice, includeTransactions);
        this.temporaryInvoiceNumber = invoice.getTemporaryInvoiceNumber();
        this.invoiceTypeCode = invoice.getInvoiceType().getCode();
        this.amount = invoice.getAmount();
        if (invoice.getRecordedInvoice() != null) {
            accountOperationId = invoice.getRecordedInvoice().getId();
        }
    }

    /**
     * Gets the temporary invoice number.
     *
     * @return the temporary invoice number
     */
    public String getTemporaryInvoiceNumber() {
        return temporaryInvoiceNumber;
    }

    /**
     * Sets the temporary invoice number.
     *
     * @param temporaryInvoiceNumber the new temporary invoice number
     */
    public void setTemporaryInvoiceNumber(String temporaryInvoiceNumber) {
        this.temporaryInvoiceNumber = temporaryInvoiceNumber;
    }

    /**
     * Gets the invoice type code.
     *
     * @return the invoice type code
     */
    public String getInvoiceTypeCode() {
        return invoiceTypeCode;
    }

    /**
     * Sets the invoice type code.
     *
     * @param invoiceTypeCode the new invoice type code
     */
    public void setInvoiceTypeCode(String invoiceTypeCode) {
        this.invoiceTypeCode = invoiceTypeCode;
    }

    /**
     * Gets the amount.
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     *
     * @param amount the new amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the account operation id.
     *
     * @return the account operation id
     */
    public Long getAccountOperationId() {
        return accountOperationId;
    }

    /**
     * Sets the account operation id.
     *
     * @param accountOperationId the new account operation id
     */
    public void setAccountOperationId(Long accountOperationId) {
        this.accountOperationId = accountOperationId;
    }

    @Override
    public String toString() {
        return "GenerateInvoiceResultDto [invoiceNumber=" + (invoiceNumber != null ? invoiceNumber : temporaryInvoiceNumber) + " ,invoiceId:" + invoiceId + "]";
    }
}
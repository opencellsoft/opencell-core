package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;

@XmlRootElement(name = "GenerateInvoiceResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class GenerateInvoiceResultDto {

	private Long invoiceId;
	 
    private String invoiceNumber;

    private String temporaryInvoiceNumber;

    private String invoiceTypeCode;

    private BigDecimal amount;

    private BigDecimal amountWithoutTax;

    private BigDecimal amountWithTax;

    private BigDecimal amountTax;

    private byte[] pdf;
    
    private Long accountOperationId;
    
    private BigDecimal discount;
    
    @XmlElementWrapper(name = "discountAggregates")
    @XmlElement(name = "discountAggregate")
    private List<SubCategoryInvoiceAgregateDto> discountAggregates = new ArrayList<>();

    /**
     * @return the invoiceNumber
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * @param invoiceNumber the invoiceNumber to set
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getTemporaryInvoiceNumber() {
        return temporaryInvoiceNumber;
    }

    public void setTemporaryInvoiceNumber(String temporaryInvoiceNumber) {
        this.temporaryInvoiceNumber = temporaryInvoiceNumber;
    }

    public String getInvoiceTypeCode() {
        return invoiceTypeCode;
    }

    public void setInvoiceTypeCode(String invoiceTypeCode) {
        this.invoiceTypeCode = invoiceTypeCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    /**
     * @return the pdf
     */
    public byte[] getPdf() {
        return pdf;
    }

    /**
     * @param pdf the pdf to set
     */
    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }
    


	public Long getAccountOperationId() {
		return accountOperationId;
	}

	public void setAccountOperationId(Long accountOperationId) {
		this.accountOperationId = accountOperationId;
	}
	
	

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	/*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GenerateInvoiceResultDto [invoiceNumber=" + (invoiceNumber != null ? invoiceNumber : temporaryInvoiceNumber) + " ,invoiceId:"+invoiceId+"]";
    }

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public List<SubCategoryInvoiceAgregateDto> getDiscountAggregates() {
		return discountAggregates;
	}

	public void setDiscountAggregates(List<SubCategoryInvoiceAgregateDto> discountAggregates) {
		this.discountAggregates = discountAggregates;
	}
}
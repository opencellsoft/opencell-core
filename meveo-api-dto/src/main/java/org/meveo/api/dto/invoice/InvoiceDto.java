package org.meveo.api.dto.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CategoryInvoiceAgregateDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.SubCategoryInvoiceAgregateDto;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceModeEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * @author R.AITYAAZZA
 * 
 */
@XmlRootElement(name = "Invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceDto extends BaseDto {

    private static final long serialVersionUID = 1072382628068718580L;

    @XmlElement(required = true)
    private Long invoiceId;
    
    @XmlElement(required = true)
    private String invoiceType;
    
    @XmlElement(required = true)
    private String billingAccountCode;

    @XmlElement(required = true)
    private Date dueDate;
    
    @XmlElement(required = true)
    private Date invoiceDate;
    
	@XmlElementWrapper
    @XmlElement(name="categoryInvoiceAgregate",required = true)
    private List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregateDto>();
	
	@XmlElementWrapper
    @XmlElement(name="invoiceIdToLink")
    private List<Long> listInvoiceIdToLink= new ArrayList<Long>();
	
    private String invoiceNumber;
    

    private BigDecimal discount;
    private BigDecimal amountWithoutTax;
    private BigDecimal amountTax;
    private BigDecimal amountWithTax;
    private PaymentMethodEnum paymentMethod;
    private boolean pdfPresent;

    private byte[] pdf;
    

    
    
    private boolean autoValidation =  true;
    private boolean returnXml = false;
    private boolean returnPdf = false;
    private boolean includeBalance = false;
    
    @XmlElement(required = true)
    private InvoiceModeEnum invoiceMode;
    
    
    

    private CustomFieldsDto customFields = new CustomFieldsDto();
    
    
    
    public InvoiceDto() {
    }

    public InvoiceDto(Invoice invoice) {
        super();
        this.setInvoiceId(invoice.getId());
        this.setBillingAccountCode(invoice.getBillingAccount().getCode());
        this.setInvoiceDate(invoice.getInvoiceDate());
        this.setDueDate(invoice.getDueDate());

        this.setAmountWithoutTax(invoice.getAmountWithoutTax());
        this.setAmountTax(invoice.getAmountTax());
        this.setAmountWithTax(invoice.getAmountWithTax());
        this.setInvoiceNumber(invoice.getInvoiceNumber());
        this.setPaymentMethod(invoice.getPaymentMethod());
        this.setPdfPresent(invoice.getPdf() != null);
        this.setInvoiceType(invoice.getInvoiceType().getCode());
        
        SubCategoryInvoiceAgregateDto subCategoryInvoiceAgregateDto = null;
        CategoryInvoiceAgregateDto  categoryInvoiceAgregateDto = new CategoryInvoiceAgregateDto();
        
        for (InvoiceAgregate invoiceAgregate : invoice.getInvoiceAgregates()) {

            subCategoryInvoiceAgregateDto = new SubCategoryInvoiceAgregateDto();

            if (invoiceAgregate instanceof CategoryInvoiceAgregate) {            	
                subCategoryInvoiceAgregateDto.setType("R");               
                categoryInvoiceAgregateDto.setCategoryInvoiceCode(((CategoryInvoiceAgregate) invoiceAgregate).getInvoiceCategory().getCode());
            } else if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
                subCategoryInvoiceAgregateDto.setType("F");
            } else if (invoiceAgregate instanceof TaxInvoiceAgregate) {
                subCategoryInvoiceAgregateDto.setType("T");
            }

            subCategoryInvoiceAgregateDto.setItemNumber(invoiceAgregate.getItemNumber());
            subCategoryInvoiceAgregateDto.setAccountingCode(invoiceAgregate.getAccountingCode());
            subCategoryInvoiceAgregateDto.setDescription(invoiceAgregate.getDescription());
            subCategoryInvoiceAgregateDto.setQuantity(invoiceAgregate.getQuantity());
            subCategoryInvoiceAgregateDto.setDiscount(invoiceAgregate.getDiscount());
            subCategoryInvoiceAgregateDto.setAmountWithoutTax(invoiceAgregate.getAmountWithoutTax());
            subCategoryInvoiceAgregateDto.setAmountTax(invoiceAgregate.getAmountTax());
            subCategoryInvoiceAgregateDto.setAmountWithTax(invoiceAgregate.getAmountWithTax());
            
            categoryInvoiceAgregateDto.getListSubCategoryInvoiceAgregateDto().add(subCategoryInvoiceAgregateDto);
            
            boolean agregateAlreadyExists = false;
            for(CategoryInvoiceAgregateDto ciadto : this.getCategoryInvoiceAgregates()) {
        		if(ciadto.getCategoryInvoiceCode() != null  
        				&&  ciadto.getCategoryInvoiceCode().equals(categoryInvoiceAgregateDto.getCategoryInvoiceCode())) {
        			agregateAlreadyExists = true;
        			break;
        		}
        	}
            
            if(!agregateAlreadyExists) {
            	this.getCategoryInvoiceAgregates().add(categoryInvoiceAgregateDto);
        	}
        }

    }

  


	/**
	 * @return the listInvoiceIdToLink
	 */
	public List<Long> getListInvoiceIdToLink() {
		return listInvoiceIdToLink;
	}

	/**
	 * @param listInvoiceIdToLink the listInvoiceIdToLink to set
	 */
	public void setListInvoiceIdToLink(List<Long> listInvoiceIdToLink) {
		this.listInvoiceIdToLink = listInvoiceIdToLink;
	}

	public String getBillingAccountCode() {
        return billingAccountCode;
    }

    public void setBillingAccountCode(String billingAccountCode) {
        this.billingAccountCode = billingAccountCode;
    }

    public Date getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountTax() {
        return amountTax;
    }

    public void setAmountTax(BigDecimal amountTax) {
        this.amountTax = amountTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public boolean isPdfPresent() {
        return pdfPresent;
    }

    public void setPdfPresent(boolean pDFpresent) {
        pdfPresent = pDFpresent;
    }

    public PaymentMethodEnum getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * @return the invoiceId
     */
    public Long getInvoiceId() {
		return invoiceId;
	}
    
    /**
     * @param invoiceId
     */
    public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

    /**
	 * @return the invoiceType
	 */
	public String getInvoiceType() {
		return invoiceType;
	}

	/**
	 * @param invoiceType the invoiceType to set
	 */
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}


    public byte[] getPdf() {
        return pdf;
    }

    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    
    /**
	 * @return the customFields
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	/**
	 * @param customFields the customFields to set
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}

	/**
	 * @return the categoryInvoiceAgregates
	 */
	public List<CategoryInvoiceAgregateDto> getCategoryInvoiceAgregates() {
		return categoryInvoiceAgregates;
	}

	/**
	 * @param categoryInvoiceAgregates the categoryInvoiceAgregates to set
	 */
	public void setCategoryInvoiceAgregates(List<CategoryInvoiceAgregateDto> categoryInvoiceAgregates) {
		this.categoryInvoiceAgregates = categoryInvoiceAgregates;
	}
	
	
	

	/**
	 * @return the autoValidation
	 */
	public boolean isAutoValidation() {
		return autoValidation;
	}

	/**
	 * @param autoValidation the autoValidation to set
	 */
	public void setAutoValidation(boolean autoValidation) {
		this.autoValidation = autoValidation;
	}

	/**
	 * @return the returnXml
	 */
	public boolean isReturnXml() {
		return returnXml;
	}

	/**
	 * @param returnXml the returnXml to set
	 */
	public void setReturnXml(boolean returnXml) {
		this.returnXml = returnXml;
	}

	/**
	 * @return the returnPdf
	 */
	public boolean isReturnPdf() {
		return returnPdf;
	}

	/**
	 * @param returnPdf the returnPdf to set
	 */
	public void setReturnPdf(boolean returnPdf) {
		this.returnPdf = returnPdf;
	}

	/**
	 * @return the invoiceMode
	 */
	public InvoiceModeEnum getInvoiceMode() {
		return invoiceMode;
	}

	/**
	 * @param invoiceMode the invoiceMode to set
	 */
	public void setInvoiceMode(InvoiceModeEnum invoiceMode) {
		this.invoiceMode = invoiceMode;
	}

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

	/**
	 * @return the includeBalance
	 */
	public boolean isIncludeBalance() {
		return includeBalance;
	}

	/**
	 * @param includeBalance the includeBalance to set
	 */
	public void setIncludeBalance(boolean includeBalance) {
		this.includeBalance = includeBalance;
	}

	
}

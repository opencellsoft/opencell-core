package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;

/**
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "OCCTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OccTemplateDto extends BusinessDto {

    private static final long serialVersionUID = 2587489734648000805L;

    @XmlElement(required = true)
    private String accountingCode;
    
    @Deprecated
    private String accountCode;

    @XmlElement(required = true)
    private OperationCategoryEnum occCategory;

    @Deprecated
    private String accountCodeClientSide;

    public OccTemplateDto() {
    }

    public OccTemplateDto(OCCTemplate e) {
        super(e);
        accountingCode = e.getAccountingCode().getCode();
        accountCode = e.getAccountingCode().getCode();
        occCategory = e.getOccCategory();
        accountCodeClientSide = e.getAccountCodeClientSide();
    }

    public OperationCategoryEnum getOccCategory() {
        return occCategory;
    }

    public void setOccCategory(OperationCategoryEnum occCategory) {
        this.occCategory = occCategory;
    }

    public String getAccountCodeClientSide() {
        return accountCodeClientSide;
    }

    public void setAccountCodeClientSide(String accountCodeClientSide) {
        this.accountCodeClientSide = accountCodeClientSide;
    }

    public String getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    @Override
    public String toString() {
        return "OccTemplateDto [accountingCode=" + accountingCode + ", occCategory=" + occCategory + ", accountCodeClientSide=" + accountCodeClientSide + "]";
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }
}
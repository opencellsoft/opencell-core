package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;

@XmlRootElement(name = "OCCTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OccTemplateDto extends BusinessDto {

    private static final long serialVersionUID = 2587489734648000805L;

    @XmlElement(required = true)
    private String accountCode;

    @XmlElement(required = true)
    private OperationCategoryEnum occCategory;

    private String accountCodeClientSide;

    public OccTemplateDto() {
    }

    public OccTemplateDto(OCCTemplate e) {
        super(e);
        accountCode = e.getAccountCode();
        occCategory = e.getOccCategory();
        accountCodeClientSide = e.getAccountCodeClientSide();
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
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

    @Override
    public String toString() {
        return "OCCTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", accountCode=" + accountCode + ", occCategory=" + occCategory + ", accountCodeClientSide="
                + accountCodeClientSide + "]";
    }
}
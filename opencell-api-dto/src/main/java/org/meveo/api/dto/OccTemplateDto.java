package org.meveo.api.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OperationCategoryEnum;

@XmlRootElement(name = "OCCTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class OccTemplateDto extends BusinessDto {

    private static final long serialVersionUID = 2587489734648000805L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    private String description;

    @XmlElement(required = true)
    private String accountCode;

    @XmlElement(required = true)
    private OperationCategoryEnum occCategory;

    private String accountCodeClientSide;

    public OccTemplateDto() {
    }

    public OccTemplateDto(OCCTemplate e) {
        code = e.getCode();
        description = e.getDescription();
        accountCode = e.getAccountCode();
        occCategory = e.getOccCategory();
        accountCodeClientSide = e.getAccountCodeClientSide();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "OCCTemplateDto [code=" + code + ", description=" + description + ", accountCode=" + accountCode + ", occCategory=" + occCategory + ", accountCodeClientSide="
                + accountCodeClientSide + "]";
    }
}
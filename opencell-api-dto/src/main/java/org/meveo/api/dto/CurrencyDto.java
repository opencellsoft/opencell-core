package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;

/**
 * @author Edward P. Legaspi
 * 
 **/
@XmlRootElement(name = "Currency")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyDto extends BaseDto implements IEnableDto {

    private static final long serialVersionUID = 9143645109603442839L;

    /**
     * Currency code
     */
    @XmlAttribute(required = true)
    private String code;

    /**
     * Description
     */
    private String description;

    /**
     * Is entity disabled. Value is ignored in Update action - use enable/disable API instead.
     */
    private Boolean disabled;

    public CurrencyDto() {

    }

    public CurrencyDto(TradingCurrency e) {
        code = e.getCurrencyCode();
        description = e.getPrDescription();
        disabled = e.isDisabled();
    }

    public CurrencyDto(Currency e) {
        code = e.getCurrencyCode();
        description = e.getDescriptionEn();
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

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean isDisabled() {
        return disabled;
    }

    @Override
    public String toString() {
        return "CurrencyDto [code=" + code + ", description=" + description + ", disabled=" + disabled + "]";
    }
}
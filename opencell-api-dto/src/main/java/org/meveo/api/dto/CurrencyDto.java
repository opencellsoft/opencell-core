package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;

/**
 * The Class CurrencyDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Currency")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyDto extends AuditableEntityDto implements IEnableDto {

    /** The Constant serialVersionUID. */
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

    /**
     * Instantiates a new currency dto.
     */
    public CurrencyDto() {

    }

    /**
     * Instantiates a new currency dto.
     *
     * @param tradingCurrency the trading currency
     */
    public CurrencyDto(TradingCurrency tradingCurrency) {
        super(tradingCurrency);
        code = tradingCurrency.getCurrencyCode();
        description = tradingCurrency.getPrDescription();
        disabled = tradingCurrency.isDisabled();
    }

    /**
     * Instantiates a new currency dto.
     *
     * @param currency the currency
     */
    public CurrencyDto(Currency currency) {
        code = currency.getCurrencyCode();
        description = currency.getDescriptionEn();
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }

    @Override
    public String toString() {
        return "CurrencyDto [code=" + code + ", description=" + description + ", disabled=" + disabled + "]";
    }
}
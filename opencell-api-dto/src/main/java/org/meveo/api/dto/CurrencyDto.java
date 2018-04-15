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
 * @deprecated will be renammed to TradingCurrencyDto
 */
@XmlRootElement(name = "Currency")
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9143645109603442839L;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The description. */
    private String description;

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
        code = tradingCurrency.getCurrencyCode();
        description = tradingCurrency.getPrDescription();
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CurrencyDto [code=" + code + ", description=" + description + "]";
    }

}

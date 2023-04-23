package org.meveo.api.dto.cpq.xml;

import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {
    @XmlAttribute
    private String currency;
    @XmlAttribute
    private String lang;
    protected Name name;
    protected Address address;

    public Customer(org.meveo.model.crm.Customer customer, TradingCurrency currency, TradingLanguage lang) {
        this.name = new Name(customer.getName());
        this.address = new Address(customer.getAddress());
        this.currency = currency != null ? currency.getCurrencyCode() : StringUtils.EMPTY;
        this.lang = lang != null ? lang.getLanguageCode() : StringUtils.EMPTY;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}

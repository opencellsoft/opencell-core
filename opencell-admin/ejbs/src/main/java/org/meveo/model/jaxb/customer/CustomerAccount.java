/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.01 at 08:25:37 PM WET 
//


package org.meveo.model.jaxb.customer;








import org.meveo.model.jaxb.account.Address;
import org.meveo.model.jaxb.account.BankCoordinates;
import org.meveo.model.jaxb.account.BillingAccounts;
import org.meveo.model.jaxb.account.Name;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}code"/&gt;
 *         &lt;element ref="{}description"/&gt;
 *         &lt;element ref="{}externalRef1"/&gt;
 *         &lt;element ref="{}externalRef2"/&gt;
 *         &lt;element ref="{}company"/&gt;
 *         &lt;element ref="{}name"/&gt;
 *         &lt;element ref="{}address"/&gt;
 *         &lt;element ref="{}paymentMethod"/&gt;
 *         &lt;element ref="{}email"/&gt;
 *         &lt;element ref="{}tel1"/&gt;
 *         &lt;element ref="{}tel2"/&gt;
 *         &lt;element ref="{}SIRET"/&gt;
 *         &lt;element ref="{}tradingCurrencyCode"/&gt;
 *         &lt;element ref="{}customFields"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="code" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="creditCategory" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "code",
        "description",
        "externalRef1",
        "externalRef2",
        "company",
        "name",
        "address",
        "paymentMethod",
        "mandateIdentification", // Added by Mohamed Ali Hammal
        "mandateDate", // Added by Mohamed Ali Hammal
        "bankCoordinates", // Added by Mohamed Ali Hammal
        "email",
        "tel1",
        "tel2",
        "tradingCurrencyCode",
        "tradingLanguageCode",
        "customFields",
        "billingAccounts"
})
@XmlRootElement(name = "customerAccount")
public class CustomerAccount {

    @XmlElement(required = true)
    protected String code;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected String externalRef1;
    @XmlElement(required = true)
    protected String externalRef2;
    @XmlElement(required = true)
    protected String company;
    @XmlElement(required = true)
    protected Name name;
    @XmlElement(required = true)
    protected Address address;
    @XmlElement(required = true)
    protected String paymentMethod;
    @XmlElement(required = false)
    protected String mandateIdentification; // Added by Mohamed Ali Hammal
    @XmlElement(required = false)
    protected String mandateDate; // Added by Mohamed Ali Hammal
    @XmlElement(required = false)
    protected BankCoordinates bankCoordinates; // Added by Mohamed Ali Hammal
    @XmlElement(required = true)
    protected String email;
    @XmlElement(required = true)
    protected String tel1;
    @XmlElement(required = true)
    protected String tel2;
    @XmlElement(required = true)
    protected String tradingCurrencyCode;
    @XmlElement(required = true)
    protected String tradingLanguageCode;
    @XmlAttribute(name = "creditCategory")
    protected String creditCategory;
    @XmlAttribute(name="ignoreCheck")
    protected Boolean ignoreCheck;
    protected CustomFields customFields;
    @XmlElement(required = false)
    protected BillingAccounts billingAccounts;





    public CustomerAccount(){}

    /**
     * Gets the value of the description property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the externalRef1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExternalRef1() {
        return externalRef1;
    }

    /**
     * Sets the value of the externalRef1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExternalRef1(String value) {
        this.externalRef1 = value;
    }

    /**
     * Gets the value of the externalRef2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getExternalRef2() {
        return externalRef2;
    }

    /**
     * Sets the value of the externalRef2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setExternalRef2(String value) {
        this.externalRef2 = value;
    }

    /**
     * Gets the value of the company property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCompany() {
        return company;
    }

    /**
     * Sets the value of the company property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCompany(String value) {
        this.company = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link Name }
     *
     */
    public Name getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link Name }
     *
     */
    public void setName(Name value) {
        this.name = value;
    }

    /**
     * Gets the value of the address property.
     *
     * @return
     *     possible object is
     *     {@link Address }
     *
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     *
     * @param value
     *     allowed object is
     *     {@link Address }
     *
     */
    public void setAddress(Address value) {
        this.address = value;
    }

    /**
     * Gets the value of the paymentMethod property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPaymentMethod() {
        return paymentMethod;
    } // Added by Mohamed Ali Hammal

    /**
     * Sets the value of the paymentMethod property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPaymentMethod(String value) {
        this.paymentMethod = value;
    } // Added by Mohamed Ali Hammal

    /**
     * Gets the value of the mandateIdentification property.
     *
     * @return
     *     possible object is
     *     {@link String}
     *
     */

    public String getMandateIdentification() { // Added by Mohamed Ali Hammal
        return mandateIdentification;
    }

    /**
     * Sets the value of the mandateIdentification property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     **/

    public void setMandateIdentification(String value) { // Added by Mohamed Ali Hammal
        this.mandateIdentification = value;
    }

    /**
     * Gets the value of the mandateDate property.
     *
     * @return
     *     possible object is
     *     {@link String}
     **/


    public String getMandateDate() { // Added by Mohamed Ali Hammal
        return mandateDate;
    }

    /**
     * Sets the value of the mandateDate property.
     *
     * @param value
     *     allowed object is
     *     {@link String}
     **/

    public void setMandateDate(String value) { // Added by Mohamed Ali Hammal
        this.mandateDate = value;
    }

    /**
     * Gets the value of the bankCoordinates property.
     *
     * @return
     *     possible object is
     *     {@link BankCoordinates}
     **/


    public BankCoordinates getBankCoordinates() {
        return bankCoordinates;
    } // Added by Mohamed Ali Hammal

    /**
     * Sets the value of the bankCoordinates property.
     *
     * @param value
     *     allowed object is
     *     {@link BankCoordinates }
     **/


    public void setBankCoordinates(BankCoordinates value) {
        this.bankCoordinates = value;
    } // Added by Mohamed Ali Hammal

    /**
     * Gets the value of the email property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the tel1 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTel1() {
        return tel1;
    }

    /**
     * Sets the value of the tel1 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTel1(String value) {
        this.tel1 = value;
    }

    /**
     * Gets the value of the tel2 property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTel2() {
        return tel2;
    }

    /**
     * Sets the value of the tel2 property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTel2(String value) {
        this.tel2 = value;
    }

    /**
     * Gets the value of the tradingCurrencyCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */

    public String getTradingCurrencyCode() {
        return tradingCurrencyCode;
    }

    /**
     * Sets the value of the tradingCurrencyCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */

    public void setTradingCurrencyCode(String value) {
        this.tradingCurrencyCode = value;


    }





    /**
     * Gets the value of the tradingLanguageCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */

    public String getTradingLanguageCode() {
        return tradingLanguageCode;
    }

    /**
     * Sets the value of the tradingLanguageCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */

    public void setTradingLanguageCode(String value) {
        this.tradingLanguageCode = value;
    }

    /**
     * Gets the value of the code property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the creditCategory property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCreditCategory() {
        return creditCategory;
    }

    /**
     * Sets the value of the creditCategory property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCreditCategory(String value) {
        this.creditCategory = value;
    }

    /**
     * @return the ignoreCheck
     */
    public Boolean getIgnoreCheck() {
        return ignoreCheck;
    }

    /**
     * @param ignoreCheck the ignoreCheck to set
     */
    public void setIgnoreCheck(Boolean ignoreCheck) {
        this.ignoreCheck = ignoreCheck;
    }

    public CustomFields getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFields customFields) {
        this.customFields = customFields;
    }

    public BillingAccounts getBillingAccounts() {
        return billingAccounts;
    }

    public void setBillingAccounts(BillingAccounts billingAccounts) {
        this.billingAccounts = billingAccounts;
    }

}

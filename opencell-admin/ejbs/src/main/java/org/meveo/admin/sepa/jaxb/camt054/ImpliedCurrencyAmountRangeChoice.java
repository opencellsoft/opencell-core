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
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.09.13 at 04:10:01 PM WET 
//


package org.meveo.admin.sepa.jaxb.camt054;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImpliedCurrencyAmountRangeChoice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImpliedCurrencyAmountRangeChoice"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="FrAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.02}AmountRangeBoundary1"/&gt;
 *           &lt;element name="ToAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.02}AmountRangeBoundary1"/&gt;
 *           &lt;element name="FrToAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.02}FromToAmountRange"/&gt;
 *           &lt;element name="EQAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.02}ImpliedCurrencyAndAmount"/&gt;
 *           &lt;element name="NEQAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.02}ImpliedCurrencyAndAmount"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImpliedCurrencyAmountRangeChoice", propOrder = {
    "frAmt",
    "toAmt",
    "frToAmt",
    "eqAmt",
    "neqAmt"
})
public class ImpliedCurrencyAmountRangeChoice {

    @XmlElement(name = "FrAmt")
    protected AmountRangeBoundary1 frAmt;
    @XmlElement(name = "ToAmt")
    protected AmountRangeBoundary1 toAmt;
    @XmlElement(name = "FrToAmt")
    protected FromToAmountRange frToAmt;
    @XmlElement(name = "EQAmt")
    protected BigDecimal eqAmt;
    @XmlElement(name = "NEQAmt")
    protected BigDecimal neqAmt;

    /**
     * Gets the value of the frAmt property.
     * 
     * @return
     *     possible object is
     *     {@link AmountRangeBoundary1 }
     *     
     */
    public AmountRangeBoundary1 getFrAmt() {
        return frAmt;
    }

    /**
     * Sets the value of the frAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountRangeBoundary1 }
     *     
     */
    public void setFrAmt(AmountRangeBoundary1 value) {
        this.frAmt = value;
    }

    /**
     * Gets the value of the toAmt property.
     * 
     * @return
     *     possible object is
     *     {@link AmountRangeBoundary1 }
     *     
     */
    public AmountRangeBoundary1 getToAmt() {
        return toAmt;
    }

    /**
     * Sets the value of the toAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountRangeBoundary1 }
     *     
     */
    public void setToAmt(AmountRangeBoundary1 value) {
        this.toAmt = value;
    }

    /**
     * Gets the value of the frToAmt property.
     * 
     * @return
     *     possible object is
     *     {@link FromToAmountRange }
     *     
     */
    public FromToAmountRange getFrToAmt() {
        return frToAmt;
    }

    /**
     * Sets the value of the frToAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link FromToAmountRange }
     *     
     */
    public void setFrToAmt(FromToAmountRange value) {
        this.frToAmt = value;
    }

    /**
     * Gets the value of the eqAmt property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEQAmt() {
        return eqAmt;
    }

    /**
     * Sets the value of the eqAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEQAmt(BigDecimal value) {
        this.eqAmt = value;
    }

    /**
     * Gets the value of the neqAmt property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNEQAmt() {
        return neqAmt;
    }

    /**
     * Sets the value of the neqAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNEQAmt(BigDecimal value) {
        this.neqAmt = value;
    }

}

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
// Generated on: 2018.09.25 at 02:01:12 PM WET 
//


package org.meveo.admin.sepa.jaxb.pain001;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for TaxInformation3 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TaxInformation3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cdtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}TaxParty1" minOccurs="0"/&gt;
 *         &lt;element name="Dbtr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}TaxParty2" minOccurs="0"/&gt;
 *         &lt;element name="AdmstnZn" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}Max35Text" minOccurs="0"/&gt;
 *         &lt;element name="RefNb" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}Max140Text" minOccurs="0"/&gt;
 *         &lt;element name="Mtd" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}Max35Text" minOccurs="0"/&gt;
 *         &lt;element name="TtlTaxblBaseAmt" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}ActiveOrHistoricCurrencyAndAmount" minOccurs="0"/&gt;
 *         &lt;element name="TtlTaxAmt" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}ActiveOrHistoricCurrencyAndAmount" minOccurs="0"/&gt;
 *         &lt;element name="Dt" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}ISODate" minOccurs="0"/&gt;
 *         &lt;element name="SeqNb" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}Number" minOccurs="0"/&gt;
 *         &lt;element name="Rcrd" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.03}TaxRecord1" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaxInformation3", propOrder = {
    "cdtr",
    "dbtr",
    "admstnZn",
    "refNb",
    "mtd",
    "ttlTaxblBaseAmt",
    "ttlTaxAmt",
    "dt",
    "seqNb",
    "rcrd"
})
public class TaxInformation3 {

    @XmlElement(name = "Cdtr")
    protected TaxParty1 cdtr;
    @XmlElement(name = "Dbtr")
    protected TaxParty2 dbtr;
    @XmlElement(name = "AdmstnZn")
    protected String admstnZn;
    @XmlElement(name = "RefNb")
    protected String refNb;
    @XmlElement(name = "Mtd")
    protected String mtd;
    @XmlElement(name = "TtlTaxblBaseAmt")
    protected ActiveOrHistoricCurrencyAndAmount ttlTaxblBaseAmt;
    @XmlElement(name = "TtlTaxAmt")
    protected ActiveOrHistoricCurrencyAndAmount ttlTaxAmt;
    @XmlElement(name = "Dt")
    protected XMLGregorianCalendar dt;
    @XmlElement(name = "SeqNb")
    protected BigDecimal seqNb;
    @XmlElement(name = "Rcrd")
    protected List<TaxRecord1> rcrd;

    /**
     * Gets the value of the cdtr property.
     * 
     * @return
     *     possible object is
     *     {@link TaxParty1 }
     *     
     */
    public TaxParty1 getCdtr() {
        return cdtr;
    }

    /**
     * Sets the value of the cdtr property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxParty1 }
     *     
     */
    public void setCdtr(TaxParty1 value) {
        this.cdtr = value;
    }

    /**
     * Gets the value of the dbtr property.
     * 
     * @return
     *     possible object is
     *     {@link TaxParty2 }
     *     
     */
    public TaxParty2 getDbtr() {
        return dbtr;
    }

    /**
     * Sets the value of the dbtr property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxParty2 }
     *     
     */
    public void setDbtr(TaxParty2 value) {
        this.dbtr = value;
    }

    /**
     * Gets the value of the admstnZn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmstnZn() {
        return admstnZn;
    }

    /**
     * Sets the value of the admstnZn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmstnZn(String value) {
        this.admstnZn = value;
    }

    /**
     * Gets the value of the refNb property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefNb() {
        return refNb;
    }

    /**
     * Sets the value of the refNb property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefNb(String value) {
        this.refNb = value;
    }

    /**
     * Gets the value of the mtd property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMtd() {
        return mtd;
    }

    /**
     * Sets the value of the mtd property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMtd(String value) {
        this.mtd = value;
    }

    /**
     * Gets the value of the ttlTaxblBaseAmt property.
     * 
     * @return
     *     possible object is
     *     {@link ActiveOrHistoricCurrencyAndAmount }
     *     
     */
    public ActiveOrHistoricCurrencyAndAmount getTtlTaxblBaseAmt() {
        return ttlTaxblBaseAmt;
    }

    /**
     * Sets the value of the ttlTaxblBaseAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActiveOrHistoricCurrencyAndAmount }
     *     
     */
    public void setTtlTaxblBaseAmt(ActiveOrHistoricCurrencyAndAmount value) {
        this.ttlTaxblBaseAmt = value;
    }

    /**
     * Gets the value of the ttlTaxAmt property.
     * 
     * @return
     *     possible object is
     *     {@link ActiveOrHistoricCurrencyAndAmount }
     *     
     */
    public ActiveOrHistoricCurrencyAndAmount getTtlTaxAmt() {
        return ttlTaxAmt;
    }

    /**
     * Sets the value of the ttlTaxAmt property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActiveOrHistoricCurrencyAndAmount }
     *     
     */
    public void setTtlTaxAmt(ActiveOrHistoricCurrencyAndAmount value) {
        this.ttlTaxAmt = value;
    }

    /**
     * Gets the value of the dt property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDt() {
        return dt;
    }

    /**
     * Sets the value of the dt property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDt(XMLGregorianCalendar value) {
        this.dt = value;
    }

    /**
     * Gets the value of the seqNb property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSeqNb() {
        return seqNb;
    }

    /**
     * Sets the value of the seqNb property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSeqNb(BigDecimal value) {
        this.seqNb = value;
    }

    /**
     * Gets the value of the rcrd property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rcrd property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRcrd().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaxRecord1 }
     * 
     * 
     */
    public List<TaxRecord1> getRcrd() {
        if (rcrd == null) {
            rcrd = new ArrayList<TaxRecord1>();
        }
        return this.rcrd;
    }

}

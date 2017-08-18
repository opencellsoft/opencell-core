//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2016.01.14 à 04:34:12 PM CET 
//

package org.tmf.dsmapi.quote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.billing.GenerateInvoiceResultDto;
import org.meveo.commons.utils.CustomDateSerializer;
import org.tmf.dsmapi.catalog.resource.RelatedParty;
import org.tmf.dsmapi.catalog.resource.ServiceLevelAgreement;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.catalog.resource.order.BillingAccount;
import org.tmf.dsmapi.catalog.resource.order.Note;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * <p>
 * Classe Java pour Quote complex type.
 * 
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="Quote">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="billingAccount" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}BillingAccount" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="state" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}StateQuote" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="href" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="externalId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="quoteDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="effectiveQuoteCompletionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="quoteCompletionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="notificationContact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="validFor" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}ValidFor" minOccurs="0"/>
 *         &lt;element name="note" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Note" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="characteristic" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Characteristic" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="customer" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Customer" minOccurs="0"/>
 *         &lt;element name="relatedParty" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}RelatedParty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="agreement" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Agreement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="quoteProductOfferingPrice" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}QuoteProductOfferingPrice" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="quoteItem" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}QuoteItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "Quote", namespace="http://www.tmforum.org")
@XmlType(name = "Quote", namespace="http://www.tmforum.org")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(value = Include.NON_NULL)
public class ProductQuote implements Serializable {

    private final static long serialVersionUID = 11L;
    protected List<BillingAccount> billingAccount;
    protected String state;
    protected String id;
    protected String href;
    protected String externalId;
    protected String description;
    protected String category;
    protected String version;
    @JsonSerialize(using = CustomDateSerializer.class)
    protected Date quoteDate;
    @JsonSerialize(using = CustomDateSerializer.class)
    protected Date effectiveQuoteCompletionDate;
    @JsonSerialize(using = CustomDateSerializer.class)
    protected Date quoteCompletionDate;
    @JsonSerialize(using = CustomDateSerializer.class)
    protected Date fulfillmentStartDate;
    protected String notificationContact;
    protected TimeRange validFor;
    protected List<Note> note;
    protected List<Characteristic> characteristic;
    protected Customer customer;
    protected List<RelatedParty> relatedParty;
    protected List<ServiceLevelAgreement> agreement;
    protected List<QuoteProductOfferingPrice> quoteProductOfferingPrice;
    protected List<ProductQuoteItem> quoteItem;

    private CustomFieldsDto customFields = new CustomFieldsDto();

    private List<GenerateInvoiceResultDto> invoices;

    /**
     * 
     * Gets the value of the billingAccount property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the billingAccount property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getBillingAccount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link BillingAccount }
     * 
     * 
     */
    public List<BillingAccount> getBillingAccount() {
        if (billingAccount == null) {
            billingAccount = new ArrayList<BillingAccount>();
        }
        return this.billingAccount;
    }

    /**
     * 
     * 
     */
    public void setBillingAccount(List<BillingAccount> billingAccount) {
        this.billingAccount = billingAccount;
    }

    /**
     * Obtient la valeur de la propriété state.
     * 
     * @return possible object is {@link StateQuote }
     * 
     */
    public String getState() {
        return state;
    }

    /**
     * Définit la valeur de la propriété state.
     * 
     * @param value allowed object is {@link StateQuote }
     * 
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Obtient la valeur de la propriété id.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété href.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getHref() {
        return href;
    }

    /**
     * Définit la valeur de la propriété href.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Obtient la valeur de la propriété externalId.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Définit la valeur de la propriété externalId.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setExternalId(String value) {
        this.externalId = value;
    }

    /**
     * Obtient la valeur de la propriété description.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getDescription() {
        return description;
    }

    /**
     * Définit la valeur de la propriété description.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Obtient la valeur de la propriété category.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getCategory() {
        return category;
    }

    /**
     * Définit la valeur de la propriété category.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setCategory(String value) {
        this.category = value;
    }

    /**
     * Obtient la valeur de la propriété version.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getVersion() {
        return version;
    }

    /**
     * Définit la valeur de la propriété version.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Obtient la valeur de la propriété quoteDate.
     * 
     * @return possible object is {@link String }
     * 
     */
    public Date getQuoteDate() {
        return quoteDate;
    }

    /**
     * Définit la valeur de la propriété quoteDate.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setQuoteDate(Date value) {
        this.quoteDate = value;
    }

    /**
     * Obtient la valeur de la propriété effectiveQuoteCompletionDate.
     * 
     * @return possible object is {@link String }
     * 
     */
    public Date getEffectiveQuoteCompletionDate() {
        return effectiveQuoteCompletionDate;
    }

    /**
     * Définit la valeur de la propriété effectiveQuoteCompletionDate.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setEffectiveQuoteCompletionDate(Date value) {
        this.effectiveQuoteCompletionDate = value;
    }

    /**
     * Obtient la valeur de la propriété quoteCompletionDate.
     * 
     * @return possible object is {@link String }
     * 
     */
    public Date getQuoteCompletionDate() {
        return quoteCompletionDate;
    }

    /**
     * Définit la valeur de la propriété quoteCompletionDate.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setQuoteCompletionDate(Date value) {
        this.quoteCompletionDate = value;
    }

    public Date getFulfillmentStartDate() {
        return fulfillmentStartDate;
    }

    public void setFulfillmentStartDate(Date fulfillmentStartDate) {
        this.fulfillmentStartDate = fulfillmentStartDate;
    }

    /**
     * Obtient la valeur de la propriété notificationContact.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getNotificationContact() {
        return notificationContact;
    }

    /**
     * Définit la valeur de la propriété notificationContact.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setNotificationContact(String value) {
        this.notificationContact = value;
    }

    /**
     * Obtient la valeur de la propriété validFor.
     * 
     * @return possible object is {@link ValidFor }
     * 
     */
    public TimeRange getValidFor() {
        return validFor;
    }

    /**
     * Définit la valeur de la propriété validFor.
     * 
     * @param value allowed object is {@link ValidFor }
     * 
     */
    public void setValidFor(TimeRange value) {
        this.validFor = value;
    }

    /**
     * Gets the value of the note property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the note property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getNote().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Note }
     * 
     * 
     */
    public List<Note> getNote() {
        if (note == null) {
            note = new ArrayList<Note>();
        }
        return this.note;
    }

    public void setNote(List<Note> note) {
        this.note = note;
    }

    /**
     * Gets the value of the characteristic property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the characteristic property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getCharacteristic().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Characteristic }
     * 
     * 
     */
    public List<Characteristic> getCharacteristic() {
        if (characteristic == null) {
            characteristic = new ArrayList<Characteristic>();
        }
        return this.characteristic;
    }

    public void setCharacteristic(List<Characteristic> characteristic) {
        this.characteristic = characteristic;
    }

    /**
     * Obtient la valeur de la propriété customer.
     * 
     * @return possible object is {@link Customer }
     * 
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Définit la valeur de la propriété customer.
     * 
     * @param value allowed object is {@link Customer }
     * 
     */
    public void setCustomer(Customer value) {
        this.customer = value;
    }

    /**
     * Gets the value of the relatedParty property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relatedParty property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getRelatedParty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link RelatedParty }
     * 
     * 
     */
    public List<RelatedParty> getRelatedParty() {
        if (relatedParty == null) {
            relatedParty = new ArrayList<RelatedParty>();
        }
        return this.relatedParty;
    }

    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
    }

    /**
     * Gets the value of the agreement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the agreement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getAgreement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Agreement }
     * 
     * 
     */
    public List<ServiceLevelAgreement> getAgreement() {
        if (agreement == null) {
            agreement = new ArrayList<ServiceLevelAgreement>();
        }
        return this.agreement;
    }

    public void setAgreement(List<ServiceLevelAgreement> agreement) {
        this.agreement = agreement;
    }

    /**
     * Gets the value of the quoteProductOfferingPrice property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quoteProductOfferingPrice property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getQuoteProductOfferingPrice().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link QuoteProductOfferingPrice }
     * 
     * 
     */
    public List<QuoteProductOfferingPrice> getQuoteProductOfferingPrice() {
        if (quoteProductOfferingPrice == null) {
            quoteProductOfferingPrice = new ArrayList<QuoteProductOfferingPrice>();
        }
        return this.quoteProductOfferingPrice;
    }

    public void setQuoteProductOfferingPrice(List<QuoteProductOfferingPrice> quoteProductOfferingPrice) {
        this.quoteProductOfferingPrice = quoteProductOfferingPrice;
    }

    /**
     * Gets the value of the quoteItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the quoteItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getQuoteItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link ProductQuoteItem }
     * 
     * 
     */
    public List<ProductQuoteItem> getQuoteItem() {
        if (quoteItem == null) {
            quoteItem = new ArrayList<ProductQuoteItem>();
        }
        return this.quoteItem;
    }

    public void setQuoteItem(List<ProductQuoteItem> quoteItem) {
        this.quoteItem = quoteItem;
    }

    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public List<GenerateInvoiceResultDto> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<GenerateInvoiceResultDto> invoices) {
        this.invoices = invoices;
    }
}
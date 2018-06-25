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
 * &lt;complexType name="Quote"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="billingAccount" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}BillingAccount" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="state" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}StateQuote" minOccurs="0"/&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="href" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="externalId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="quoteDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="effectiveQuoteCompletionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="quoteCompletionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="notificationContact" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="validFor" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}ValidFor" minOccurs="0"/&gt;
 *         &lt;element name="note" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Note" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="characteristic" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Characteristic" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="customer" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Customer" minOccurs="0"/&gt;
 *         &lt;element name="relatedParty" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}RelatedParty" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="agreement" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Agreement" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="quoteProductOfferingPrice" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}QuoteProductOfferingPrice" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="quoteItem" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}QuoteItem" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
    /**
     * By default we do not generate the pdf as it cost CPU usage
     */
    private boolean generatePdf = true;

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
     * @return list of billing account.
     * 
     */
    public List<BillingAccount> getBillingAccount() {
        if (billingAccount == null) {
            billingAccount = new ArrayList<BillingAccount>();
        }
        return this.billingAccount;
    }

    /**
     * @param billingAccount billing account
     * 
     */
    public void setBillingAccount(List<BillingAccount> billingAccount) {
        this.billingAccount = billingAccount;
    }

    /**.
     * 
     * @return possible object
     * 
     */
    public String getState() {
        return state;
    }

    /**
     * 
     * @param value allowed object is
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
     * Oget value of property validFor.
     * 
     * @return possible object time rang
     * 
     */
    public TimeRange getValidFor() {
        return validFor;
    }

    /**
     * 
     * @param value allowed object
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
     * @return list o note
     * 
     * 
     */
    public List<Note> getNote() {
        if (note == null) {
            note = new ArrayList<Note>();
        }
        return this.note;
    }

    /**
     * @param note list of note to set
     */
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
     * </p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getCharacteristic().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Characteristic }
     * @return list of characteristic.
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
     * @return list of related party.
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
     * Objects of the following type(s) are allowed in the list {@link ServiceLevelAgreement }
     * @return list of service level agreement.
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
     * @return list of quote product offering price.
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
     * @return list of product quote item.
     * 
     * 
     */
    public List<ProductQuoteItem> getQuoteItem() {
        if (quoteItem == null) {
            quoteItem = new ArrayList<ProductQuoteItem>();
        }
        return this.quoteItem;
    }

    /**
     * @param quoteItem list of product quote item.
     */
    public void setQuoteItem(List<ProductQuoteItem> quoteItem) {
        this.quoteItem = quoteItem;
    }

    /**
     * @return a wrapper of custom field
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * @param customFields a wrapper of list of custom field.
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    /**
     * @return list of genereate invoice result.
     */
    public List<GenerateInvoiceResultDto> getInvoices() {
        return invoices;
    }

    /**
     * @param invoices list of generate invoice result.
     */
    public void setInvoices(List<GenerateInvoiceResultDto> invoices) {
        this.invoices = invoices;
    }

    public boolean isGeneratePdf() {
        return generatePdf;
    }

    public void setGeneratePdf(boolean generatePdf) {
        this.generatePdf = generatePdf;
    }
}
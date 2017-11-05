//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2016.01.14 à 04:34:12 PM CET 
//

package org.tmf.dsmapi.quote;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.admin.exception.BusinessException;
import org.tmf.dsmapi.catalog.resource.Attachment;
import org.tmf.dsmapi.catalog.resource.RelatedParty;
import org.tmf.dsmapi.catalog.resource.TimeRange;
import org.tmf.dsmapi.catalog.resource.order.BillingAccount;
import org.tmf.dsmapi.catalog.resource.order.Note;
import org.tmf.dsmapi.catalog.resource.order.Product;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>
 * Classe Java pour QuoteItem complex type.
 * 
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="QuoteItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="attachment" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Attachment" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="relatedParty" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}RelatedParty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="note" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Note" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="productOffering" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}ProductOffering" minOccurs="0"/>
 *         &lt;element name="product" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Product" minOccurs="0"/>
 *         &lt;element name="itemQuoteProductOfferingPrice" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}QuoteProductOfferingPrice" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "QuoteItem", namespace = "http://www.tmforum.org")
@XmlType(name = "QuoteItem", namespace = "http://www.tmforum.org")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductQuoteItem implements Serializable {

    private final static long serialVersionUID = 11L;
    protected String id;
    protected String state;
    protected String action;
    protected String appointment;
    protected Attachment attachment;
    protected List<RelatedParty> relatedParty;
    protected List<Note> note;
    protected ProductOffering productOffering;
    protected Product product;
    protected List<QuoteProductOfferingPrice> itemQuoteProductOfferingPrice;
    protected List<BillingAccount> billingAccount;

    protected TimeRange subscriptionPeriod;
    protected List<String> consumptionCdr;
    
    private static Unmarshaller m ;
    private static Marshaller mar;
    
    private static JAXBContext jaxbCxt;
	static {
		try {
			jaxbCxt = JAXBContext.newInstance(ProductQuoteItem.class);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
     * Obtient la valeur de la propriété state.
     * 
     * @return possible object is {@link String }
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
     * Obtient la valeur de la propriété action.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getAction() {
        return action;
    }

    /**
     * Définit la valeur de la propriété action.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setAction(String value) {
        this.action = value;
    }

    /**
     * Obtient la valeur de la propriété appointment.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getAppointment() {
        return appointment;
    }

    /**
     * Définit la valeur de la propriété appointment.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setAppointment(String value) {
        this.appointment = value;
    }

    /**
     * Gets the value of the attachment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attachment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getAttachment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Attachment }
     * 
     * 
     */
    public Attachment getAttachment() {
        return this.attachment;
    }

    /**
     * 
     * 
     */
    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
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

    /**
     * 
     * 
     */
    public void setRelatedParty(List<RelatedParty> relatedParty) {
        this.relatedParty = relatedParty;
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

    /**
     * 
     * 
     */
    public void setNote(List<Note> note) {
        this.note = note;
    }

    /**
     * Obtient la valeur de la propriété productOffering.
     * 
     * @return possible object is {@link ProductOffering }
     * 
     */
    public ProductOffering getProductOffering() {
        return productOffering;
    }

    /**
     * Définit la valeur de la propriété productOffering.
     * 
     * @param value allowed object is {@link ProductOffering }
     * 
     */
    public void setProductOffering(ProductOffering value) {
        this.productOffering = value;
    }

    /**
     * Obtient la valeur de la propriété product.
     * 
     * @return possible object is {@link Product }
     * 
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Définit la valeur de la propriété product.
     * 
     * @param value allowed object is {@link Product }
     * 
     */
    public void setProduct(Product value) {
        this.product = value;
    }

    /**
     * Gets the value of the itemQuoteProductOfferingPrice property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itemQuoteProductOfferingPrice property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getItemQuoteProductOfferingPrice().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link QuoteProductOfferingPrice }
     * 
     * 
     */
    public List<QuoteProductOfferingPrice> getItemQuoteProductOfferingPrice() {
        if (itemQuoteProductOfferingPrice == null) {
            itemQuoteProductOfferingPrice = new ArrayList<QuoteProductOfferingPrice>();
        }
        return this.itemQuoteProductOfferingPrice;
    }

    public void setItemQuoteProductOfferingPrice(List<QuoteProductOfferingPrice> itemQuoteProductOfferingPrice) {
        this.itemQuoteProductOfferingPrice = itemQuoteProductOfferingPrice;
    }

    public TimeRange getSubscriptionPeriod() {
        return subscriptionPeriod;
    }

    public void setSubscriptionPeriod(TimeRange subscriptionPeriod) {
        this.subscriptionPeriod = subscriptionPeriod;
    }

    public List<String> getConsumptionCdr() {
        return consumptionCdr;
    }

    public void setConsumptionCdr(List<String> consumptionCdr) {
        this.consumptionCdr = consumptionCdr;
    }

    public List<BillingAccount> getBillingAccount() {
        return this.billingAccount;
    }

    public void setBillingAccount(List<BillingAccount> billingAccount) {
        this.billingAccount = billingAccount;
    }

    public void addBillingAccount(BillingAccount billingAccountToAdd) {
        if (billingAccount == null) {
            this.billingAccount = new ArrayList<BillingAccount>();
        }
        billingAccount.add(billingAccountToAdd);
    }

    public void addBillingAccount(String baId) {
        BillingAccount ba = new BillingAccount();
        ba.setId(baId);
        addBillingAccount(ba);
    }

    /**
     * Serialize orderItem DTO into a string
     * 
     * @param productQuoteItem Quote item to serialize
     * @return String in XML format
     * 
     * @throws BusinessException
     */
    public static String serializeQuoteItem(ProductQuoteItem productQuoteItem) throws BusinessException {
        try {
            //Marshaller m = JAXBContext.newInstance(ProductQuoteItem.class).createMarshaller();
            //m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        	Marshaller mar = jaxbCxt.createMarshaller();
        	mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        	StringWriter w = new StringWriter();
            mar.marshal(productQuoteItem, w);
            return w.toString();

        } catch (JAXBException e) {
            throw new BusinessException(e);
        }
    }

    /**
     * Deserialize quote item from a string
     * 
     * @param quoteItemSource Serialized quoteItem dto
     * @return Quote item object
     * 
     * @throws BusinessException
     */
    public static ProductQuoteItem deserializeQuoteItem(String quoteItemSource) throws BusinessException {
        // Store quoteItem DTO into DB to be retrieved for full information
        try {
            //Unmarshaller m = JAXBContext.newInstance(ProductQuoteItem.class).createUnmarshaller();
            // m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        	Unmarshaller umar = jaxbCxt.createUnmarshaller();
            ProductQuoteItem productQuoteItem = (ProductQuoteItem) umar.unmarshal(new StringReader(quoteItemSource));
            
            return productQuoteItem;

        } catch (JAXBException e) {
            throw new BusinessException(e);
        }
    }
}
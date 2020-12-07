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
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2016.01.14 à 04:34:12 PM CET 
//

package org.tmf.dsmapi.quote;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
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
import org.tmf.dsmapi.catalog.resource.order.CustomerService;
import org.tmf.dsmapi.catalog.resource.order.Note;
import org.tmf.dsmapi.catalog.resource.order.Product;
import org.tmf.dsmapi.catalog.resource.order.Service;
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
 * &lt;complexType name="QuoteItem"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="attachment" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Attachment" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="relatedParty" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}RelatedParty" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="note" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Note" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="productOffering" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}ProductOffering" minOccurs="0"/&gt;
 *         &lt;element name="product" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Product" minOccurs="0"/&gt;
 *         &lt;element name="itemQuoteProductOfferingPrice" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}QuoteProductOfferingPrice" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
    protected Service service;
    protected Integer quantity;
    protected CustomerService customerService;
    protected List<QuoteProductOfferingPrice> itemQuoteProductOfferingPrice;
    protected List<BillingAccount> billingAccount;
    protected String produtQuote;
    protected String produtQuoteVersion;
    protected String produtVersion;
    
    

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
     * 
     * @param value allowed object
     * 
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getAction() {
        return action;
    }

    /**
     * 
     * @param value allowed object
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
     * @return list of attachment
     * 
     */
    public Attachment getAttachment() {
        return this.attachment;
    }

    /**
     * @param attachment to set.
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
     * @return list of related Party
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
     * @param relatedParty list of related party {@link RelatedParty}
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
     * @return list of note.
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
     * @param note list of {@link Note} to be set.
     * 
     */
    public void setNote(List<Note> note) {
        this.note = note;
    }

    /**
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



	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getProdutQuote() {
		return produtQuote;
	}

	public void setProdutQuote(String produtQuote) {
		this.produtQuote = produtQuote;
	}

	public String getProdutQuoteVersion() {
		return produtQuoteVersion;
	}

	public void setProdutQuoteVersion(String produtQuoteVersion) {
		this.produtQuoteVersion = produtQuoteVersion;
	}

	/**
     * Obtient la valeur de la propriété service.
     * 
     * @return possible object is {@link Service }
     * 
     */
    public Service getService() {
		return service;
	}


    /**
     * Définit la valeur de la propriété service.
     * 
     * @param value allowed object is {@link Service }
     * 
     */
	public void setService(Service service) {
		this.service = service;
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
     * @return list of quote product offering price.
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

    public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
	
	

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	
	

	public String getProdutVersion() {
		return produtVersion;
	}

	public void setProdutVersion(String produtVersion) {
		this.produtVersion = produtVersion;
	}

	/**
     * Serialize orderItem DTO into a string.
     * 
     * @param productQuoteItem Quote item to serialize
     * @return String in XML format
     * 
     * @throws BusinessException business exception.
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
     * Deserialize quote item from a string.
     * 
     * @param quoteItemSource Serialized quoteItem dto
     * @return Quote item object
     * 
     * @throws BusinessException business exception.
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
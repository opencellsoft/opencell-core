//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.7 
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2016.01.14 à 04:34:12 PM CET 
//

package org.tmf.dsmapi.quote;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.tmf.dsmapi.catalog.resource.product.Price;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>
 * Classe Java pour QuoteProductOfferingPrice complex type.
 * 
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="QuoteProductOfferingPrice"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="priceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="recurringChargePeriod" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="priceCondition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="quotePriceAlteration" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}QuotePriceAlteration" minOccurs="0"/&gt;
 *         &lt;element name="price" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}Price" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuoteProductOfferingPrice", propOrder = { "priceType", "recurringChargePeriod", "priceCondition", "quotePriceAlteration", "price" }, namespace="http://www.tmforum.org")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuoteProductOfferingPrice implements Serializable {

    private final static long serialVersionUID = 11L;
    protected String priceType;
    protected String recurringChargePeriod;
    protected String priceCondition;
    protected QuotePriceAlteration quotePriceAlteration;
    protected Price price;

    /**
     * Obtient la valeur de la propriété priceType.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPriceType() {
        return priceType;
    }

    /**
     * Définit la valeur de la propriété priceType.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setPriceType(String value) {
        this.priceType = value;
    }

    /**
     * Obtient la valeur de la propriété recurringChargePeriod.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getRecurringChargePeriod() {
        return recurringChargePeriod;
    }

    /**
     * Définit la valeur de la propriété recurringChargePeriod.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setRecurringChargePeriod(String value) {
        this.recurringChargePeriod = value;
    }

    /**
     * Obtient la valeur de la propriété priceCondition.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getPriceCondition() {
        return priceCondition;
    }

    /**
     * Définit la valeur de la propriété priceCondition.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setPriceCondition(String value) {
        this.priceCondition = value;
    }

    /**
     * Obtient la valeur de la propriété quotePriceAlteration.
     * 
     * @return possible object is {@link QuotePriceAlteration }
     * 
     */
    public QuotePriceAlteration getQuotePriceAlteration() {
        return quotePriceAlteration;
    }

    /**
     * Définit la valeur de la propriété quotePriceAlteration.
     * 
     * @param value allowed object is {@link QuotePriceAlteration }
     * 
     */
    public void setQuotePriceAlteration(QuotePriceAlteration value) {
        this.quotePriceAlteration = value;
    }

    /**
     * Obtient la valeur de la propriété price.
     * 
     * @return possible object is {@link Price }
     * 
     */
    public Price getPrice() {
        return price;
    }

    /**
     * Définit la valeur de la propriété price.
     * 
     * @param value allowed object is {@link Price }
     * 
     */
    public void setPrice(Price value) {
        this.price = value;
    }
}
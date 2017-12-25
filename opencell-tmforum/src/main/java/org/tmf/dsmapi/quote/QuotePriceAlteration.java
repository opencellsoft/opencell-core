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

import org.tmf.dsmapi.catalog.resource.TimeRange;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * <p>
 * Classe Java pour QuotePriceAlteration complex type.
 * 
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType name="QuotePriceAlteration">
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="validFor" type="{http://orange.com/api/quoteManagement/tmf/v1/model/business}ValidFor" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuotePriceAlteration", propOrder = { "name", "description", "validFor" }, namespace="http://www.tmforum.org")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuotePriceAlteration implements Serializable {

    private final static long serialVersionUID = 11L;
    protected String name;
    protected String description;
    protected TimeRange validFor;

    /**
     * Obtient la valeur de la propriété name.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setName(String value) {
        this.name = value;
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
}
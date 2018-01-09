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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * <p>
 * Classe Java pour Customer complex type.
 * 
 * </p>
 *
 * <pre>
 * &lt;complexType name="Customer"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="href" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="Customer", propOrder = { "id", "href", "name" }, namespace="http://www.tmforum.org")
@JsonInclude(value = Include.NON_NULL)
public class Customer implements Serializable {

    private final static long serialVersionUID = 11L;
    protected String id;
    protected String href;
    protected String name;

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
}
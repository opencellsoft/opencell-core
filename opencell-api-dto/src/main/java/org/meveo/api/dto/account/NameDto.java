package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class NameDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Name")
@XmlAccessorType(XmlAccessType.FIELD)
public class NameDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4516337040269767031L;

    /** The title. */
    private String title;
    
    /** The first name. */
    private String firstName;

    /** The last name. */
    @XmlElement(required = true)
    private String lastName;

    /**
     * Instantiates a new name dto.
     */
    public NameDto() {

    }

    /**
     * Instantiates a new name dto.
     *
     * @param name the name entity
     */
    public NameDto(org.meveo.model.shared.Name name) {
        if (name != null) {
            firstName = name.getFirstName();
            lastName = name.getLastName();
            if (name.getTitle() != null) {
                title = name.getTitle().getCode();
            }
        }
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Name [title=" + title + ", firstName=" + firstName + ", lastName=" + lastName + "]";
    }

}
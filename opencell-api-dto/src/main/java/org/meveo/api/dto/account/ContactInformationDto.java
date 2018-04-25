package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.model.shared.ContactInformation;

/**
 * The Class ContactInformationDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactInformationDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5401291437074205081L;

    /** The email. */
    protected String email;
    
    /** The phone. */
    protected String phone;
    
    /** The mobile. */
    protected String mobile;
    
    /** The fax. */
    protected String fax;

    /**
     * Instantiates a new contact information dto.
     */
    public ContactInformationDto() {

    }

    /**
     * Instantiates a new contact information dto.
     *
     * @param contactInformation the contactInformation entity
     */
    public ContactInformationDto(ContactInformation contactInformation) {
        email = contactInformation.getEmail();
        phone = contactInformation.getPhone();
        mobile = contactInformation.getMobile();
        fax = contactInformation.getFax();
    }

    /**
     * Gets the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone.
     *
     * @param phone the new phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets the mobile.
     *
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Sets the mobile.
     *
     * @param mobile the new mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * Gets the fax.
     *
     * @return the fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * Sets the fax.
     *
     * @param fax the new fax
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    @Override
    public String toString() {
        return "ContactInformationDto [email=" + email + ", phone=" + phone + ", mobile=" + mobile + ", fax=" + fax + "]";
    }

}

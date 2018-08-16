package org.meveo.api.dto.document.sign;

import org.meveo.api.dto.BaseEntityDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A DTO class for a Signature procedure member
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class SignMemberDto extends BaseEntityDto { 
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    public SignMemberDto () {
    }
    
    public SignMemberDto (String user) {
        this.user = user;
    }
    
    /** The id. */
    private String id;
    
    /** The firstname. */
    private String firstname;
    
    /** The lastname. */
    private String lastname;
    
    /** The email. */
    private String email;
    
    /** The phone. */
    private String phone;
    
    /** The user id for internal member */
    private String user;
    
    /** The internal. */
    private Boolean internal;
    
    /**
     * Gets the firstname.
     *
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }
    
    /**
     * Gets the lastname.
     *
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
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
     * Gets the phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Sets the firstname.
     *
     * @param firstname the firstname to set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    
    /**
     * Sets the lastname.
     *
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    /**
     * Sets the email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Sets the phone.
     *
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the internal
     */
    public Boolean getInternal() {
        return internal;
    }

    /**
     * @param internal the internal to set
     */
    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}

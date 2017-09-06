package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.crm.ProviderContact;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 1:28:29 AM
 *
 */
@XmlRootElement(name = "ProviderContract")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProviderContactDto extends BaseDto {

    private static final long serialVersionUID = -763450889692487278L;

    @XmlAttribute(required = true)
    private String code;
    @XmlAttribute
    private String description;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String mobile;
    private String fax;
    private String genericMail;

    private AddressDto addressDto = new AddressDto();

    public ProviderContactDto() {
        super();
    }

    public ProviderContactDto(ProviderContact providerContact) {
        this.code = providerContact.getCode();
        this.description = providerContact.getDescription();
        this.firstName = providerContact.getFirstName();
        this.lastName = providerContact.getLastName();
        this.email = providerContact.getEmail();
        this.phone = providerContact.getPhone();
        this.mobile = providerContact.getMobile();
        this.fax = providerContact.getFax();
        this.genericMail = providerContact.getGenericMail();
        if (providerContact.getAddress() != null) {
            this.addressDto = new AddressDto(providerContact.getAddress());
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getGenericMail() {
        return genericMail;
    }

    public void setGenericMail(String genericMail) {
        this.genericMail = genericMail;
    }

    public AddressDto getAddressDto() {
        if (addressDto == null) {
            addressDto = new AddressDto();
        }
        return addressDto;
    }

    public void setAddressDto(AddressDto addressDto) {
        this.addressDto = addressDto;
    }
}

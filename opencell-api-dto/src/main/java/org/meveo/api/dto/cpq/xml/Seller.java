package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Seller {
    @XmlAttribute
    private String description;
    @XmlAttribute
    private String legalType;
    @XmlAttribute
    private String registrationNo;
    @XmlAttribute
    private String vatNo;
    protected ContactInformation contactInformation;
    protected Address address;

    public Seller(org.meveo.model.admin.Seller seller) {
        if (seller == null) {
            return;
        }

        this.description = seller.getDescription();
        this.legalType = seller.getLegalType();
       // this.registrationNo = seller.getRegistrationNo();
        this.vatNo = seller.getVatNo();

        if (seller.getContactInformation() != null) {
            this.contactInformation = new ContactInformation(seller.getContactInformation().getEmail(),
                    seller.getContactInformation().getPhone(), seller.getContactInformation().getMobile(),
                    seller.getContactInformation().getFax());
        }
        this.address = new Address(seller.getAddress());
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ContactInformation getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(ContactInformation contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLegalType() {
        return legalType;
    }

    public void setLegalType(String legalType) {
        this.legalType = legalType;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }
}

package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Seller {
    @XmlAttribute
    private String registrationNo;
    @XmlAttribute
    private String vatNo;
    protected ContactInformation contactInformation;
    protected Address address;

    public Seller(org.meveo.model.admin.Seller seller) {
        this.registrationNo = seller.getRegistrationNo();
        this.vatNo = seller.getVatNo();
        this.contactInformation = new ContactInformation(seller.getContactInformation().getEmail(),
                seller.getContactInformation().getPhone(), seller.getContactInformation().getMobile(),
                seller.getContactInformation().getFax());
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
}

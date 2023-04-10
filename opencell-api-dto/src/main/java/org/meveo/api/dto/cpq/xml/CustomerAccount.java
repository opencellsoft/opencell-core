package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerAccount {
    protected Name name;
    protected Address address;

    public CustomerAccount(org.meveo.model.payments.CustomerAccount customerAccount) {
        this.name = new Name(customerAccount.getName());
        this.address = new Address(customerAccount.getAddress());
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

}

package org.meveo.model.crm;

import java.io.Serializable;
import java.util.Objects;

public class CustomerContactId implements Serializable {

    private Long contact;

    private Long customer;

    public Long getContact() {
        return contact;
    }

    public void setContact(Long contact) {
        this.contact = contact;
    }

    public Long getCustomer() {
        return customer;
    }

    public void setCustomer(Long customer) {
        this.customer = customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerContactId)) return false;
        CustomerContactId that = (CustomerContactId) o;
        return Objects.equals(getContact(), that.getContact()) &&
                Objects.equals(getCustomer(), that.getCustomer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContact(), getCustomer());
    }
}

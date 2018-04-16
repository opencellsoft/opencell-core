package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class CustomersDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
// @FilterResults(propertyToFilter = "customer", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = Customer.class) })
public class CustomersDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1693325835765290126L;

    /** The customer. */
    private List<CustomerDto> customer;
    
    /** The total number of records. */
    private Long totalNumberOfRecords;

    /**
     * Gets the customer.
     *
     * @return the customer
     */
    public List<CustomerDto> getCustomer() {
        if (customer == null) {
            customer = new ArrayList<CustomerDto>();
        }

        return customer;
    }

    /**
     * Sets the customer.
     *
     * @param customer the new customer
     */
    public void setCustomer(List<CustomerDto> customer) {
        this.customer = customer;
    }

    /**
     * Gets the total number of records.
     *
     * @return the total number of records
     */
    public Long getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    /**
     * Sets the total number of records.
     *
     * @param totalNumberOfRecords the new total number of records
     */
    public void setTotalNumberOfRecords(Long totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
    }
    
    @Override
    public String toString() {
        return "CustomersDto [customer=" + customer + "]";
    }
}
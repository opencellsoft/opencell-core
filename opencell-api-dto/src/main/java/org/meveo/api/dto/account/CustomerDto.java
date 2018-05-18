package org.meveo.api.dto.account;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class CustomerDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "Customer")
@XmlAccessorType(XmlAccessType.FIELD)
// @FilterResults(propertyToFilter = "customerAccounts.customerAccount", itemPropertiesToFilter = { @FilterProperty(property = "code", entityClass = CustomerAccount.class) })
public class CustomerDto extends AccountDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3243716253817571391L;

    /** The customer category. */
    @XmlElement(required = true)
    private String customerCategory;

    /** The customer brand. */
    @XmlElement()
    private String customerBrand;

    /** The seller. */
    @XmlElement(required = true)
    private String seller;

    /** The mandate identification. */
    private String mandateIdentification = "";
    
    /** The mandate date. */
    private Date mandateDate;
    
    /** The vat no. */
    private String vatNo;
    
    /** The registration no. */
    private String registrationNo;

    /** The contact information. */
    private ContactInformationDto contactInformation;

    /**
     * Use for GET / LIST only.
     */
    private CustomerAccountsDto customerAccounts = new CustomerAccountsDto();

    /**
     * Instantiates a new customer dto.
     */
    public CustomerDto() {
        super();
    }

    /**
     * Gets the customer category.
     *
     * @return the customer category
     */
    public String getCustomerCategory() {
        return customerCategory;
    }

    /**
     * Sets the customer category.
     *
     * @param customerCategory the new customer category
     */
    public void setCustomerCategory(String customerCategory) {
        this.customerCategory = customerCategory;
    }

    /**
     * Gets the seller.
     *
     * @return the seller
     */
    public String getSeller() {
        return seller;
    }

    /**
     * Sets the seller.
     *
     * @param seller the new seller
     */
    public void setSeller(String seller) {
        this.seller = seller;
    }

    /**
     * Gets the customer brand.
     *
     * @return the customer brand
     */
    public String getCustomerBrand() {
        return customerBrand;
    }

    /**
     * Sets the customer brand.
     *
     * @param customerBrand the new customer brand
     */
    public void setCustomerBrand(String customerBrand) {
        this.customerBrand = customerBrand;
    }

    /**
     * Gets the customer accounts.
     *
     * @return the customer accounts
     */
    public CustomerAccountsDto getCustomerAccounts() {
        return customerAccounts;
    }

    /**
     * Sets the customer accounts.
     *
     * @param customerAccounts the new customer accounts
     */
    public void setCustomerAccounts(CustomerAccountsDto customerAccounts) {
        this.customerAccounts = customerAccounts;
    }

    /**
     * Gets the contact information.
     *
     * @return the contact information
     */
    public ContactInformationDto getContactInformation() {
        return contactInformation;
    }

    /**
     * Sets the contact information.
     *
     * @param contactInformation the new contact information
     */
    public void setContactInformation(ContactInformationDto contactInformation) {
        this.contactInformation = contactInformation;
    }

    /**
     * Gets the mandate identification.
     *
     * @return the mandate identification
     */
    public String getMandateIdentification() {
        return mandateIdentification;
    }

    /**
     * Sets the mandate identification.
     *
     * @param mandateIdentification the new mandate identification
     */
    public void setMandateIdentification(String mandateIdentification) {
        this.mandateIdentification = mandateIdentification;
    }

    /**
     * Gets the mandate date.
     *
     * @return the mandate date
     */
    public Date getMandateDate() {
        return mandateDate;
    }

    /**
     * Sets the mandate date.
     *
     * @param mandateDate the new mandate date
     */
    public void setMandateDate(Date mandateDate) {
        this.mandateDate = mandateDate;
    }

    /**
     * Gets the registration no.
     *
     * @return the registration no
     */
    public String getRegistrationNo() {
        return registrationNo;
    }

    /**
     * Sets the registration no.
     *
     * @param registrationNo the new registration no
     */
    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    /**
     * Gets the vat no.
     *
     * @return the vat no
     */
    public String getVatNo() {
        return vatNo;
    }

    /**
     * Sets the vat no.
     *
     * @param vatNo the new vat no
     */
    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    @Override
    public String toString() {
        return "CustomerDto [customerCategory=" + customerCategory + ", customerBrand=" + customerBrand + ", seller=" + seller + ", mandateIdentification=" + mandateIdentification
                + ", mandateDate=" + mandateDate + ", contactInformation=" + contactInformation + ", customerAccounts=" + customerAccounts + "]";
    }
}
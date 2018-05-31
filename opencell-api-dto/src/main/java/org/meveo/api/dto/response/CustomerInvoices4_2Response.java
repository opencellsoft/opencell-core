package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.invoice.Invoice4_2Dto;

/**
 * The Class CustomerInvoices4_2Response.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CustomerInvoices4_2Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerInvoices4_2Response extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -954637537391623233L;

    /** The Customer invoice dto list. */
    private List<Invoice4_2Dto> CustomerInvoiceDtoList;

    /**
     * Instantiates a new customer invoices 4 2 response.
     */
    public CustomerInvoices4_2Response() {
        super();
    }

    /**
     * Gets the customer invoice dto list.
     *
     * @return the customer invoice dto list
     */
    public List<Invoice4_2Dto> getCustomerInvoiceDtoList() {
        return CustomerInvoiceDtoList;
    }

    /**
     * Sets the customer invoice dto list.
     *
     * @param customerInvoiceDtoList the new customer invoice dto list
     */
    public void setCustomerInvoiceDtoList(List<Invoice4_2Dto> customerInvoiceDtoList) {
        CustomerInvoiceDtoList = customerInvoiceDtoList;
    }


    @Override
    public String toString() {
        return "CustomerInvoicesResponse [CustomerInvoiceDtoList=" + CustomerInvoiceDtoList + ", toString()=" + super.toString() + "]";
    }

}

package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.invoice.InvoiceDto;

/**
 * The Class CustomerInvoicesResponse.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CustomerInvoicesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerInvoicesResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -954637537391623233L;

    /** The Customer invoice dto list. */
    private List<InvoiceDto> CustomerInvoiceDtoList;

    /**
     * Instantiates a new customer invoices response.
     */
    public CustomerInvoicesResponse() {
        super();
    }

    /**
     * Gets the customer invoice dto list.
     *
     * @return the customer invoice dto list
     */
    public List<InvoiceDto> getCustomerInvoiceDtoList() {
        return CustomerInvoiceDtoList;
    }

    /**
     * Sets the customer invoice dto list.
     *
     * @param customerInvoiceDtoList the new customer invoice dto list
     */
    public void setCustomerInvoiceDtoList(List<InvoiceDto> customerInvoiceDtoList) {
        CustomerInvoiceDtoList = customerInvoiceDtoList;
    }


    @Override
    public String toString() {
        return "CustomerInvoicesResponse [CustomerInvoiceDtoList=" + CustomerInvoiceDtoList + ", toString()=" + super.toString() + "]";
    }

}

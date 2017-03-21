package org.meveo.api.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.invoice.Invoice4_2Dto;

@XmlRootElement(name = "CustomerInvoices4_2Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerInvoices4_2Response extends BaseResponse {

	private static final long serialVersionUID = -954637537391623233L;

	private List<Invoice4_2Dto> CustomerInvoiceDtoList;

	public CustomerInvoices4_2Response() {
		super();
	}

	public List<Invoice4_2Dto> getCustomerInvoiceDtoList() {
		return CustomerInvoiceDtoList;
	}

	public void setCustomerInvoiceDtoList(List<Invoice4_2Dto> customerInvoiceDtoList) {
		CustomerInvoiceDtoList = customerInvoiceDtoList;
	}

	@Override
	public String toString() {
		return "CustomerInvoicesResponse [CustomerInvoiceDtoList=" + CustomerInvoiceDtoList + ", toString()="
				+ super.toString() + "]";
	}

}

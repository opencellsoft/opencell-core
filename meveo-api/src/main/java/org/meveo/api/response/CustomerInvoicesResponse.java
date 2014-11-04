package org.meveo.api.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.InvoiceDto;

@XmlRootElement(name = "CustomerInvoicesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerInvoicesResponse extends BaseResponse {

	private static final long serialVersionUID = -954637537391623233L;

	private List<InvoiceDto> CustomerInvoiceDtoList;

	public CustomerInvoicesResponse() {
		super();
	}

	public List<InvoiceDto> getCustomerInvoiceDtoList() {
		return CustomerInvoiceDtoList;
	}

	public void setCustomerInvoiceDtoList(
			List<InvoiceDto> customerInvoiceDtoList) {
		CustomerInvoiceDtoList = customerInvoiceDtoList;
	}

}

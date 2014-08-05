package org.meveo.api.rest.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.dto.InvoiceDto;


@XmlRootElement(name = "customerInvoicesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomerInvoicesResponse {

	private ActionStatus actionStatus = new ActionStatus(
			ActionStatusEnum.SUCCESS, "");
	private List<InvoiceDto> CustomerInvoiceDtoList;

	public CustomerInvoicesResponse() {

	}

	public ActionStatus getActionStatus() {
		return actionStatus;
	}

	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	public List<InvoiceDto> getCustomerInvoiceDtoList() {
		return CustomerInvoiceDtoList;
	}

	public void setCustomerInvoiceDtoList(
			List<InvoiceDto> customerInvoiceDtoList) {
		CustomerInvoiceDtoList = customerInvoiceDtoList;
	}

	

}

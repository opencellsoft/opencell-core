package org.meveo.api.dto.response.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ContractItemDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetContractLineDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetContractLineDtoResponse extends BaseResponse{

	private ContractItemDto contractItem;
	
	public GetContractLineDtoResponse() {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	/**
	 * @return the contractItem
	 */
	public ContractItemDto getContractItem() {
		return contractItem;
	}

	/**
	 * @param contractItem the contractItem to set
	 */
	public void setContractItem(ContractItemDto contractItem) {
		this.contractItem = contractItem;
	}


	
	
}

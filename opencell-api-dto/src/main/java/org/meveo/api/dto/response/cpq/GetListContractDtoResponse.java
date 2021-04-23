package org.meveo.api.dto.response.cpq;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ContractDto;
import org.meveo.api.dto.response.BaseResponse;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetListContractDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListContractDtoResponse extends BaseResponse{

	private List<ContractDto> contracts;
	
	public GetListContractDtoResponse() {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	/**
	 * @return the contracts
	 */
	public List<ContractDto> getContracts() {
		return contracts;
	}

	/**
	 * @param contracts the contracts to set
	 */
	public void setContracts(List<ContractDto> contracts) {
		this.contracts = contracts;
	}

	

	
	
}

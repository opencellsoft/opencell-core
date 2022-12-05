package org.meveo.api.dto.response.cpq;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.cpq.ContractDto;
import org.meveo.api.dto.cpq.TagDto;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.model.cpq.contract.Contract;



/**
 * @author Tarik F.
 *
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "GetContractDtoResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetContractDtoResponse extends BaseResponse{

	private ContractDto contract;
	
	public GetContractDtoResponse() {
		this.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);
	}

	/**
	 * @return the contract
	 */
	public ContractDto getContract() {
		return contract;
	}

	/**
	 * @param contract the contract to set
	 */
	public void setContract(ContractDto contract) {
		this.contract = contract;
	}

	
	
}

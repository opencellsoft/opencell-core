package org.meveo.api.dto.cpq;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@SuppressWarnings("serial")
@XmlRootElement(name = "ContractListResponsDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContractListResponsDto extends SearchResponse {


	
    /** list of contract **/
    private ContractListDto contract;


	/**
	 * @return the contracts
	 */
	public ContractListDto getContracts() {
		if(contract == null)
			contract = new ContractListDto();
		return contract;
	}

	/**
	 * @param contracts the contracts to set
	 */
	public void setContracts(ContractListDto contracts) {
		this.contract = contracts;
	}
	
	
	
}

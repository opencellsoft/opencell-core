package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ContractListDto {


    /** The list size. */
    private int listSize;
	
    /** list of contract **/
    private List<ContractDto> contracts = new ArrayList<>();

	/**
	 * @return the listSize
	 */
	public int getListSize() {
		return contracts.size();
	}

	/**
	 * @param listSize the listSize to set
	 */
	public void setListSize(int listSize) {
		this.listSize = listSize;
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

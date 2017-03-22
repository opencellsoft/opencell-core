package org.meveo.api.dto.usage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

@XmlRootElement(name = "ChargeAggregate")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeAggregateDto extends BaseDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String description;
	private String quantity;
	private String amount;
	
		
	public ChargeAggregateDto(){
		
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the quantity
	 */
	public String getQuantity() {
		return quantity;
	}


	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}


	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}


	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	
}

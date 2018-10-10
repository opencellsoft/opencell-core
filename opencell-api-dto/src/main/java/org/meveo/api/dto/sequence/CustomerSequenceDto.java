package org.meveo.api.dto.sequence;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.BusinessEntityDto;

/**
 * Customer Sequence value DTO representation.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
public class CustomerSequenceDto extends BusinessEntityDto {

	private static final long serialVersionUID = 7614738733647971026L;

	private GenericSequenceDto genericSequence;

	/** Code of the seller */
	@NotNull
	private String seller;

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	public GenericSequenceDto getGenericSequence() {
		return genericSequence;
	}

	public void setGenericSequence(GenericSequenceDto genericSequence) {
		this.genericSequence = genericSequence;
	}

}

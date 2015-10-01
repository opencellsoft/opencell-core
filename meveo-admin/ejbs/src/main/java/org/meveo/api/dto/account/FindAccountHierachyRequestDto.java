package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "FindAccountHierachyRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class FindAccountHierachyRequestDto implements Serializable {

	private static final long serialVersionUID = 9110625442489443755L;

	/**
	 * Possible values.
	 * CUST = 1;
	 * CA = 2;
	 * BA = 4;
	 * UA = 8;
	**/
	private int level;
	public static List<Integer> VALID_LEVEL_VALUES = Arrays.asList(1, 2, 4, 8); 
	private NameDto name;
	private AddressDto address;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public NameDto getName() {
		return name;
	}

	public void setName(NameDto name) {
		this.name = name;
	}

	public AddressDto getAddress() {
		return address;
	}

	public void setAddress(AddressDto address) {
		this.address = address;
	}

}

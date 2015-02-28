package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.AccessesDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListAccessResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListAccessResponseDto extends BaseResponse {

	private static final long serialVersionUID = -2223795184710609153L;

	private AccessesDto accesses;

	public AccessesDto getAccesses() {
		return accesses;
	}

	public void setAccesses(AccessesDto accesses) {
		this.accesses = accesses;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "ListAccessResponseDto [accesses=" + accesses + ", toString()=" + super.toString() + "]";
	}

}

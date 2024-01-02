package org.meveo.api.dto.account;

import org.meveo.model.RegistrationNumber;

import java.io.Serializable;
import java.util.Objects;

public class RegistrationNumberDto  implements Serializable {
	
	private String registrationNo;
	private String isoIcdCode;
	
	
	public RegistrationNumberDto() {}
	public RegistrationNumberDto(RegistrationNumber registrationNumber) {
		this.registrationNo = registrationNumber.getRegistrationNo();
		this.isoIcdCode = registrationNumber.getIsoIcd() != null ? registrationNumber.getIsoIcd().getCode() : null;
	}
	
	public String getRegistrationNo() {
		return registrationNo;
	}
	
	public void setRegistrationNo(String registationNumber) {
		this.registrationNo = registationNumber;
	}
	
	public String getIsoIcdCode() {
		return isoIcdCode;
	}
	
	public void setIsoIcdCode(String isoIcdCode) {
		this.isoIcdCode = isoIcdCode;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof RegistrationNumberDto)) return false;
		RegistrationNumberDto that = (RegistrationNumberDto) o;
		return Objects.equals(getRegistrationNo(), that.getRegistrationNo()) && Objects.equals(getIsoIcdCode(), that.getIsoIcdCode());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getRegistrationNo(), getIsoIcdCode());
	}
}

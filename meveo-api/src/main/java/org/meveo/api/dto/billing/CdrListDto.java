package org.meveo.api.dto.billing;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.BaseDto;


@XmlRootElement(name = "CdrList")
@XmlAccessorType(XmlAccessType.FIELD)
public class CdrListDto extends BaseDto {

	private static final long serialVersionUID = 8776429565161215766L;

	private List<String> cdr;

	@XmlTransient
	private String ipAddress;

	public List<String> getCdr() {
		return cdr;
	}

	public void setCdr(List<String> cdr) {
		this.cdr = cdr;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}

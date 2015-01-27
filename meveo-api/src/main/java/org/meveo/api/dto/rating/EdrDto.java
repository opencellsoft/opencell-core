package org.meveo.api.dto.rating;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Edr")
@XmlAccessorType(XmlAccessType.FIELD)
public class EdrDto extends BaseDto {

	private static final long serialVersionUID = 8776429565161215766L;

	private List<String> edrs;

	@XmlTransient
	private String ipAddress;

	public List<String> getEdrs() {
		return edrs;
	}

	public void setEdrs(List<String> edrs) {
		this.edrs = edrs;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}

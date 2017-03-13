package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "Accesses")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessesDto implements Serializable {

	private static final long serialVersionUID = 1614784156576503978L;

	private List<AccessDto> access;

	public List<AccessDto> getAccess() {
		if (access == null) {
			access = new ArrayList<AccessDto>();
		}

		return access;
	}

	public void setAccess(List<AccessDto> access) {
		this.access = access;
	}

	@Override
	public String toString() {
		return "AccessesDto [access=" + access + "]";
	}

}

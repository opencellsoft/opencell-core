package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Tony Alejandro.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ParentEntitiesDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ParentEntityDto> parent;

	public List<ParentEntityDto> getParent() {
		if(parent == null) {
			parent = new ArrayList<>();
		}
		return parent;
	}

	public void setParent(List<ParentEntityDto> parent) {
		this.parent = parent;
	}
}

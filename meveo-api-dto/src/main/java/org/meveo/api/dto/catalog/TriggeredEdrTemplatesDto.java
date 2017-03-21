package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "TriggeredEdrTemplates")
@XmlAccessorType(XmlAccessType.FIELD)
public class TriggeredEdrTemplatesDto implements Serializable {

	private static final long serialVersionUID = 5790679004639676207L;

	private List<TriggeredEdrTemplateDto> triggeredEdr;

	public List<TriggeredEdrTemplateDto> getTriggeredEdr() {
		if (triggeredEdr == null)
			triggeredEdr = new ArrayList<TriggeredEdrTemplateDto>();
		return triggeredEdr;
	}

	public void setTriggeredEdr(List<TriggeredEdrTemplateDto> triggeredEdr) {
		this.triggeredEdr = triggeredEdr;
	}

	@Override
	public String toString() {
		return "TriggeredEdrTemplatesDto [triggeredEdr=" + triggeredEdr + ", toString()=" + super.toString() + "]";
	}

}

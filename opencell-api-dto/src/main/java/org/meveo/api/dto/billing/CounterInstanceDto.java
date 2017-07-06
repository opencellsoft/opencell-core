package org.meveo.api.dto.billing;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.model.billing.CounterInstance;

@XmlAccessorType(XmlAccessType.FIELD)
public class CounterInstanceDto implements Serializable{

	private static final long serialVersionUID = -72154111229222183L;
	
	public CounterInstanceDto() {}

	public CounterInstanceDto(CounterInstance counterInstance) {
		this.code = counterInstance.getCode();
		this.description = counterInstance.getDescription();
	}
	
	@XmlElement(required = true)
	private String code;
	
	@XmlElement(required = false)
	private String description;
	
}

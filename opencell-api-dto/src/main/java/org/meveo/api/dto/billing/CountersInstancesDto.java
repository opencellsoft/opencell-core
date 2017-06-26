package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class CountersInstancesDto implements Serializable{

	private static final long serialVersionUID = 49018302870831847L;
	
	private List<CounterInstanceDto> counterInstance;

	public List<CounterInstanceDto> getCounterInstance() {
		if (counterInstance == null) {
			counterInstance = new ArrayList<CounterInstanceDto>();
		}

		return counterInstance;
	}

	public void setCounterInstance(List<CounterInstanceDto> counterInstance) {
		this.counterInstance = counterInstance;
	}

	@Override
	public String toString() {
		return "CountersInstancesDto [counterInstance=" + counterInstance + "]";
	}

	
}

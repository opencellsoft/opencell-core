package org.meveo.api.dto.payment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="DunningPlans")
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningPlansDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5327236171806867044L;
	private List<DunningPlanDto> dunningPlans;
	public List<DunningPlanDto> getDunningPlans() {
		if(dunningPlans==null){
			dunningPlans=new ArrayList<DunningPlanDto>();
		}
		return dunningPlans;
	}
	public void setDunningPlans(List<DunningPlanDto> dunningPlans) {
		this.dunningPlans = dunningPlans;
	}
	

}


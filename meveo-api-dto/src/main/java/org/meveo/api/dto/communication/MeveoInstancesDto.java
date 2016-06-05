package org.meveo.api.dto.communication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 7:07:20 AM
 *
 */
@XmlRootElement(name="MeveoInstances")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoInstancesDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3430126501128806303L;
	private List<MeveoInstanceDto> meveoInstances;

	public List<MeveoInstanceDto> getMeveoInstances() {
		if(meveoInstances==null){
			meveoInstances=new ArrayList<MeveoInstanceDto>();
		}
		return meveoInstances;
	}

	public void setMeveoInstances(List<MeveoInstanceDto> meveoInstances) {
		this.meveoInstances = meveoInstances;
	}
	

}


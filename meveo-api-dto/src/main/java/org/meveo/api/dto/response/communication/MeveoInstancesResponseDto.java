package org.meveo.api.dto.response.communication;

import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.communication.MeveoInstancesDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 7:09:34 AM
 *
 */
@XmlRootElement(name="MeveoInstancesResponse")
public class MeveoInstancesResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5630363416438814136L;
	private MeveoInstancesDto meveoInstances;
	public MeveoInstancesDto getMeveoInstances() {
		return meveoInstances;
	}
	public void setMeveoInstances(MeveoInstancesDto meveoInstances) {
		this.meveoInstances = meveoInstances;
	}
	
}


package org.meveo.api.dto.response.communication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.communication.MeveoInstanceDto;
import org.meveo.api.dto.response.BaseResponse;
/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 7:08:28 AM
 *
 */
@XmlRootElement(name="MeveoInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoInstanceResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9151837082569910954L;
	private MeveoInstanceDto meveoInstance;

	public MeveoInstanceDto getMeveoInstance() {
		return meveoInstance;
	}

	public void setMeveoInstance(MeveoInstanceDto meveoInstance) {
		this.meveoInstance = meveoInstance;
	}
}


package org.meveo.api.dto.response.payment;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author TyshanaShi(tyshan@manaty.net)
 *
 */
@XmlRootElement(name="DDRequestLotOpsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestLotOpsResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 317999006133708067L;

	@XmlElementWrapper(name="ddrequestLotOps")
	@XmlElement(name="ddrequestLotOp")
	private List<DDRequestLotOpDto> ddrequestLotOps;

	public List<DDRequestLotOpDto> getDdrequestLotOps() {
		return ddrequestLotOps;
	}

	public void setDdrequestLotOps(List<DDRequestLotOpDto> ddrequestLotOps) {
		this.ddrequestLotOps = ddrequestLotOps;
	}

	
}


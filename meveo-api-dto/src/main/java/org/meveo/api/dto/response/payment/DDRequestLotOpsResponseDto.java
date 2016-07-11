package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.DDRequestLotOpsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 6:11:07 AM
 *
 */
@XmlRootElement(name="DDRequestLotOpsResponse")
public class DDRequestLotOpsResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 317999006133708067L;
	private DDRequestLotOpsDto ddrequestLotOps;

	public DDRequestLotOpsDto getDdrequestLotOps() {
		return ddrequestLotOps;
	}

	public void setDdrequestLotOps(DDRequestLotOpsDto ddrequestLotOps) {
		this.ddrequestLotOps = ddrequestLotOps;
	}
	
	
	
}


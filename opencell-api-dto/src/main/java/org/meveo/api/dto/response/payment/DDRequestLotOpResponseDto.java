package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.payment.DDRequestLotOpDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jul 11, 2016 7:23:52 PM
 *
 */
@XmlRootElement(name="DDRequestLotOpResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestLotOpResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1633154547155351550L;
	private DDRequestLotOpDto ddrequestLotOpDto;

	public DDRequestLotOpDto getDdrequestLotOpDto() {
		return ddrequestLotOpDto;
	}

	public void setDdrequestLotOpDto(DDRequestLotOpDto ddrequestLotOpDto) {
		this.ddrequestLotOpDto = ddrequestLotOpDto;
	}
}


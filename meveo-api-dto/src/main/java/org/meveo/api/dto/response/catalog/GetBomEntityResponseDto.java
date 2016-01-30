package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.BusinessOfferModelDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetBomEntityResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBomEntityResponseDto extends BaseResponse {

	private static final long serialVersionUID = -6781250820569600144L;

	private BusinessOfferModelDto bomEntity;

	public BusinessOfferModelDto getBomEntity() {
		return bomEntity;
	}

	public void setBomEntity(BusinessOfferModelDto bomEntity) {
		this.bomEntity = bomEntity;
	}

	@Override
	public String toString() {
		return "GetBomEntityResponseDto [bomEntity=" + bomEntity + "]";
	}

}

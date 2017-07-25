/**
 * 
 */
package org.meveo.api.dto.response.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.catalog.OneShotChargeTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author phung
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOneShotChargesResponseDto extends BaseResponse implements Serializable {
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -1931425527526409004L;
	
	
	private List<OneShotChargeTemplateDto> oneshotCharges;


	@Override
	public String toString() {
		return "GetOneShotChargesResponseDto [oneshotCharges=" + oneshotCharges + "]";
	}

	public List<OneShotChargeTemplateDto> getOneshotCharges() {
		if (oneshotCharges == null) {
			oneshotCharges = new ArrayList<OneShotChargeTemplateDto>();
		}
		return oneshotCharges;
	}

	public void setOneshotCharges(List<OneShotChargeTemplateDto> oneshotCharges) {
		this.oneshotCharges = oneshotCharges;
	}


}

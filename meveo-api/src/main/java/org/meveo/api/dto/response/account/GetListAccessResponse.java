package org.meveo.api.dto.response.account;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetListAccessResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetListAccessResponse extends BaseResponse {

	private static final long serialVersionUID = 1318221042952621487L;

	private List<AccessDto> accesses;

	public List<AccessDto> getAccesses() {
		return accesses;
	}

	public void setAccesses(List<AccessDto> accesses) {
		this.accesses = accesses;
	}

}

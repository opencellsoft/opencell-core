package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetAccessResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAccessResponse extends BaseResponse {

	private static final long serialVersionUID = 4586760970405068724L;

	private AccessDto access;

	public AccessDto getAccess() {
		return access;
	}

	public void setAccess(AccessDto access) {
		this.access = access;
	}

	@Override
	public String toString() {
		return "GetAccessResponse [access=" + access + ", toString()=" + super.toString() + "]";
	}

}

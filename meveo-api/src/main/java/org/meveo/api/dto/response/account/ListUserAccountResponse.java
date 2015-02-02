package org.meveo.api.dto.response.account;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListUserAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListUserAccountResponse extends BaseResponse {

	private static final long serialVersionUID = 260051867290645750L;

	private List<UserAccountDto> userAccounts;

	public List<UserAccountDto> getUserAccounts() {
		return userAccounts;
	}

	public void setUserAccounts(List<UserAccountDto> userAccounts) {
		this.userAccounts = userAccounts;
	}

}

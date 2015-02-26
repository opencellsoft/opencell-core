package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.UserAccountsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ListUserAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListUserAccountResponseDto extends BaseResponse {

	private static final long serialVersionUID = 260051867290645750L;

	private UserAccountsDto userAccounts = new UserAccountsDto();

	public UserAccountsDto getUserAccounts() {
		return userAccounts;
	}

	public void setUserAccounts(UserAccountsDto userAccounts) {
		this.userAccounts = userAccounts;
	}

	@Override
	public String toString() {
		return "ListUserAccountResponseDto [userAccounts=" + userAccounts + ", toString()=" + super.toString() + "]";
	}

}

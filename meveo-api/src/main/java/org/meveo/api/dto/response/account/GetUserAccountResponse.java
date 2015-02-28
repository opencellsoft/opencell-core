package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetUserAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetUserAccountResponse extends BaseResponse {

	private static final long serialVersionUID = -7424258671739985150L;

	private UserAccountDto userAccount;

	public UserAccountDto getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccountDto userAccount) {
		this.userAccount = userAccount;
	}

	@Override
	public String toString() {
		return "GetUserAccountResponse [userAccount=" + userAccount + ", toString()=" + super.toString() + "]";
	}

}

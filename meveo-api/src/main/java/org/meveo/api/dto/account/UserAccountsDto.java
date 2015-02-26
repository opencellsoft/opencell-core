package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Edward P. Legaspi
 **/
@XmlType(name = "UserAccounts")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccountsDto implements Serializable {

	private static final long serialVersionUID = -7157890853854236463L;

	private List<UserAccountDto> userAccount;

	public List<UserAccountDto> getUserAccount() {
		if(userAccount == null) {
			userAccount = new ArrayList<UserAccountDto>();	
		}
		
		return userAccount;
	}

	public void setUserAccount(List<UserAccountDto> userAccount) {
		this.userAccount = userAccount;
	}

	@Override
	public String toString() {
		return "UserAccountsDto [userAccount=" + userAccount + "]";
	}

}

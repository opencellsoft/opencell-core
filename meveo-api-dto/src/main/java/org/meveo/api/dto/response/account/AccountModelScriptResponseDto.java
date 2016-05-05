package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.script.AccountModelScriptDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "AccountModelScriptResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountModelScriptResponseDto extends BaseResponse {

	private static final long serialVersionUID = -5360300407045696599L;
	
	private AccountModelScriptDto accountModelScript;

	public AccountModelScriptDto getAccountModelScript() {
		return accountModelScript;
	}

	public void setAccountModelScript(AccountModelScriptDto accountModelScript) {
		this.accountModelScript = accountModelScript;
	}

	@Override
	public String toString() {
		return "AccountModelScriptResponseDto [accountModelScript=" + accountModelScript + ", toString()=" + super.toString() + "]";
	}

}

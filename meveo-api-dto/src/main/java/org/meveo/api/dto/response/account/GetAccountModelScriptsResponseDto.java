package org.meveo.api.dto.response.account;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.script.AccountModelScriptDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetAccountModelScriptsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAccountModelScriptsResponseDto extends BaseResponse {

	private static final long serialVersionUID = -6887184178536615526L;

	private List<AccountModelScriptDto> accountModelScripts;

	public List<AccountModelScriptDto> getAccountModelScripts() {
		return accountModelScripts;
	}

	public void setAccountModelScripts(List<AccountModelScriptDto> accountModelScripts) {
		this.accountModelScripts = accountModelScripts;
	}

	@Override
	public String toString() {
		return "GetAccountModelScriptsResponseDto [accountModelScripts=" + accountModelScripts + ", toString()=" + super.toString() + "]";
	}

}

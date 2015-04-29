package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/

@XmlType(name = "FindWalletOperations")
@XmlAccessorType(XmlAccessType.FIELD)
public class FindWalletOperationsDto extends BaseDto {

	private static final long serialVersionUID = 4342970913973071312L;

	private String status;
	private String walletTemplate;

	@XmlElement(required = true)
	private String userAccount;

	@Override
	public String toString() {
		return "FindWalletOperationsDto [status=" + status + ", walletTemplate=" + walletTemplate + ", userAccount=" + userAccount + "]";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getWalletTemplate() {
		return walletTemplate;
	}

	public void setWalletTemplate(String walletTemplate) {
		this.walletTemplate = walletTemplate;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

}

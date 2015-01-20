package org.meveo.api.dto.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.AccountEntity;
import org.meveo.model.billing.UserAccount;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "UserAccount")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccountDto extends AccountDto {

	private static final long serialVersionUID = -13552444627686818L;

	@XmlAttribute(required = true)
	private String billingAccount;

	public UserAccountDto() {
		super();
	}

	public UserAccountDto(UserAccount e) {
		super((AccountEntity) e);

		if (e.getBillingAccount() != null) {
			billingAccount = e.getBillingAccount().getCode();
		}
	}

	@Override
	public String toString() {
		return "UserAccountDto [billingAccount=" + billingAccount + ", toString()=" + super.toString() + "]";
	}

	public String getBillingAccount() {
		return billingAccount;
	}

	public void setBillingAccount(String billingAccount) {
		this.billingAccount = billingAccount;
	}

}

package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.ProviderContactsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 4:08:37 AM
 *
 */
@XmlRootElement(name = "ProviderContactsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProviderContactsResponseDto extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4646044595190907415L;
	private ProviderContactsDto providerContacts;
	public ProviderContactsDto getProviderContacts() {
		return providerContacts;
	}
	public void setProviderContacts(ProviderContactsDto providerContacts) {
		this.providerContacts = providerContacts;
	}
}


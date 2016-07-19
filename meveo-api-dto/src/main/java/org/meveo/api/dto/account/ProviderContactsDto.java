package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 1:19:48 AM
 *
 */

@XmlRootElement(name = "ProviderContacts")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProviderContactsDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6225404885017760717L;
	
	@XmlElement(name="providerContact")
	private List<ProviderContactDto> providerContacts;

	public List<ProviderContactDto> getProviderContacts() {
		if(providerContacts==null){
			providerContacts=new ArrayList<ProviderContactDto>();
		}
		return providerContacts;
	}

	public void setProviderContacts(List<ProviderContactDto> providerContacts) {
		this.providerContacts = providerContacts;
	}
	
}

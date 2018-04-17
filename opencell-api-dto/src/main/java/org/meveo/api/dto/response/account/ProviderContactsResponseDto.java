package org.meveo.api.dto.response.account;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.ProviderContactDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class ProviderContactsResponseDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 4:08:37 AM
 */
@XmlRootElement(name = "ProviderContactsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProviderContactsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4646044595190907415L;

    /** The provider contacts. */
    @XmlElementWrapper(name = "providerContacts")
    @XmlElement(name = "providerContact")
    private List<ProviderContactDto> providerContacts;

    /**
     * Gets the provider contacts.
     *
     * @return the provider contacts
     */
    public List<ProviderContactDto> getProviderContacts() {
        return providerContacts;
    }

    /**
     * Sets the provider contacts.
     *
     * @param providerContacts the new provider contacts
     */
    public void setProviderContacts(List<ProviderContactDto> providerContacts) {
        this.providerContacts = providerContacts;
    }
}
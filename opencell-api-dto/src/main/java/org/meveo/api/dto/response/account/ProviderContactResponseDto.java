package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.ProviderContactDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class ProviderContactResponseDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 3:54:23 AM
 */

@XmlRootElement(name = "ProviderContactResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProviderContactResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 881096582333172546L;
    
    /** The provider contact. */
    private ProviderContactDto providerContact;

    /**
     * Gets the provider contact.
     *
     * @return the provider contact
     */
    public ProviderContactDto getProviderContact() {
        return providerContact;
    }

    /**
     * Sets the provider contact.
     *
     * @param providerContact the new provider contact
     */
    public void setProviderContact(ProviderContactDto providerContact) {
        this.providerContact = providerContact;
    }

    @Override
    public String toString() {
        return "GetProviderContactResponseDto [providerContact=" + providerContact + "]";
    }
}
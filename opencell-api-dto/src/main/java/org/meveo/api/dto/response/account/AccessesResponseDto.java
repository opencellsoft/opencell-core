package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.AccessesDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class AccessesResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "AccessesResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessesResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2223795184710609153L;

    /** The accesses. */
    private AccessesDto accesses;

    /**
     * Gets the accesses.
     *
     * @return the accesses
     */
    public AccessesDto getAccesses() {
        return accesses;
    }

    /**
     * Sets the accesses.
     *
     * @param accesses the new accesses
     */
    public void setAccesses(AccessesDto accesses) {
        this.accesses = accesses;
    }

    /**
     * Gets the serialversionuid.
     *
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return "ListAccessResponseDto [accesses=" + accesses + ", toString()=" + super.toString() + "]";
    }
}
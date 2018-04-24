package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.AccessDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetAccessResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetAccessResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAccessResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4586760970405068724L;

    /** The access. */
    private AccessDto access;

    /**
     * Gets the access.
     *
     * @return the access
     */
    public AccessDto getAccess() {
        return access;
    }

    /**
     * Sets the access.
     *
     * @param access the new access
     */
    public void setAccess(AccessDto access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "GetAccessResponse [access=" + access + ", toString()=" + super.toString() + "]";
    }
}
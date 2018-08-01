package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class GetUserAccountResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetUserAccountResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetUserAccountResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7424258671739985150L;

    /** The user account. */
    private UserAccountDto userAccount;

    /**
     * Gets the user account.
     *
     * @return the user account
     */
    public UserAccountDto getUserAccount() {
        return userAccount;
    }

    /**
     * Sets the user account.
     *
     * @param userAccount the new user account
     */
    public void setUserAccount(UserAccountDto userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public String toString() {
        return "GetUserAccountResponse [userAccount=" + userAccount + ", toString()=" + super.toString() + "]";
    }
}
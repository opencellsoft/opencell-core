package org.meveo.api.dto.response.account;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.account.UserAccountsDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class UserAccountsResponseDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "UserAccountsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccountsResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 260051867290645750L;

    /** The user accounts. */
    private UserAccountsDto userAccounts = new UserAccountsDto();

    /**
     * Gets the user accounts.
     *
     * @return the user accounts
     */
    public UserAccountsDto getUserAccounts() {
        return userAccounts;
    }

    /**
     * Sets the user accounts.
     *
     * @param userAccounts the new user accounts
     */
    public void setUserAccounts(UserAccountsDto userAccounts) {
        this.userAccounts = userAccounts;
    }

    @Override
    public String toString() {
        return "ListUserAccountResponseDto [userAccounts=" + userAccounts + ", toString()=" + super.toString() + "]";
    }
}
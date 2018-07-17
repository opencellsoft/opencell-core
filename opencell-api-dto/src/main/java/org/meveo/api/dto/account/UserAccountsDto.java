package org.meveo.api.dto.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class UserAccountsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccountsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7157890853854236463L;

    /** The user account. */
    private List<UserAccountDto> userAccount;

    /**
     * Gets the user account.
     *
     * @return the user account
     */
    public List<UserAccountDto> getUserAccount() {
        if (userAccount == null) {
            userAccount = new ArrayList<>();
        }

        return userAccount;
    }

    /**
     * Sets the user account.
     *
     * @param userAccount the new user account
     */
    public void setUserAccount(List<UserAccountDto> userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public String toString() {
        return "UserAccountsDto [userAccount=" + userAccount + "]";
    }
}
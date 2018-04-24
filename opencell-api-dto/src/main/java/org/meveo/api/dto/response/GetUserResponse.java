package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.UserDto;

/**
 * The Class GetUserResponse.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetUserResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetUserResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6212357569361512794L;

    /** The user. */
    public UserDto user;

    /**
     * Instantiates a new gets the user response.
     */
    public GetUserResponse() {
        super();
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public UserDto getUser() {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user the new user
     */
    public void setUser(UserDto user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "GetUser4_3Response [user=" + user + ", toString()=" + super.toString() + "]";
    }
}
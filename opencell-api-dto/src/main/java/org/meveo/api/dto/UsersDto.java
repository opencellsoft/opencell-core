package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.SearchResponse;

/**
 * The Class UsersDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "Users")
@XmlAccessorType(XmlAccessType.FIELD)

public class UsersDto extends SearchResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2293858797034342550L;

    /** The users. */
    @XmlElementWrapper(name = "users")
    @XmlElement(name = "user")
    private List<UserDto> users = new ArrayList<>();

    /**
     * Gets the users.
     *
     * @return the users
     */
    public List<UserDto> getUsers() {
        return users;
    }

    /**
     * Sets the users.
     *
     * @param users the new users
     */
    public void setUsers(List<UserDto> users) {
        this.users = users;
    }
}
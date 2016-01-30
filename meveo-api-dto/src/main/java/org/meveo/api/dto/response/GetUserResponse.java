package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.UserDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetUserResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetUserResponse extends BaseResponse {

	private static final long serialVersionUID = 6212357569361512794L;

	public UserDto user;

	public GetUserResponse() {
		super();
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "GetUserResponse [user=" + user + ", toString()=" + super.toString() + "]";
	}

}

package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.User4_2Dto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetUserResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetUser4_2Response extends BaseResponse {

	private static final long serialVersionUID = 6212357569361512794L;

	public User4_2Dto user;

	public GetUser4_2Response() {
		super();
	}

	public User4_2Dto getUser() {
		return user;
	}

	public void setUser(User4_2Dto user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "GetUser4_3Response [user=" + user + ", toString()=" + super.toString() + "]";
	}

}

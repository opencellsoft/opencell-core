package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Name")
@XmlAccessorType(XmlAccessType.FIELD)
public class NameDto implements Serializable {

	private static final long serialVersionUID = 4516337040269767031L;

	private String title;
	private String firstName;

	@XmlElement(required = true)
	private String lastName;

	public NameDto() {

	}

	public NameDto(org.meveo.model.shared.Name e) {
		if (e != null) {
			firstName = e.getFirstName();
			lastName = e.getLastName();
			if (e.getTitle() != null) {
				title = e.getTitle().getCode();
			}
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		return "Name [title=" + title + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}

}

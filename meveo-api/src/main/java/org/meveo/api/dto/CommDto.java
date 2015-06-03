package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Nasseh
 **/
@XmlRootElement(name = "Comm")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommDto extends BaseDto {

	private static final long serialVersionUID = 725968016559888810L;

	@XmlAttribute(required = true)
	private String meveoInstanceCode;
	private String macAddress;
	private String subject;
	private String body;
	
	public CommDto() {

	}

	/**
	 * @return the meveoInstanceCode
	 */
	public String getMeveoInstanceCode() {
		return meveoInstanceCode;
	}

	/**
	 * @param meveoInstanceCode the meveoInstanceCode to set
	 */
	public void setMeveoInstanceCode(String meveoInstanceCode) {
		this.meveoInstanceCode = meveoInstanceCode;
	}

	/**
	 * @return the macAddress
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * @param macAddress the macAddress to set
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CommDto [meveoInstanceCode=" + meveoInstanceCode
				+ ", macAddress=" + macAddress + ", subject=" + subject
				+ ", body=" + body + "]";
	}
}

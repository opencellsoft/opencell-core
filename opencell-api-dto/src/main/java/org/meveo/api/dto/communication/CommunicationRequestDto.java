package org.meveo.api.dto.communication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.commons.utils.StringUtils;

@XmlRootElement(name = "CommunicationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommunicationRequestDto  extends BaseDto {

	private static final long serialVersionUID = 1L;

	@XmlElement(required = true)
	private String meveoInstanceCode;
	
	@XmlElement(required = true)
	private String macAddress;
	 
	@XmlElement(required = true)
	private String subject;
	
	private String body;
	
	private String additionnalInfo1;
	
	private String additionnalInfo2;
	
	private String additionnalInfo3;
	
	private String additionnalInfo4;
	
	public CommunicationRequestDto(){
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

	/**
	 * @return the additionnalInfo1
	 */
	public String getAdditionnalInfo1() {
		return additionnalInfo1;
	}

	/**
	 * @param additionnalInfo1 the additionnalInfo1 to set
	 */
	public void setAdditionnalInfo1(String additionnalInfo1) {
		this.additionnalInfo1 = additionnalInfo1;
	}

	/**
	 * @return the additionnalInfo2
	 */
	public String getAdditionnalInfo2() {
		return additionnalInfo2;
	}

	/**
	 * @param additionnalInfo2 the additionnalInfo2 to set
	 */
	public void setAdditionnalInfo2(String additionnalInfo2) {
		this.additionnalInfo2 = additionnalInfo2;
	}

	/**
	 * @return the additionnalInfo4
	 */
	public String getAdditionnalInfo4() {
		return additionnalInfo4;
	}

	/**
	 * @param additionnalInfo4 the additionnalInfo4 to set
	 */
	public void setAdditionnalInfo4(String additionnalInfo4) {
		this.additionnalInfo4 = additionnalInfo4;
	}

	/**
	 * @return the additionnalInfo3
	 */
	public String getAdditionnalInfo3() {
		return additionnalInfo3;
	}

	/**
	 * @param additionnalInfo3 the additionnalInfo3 to set
	 */
	public void setAdditionnalInfo3(String additionnalInfo3) {
		this.additionnalInfo3 = additionnalInfo3;
	}

	@Override
	public String toString() {
		return "CommunicationRequestDto [meveoInstanceCode=" + meveoInstanceCode + ", macAddress=" + macAddress
				+ ", subject=" + subject + ", body=" + body + ", additionnalInfo1=" + additionnalInfo1
				+ ", additionnalInfo2=" + additionnalInfo2 + ", additionnalInfo3=" + additionnalInfo3
				+ ", additionnalInfo4=" + additionnalInfo4 + "]";
	}
	

	public boolean isVaild() {
		return !StringUtils.isBlank(meveoInstanceCode) && !StringUtils.isBlank(subject);
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
}

package org.meveo.api.dto.usage;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

@XmlRootElement(name = "UsageRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageRequestDto extends BaseDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElement(required = true)
	private String userAccountCode;
	
	private Date   fromDate;
	
	private Date   toDate;
	
	public UsageRequestDto(){
		
	}



	/**
	 * @return the userAccountCode
	 */
	public String getUserAccountCode() {
		return userAccountCode;
	}



	/**
	 * @param userAccountCode the userAccountCode to set
	 */
	public void setUserAccountCode(String userAccountCode) {
		this.userAccountCode = userAccountCode;
	}



	/**
	 * @return the fromDate
	 */
	public Date getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	@Override
	public String toString() {
		return "UsageRequestDto [userAccountCode=" + userAccountCode + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
	}
	
	

}

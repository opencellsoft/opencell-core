package org.meveo.event.monitoring;

import java.util.Date;

import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;

@Named
public class BusinessExceptionEvent {

	public Date dateTime;
	public String meveoInstanceCode;
	public BusinessException businessException;
	
	public BusinessExceptionEvent(){
		
	}

	/**
	 * @return the dateTime
	 */
	public Date getDateTime() {
		return dateTime;
	}

	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * @return the meveoInstanceCode
	 */
	public String getMeveoInstanceCode() {
		return meveoInstanceCode;
	}

	/**
	 * @param meveoInstanceCode th meveoInstanceCode to set
	 */
	public void setMeveoInstanceCode(String meveoInstanceCode) {
		this.meveoInstanceCode = meveoInstanceCode;
	}

	/**
	 * @return the businessException
	 */
	public BusinessException getBusinessException() {
		return businessException;
	}

	/**
	 * @param businessException the businessException to set
	 */
	public void setBusinessException(BusinessException businessException) {
		this.businessException = businessException;
	}
	
	@Override
	public String toString() {
		return "{ dateTime:"+dateTime+", "
				+ "meveoInstanceCode:"+meveoInstanceCode+","
				+ " businessException:"+businessException+" }";
	}
}

package org.meveo.event.monitoring;

import java.util.Date;

import javax.inject.Named;

@Named
public class BusinessExceptionEvent {

	public Date dateTime;
	public String meveoInstanceCode;
	public Exception exception;
	
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
	 * @return the exception
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * @param exception the exception to set
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}
	
	@Override
	public String toString() {
		return "{ dateTime:"+dateTime+", "
				+ "meveoInstanceCode:"+meveoInstanceCode+","
				+ " exception:"+exception+" }";
	}
}

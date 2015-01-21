package org.meveo.api.dto.account;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.mediation.Access;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "Access")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccessDto extends BaseDto {

	private static final long serialVersionUID = 6495211234062070223L;

	@XmlAttribute(required = false)
	private Long accessId;
	
	private Date startDate;
	private Date endDate;

	@XmlAttribute(required = true)
	private String user;
	
	@XmlAttribute(required = true)
	private String subscription;

	public AccessDto() {

	}

	public AccessDto(Access e) {
		accessId = e.getId();
		startDate = e.getStartDate();
		endDate = e.getEndDate();
		user = e.getAccessUserId();
		if (e.getSubscription() != null) {
			subscription = e.getSubscription().getCode();
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "AccessDto [accessId=" + accessId + ", startDate=" + startDate + ", endDate=" + endDate + ", user="
				+ user + ", subscription=" + subscription + "]";
	}

	public Long getAccessId() {
		return accessId;
	}

	public void setAccessId(Long accessId) {
		this.accessId = accessId;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

}

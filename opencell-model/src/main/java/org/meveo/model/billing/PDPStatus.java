package org.meveo.model.billing;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "pdp_status")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "pdp_status_seq"), })
public class PDPStatus  extends AuditableEntity {
	
	@Enumerated(EnumType.STRING)
	@Column(name = "pdp_status", nullable = false)
	private PDPStatusEnum pdpStatus;
	
	@Column(name = "event_date", nullable = false)
	private Date eventDate;
	
	@Column(name = "origin", nullable = false)
	private String origin;
	
	public PDPStatusEnum getPdpStatus() {
		return pdpStatus;
	}
	
	public void setPdpStatus(PDPStatusEnum pdpStatus) {
		this.pdpStatus = pdpStatus;
	}
	
	public Date getEventDate() {
		return eventDate;
	}
	
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
}

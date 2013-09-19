package org.meveo.service.medina.impl;

import java.math.BigDecimal;
import java.util.Date;

public class EDRDAO {
	
	private String originBatch;
	private String originRecord;
	private Date eventDate;
	private BigDecimal quantity;
	private String parameter1;
	private String parameter2;
	private String parameter3;
	private String parameter4;
	public String getOriginBatch() {
		return originBatch;
	}
	public void setOriginBatch(String originBatch) {
		this.originBatch = originBatch;
	}
	public String getOriginRecord() {
		return originRecord;
	}
	public void setOriginRecord(String originRecord) {
		this.originRecord = originRecord;
	}
	public Date getEventDate() {
		return eventDate;
	}
	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}
	public BigDecimal getQuantity() {
		return quantity;
	}
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	public String getParameter1() {
		return parameter1;
	}
	public void setParameter1(String parameter1) {
		this.parameter1 = parameter1;
	}
	public String getParameter2() {
		return parameter2;
	}
	public void setParameter2(String parameter2) {
		this.parameter2 = parameter2;
	}
	public String getParameter3() {
		return parameter3;
	}
	public void setParameter3(String parameter3) {
		this.parameter3 = parameter3;
	}
	public String getParameter4() {
		return parameter4;
	}
	public void setParameter4(String parameter4) {
		this.parameter4 = parameter4;
	}

	
}

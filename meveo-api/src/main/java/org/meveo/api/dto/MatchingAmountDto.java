package org.meveo.api.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingAmount;
import org.meveo.model.payments.MatchingCode;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;

 
@XmlRootElement(name = "matchingAmount")
@XmlAccessorType(XmlAccessType.FIELD)
public class MatchingAmountDto{

	private static final long serialVersionUID = -137632696663739285L;
	 
	  
	private String matchingCode;  
	private BigDecimal matchingAmount;
	
	
	public String getMatchingCode() {
		return matchingCode;
	}
	public void setMatchingCode(String matchingCode) {
		this.matchingCode = matchingCode;
	}
	public BigDecimal getMatchingAmount() {
		return matchingAmount;
	}
	public void setMatchingAmount(BigDecimal matchingAmount) {
		this.matchingAmount = matchingAmount;
	}
	
	
	 
}

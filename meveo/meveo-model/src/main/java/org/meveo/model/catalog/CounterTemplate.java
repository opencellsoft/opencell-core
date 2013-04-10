package org.meveo.model.catalog;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "CAT_COUNTER_TEMPLATE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_COUNTER_TEMPLATE_SEQ")
public class CounterTemplate extends BusinessEntity {

	private static final long serialVersionUID = -1246995971618884001L;

	@Enumerated(EnumType.STRING)
    @Column(name = "COUNTER_TYPE")
    private CounterTypeEnum counterType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CALENDAR_ID")
    private Calendar calendar;
    
	@Column(name = "LEVEL", precision = 23, scale = 12)
	@Digits(integer = 23, fraction = 12)
	private BigDecimal level;
	
	@Column(name = "COUNTER_TYPE",length=20)
	@Size(min=0,max=20)
    private String unityDescription;

	public CounterTypeEnum getCounterType() {
		return counterType;
	}

	public void setCounterType(CounterTypeEnum counterType) {
		this.counterType = counterType;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public BigDecimal getLevel() {
		return level;
	}

	public void setLevel(BigDecimal level) {
		this.level = level;
	}

	public String getUnityDescription() {
		return unityDescription;
	}

	public void setUnityDescription(String unityDescription) {
		this.unityDescription = unityDescription;
	}
	
	
}

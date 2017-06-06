package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.catalog.TriggeredEDRTemplate;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "TriggeredEdrTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class TriggeredEdrTemplateDto extends BusinessDto {

	private static final long serialVersionUID = 5790679004639676207L;

	private String subscriptionEl;
	
	private String meveoInstanceCode;
	
	private String conditionEl;

	@XmlElement(required = true)
	private String quantityEl;

	private String param1El;
	private String param2El;
	private String param3El;
	private String param4El;

	public TriggeredEdrTemplateDto() {

	}

	public TriggeredEdrTemplateDto(TriggeredEDRTemplate e) {
		super(e);
		
		subscriptionEl = e.getSubscriptionEl();
		meveoInstanceCode = e.getMeveoInstance()==null?null:e.getMeveoInstance().getCode();
		conditionEl = e.getConditionEl();
		quantityEl = e.getQuantityEl();
		param1El = e.getParam1El();
		param2El = e.getParam2El();
		param3El = e.getParam3El();
		param4El = e.getParam4El();
	}

	public String getSubscriptionEl() {
		return subscriptionEl;
	}

	public void setSubscriptionEl(String subscriptionEl) {
		this.subscriptionEl = subscriptionEl;
	}

	public String getMeveoInstanceCode() {
		return meveoInstanceCode;
	}

	public void setMeveoInstanceCode(String meveoInstanceCode) {
		this.meveoInstanceCode = meveoInstanceCode;
	}

	public String getConditionEl() {
		return conditionEl;
	}

	public void setConditionEl(String conditionEl) {
		this.conditionEl = conditionEl;
	}

	public String getQuantityEl() {
		return quantityEl;
	}

	public void setQuantityEl(String quantityEl) {
		this.quantityEl = quantityEl;
	}

	public String getParam1El() {
		return param1El;
	}

	public void setParam1El(String param1El) {
		this.param1El = param1El;
	}

	public String getParam2El() {
		return param2El;
	}

	public void setParam2El(String param2El) {
		this.param2El = param2El;
	}

	public String getParam3El() {
		return param3El;
	}

	public void setParam3El(String param3El) {
		this.param3El = param3El;
	}

	public String getParam4El() {
		return param4El;
	}

	public void setParam4El(String param4El) {
		this.param4El = param4El;
	}

	@Override
	public String toString() {
		return "TriggeredEdrTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", subscriptionEl=" + subscriptionEl + ", conditionEl=" + conditionEl + ", quantityEl="
				+ quantityEl + ", param1El=" + param1El + ", param2El=" + param2El + ", param3El=" + param3El + ", param4El=" + param4El + "]";
	}

}

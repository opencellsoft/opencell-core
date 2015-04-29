package org.meveo.api.dto.catalog;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.catalog.UsageChargeTemplate;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "UsageChargeTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageChargeTemplateDto extends ChargeTemplateDto {

	private static final long serialVersionUID = -192169359113319490L;

	static String WILCARD = "";

	private BigDecimal unityMultiplicator = BigDecimal.ONE;
	private String unityDescription;
	private Integer unityFormatter;
	private int unityNbDecimal = 2;
	private String filterParam1 = WILCARD;
	private String filterParam2 = WILCARD;
	private String filterParam3 = WILCARD;
	private String filterParam4 = WILCARD;
	private String filterExpression = null;
	private int priority = 1;

	public UsageChargeTemplateDto() {

	}

	public UsageChargeTemplateDto(UsageChargeTemplate e) {
		setCode(e.getCode());
		setDescription(e.getDescription());
		setInvoiceSubCategory(e.getInvoiceSubCategory().getCode());
		setAmountEditable(e.getAmountEditable());
		setDisabled(e.isDisabled());
		unityDescription = e.getUnityDescription();
		if (e.getUnityFormatter() != null) {
			unityFormatter = e.getUnityFormatter().getId();
		}
		unityMultiplicator = e.getUnityMultiplicator();
		unityNbDecimal = e.getUnityNbDecimal();
		filterParam1 = e.getFilterParam1();
		filterParam2 = e.getFilterParam2();
		filterParam3 = e.getFilterParam3();
		filterParam4 = e.getFilterParam4();
		filterExpression = e.getFilterExpression();
		priority = e.getPriority();
	}

	public BigDecimal getUnityMultiplicator() {
		return unityMultiplicator;
	}

	public void setUnityMultiplicator(BigDecimal unityMultiplicator) {
		this.unityMultiplicator = unityMultiplicator;
	}

	public String getUnityDescription() {
		return unityDescription;
	}

	public void setUnityDescription(String unityDescription) {
		this.unityDescription = unityDescription;
	}

	public Integer getUnityFormatter() {
		return unityFormatter;
	}

	public void setUnityFormatter(Integer unityFormatter) {
		this.unityFormatter = unityFormatter;
	}

	public int getUnityNbDecimal() {
		return unityNbDecimal;
	}

	public void setUnityNbDecimal(int unityNbDecimal) {
		this.unityNbDecimal = unityNbDecimal;
	}

	public String getFilterParam1() {
		return filterParam1;
	}

	public void setFilterParam1(String filterParam1) {
		this.filterParam1 = filterParam1;
	}

	public String getFilterParam2() {
		return filterParam2;
	}

	public void setFilterParam2(String filterParam2) {
		this.filterParam2 = filterParam2;
	}

	public String getFilterParam3() {
		return filterParam3;
	}

	public void setFilterParam3(String filterParam3) {
		this.filterParam3 = filterParam3;
	}

	public String getFilterParam4() {
		return filterParam4;
	}

	public void setFilterParam4(String filterParam4) {
		this.filterParam4 = filterParam4;
	}

	public String getFilterExpression() {
		return filterExpression;
	}

	public void setFilterExpression(String filterExpression) {
		this.filterExpression = filterExpression;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "UsageChargeTemplateDto [unityMultiplicator="
				+ unityMultiplicator + ", unityDescription=" + unityDescription
				+ ", unityFormatter=" + unityFormatter + ", unityNbDecimal="
				+ unityNbDecimal + ", filterParam1=" + filterParam1
				+ ", filterParam2=" + filterParam2 + ", filterParam3="
				+ filterParam3 + ", filterParam4=" + filterParam4
				+ ", filterExpression=" + filterExpression + ", priority="
				+ priority + ", getCode()=" + getCode() + ", getDescription()="
				+ getDescription() + ", getLanguageDescriptions()="
				+ getLanguageDescriptions() + ", toString()="
				+ super.toString() + ", getAmountEditable()="
				+ getAmountEditable() + ", getInvoiceSubCategory()="
				+ getInvoiceSubCategory() + ", isDisabled()=" + isDisabled()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ "]";
	}
}

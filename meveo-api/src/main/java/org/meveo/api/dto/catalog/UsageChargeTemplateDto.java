package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

	static String WILCARD = null;

	@Size(min = 1, max = 20)
	@NotNull
	private String filterParam1 = WILCARD;
	
	@Size(min = 1, max = 20)
	@NotNull
	private String filterParam2 = WILCARD;
	
	@Size(min = 1, max = 20)
	@NotNull
	private String filterParam3 = WILCARD;
	
	@Size(min = 1, max = 20)
	@NotNull
	private String filterParam4 = WILCARD;
	
	@Size(max = 255)
	private String filterExpression = null;
	private int priority = 1;

	public UsageChargeTemplateDto() {

	}

	public UsageChargeTemplateDto(UsageChargeTemplate e) {
		super(e);
		filterParam1 = e.getFilterParam1();
		filterParam2 = e.getFilterParam2();
		filterParam3 = e.getFilterParam3();
		filterParam4 = e.getFilterParam4();
		filterExpression = e.getFilterExpression();
		priority = e.getPriority();
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
		return "UsageChargeTemplateDto [filterParam1=" + filterParam1 + ", filterParam2=" + filterParam2 + ", filterParam3=" + filterParam3 + ", filterParam4=" + filterParam4
				+ ", filterExpression=" + filterExpression + ", priority=" + priority + "]";
	}
}

/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.catalog;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;
import org.meveo.model.payments.CustomerAccount;

@Entity
@Table(name = "CAT_USAGE_DISCOUNT_PLAN_ITEM")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_USAGE_DISCOUNT_PLAN_ITEM_SEQ")
public class UsageDiscountPlanItem extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="servicetemplate_id")
    private ServiceTemplate serviceTemplate;

    @ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="customeraccount_id")
    private CustomerAccount customerAccount;
	
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

    @Column(name = "PRIORITY")
    private Integer priority;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private RatingUsageTypeEnum type;

    @Column(name = "NUMBER_PARAM_1_MIN")
    private Long numberParam1Min;

    @Column(name = "NUMBER_PARAM_1_MAX")
    private Long numberParam1Max;

    @Column(name = "STRING_PARAM_1")
    private String stringParam1;

    @Column(name = "STRING_PARAM_2")
    private String stringParam2;

    @Column(name = "STRING_PARAM_3")
    private String stringParam3;

    @Column(name = "BOOLEAN_PARAM_1")
    private Boolean booleanParam1;
    
    @Column(name = "PERCENTAGE")
    private Double percentage;

	public ServiceTemplate getServiceTemplate() {
		return serviceTemplate;
	}

	public void setServiceTemplate(ServiceTemplate serviceTemplate) {
		this.serviceTemplate = serviceTemplate;
	}

	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
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

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public RatingUsageTypeEnum getType() {
		return type;
	}

	public void setType(RatingUsageTypeEnum type) {
		this.type = type;
	}

	public Long getNumberParam1Min() {
		return numberParam1Min;
	}

	public void setNumberParam1Min(Long numberParam1Min) {
		this.numberParam1Min = numberParam1Min;
	}

	public Long getNumberParam1Max() {
		return numberParam1Max;
	}

	public void setNumberParam1Max(Long numberParam1Max) {
		this.numberParam1Max = numberParam1Max;
	}


	public String getStringParam1() {
		return stringParam1;
	}

	public void setStringParam1(String stringParam1) {
		this.stringParam1 = stringParam1;
	}

	public String getStringParam2() {
		return stringParam2;
	}

	public void setStringParam2(String stringParam2) {
		this.stringParam2 = stringParam2;
	}

	public String getStringParam3() {
		return stringParam3;
	}

	public void setStringParam3(String stringParam3) {
		this.stringParam3 = stringParam3;
	}

	public Boolean getBooleanParam1() {
		return booleanParam1;
	}

	public void setBooleanParam1(Boolean booleanParam1) {
		this.booleanParam1 = booleanParam1;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	
}

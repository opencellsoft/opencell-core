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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.AuditableEntity;
import org.meveo.model.payments.CustomerAccount;

@Entity
@Table(name = "CAT_USAGE_PRICE_PLAN_ITEM")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_USAGE_PRICE_PLAN_ITEM_SEQ")
public class UsagePricePlanItem extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "servicetemplate_id")
	private ServiceTemplate serviceTemplate;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "customeraccount_id")
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

	@Column(name = "NUMBER_PARAM_2_MIN")
	private Long numberParam2Min;

	@Column(name = "NUMBER_PARAM_3_MIN")
	private Long numberParam3Min;

	@Column(name = "NUMBER_PARAM_1_MAX")
	private Long numberParam1Max;

	@Column(name = "NUMBER_PARAM_2_MAX")
	private Long numberParam2Max;

	@Column(name = "NUMBER_PARAM_3_MAX")
	private Long numberParam3Max;

	@Column(name = "STRING_PARAM_1")
	private String stringParam1;

	@Column(name = "STRING_PARAM_2")
	private String stringParam2;

	@Column(name = "STRING_PARAM_3")
	private String stringParam3;

	@Column(name = "STRING_PARAM_4")
	private String stringParam4;

	@Column(name = "STRING_PARAM_5")
	private String stringParam5;

	@Column(name = "STRING_PARAM_6")
	private String stringParam6;

	@Column(name = "STRING_PARAM_7")
	private String stringParam7;

	@Column(name = "STRING_PARAM_8")
	private String stringParam8;

	@Column(name = "STRING_PARAM_9")
	private String stringParam9;

	@Column(name = "STRING_PARAM_10")
	private String stringParam10;

	@Column(name = "BOOLEAN_PARAM_1")
	private Boolean booleanParam1;

	@Column(name = "BOOLEAN_PARAM_2")
	private Boolean booleanParam2;

	@OneToOne(cascade = CascadeType.ALL)
	private PriceCode priceCode;

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

	public Long getNumberParam2Min() {
		return numberParam2Min;
	}

	public void setNumberParam2Min(Long numberParam2Min) {
		this.numberParam2Min = numberParam2Min;
	}

	public Long getNumberParam3Min() {
		return numberParam3Min;
	}

	public void setNumberParam3Min(Long numberParam3Min) {
		this.numberParam3Min = numberParam3Min;
	}

	public Long getNumberParam1Max() {
		return numberParam1Max;
	}

	public void setNumberParam1Max(Long numberParam1Max) {
		this.numberParam1Max = numberParam1Max;
	}

	public Long getNumberParam2Max() {
		return numberParam2Max;
	}

	public void setNumberParam2Max(Long numberParam2Max) {
		this.numberParam2Max = numberParam2Max;
	}

	public Long getNumberParam3Max() {
		return numberParam3Max;
	}

	public void setNumberParam3Max(Long numberParam3Max) {
		this.numberParam3Max = numberParam3Max;
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

	public String getStringParam4() {
		return stringParam4;
	}

	public void setStringParam4(String stringParam4) {
		this.stringParam4 = stringParam4;
	}

	public String getStringParam5() {
		return stringParam5;
	}

	public void setStringParam5(String stringParam5) {
		this.stringParam5 = stringParam5;
	}

	public String getStringParam6() {
		return stringParam6;
	}

	public void setStringParam6(String stringParam6) {
		this.stringParam6 = stringParam6;
	}

	public String getStringParam7() {
		return stringParam7;
	}

	public void setStringParam7(String stringParam7) {
		this.stringParam7 = stringParam7;
	}

	public String getStringParam8() {
		return stringParam8;
	}

	public void setStringParam8(String stringParam8) {
		this.stringParam8 = stringParam8;
	}

	public String getStringParam9() {
		return stringParam9;
	}

	public void setStringParam9(String stringParam9) {
		this.stringParam9 = stringParam9;
	}

	public String getStringParam10() {
		return stringParam10;
	}

	public void setStringParam10(String stringParam10) {
		this.stringParam10 = stringParam10;
	}

	public Boolean getBooleanParam1() {
		return booleanParam1;
	}

	public void setBooleanParam1(Boolean booleanParam1) {
		this.booleanParam1 = booleanParam1;
	}

	public Boolean getBooleanParam2() {
		return booleanParam2;
	}

	public void setBooleanParam2(Boolean booleanParam2) {
		this.booleanParam2 = booleanParam2;
	}

	public PriceCode getPriceCode() {
		return priceCode;
	}

	public void setPriceCode(PriceCode priceCode) {
		this.priceCode = priceCode;
	}

}

/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "cat_usage_charge_template")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "cat_usage_charge_template_seq"), })
@NamedQueries({ @NamedQuery(name = "UsageChargeTemplate.getWithTemplateEDR", query = "SELECT u FROM UsageChargeTemplate u join u.edrTemplates t WHERE :edrTemplate=t"
		+ " and u.disabled=false"),		
@NamedQuery(name = "usageChargeTemplate.getNbrUsagesChrgWithNotPricePlan", 
query = "select count (*) from UsageChargeTemplate u where u.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null)  "),

@NamedQuery(name = "usageChargeTemplate.getUsagesChrgWithNotPricePlan", 
query = "from UsageChargeTemplate u where u.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null) "),

@NamedQuery(name = "usageChargeTemplate.getNbrUsagesChrgNotAssociated", 
query = "select count(*) from UsageChargeTemplate u where (u.id not in (select serv.chargeTemplate from ServiceChargeTemplateUsage serv) "
		+ " OR u.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null)) "),
		
@NamedQuery(name = "usageChargeTemplate.getUsagesChrgNotAssociated", 
		query = "from UsageChargeTemplate u where (u.id not in (select serv.chargeTemplate from ServiceChargeTemplateUsage serv) "
				+ " OR u.code not in (select p.eventCode from  PricePlanMatrix p where p.eventCode is not null)) ")
})
public class UsageChargeTemplate extends ChargeTemplate {
	static String WILCARD = "";
	
	@Transient
	public static final String CHARGE_TYPE = "USAGE";

	private static final long serialVersionUID = 1L;

	@Column(name = "filter_param_1", length = 255)
	@Size(max = 255)
	private String filterParam1 = WILCARD;

	@Column(name = "filter_param_2", length = 255)
    @Size(max = 255)
	private String filterParam2 = WILCARD;

	@Column(name = "filter_param_3", length = 255)
    @Size(max = 255)
	private String filterParam3 = WILCARD;

	@Column(name = "filter_param_4", length = 255)
    @Size(max = 255)
	private String filterParam4 = WILCARD;

	@Column(name = "filter_expression", length=2000)
    @Size(max = 2000)
	private String filterExpression = null;

	@Column(name = "priority", columnDefinition = "int default 1")
	private int priority = 1;

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

	public String getChargeType() {
		return CHARGE_TYPE;
	}

}

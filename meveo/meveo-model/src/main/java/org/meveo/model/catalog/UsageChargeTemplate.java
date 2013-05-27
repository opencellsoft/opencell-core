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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "CAT_USAGE_CHARGE_TEMPLATE")
// @SequenceGenerator(name = "ID_GENERATOR", sequenceName =
// CAT_USAGE_CHARGE_TEMPLATE_SEQ")
public class UsageChargeTemplate extends ChargeTemplate {
	static String WILCARD = "";

	private static final long serialVersionUID = 1L;

	@Column(name = "UNITY_MULTIPLICATOR")
	private BigDecimal unityMultiplicator = BigDecimal.ONE;

	@Column(name = "UNITY_DESCRIPTION", length = 20)
	private String unityDescription;

	@Column(name = "UNITY_FORMATTER")
	@Enumerated(EnumType.STRING)
	private UsageChgTemplateEnum unityFormatter;

	@Column(name = "UNITY_NB_DECIMAL")
	private int unityNbDecimal = 2;

	@Column(name = "FILTER_PARAM_1", length = 20)
	private String filterParam1 = WILCARD;

	@Column(name = "FILTER_PARAM_2", length = 20)
	private String filterParam2 = WILCARD;

	@Column(name = "FILTER_PARAM_3", length = 20)
	private String filterParam3 = WILCARD;

	@Column(name = "FILTER_PARAM_4", length = 20)
	private String filterParam4 = WILCARD;

	@Column(name = "FILTER_EXPRESSION", length = 255)
	private String filterExpression = null;

	@Column(name = "PRIORITY", columnDefinition = "int default 1")
	private int priority = 1;

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

	public UsageChgTemplateEnum getUnityFormatter() {
		return unityFormatter;
	}

	public void setUnityFormatter(UsageChgTemplateEnum unityFormatter) {
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

}

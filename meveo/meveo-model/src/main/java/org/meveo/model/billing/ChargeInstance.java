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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.catalog.ChargeTemplate;

@Entity
@Table(name = "BILLING_CHARGE_INSTANCE")
// @SequenceGenerator(name = "ID_GENERATOR", sequenceName =
// "BILLING_CHARGE_INSTANCE_SEQ")
@AttributeOverrides({ @AttributeOverride(name = "code", column = @Column(name = "code", unique = false)) })
@Inheritance(strategy = InheritanceType.JOINED)
public class ChargeInstance extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	protected InstanceStatusEnum status = InstanceStatusEnum.ACTIVE;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STATUS_DATE")
	protected Date statusDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "TERMINATION_DATE")
	protected Date terminationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHARGE_TEMPLATE_ID")
	protected ChargeTemplate chargeTemplate;

	@Temporal(TemporalType.DATE)
	@Column(name = "CHARGE_DATE")
	protected Date chargeDate;

	@Column(name = "AMOUNT_WITHOUT_TAX", precision = 23, scale = 12)
	protected BigDecimal amountWithoutTax;

	@Column(name = "AMOUNT_WITH_TAX", precision = 23, scale = 12)
	protected BigDecimal amountWithTax;

	@Column(name = "CRITERIA_1")
	protected String criteria1;

	@Column(name = "CRITERIA_2")
	protected String criteria2;

	@Column(name = "CRITERIA_3")
	protected String criteria3;

	@OneToMany(mappedBy = "chargeInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	protected Set<WalletOperation> walletOperations = new HashSet<WalletOperation>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SELLER_ID")
	private Seller seller;

	@Column(name = "PR_DESCRIPTION", length = 100)
	protected String prDescription;

	public String getCriteria1() {
		return criteria1;
	}

	public void setCriteria1(String criteria1) {
		this.criteria1 = criteria1;
	}

	public String getCriteria2() {
		return criteria2;
	}

	public void setCriteria2(String criteria2) {
		this.criteria2 = criteria2;
	}

	public String getCriteria3() {
		return criteria3;
	}

	public void setCriteria3(String criteria3) {
		this.criteria3 = criteria3;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public InstanceStatusEnum getStatus() {
		return status;
	}

	public void setStatus(InstanceStatusEnum status) {
		this.status = status;
		this.statusDate = new Date();
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public ChargeTemplate getChargeTemplate() {
		return chargeTemplate;
	}

	public void setChargeTemplate(ChargeTemplate chargeTemplate) {
		this.chargeTemplate = chargeTemplate;
		this.code = chargeTemplate.getCode();
		this.description = chargeTemplate.getDescription();
	}

	public Date getChargeDate() {
		return chargeDate;
	}

	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}

	public Set<WalletOperation> getWalletOperations() {
		return walletOperations;
	}

	public void setWalletOperations(Set<WalletOperation> walletOperations) {
		this.walletOperations = walletOperations;
	}

	public String getPrDescription() {
		return prDescription;
	}

	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}

	public BigDecimal getAmountWithTax() {
		return amountWithTax;
	}

	public void setAmountWithTax(BigDecimal amountWithTax) {
		this.amountWithTax = amountWithTax;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

}

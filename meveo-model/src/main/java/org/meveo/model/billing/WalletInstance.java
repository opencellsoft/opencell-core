/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BusinessEntity;
import org.meveo.model.catalog.WalletTemplate;

@Entity
@Table(name = "BILLING_WALLET")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_WALLET_SEQ")
@NamedQueries({
	@NamedQuery(name = "WalletInstance.listPrepaidActiiveWalletIds", 
			query = "SELECT c.id FROM WalletInstance c where c.walletTemplate.walletType=org.meveo.model.billing.BillingWalletTypeEnum.PREPAID and "
					+ "c.userAccount.status=org.meveo.model.billing.AccountStatusEnum.ACTIVE"),
})
public class WalletInstance extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAT_WALLET_TEMPLATE_ID")
	private WalletTemplate walletTemplate;

	@ManyToOne
	@JoinColumn(name = "USER_ACCOUNT_ID")
	private UserAccount userAccount;

	@Column(name = "EXPIRY_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creditExpiryDate;

	@OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<WalletOperation> operations;

	@OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<RatedTransaction> ratedTransactions;

	public WalletTemplate getWalletTemplate() {
		return walletTemplate;
	}

	public void setWalletTemplate(WalletTemplate walletTemplate) {
		this.walletTemplate = walletTemplate;
		if (walletTemplate != null) {
			this.code = walletTemplate.getCode();
			this.description = walletTemplate.getDescription();
		} else {
			this.code = null;
			this.description = null;
		}
	}

	public String toString() {
		return walletTemplate == null ? null : walletTemplate.getCode();
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public List<RatedTransaction> getRatedTransactions() {
		return ratedTransactions;
	}

	public void setRatedTransactions(List<RatedTransaction> ratedTransactions) {
		this.ratedTransactions = ratedTransactions;
	}

	public List<WalletOperation> getOperations() {
		return operations;
	}

	public void setOperations(List<WalletOperation> operations) {
		this.operations = operations;
	}

	public Set<InvoiceSubCategory> getInvoiceSubCategories() {
		Set<InvoiceSubCategory> invoiceSubCategories = new HashSet<InvoiceSubCategory>();
		for (RatedTransaction ratedTransaction : ratedTransactions) {
			invoiceSubCategories.add(ratedTransaction.getInvoiceSubCategory());
		}
		return invoiceSubCategories;
	}

	public Date getCreditExpiryDate() {
		return creditExpiryDate;
	}

	public void setCreditExpiryDate(Date creditExpiryDate) {
		this.creditExpiryDate = creditExpiryDate;
	}

	public boolean equals(WalletInstance w) {
		return (w == null) || (w.getCode().equals(this.code));
	}
}

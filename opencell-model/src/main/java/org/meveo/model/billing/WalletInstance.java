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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.catalog.WalletTemplate;

/**
 * Prepaid or postpaid Wallet instance
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@ExportIdentifier({ "code", "userAccount.code" })
@Table(name = "billing_wallet", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "user_account_id" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_wallet_seq"), })
@NamedQueries({
        @NamedQuery(name = "WalletInstance.listPrepaidActiveWalletIds", query = "SELECT c.id FROM WalletInstance c where c.walletTemplate.walletType=org.meveo.model.billing.BillingWalletTypeEnum.PREPAID and c.userAccount.status=org.meveo.model.billing.AccountStatusEnum.ACTIVE"),
        @NamedQuery(name = "WalletInstance.listPrepaidWalletsToMatch", query = "SELECT c FROM WalletInstance c where c.walletTemplate.walletType=org.meveo.model.billing.BillingWalletTypeEnum.PREPAID and c.userAccount.status=org.meveo.model.billing.AccountStatusEnum.ACTIVE AND (c.nextMatchingDate IS NULL OR nextMatchingDate <= :matchingDate) "), })
public class WalletInstance extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Wallet template/definition
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_wallet_template_id")
    private WalletTemplate walletTemplate;

    /**
     * User account wallet is associated to
     */
    @ManyToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "expiry_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creditExpiryDate;

    /**
     * Deprecated in 5.3 for not use
     */
    @Deprecated
    @Column(name = "next_matching_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextMatchingDate;

    /**
     * Balance level at which LowBalance event should be fired
     */
    @Column(name = "low_balance_level", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal lowBalanceLevel;

    /**
     * Balance level at which further consumption should be rejected
     */
    @Column(name = "reject_level", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal rejectLevel;

    /**
     * Operations against this wallet
     */
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<WalletOperation> operations;

    /**
     * Rated transactions against this wallet
     */
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<RatedTransaction> ratedTransactions;

    /**
     * @return Wallet template associated to the wallet instance
     */
    public WalletTemplate getWalletTemplate() {
        return walletTemplate;
    }

    /**
     * @param walletTemplate Wallet template associated to the wallet instance
     */
    public void setWalletTemplate(WalletTemplate walletTemplate) {
        this.walletTemplate = walletTemplate;
        if (walletTemplate != null) {
            this.code = walletTemplate.getCode();
            this.description = walletTemplate.getDescription();
            this.lowBalanceLevel = walletTemplate.getLowBalanceLevel();
            this.rejectLevel = walletTemplate.getRejectLevel();
        } else {
            this.code = null;
            this.description = null;
            this.lowBalanceLevel = null;
        }
    }

    @Override
    public String toString() {
        return String.format("WalletInstance [%s, walletTemplate=%s, userAccount=%s]", super.toString(), walletTemplate != null ? walletTemplate.getId() : null,
            userAccount != null ? userAccount.getId() : null);
    }

    /**
     * @return Associated user account
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    /**
     * @param userAccount Associated user account
     */
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

    public Date getCreditExpiryDate() {
        return creditExpiryDate;
    }

    public void setCreditExpiryDate(Date creditExpiryDate) {
        this.creditExpiryDate = creditExpiryDate;
    }

    public Date getNextMatchingDate() {
        return nextMatchingDate;
    }

    public void setNextMatchingDate(Date nextMatchingDate) {
        this.nextMatchingDate = nextMatchingDate;
    }

    /**
     * @return Balance level at which LowBalance event should be fired
     */
    public BigDecimal getLowBalanceLevel() {
        return lowBalanceLevel;
    }

    /**
     * @param lowBalanceLevel Balance level at which LowBalance event should be fired
     */
    public void setLowBalanceLevel(BigDecimal lowBalanceLevel) {
        this.lowBalanceLevel = lowBalanceLevel;
    }

    /**
     * @return Balance level at which further consumption should be rejected
     */
    public BigDecimal getRejectLevel() {
        return rejectLevel;
    }

    /**
     * @param rejectLevel Balance level at which further consumption should be rejected
     */
    public void setRejectLevel(BigDecimal rejectLevel) {
        this.rejectLevel = rejectLevel;
    }

    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof WalletInstance)) {
            return false;
        }

        WalletInstance other = (WalletInstance) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        return (other.getCode().equals(this.code));
    }
}

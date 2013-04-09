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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.meveo.model.IEntity;

@Entity
@Table(name = "BILLING_WALLET_OPERATION")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_OPERATION_SEQ")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WalletOperation implements IEntity {

    private static final long serialVersionUID = 1L;

    @Id
//    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.TABLE)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Version
    @Column(name = "VERSION")
    private Integer version;

    /**
     * The wallet on which the account operation is applied.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WALLET_ID")
    private Wallet wallet;

    /**
     * CREDIT or DEBIT
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "CREDIT_DEBIT_FLAG")
    private OperationTypeEnum type;

    /**
     * Positive amount to credit or debit on the account (i.e. wallet for
     * prepaid case) when the accountOperationType is executed.
     */
    @Column(name = "AMOUNT", precision = 23, scale = 12)
    private BigDecimal amount;

    /**
     * Wallet total balance before the operation execution.
     */
    @Column(name = "PREVIOUS_BALANCE", precision = 23, scale = 12)
    private BigDecimal previousBalance;

    /**
     * Wallet total balance after the operation execution.
     */
    @Column(name = "RESULTING_BALANCE", precision = 23, scale = 12)
    private BigDecimal resultingBalance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public OperationTypeEnum getType() {
        return type;
    }

    public void setType(OperationTypeEnum type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    public void setResultingBalance(BigDecimal resultingBalance) {
        this.resultingBalance = resultingBalance;
    }


    public boolean isTransient() {
        return id == null;
    }
}

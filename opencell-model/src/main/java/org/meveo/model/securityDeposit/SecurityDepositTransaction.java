package org.meveo.model.securityDeposit;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OperationCategoryEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "security_deposit_transaction")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "security_deposit_transaction_seq"), })
@NamedQueries({
        @NamedQuery(name = "SecurityDepositTransaction.getSecurityDepositCodesByAoId",
                query = "SELECT sd.securityDeposit.code FROM SecurityDepositTransaction sd WHERE sd.accountOperation.id = :aoId")
})
public class SecurityDepositTransaction extends BusinessCFEntity {
    private static final long serialVersionUID = 1L;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "security_deposit_id")
    private SecurityDeposit securityDeposit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_operation_id")
    private AccountOperation accountOperation;
    
    @Column(name = "security_deposit_operation")
    @Enumerated(value = EnumType.STRING)
    private SecurityDepositOperationEnum operation;
    
    @Column(name = "amount", precision = 23, scale = 12)
    private BigDecimal amount;
    
    /**
     * Operation date
     */
    @Column(name = "transaction_date")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;
    
    /**
     * Operation category Debit/Credit
     */
    @Column(name = "transaction_category")
    @Enumerated(value = EnumType.STRING)
    private OperationCategoryEnum transactionCategory;
    
    public SecurityDeposit getSecurityDeposit() {
    	return securityDeposit;
    }
    
    public void setSecurityDeposit(SecurityDeposit securityDeposit) {
    	this.securityDeposit = securityDeposit;
    }
    
    public AccountOperation getAccountOperation() {
    	return accountOperation;
    }
    
    public void setAccountOperation(AccountOperation accountOperation) {
    	this.accountOperation = accountOperation;
    }
    
    public BigDecimal getAmount() {
    	return amount;
    }
    
    public void setAmount(BigDecimal amount) {
    	this.amount = amount;
    }
    
    public Date getTransactionDate() {
    	return transactionDate;
    }
    
    public void setTransactionDate(Date transactionDate) {
    	this.transactionDate = transactionDate;
    }
    
    public OperationCategoryEnum getTransactionCategory() {
    	return transactionCategory;
    }
    
    public void setTransactionCategory(OperationCategoryEnum transactionCategory) {
    	this.transactionCategory = transactionCategory;
    }	    
       
    public SecurityDepositOperationEnum getOperation() {
        return operation;
    }
    
    public void setOperation(SecurityDepositOperationEnum operation) {
        this.operation = operation;
    }
}

package org.meveo.model.billing;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Wallet operation processing status
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "billing_wallet_operation_status")
public class WalletOperationProcessingStatus implements Serializable {

    private static final long serialVersionUID = 6657502865239494328L;

    /**
     * EDR identifier
     */
    @Id
    @Column(name = "id")
    @Access(AccessType.PROPERTY) // Access is set to property so a call to getId() wont trigger hibernate proxy loading
    protected Long id;

    /**
     * Wallet operation
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private WalletOperation walletOperation;

    /**
     * Rated transaction
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_transaction_id")
    protected RatedTransaction ratedTransaction;

    /**
     * Processing status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private WalletOperationStatusEnum status;

    /**
     * Status change timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate;

    /**
     * Constructor
     */
    public WalletOperationProcessingStatus() {

    }

    /**
     * Constructor
     * 
     * @param id Wallet operation identifier
     * @param ratedTransaction Rated transaction
     * @param status Status
     */
    public WalletOperationProcessingStatus(Long id, RatedTransaction ratedTransaction, WalletOperationStatusEnum status) {
        super();
        this.id = id;
        this.ratedTransaction = ratedTransaction;
        this.status = status;
        this.statusDate = new Date();
    }

    public WalletOperationProcessingStatus(WalletOperation walletOperation, RatedTransaction ratedTransaction, WalletOperationStatusEnum status) {
        super();

        if (walletOperation.getId() != null) {
            this.id = walletOperation.getId();
        } else {
            this.walletOperation = walletOperation;
        }

        this.ratedTransaction = ratedTransaction;
        this.status = status;
        this.statusDate = new Date();
    }

    /**
     * @return Wallet operation identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id Wallet operation identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Wallet operation
     */
    public WalletOperation getWalletOperation() {
        return walletOperation;
    }

    /**
     * @param walletOperation Wallet operation
     */
    public void setWalletOperation(WalletOperation walletOperation) {
        this.walletOperation = walletOperation;
    }

    /**
     * @return Rated transaction
     */
    public RatedTransaction getRatedTransaction() {
        return ratedTransaction;
    }

    /**
     * @param ratedTransaction Rated transaction
     */
    public void setRatedTransaction(RatedTransaction ratedTransaction) {
        this.ratedTransaction = ratedTransaction;
    }

    /**
     * @return Processing status
     */
    public WalletOperationStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status Processing status
     */
    public void setStatus(WalletOperationStatusEnum status) {
        this.status = status;
    }

    /**
     * @return Status change timestamp
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * @param statusDate Status change timestamp
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * Change status and set a status change timestamp.
     * 
     * @param newStatus New status
     */
    public void changeStatus(WalletOperationStatusEnum newStatus) {
        this.status = newStatus;
        this.statusDate = new Date();
    }
}
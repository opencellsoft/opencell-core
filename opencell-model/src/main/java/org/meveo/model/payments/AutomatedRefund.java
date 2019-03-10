/**
 *
 */
package org.meveo.model.payments;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
/**
 * @author anasseh
 *
 */
@Entity
@DiscriminatorValue(value = "ARF")
public class AutomatedRefund  extends Refund{
    /**
     *
     */
    private static final long serialVersionUID = -8295409359854665764L;
    
    @Column(name = "bank_lot", columnDefinition = "text")
    private String bankLot;

    @Column(name = "bank_reference", length = 255)
    @Size(max = 255)
    private String bankReference;

    @Column(name = "deposit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date depositDate;

    @Column(name = "bank_collection_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date bankCollectionDate;

	public String getBankLot() {
		return bankLot;
	}

	public void setBankLot(String bankLot) {
		this.bankLot = bankLot;
	}

	public String getBankReference() {
		return bankReference;
	}

	public void setBankReference(String bankReference) {
		this.bankReference = bankReference;
	}

	public Date getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}

	public Date getBankCollectionDate() {
		return bankCollectionDate;
	}

	public void setBankCollectionDate(Date bankCollectionDate) {
		this.bankCollectionDate = bankCollectionDate;
	}
    
    

}
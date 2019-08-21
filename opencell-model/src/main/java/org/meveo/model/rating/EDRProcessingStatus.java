package org.meveo.model.rating;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

/**
 * EDR processing status
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Table(name = "rating_edr_status")
public class EDRProcessingStatus implements Serializable {

    private static final long serialVersionUID = 6032684167078694759L;

    /**
     * EDR identifier
     */
    @Id
    @Column(name = "id")
    @Access(AccessType.PROPERTY) // Access is set to property so a call to getId() wont trigger hibernate proxy loading
    protected Long id;

    /**
     * EDR
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private EDR edr;

    /**
     * Processing status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EDRStatusEnum status;

    /**
     * Status change date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "status_date")
    private Date statusDate;

    /**
     * Rejection reason
     */
    @Column(name = "reject_reason", columnDefinition = "text")
    @Size(max = 255)
    private String rejectReason;

    public EDRProcessingStatus() {

    }

    /**
     * EDR processing status constructor
     * 
     * @param id EDR identifier
     * @param status Processing status
     * @param rejectReason Rejection reason
     */
    public EDRProcessingStatus(Long id, EDRStatusEnum status, String rejectReason) {
        super();
        this.id = id;
        this.status = status;
        this.statusDate = new Date();
        this.rejectReason = rejectReason;
    }

    /**
     * EDR processing status constructor
     * 
     * @param edr EDR
     * @param status Processing status
     * @param rejectReason Rejection reason
     */
    public EDRProcessingStatus(EDR edr, EDRStatusEnum status, String rejectReason) {
        super();
        if (edr.getId() != null) {
            this.id = edr.getId();
        } else {
            this.edr = edr;
        }
        this.status = status;
        this.statusDate = new Date();
        this.rejectReason = rejectReason;
    }

    /**
     * @return EDR identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id EDR identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return EDR
     */
    public EDR getEdr() {
        return edr;
    }

    /**
     * @param edr EDR
     */
    public void setEdr(EDR edr) {
        this.edr = edr;
    }

    /**
     * @return Processing status
     */
    public EDRStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status Processing status
     */
    public void setStatus(EDRStatusEnum status) {
        this.status = status;
    }

    /**
     * 
     * @return Status change date
     */
    public Date getStatusDate() {
        return statusDate;
    }

    /**
     * @param statusDate Status change date
     */
    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    /**
     * @return Rejection reason
     */
    public String getRejectReason() {
        return rejectReason;
    }

    /**
     * @param rejectReason Rejection reason
     */
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
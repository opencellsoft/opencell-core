package org.meveo.model.jobs;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

/**
 * File transferred via FTP
 *
 * @author Andrius Karpavicius
 * @author Mounir BOUKAYOUA
 */
@Entity
@Table(name = "ftp_transferred_file")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ftp_transferred_file_seq"), })
public class FtpTransferredFile extends BusinessEntity {
    private static final long serialVersionUID = 430457580612075457L;

    /**
     * URI
     */
    @Column(name = "uri", length = 2000, nullable = false)
    @Size(max = 2000)
    @NotNull
    private String uri;

    /**
     * File size
     */
    @Column(name = "size")
    private Long size;

    /**
     * Import timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transfer_date")
    private Date transferDate = new Date();

    /**
     * Last file modification timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modification")
    private Date lastModification;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation")
    private FtpOperationEnum operation;


    public FtpTransferredFile(){
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the transferDate
     */
    public Date getTransferDate() {
        return transferDate;
    }

    /**
     * @param transferDate the transferDate to set
     */
    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }

    /**
     * @return the lastModification
     */
    public Date getLastModification() {
        return lastModification;
    }

    /**
     * @param lastModification the lastModification to set
     */
    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }

    /**
     * @return Ftp operation : IMPORT or EXPORT
     */
    public FtpOperationEnum getOperation() {
        return operation;
    }

    /**
     * operation : IMPORT or EXPORT
     * @param operation FTP operation
     */
    public void setOperation(FtpOperationEnum operation) {
        this.operation = operation;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FtpTransferredFile [uri=" + uri + ", size=" + size + ", transferDate=" + transferDate + ", lastModification=" + lastModification + ", code=" + code + ", description="
                + description + ", appendGeneratedCode=" + appendGeneratedCode + ", id=" + id + ", operation=" + operation + "]";
    }

}
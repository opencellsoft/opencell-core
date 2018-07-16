package org.meveo.api.dto.payment;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.model.payments.DDRequestFileFormatEnum;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;

/**
 * The Class DDRequestLotOpDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jul 11, 2016 7:15:09 PM
 */
@XmlRootElement(name = "DDRequestLotOp")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestLotOpDto extends AuditableEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6185045412352889135L;

    /** The from due date. */
    @XmlElement(required = true)
    private Date fromDueDate;
    
    /** The to due date. */
    @XmlElement(required = true)
    private Date toDueDate;
    
    /** The ddrequest op. */
    private DDRequestOpEnum ddrequestOp;
    
    /** The status. */
    private DDRequestOpStatusEnum status;
    
    /** The error cause. */
    private String errorCause;
    
    /** The file format. */
    @XmlElement(required = true)
    private DDRequestFileFormatEnum fileFormat;

    /**
     * Instantiates a new DD request lot op dto.
     */
    public DDRequestLotOpDto() {

    }

    /**
     * Instantiates a new DD request lot op dto.
     *
     * @param ddrequestLotOp the DDRequestLotOp entity
     */
    public DDRequestLotOpDto(DDRequestLotOp ddrequestLotOp) {
        super(ddrequestLotOp);
        this.fromDueDate = ddrequestLotOp.getFromDueDate();
        this.toDueDate = ddrequestLotOp.getToDueDate();
        this.ddrequestOp = ddrequestLotOp.getDdrequestOp();
        this.status = ddrequestLotOp.getStatus();
        this.errorCause = ddrequestLotOp.getErrorCause();
        this.fileFormat = ddrequestLotOp.getFileFormat();
    }

    /**
     * Gets the from due date.
     *
     * @return the from due date
     */
    public Date getFromDueDate() {
        return fromDueDate;
    }

    /**
     * Sets the from due date.
     *
     * @param fromDueDate the new from due date
     */
    public void setFromDueDate(Date fromDueDate) {
        this.fromDueDate = fromDueDate;
    }

    /**
     * Gets the to due date.
     *
     * @return the to due date
     */
    public Date getToDueDate() {
        return toDueDate;
    }

    /**
     * Sets the to due date.
     *
     * @param toDueDate the new to due date
     */
    public void setToDueDate(Date toDueDate) {
        this.toDueDate = toDueDate;
    }

    /**
     * Gets the ddrequest op.
     *
     * @return the ddrequest op
     */
    public DDRequestOpEnum getDdrequestOp() {
        return ddrequestOp;
    }

    /**
     * Sets the ddrequest op.
     *
     * @param ddrequestOp the new ddrequest op
     */
    public void setDdrequestOp(DDRequestOpEnum ddrequestOp) {
        this.ddrequestOp = ddrequestOp;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public DDRequestOpStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(DDRequestOpStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the error cause.
     *
     * @return the error cause
     */
    public String getErrorCause() {
        return errorCause;
    }

    /**
     * Sets the error cause.
     *
     * @param errorCause the new error cause
     */
    public void setErrorCause(String errorCause) {
        this.errorCause = errorCause;
    }

    /**
     * Gets the file format.
     *
     * @return the file format
     */
    public DDRequestFileFormatEnum getFileFormat() {
        return fileFormat;
    }

    /**
     * Sets the file format.
     *
     * @param fileFormat the new file format
     */
    public void setFileFormat(DDRequestFileFormatEnum fileFormat) {
        this.fileFormat = fileFormat;
    }

    @Override
    public String toString() {
        return "DDRequestLotOpDto [fromDueDate=" + fromDueDate + ", toDueDate=" + toDueDate + ", ddrequestOp=" + ddrequestOp + ", status=" + status + ", errorCause=" + errorCause
                + ", fileFormat=" + fileFormat + "]";
    }

}
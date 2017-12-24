package org.meveo.api.dto.payment;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.DDRequestFileFormatEnum;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jul 11, 2016 7:15:09 PM
 **/
@XmlRootElement(name="DDRequestLotOp")
@XmlAccessorType(XmlAccessType.FIELD)
public class DDRequestLotOpDto extends BaseDto {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6185045412352889135L;
	
	@XmlElement(required=true)
	private Date fromDueDate;
	@XmlElement(required=true)
	private Date toDueDate;
	private DDRequestOpEnum ddrequestOp;
	private DDRequestOpStatusEnum status;
	private String errorCause;
	@XmlElement(required=true)
	private DDRequestFileFormatEnum fileFormat;
	
	
	public DDRequestLotOpDto(){
		
	}
	public DDRequestLotOpDto(DDRequestLotOp ddrequestLotOp){
		this.fromDueDate=ddrequestLotOp.getFromDueDate();
		this.toDueDate=ddrequestLotOp.getToDueDate();
		this.ddrequestOp=ddrequestLotOp.getDdrequestOp();
		this.status=ddrequestLotOp.getStatus();
		this.errorCause=ddrequestLotOp.getErrorCause();
		this.fileFormat=ddrequestLotOp.getFileFormat();
	}
	public Date getFromDueDate() {
		return fromDueDate;
	}
	public void setFromDueDate(Date fromDueDate) {
		this.fromDueDate = fromDueDate;
	}
	public Date getToDueDate() {
		return toDueDate;
	}
	public void setToDueDate(Date toDueDate) {
		this.toDueDate = toDueDate;
	}
	public DDRequestOpEnum getDdrequestOp() {
		return ddrequestOp;
	}
	public void setDdrequestOp(DDRequestOpEnum ddrequestOp) {
		this.ddrequestOp = ddrequestOp;
	}
	public DDRequestOpStatusEnum getStatus() {
		return status;
	}
	public void setStatus(DDRequestOpStatusEnum status) {
		this.status = status;
	}
	public String getErrorCause() {
		return errorCause;
	}
	public void setErrorCause(String errorCause) {
		this.errorCause = errorCause;
	}
	public DDRequestFileFormatEnum getFileFormat() {
		return fileFormat;
	}
	public void setFileFormat(DDRequestFileFormatEnum fileFormat) {
		this.fileFormat = fileFormat;
	}
	@Override
	public String toString() {
		return "DDRequestLotOpDto [fromDueDate=" + fromDueDate + ", toDueDate=" + toDueDate + ", ddrequestOp=" + ddrequestOp + ", status=" + status + ", errorCause=" + errorCause
				+ ", fileFormat=" + fileFormat + "]";
	}
	
	
}


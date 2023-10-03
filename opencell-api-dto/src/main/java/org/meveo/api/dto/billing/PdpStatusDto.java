package org.meveo.api.dto.billing;

import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.model.billing.PDPStatusEnum;
import org.meveo.model.billing.TransmittedFormatEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Objects;

@XmlRootElement(name = "pdpStatusDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PdpStatusDto {
	
	@Schema(description = "transmitted format => UBL, XML, PDF ")
	private TransmittedFormatEnum transmittedFormat;
	
	@Schema(description = "origin can be UI, API")
	private String origin;
	
	@Schema(description = "the code returned by the caller")
	private int returnCode;
	
	@Schema(description = "label of the status")
	private String label;
	
	@Schema(description = "the invoice identifier")
	private String invoiceIdentifier;
	
	@Schema(description = "the invoice number")
	private String invoiceNumber;
	
	@Schema(description = "status of the invoice")
	private PDPStatusEnum status;
	
	@Schema(description = "deposit date")
	private Date depositDate;
	
	
	public TransmittedFormatEnum getTransmittedFormat() {
		return transmittedFormat;
	}
	
	public void setTransmittedFormat(TransmittedFormatEnum transmittedFormat) {
		this.transmittedFormat = transmittedFormat;
	}
	
	public String getOrigin() {
		return origin;
	}
	
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public int getReturnCode() {
		return returnCode;
	}
	
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getInvoiceIdentifier() {
		return invoiceIdentifier;
	}
	
	public void setInvoiceIdentifier(String invoiceIdentifier) {
		this.invoiceIdentifier = invoiceIdentifier;
	}
	
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	
	public PDPStatusEnum getStatus() {
		return status;
	}
	
	public void setStatus(PDPStatusEnum status) {
		this.status = status;
	}
	
	public Date getDepositDate() {
		return depositDate;
	}
	
	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PdpStatusDto)) return false;
		PdpStatusDto that = (PdpStatusDto) o;
		return getReturnCode() == that.getReturnCode() && getTransmittedFormat() == that.getTransmittedFormat() && Objects.equals(getOrigin(), that.getOrigin()) && Objects.equals(getLabel(), that.getLabel()) && Objects.equals(getInvoiceIdentifier(), that.getInvoiceIdentifier()) && Objects.equals(getInvoiceNumber(), that.getInvoiceNumber()) && getStatus() == that.getStatus() && Objects.equals(getDepositDate(), that.getDepositDate());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getTransmittedFormat(), getOrigin(), getReturnCode(), getLabel(), getInvoiceIdentifier(), getInvoiceNumber(), getStatus(), getDepositDate());
	}
}

package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "InvoicingPlanItemResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoicingPlanItemResponseDto extends BaseResponse {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1990918305354682187L;

	/** The invoicingPlanItem dto. */
	private InvoicingPlanItemDto invoicingPlanItemDto;

	/**
	 * Gets the invoicingPlanItem dto.
	 *
	 * @return the invoicingPlanItem dto
	 */
	public InvoicingPlanItemDto getInvoicingPlanItemDto() {
		return invoicingPlanItemDto;
	}

	/**
	 * Sets the invoicingPlanItem dto.
	 *
	 * @param invoicingPlanItemDto the new invoicingPlanItem dto
	 */
	public void setInvoicingPlanItemDto(InvoicingPlanItemDto invoicingPlanItemDto) {
		this.invoicingPlanItemDto = invoicingPlanItemDto;
	}

	@Override
	public String toString() {
		return "InvoicingPlanItemResponse [invoicingPlanItem=" + invoicingPlanItemDto + ", toString()="
				+ super.toString() + "]";
	}
}
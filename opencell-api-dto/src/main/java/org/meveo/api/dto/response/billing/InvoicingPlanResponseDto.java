package org.meveo.api.dto.response.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement(name = "InvoicingPlanResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoicingPlanResponseDto extends BaseResponse {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1990918305354682187L;

	/** The invoicingPlan dto. */
	private InvoicingPlanDto invoicingPlanDto;

	/**
	 * Gets the invoicingPlan dto.
	 *
	 * @return the invoicingPlan dto
	 */
	public InvoicingPlanDto getInvoicingPlanDto() {
		return invoicingPlanDto;
	}

	/**
	 * Sets the invoicingPlan dto.
	 *
	 * @param invoicingPlanDto the new invoicingPlan dto
	 */
	public void setInvoicingPlanDto(InvoicingPlanDto invoicingPlanDto) {
		this.invoicingPlanDto = invoicingPlanDto;
	}

	@Override
	public String toString() {
		return "InvoicingPlanResponse [invoicingPlan=" + invoicingPlanDto + ", toString()="
				+ super.toString() + "]";
	}
}
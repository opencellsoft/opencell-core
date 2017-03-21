package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BillingCyclesDto;
import org.meveo.api.dto.CalendarsDto;
import org.meveo.api.dto.InvoiceCategoriesDto;
import org.meveo.api.dto.InvoiceSubCategoriesDto;
import org.meveo.api.dto.TaxesDto;
import org.meveo.api.dto.TerminationReasonsDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetInvoicingConfigurationResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetInvoicingConfigurationResponseDto extends BaseResponse {

	private static final long serialVersionUID = -3000516095971053199L;

	private CalendarsDto calendars = new CalendarsDto();
	private TaxesDto taxes = new TaxesDto();
	private InvoiceCategoriesDto invoiceCategories = new InvoiceCategoriesDto();
	private InvoiceSubCategoriesDto invoiceSubCategories = new InvoiceSubCategoriesDto();
	private BillingCyclesDto billingCycles = new BillingCyclesDto();
	private TerminationReasonsDto terminationReasons = new TerminationReasonsDto();

	public CalendarsDto getCalendars() {
		return calendars;
	}

	public void setCalendars(CalendarsDto calendars) {
		this.calendars = calendars;
	}

	public TaxesDto getTaxes() {
		return taxes;
	}

	public void setTaxes(TaxesDto taxes) {
		this.taxes = taxes;
	}

	public InvoiceCategoriesDto getInvoiceCategories() {
		return invoiceCategories;
	}

	public void setInvoiceCategories(InvoiceCategoriesDto invoiceCategories) {
		this.invoiceCategories = invoiceCategories;
	}

	public InvoiceSubCategoriesDto getInvoiceSubCategories() {
		return invoiceSubCategories;
	}

	public void setInvoiceSubCategories(InvoiceSubCategoriesDto invoiceSubCategories) {
		this.invoiceSubCategories = invoiceSubCategories;
	}

	public BillingCyclesDto getBillingCycles() {
		return billingCycles;
	}

	public void setBillingCycles(BillingCyclesDto billingCycles) {
		this.billingCycles = billingCycles;
	}

	public TerminationReasonsDto getTerminationReasons() {
		return terminationReasons;
	}

	public void setTerminationReasons(TerminationReasonsDto terminationReasons) {
		this.terminationReasons = terminationReasons;
	}

	@Override
	public String toString() {
		return "GetInvoicingConfigurationResponseDto [calendars=" + calendars + ", taxes=" + taxes
				+ ", invoiceCategories=" + invoiceCategories + ", invoiceSubCategories=" + invoiceSubCategories
				+ ", billingCycles=" + billingCycles + ", terminationReasons=" + terminationReasons + ", toString()="
				+ super.toString() + "]";
	}

}

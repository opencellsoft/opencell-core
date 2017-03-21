package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

@XmlRootElement(name = "InvoiceTypes")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceTypesDto  extends BaseDto{
	
		private static final long serialVersionUID = 1L;
		
		
		private List<InvoiceTypeDto> invoiceTypes  = new ArrayList<InvoiceTypeDto>();
		
		public InvoiceTypesDto(){
			
		}

		/**
		 * @return the invoiceTypes
		 */
		public List<InvoiceTypeDto> getInvoiceTypes() {
			return invoiceTypes;
		}

		/**
		 * @param invoiceTypes the invoiceTypes to set
		 */
		public void setInvoiceTypes(List<InvoiceTypeDto> invoiceTypes) {
			this.invoiceTypes = invoiceTypes;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "InvoiceTypesDto [InvoiceTypes=" + invoiceTypes + "]";
		}
		
		
	
}

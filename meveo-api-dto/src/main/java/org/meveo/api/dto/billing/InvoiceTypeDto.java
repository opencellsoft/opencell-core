package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.SequenceDto;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;

@XmlRootElement(name = "InvoiceType")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceTypeDto  extends BaseDto{
	
		private static final long serialVersionUID = 1L;
		
		@XmlElement(required = true)
		private String code;
		
		private String description;
		
		@XmlElement(required = true)
		private String occTemplateCode;
		
		private String occTemplateNegativeCode;		
		
		private SequenceDto sequenceDto;
		
		@XmlElementWrapper
	    @XmlElement(name="sellerSequence")
		private Map<String,SequenceDto> sellerSequences = new HashMap<String,SequenceDto>();
		
		
		private List<String> appliesTo = new ArrayList<String>();
		
		private boolean matchingAuto = false;
		
		public InvoiceTypeDto(){
			
		}

		public InvoiceTypeDto(InvoiceType invoiceType){
			this.code = invoiceType.getCode();
			this.description = invoiceType.getDescription();			
			this.occTemplateCode = invoiceType.getOccTemplate() != null ? invoiceType.getOccTemplate().getCode():null;
			this.occTemplateNegativeCode = invoiceType.getOccTemplateNegative() != null ? invoiceType.getOccTemplateNegative().getCode():null;
			this.sequenceDto = new SequenceDto(invoiceType.getSequence());
			if(invoiceType.getAppliesTo() != null){				
				for(InvoiceType tmpInvoiceType : invoiceType.getAppliesTo()){
					this.getAppliesTo().add(tmpInvoiceType.getCode());
				}
			}			
			for(InvoiceTypeSellerSequence seq : invoiceType.getSellerSequence()){
				sellerSequences.put(seq.getSeller().getCode(), new SequenceDto(seq.getSequence()));
			}		
			this.matchingAuto = invoiceType.isMatchingAuto();
		}
		
		
		/**
		 * @return the code
		 */
		public String getCode() {
			return code;
		}

		/**
		 * @param code the code to set
		 */
		public void setCode(String code) {
			this.code = code;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		/**
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}

		/**
		 * @return the occTemplateCode
		 */
		public String getOccTemplateCode() {
			return occTemplateCode;
		}

		/**
		 * @param occTemplateCode the occTemplateCode to set
		 */
		public void setOccTemplateCode(String occTemplateCode) {
			this.occTemplateCode = occTemplateCode;
		}

		
		/**
		 * @return the appliesTo
		 */
		public List<String> getAppliesTo() {
			return appliesTo;
		}

		/**
		 * @param appliesTo the appliesTo to set
		 */
		public void setAppliesTo(List<String> appliesTo) {
			this.appliesTo = appliesTo;
		}

		/**
		 * @return the matchingAuto
		 */
		public boolean isMatchingAuto() {
			return matchingAuto;
		}

		/**
		 * @param matchingAuto the matchingAuto to set
		 */
		public void setMatchingAuto(boolean matchingAuto) {
			this.matchingAuto = matchingAuto;
		}

		/**
		 * @return the sequenceDto
		 */
		public SequenceDto getSequenceDto() {
			return sequenceDto;
		}

		/**
		 * @param sequenceDto the sequenceDto to set
		 */
		public void setSequenceDto(SequenceDto sequenceDto) {
			this.sequenceDto = sequenceDto;
		}

		/**
		 * @return the sellerSequences
		 */
		public Map<String, SequenceDto> getSellerSequences() {
			return sellerSequences;
		}

		/**
		 * @param sellerSequences the sellerSequences to set
		 */
		public void setSellerSequences(Map<String, SequenceDto> sellerSequences) {
			this.sellerSequences = sellerSequences;
		}

		/**
		 * @return the occTemplateNegativeCode
		 */
		public String getOccTemplateNegativeCode() {
			return occTemplateNegativeCode;
		}

		/**
		 * @param occTemplateNegativeCode the occTemplateNegativeCode to set
		 */
		public void setOccTemplateNegativeCode(String occTemplateNegativeCode) {
			this.occTemplateNegativeCode = occTemplateNegativeCode;
		}

		@Override
		public String toString() {
			return "InvoiceTypeDto [code=" + code + ", description=" + description + ", occTemplateCode=" + occTemplateCode + ", occTemplateNegativeCode=" + occTemplateNegativeCode + ", sequenceDto=" + sequenceDto + ", sellerSequences=" + sellerSequences + ", appliesTo=" + appliesTo + ", matchingAuto=" + matchingAuto + "]";
		}		
}

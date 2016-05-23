package org.meveo.api.dto.billing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeEnum;

@XmlRootElement(name = "InvoiceType")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceTypeDto  extends BaseDto{
	
		private static final long serialVersionUID = 1L;
		
		@XmlElement(required = true)
		private String code;
		
		private String description;
		
		@XmlElement(required = true)
		private InvoiceTypeEnum invoiceTypeEnum;
		
		@XmlElement(required = true)
		private String occTemplateCode;
		
		private String prefix;
		
		private Integer sequenceSize;
		
		private List<String> appliesTo = new ArrayList<String>();
		
		private boolean matchingAuto = false;
		
		public InvoiceTypeDto(){
			
		}

		public InvoiceTypeDto(InvoiceType invoiceType){
			this.code = invoiceType.getCode();
			this.description = invoiceType.getDescription();
			this.invoiceTypeEnum = invoiceType.getInvoiceTypeEnum();
			this.occTemplateCode = invoiceType.getOccTemplate() != null ? invoiceType.getOccTemplate().getCode():null;
			this.prefix = invoiceType.getPrefixEL();
			this.sequenceSize = invoiceType.getSequenceSize();
			if(invoiceType.getAppliesTo() != null){				
				for(InvoiceType tmpInvoiceType : invoiceType.getAppliesTo()){
					this.getAppliesTo().add(tmpInvoiceType.getCode());
				}
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
		 * @return the prefix
		 */
		public String getPrefix() {
			return prefix;
		}

		/**
		 * @param prefix the prefix to set
		 */
		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		/**
		 * @return the sequenceSize
		 */
		public Integer getSequenceSize() {
			return sequenceSize;
		}

		/**
		 * @param sequenceSize the sequenceSize to set
		 */
		public void setSequenceSize(Integer sequenceSize) {
			this.sequenceSize = sequenceSize;
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
		 * @return the invoiceTypeEnum
		 */
		public InvoiceTypeEnum getInvoiceTypeEnum() {
			return invoiceTypeEnum;
		}

		/**
		 * @param invoiceTypeEnum the invoiceTypeEnum to set
		 */
		public void setInvoiceTypeEnum(InvoiceTypeEnum invoiceTypeEnum) {
			this.invoiceTypeEnum = invoiceTypeEnum;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "InvoiceTypeDto [code=" + code + ", description=" + description + ", invoiceTypeEnum=" + invoiceTypeEnum + ", occTemplateCode=" + occTemplateCode + ", prefix=" + prefix + ", sequenceSize=" + sequenceSize + ", appliesTo=" + (appliesTo==null?"null":appliesTo) + ", matchingAuto=" + matchingAuto + "]";
		}


		
}

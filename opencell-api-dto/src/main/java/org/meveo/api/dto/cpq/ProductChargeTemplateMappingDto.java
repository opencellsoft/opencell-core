package org.meveo.api.dto.cpq;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement(name = "ProductChargeTemplateMappingDto")
@XmlType(name = "ProductChargeTemplateMappingDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductChargeTemplateMappingDto {

	@Schema(description = "The charge code")
	private String chargeCode;
	
	@Schema(description = "The product code")
	private String productCode;
	
	@Schema(description = "The counter code")
	private String counterCode;
	
    @XmlElementWrapper(name = "accumulatorCounterCodes")
    @XmlElement(name = "accumulatorCounterCodes")
	@Schema(description = "List of accumulator counter codes")
	private List<String> accumulatorCounterCodes = new ArrayList<String>();
	
	/**
	 * @return the chargeCode
	 */
	public String getChargeCode() {
		return chargeCode;
	}
	/**
	 * @param chargeCode the chargeCode to set
	 */
	public void setChargeCode(String chargeCode) {
		this.chargeCode = chargeCode;
	}
	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}
	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	/**
	 * @return the counterCode
	 */
	public String getCounterCode() {
		return counterCode;
	}
	/**
	 * @param counterCode the counterCode to set
	 */
	public void setCounterCode(String counterCode) {
		this.counterCode = counterCode;
	}
	/**
	 * @return the accumulatorCounterCodes
	 */
	public List<String> getAccumulatorCounterCodes() {
		return accumulatorCounterCodes;
	}
	/**
	 * @param accumulatorCounterCodes the accumulatorCounterCodes to set
	 */
	public void setAccumulatorCounterCodes(List<String> accumulatorCounterCodes) {
		this.accumulatorCounterCodes = accumulatorCounterCodes;
	}
	
	
}

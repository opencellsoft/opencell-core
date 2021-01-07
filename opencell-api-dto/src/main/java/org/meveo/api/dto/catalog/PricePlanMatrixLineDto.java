package org.meveo.api.dto.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.catalog.PricePlanMatrixLine;

@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixLineDto extends BaseEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3498847257781821440L;

	private Long ppmLineId;

	private BigDecimal pricetWithoutTax;

	private String pricePlanMatrixCode;

	private int pricePlanMatrixVersion;

	private List<PricePlanMatrixValueDto> pricePlanMatrixValues = new ArrayList<PricePlanMatrixValueDto>();

	public PricePlanMatrixLineDto() {
	}

	public PricePlanMatrixLineDto(PricePlanMatrixLine pricePlanMatrixLine) {
		this.ppmLineId = pricePlanMatrixLine.getId();
		this.pricetWithoutTax = pricePlanMatrixLine.getPricetWithoutTax();
		this.pricePlanMatrixCode = pricePlanMatrixLine.getPricePlanMatrixVersion().getPricePlanMatrix().getCode();
		this.pricePlanMatrixVersion = pricePlanMatrixLine.getPricePlanMatrixVersion().getCurrentVersion();
		pricePlanMatrixValues = pricePlanMatrixLine.getPricePlanMatrixValues()
				.stream()
				.map(value -> new PricePlanMatrixValueDto(value))
				.collect(Collectors.toList());
	}

	/**
	 * @return the ppmLineId
	 */
	public Long getPpmLineId() {
		return ppmLineId;
	}

	/**
	 * @param ppmLineId the ppmLineId to set
	 */
	public void setPpmLineId(Long ppmLineId) {
		this.ppmLineId = ppmLineId;
	}

	/**
	 * @return the pricetWithoutTax
	 */
	public BigDecimal getPricetWithoutTax() {
		return pricetWithoutTax;
	}

	/**
	 * @param pricetWithoutTax the pricetWithoutTax to set
	 */
	public void setPricetWithoutTax(BigDecimal pricetWithoutTax) {
		this.pricetWithoutTax = pricetWithoutTax;
	}

	/**
	 * @return the pricePlanMatrixValues
	 */
	public List<PricePlanMatrixValueDto> getPricePlanMatrixValues() {
		return pricePlanMatrixValues;
	}

	/**
	 * @param pricePlanMatrixValues the pricePlanMatrixValues to set
	 */
	public void setPricePlanMatrixValues(List<PricePlanMatrixValueDto> pricePlanMatrixValues) {
		this.pricePlanMatrixValues = pricePlanMatrixValues;
	}

	public String getPricePlanMatrixCode() {
		return pricePlanMatrixCode;
	}

	public void setPricePlanMatrixCode(String pricePlanMatrixCode) {
		pricePlanMatrixCode = pricePlanMatrixCode;
	}

	public int getPricePlanMatrixVersion() {
		return pricePlanMatrixVersion;
	}

	public void setPricePlanMatrixVersion(int pricePlanMatrixVersion) {
		this.pricePlanMatrixVersion = pricePlanMatrixVersion;
	}
}

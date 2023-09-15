package org.meveo.api.dto.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.PositiveOrZero;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.PricePlanMatrixLine;

import io.swagger.v3.oas.annotations.media.Schema;

@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixLineDto extends BaseEntityDto {

	private static final long serialVersionUID = -3498847257781821440L;

    @Schema(description = "The price plan line id")
	private Long ppmLineId;

    @Schema(description = "The price without tax")
    @PositiveOrZero
	@Deprecated
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private BigDecimal priceWithoutTax;

	@Schema(description = "The value of price without tax or a discount")
	@PositiveOrZero
	private BigDecimal value;

    @Schema(description = "The price plan matrix code")
	private String pricePlanMatrixCode;

    @Schema(description = "The price plan matrix version")
	private int pricePlanMatrixVersion;

    @Schema(description = "The description price plan line")
	private String description;

	private List<PricePlanMatrixValueDto> pricePlanMatrixValues = new ArrayList<PricePlanMatrixValueDto>();

    @Schema(description = "The priority")
	private Integer priority;
    
    @Schema(description = "The Price EL")
    private String priceEL;

	@Schema(description = "The EL Value")
	private String valueEL;

	@Schema(description = "list of trading price plan matrix line")
	private List<TradingPricePlanMatrixLineDto> tradingPricePlanMatrixLines = new ArrayList<>();

	@Schema(description = "Price plan matrix custom fields")
	private CustomFieldsDto customFields;

	public PricePlanMatrixLineDto() {
	}

	public PricePlanMatrixLineDto(PricePlanMatrixLine pricePlanMatrixLine) {
		this.ppmLineId = pricePlanMatrixLine.getId();
		this.priceWithoutTax = pricePlanMatrixLine.getValue();
		this.value = pricePlanMatrixLine.getValue();
		this.description = pricePlanMatrixLine.getDescription();
		this.priority = pricePlanMatrixLine.getPriority();
		this.priceEL = pricePlanMatrixLine.getValueEL();
		this.valueEL = pricePlanMatrixLine.getValueEL();
		this.pricePlanMatrixCode = pricePlanMatrixLine.getPricePlanMatrixVersion().getPricePlanMatrix().getCode();
		this.pricePlanMatrixVersion = pricePlanMatrixLine.getPricePlanMatrixVersion().getCurrentVersion();
		pricePlanMatrixValues = pricePlanMatrixLine.getPricePlanMatrixValues()
				.stream()
				.map(value -> new PricePlanMatrixValueDto(value))
				.collect(Collectors.toList());
		tradingPricePlanMatrixLines = pricePlanMatrixLine.getTradingPricePlanMatrixLines()
				.stream()
				.map(line -> new TradingPricePlanMatrixLineDto(line))
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


	@Deprecated
	public BigDecimal getPriceWithoutTax() {
		return priceWithoutTax;
	}
	@Deprecated
	public void setPriceWithoutTax(BigDecimal priceWithoutTax) {
		this.priceWithoutTax = priceWithoutTax;
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
		this.pricePlanMatrixCode = pricePlanMatrixCode;
	}

	public int getPricePlanMatrixVersion() {
		return pricePlanMatrixVersion;
	}

	public void setPricePlanMatrixVersion(int pricePlanMatrixVersion) {
		this.pricePlanMatrixVersion = pricePlanMatrixVersion;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

    public String getPriceEL() {
        return priceEL;
    }

    public void setPriceEL(String priceEL) {
        this.priceEL = priceEL;
    }

	public String getValueEL() {
		return valueEL;
	}

	public void setValueEL(String valueEL) {
		this.valueEL = valueEL;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public List<TradingPricePlanMatrixLineDto> getTradingPricePlanMatrixLines() {
		return tradingPricePlanMatrixLines;
	}

	public void setTradingPricePlanMatrixLines(List<TradingPricePlanMatrixLineDto> tradingPricePlanMatrixLines) {
		this.tradingPricePlanMatrixLines = tradingPricePlanMatrixLines;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
}

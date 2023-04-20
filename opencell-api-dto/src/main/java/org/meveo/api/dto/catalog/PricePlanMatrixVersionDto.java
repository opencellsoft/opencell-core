package org.meveo.api.dto.catalog;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.PriceVersionTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixVersionDto extends BaseEntityDto {

    /**
     *
     */
    private static final long serialVersionUID = 1105680934764861643L;

    @Schema(description = "The price plan matrix version id")
    private Long id; 
    
    @NotNull
    @Schema(description = "The price plan matrix code")
    private String pricePlanMatrixCode;

    @Schema(description = "The status of price plan matrix version")
    private VersionStatusEnum statusEnum;
    
    @Schema(description = "the current version of price plan matrix")
    protected Integer version;
    
    @Schema(description = "The status date. it set automatically when the status change")
    private Date statusDate;
    
    @Schema(description = "The label of price plan matrix version")
    private String label;
    
    protected DatePeriod validity = new DatePeriod();
    @NotNull
    @Schema(description = "Indicate is the price plan is a matrix")
    private Boolean isMatrix;
    
    @Schema(description = "The price")
    private BigDecimal price;

    @Schema(description = "The amount without tax")
    @Deprecated
    private BigDecimal amountWithoutTax;
    
    @Schema(description = "The amount with tax")
    @Deprecated
    private BigDecimal amountWithTax;

    /**
     * Field was deprecated in 12 version. Use 'priceEL' field instead.
     */
    @Deprecated
    @Schema(description = "The amount without tax EL")
    private String amountWithoutTaxEL;

    /**
     * Field was deprecated in 12 version. Use 'priceEL' field instead.
     */
    @Deprecated
    @Schema(description = "The amount with tax EL")
    private String amountWithTaxEL;

    @Schema(description = "The Price EL")
    private String priceEL;
    
    @Schema(description = "The priority")
    protected int priority=0;

    @Schema(description = "The price version type, can be PERCENTAGE or FIXED.")
    private PriceVersionTypeEnum priceVersionType;

    private Set<PricePlanMatrixColumnDto> columns;

    private Set<PricePlanMatrixLineDto> lines;

    public PricePlanMatrixVersionDto() {
    }

    public PricePlanMatrixVersionDto(PricePlanMatrixVersion pricePlanMatrixVersion, boolean returnPricePlanMatrixLine) {
    	this.id = pricePlanMatrixVersion.getId();
        setLabel(pricePlanMatrixVersion.getLabel());
        setMatrix(pricePlanMatrixVersion.isMatrix());
        setPricePlanMatrixCode(pricePlanMatrixVersion.getPricePlanMatrix().getCode());
        setVersion(pricePlanMatrixVersion.getCurrentVersion());
        setStatusEnum(pricePlanMatrixVersion.getStatus());
        setStatusDate(pricePlanMatrixVersion.getStatusDate());
        setValidity(pricePlanMatrixVersion.getValidity());
        setPrice(pricePlanMatrixVersion.isMatrix()? BigDecimal.ZERO : pricePlanMatrixVersion.getPrice());
        setAmountWithoutTax(pricePlanMatrixVersion.getAmountWithoutTax());
        setAmountWithTax(pricePlanMatrixVersion.getAmountWithTax());
        setPriceEL(pricePlanMatrixVersion.getPriceEL());
        setPriority(pricePlanMatrixVersion.getPriority());
        if (returnPricePlanMatrixLine) {
            if (pricePlanMatrixVersion.getLines() != null && !pricePlanMatrixVersion.getLines().isEmpty()) {
                lines = pricePlanMatrixVersion.getLines().stream()
                        .map(PricePlanMatrixLineDto::new)
                        .collect(Collectors.toSet());
            }
        }
        if (pricePlanMatrixVersion.getColumns() != null && !pricePlanMatrixVersion.getColumns().isEmpty()) {
            columns = pricePlanMatrixVersion.getColumns().stream()
                    .map(PricePlanMatrixColumnDto::new)
                    .collect(Collectors.toSet());
        }
        setPriceVersionType(pricePlanMatrixVersion.getPriceVersionType());
    }

    public String getPricePlanMatrixCode() {
        return pricePlanMatrixCode;
    }

    public void setPricePlanMatrixCode(String pricePlanMatrixCode) {
        this.pricePlanMatrixCode = pricePlanMatrixCode;
    }

    public VersionStatusEnum getStatusEnum() {
        return statusEnum;
    }

    public void setStatusEnum(VersionStatusEnum statusEnum) {
        this.statusEnum = statusEnum;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DatePeriod getValidity() {
        return validity;
    }

    public void setValidity(DatePeriod validity) {
        this.validity = validity;
    }

    public Boolean getMatrix() {
        return isMatrix;
    }

    public void setMatrix(Boolean matrix) {
        isMatrix = matrix;
    }

    public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public String getAmountWithoutTaxEL() {
        return amountWithoutTaxEL;
    }

    public void setAmountWithoutTaxEL(String amountWithoutTaxEL) {
        this.amountWithoutTaxEL = amountWithoutTaxEL;
    }

    public String getAmountWithTaxEL() {
        return amountWithTaxEL;
    }

    public void setAmountWithTaxEL(String amountWithTaxEL) {
        this.amountWithTaxEL = amountWithTaxEL;
    }

    public String getPriceEL() {
        return priceEL;
    }

    public void setPriceEL(String priceEL) {
        this.priceEL = priceEL;
    }

    public Set<PricePlanMatrixLineDto> getLines() {
        return lines == null ? new HashSet<>() : lines;
    }

    public void setLines(Set<PricePlanMatrixLineDto> lines) {
        this.lines = lines;
    }

    public Set<PricePlanMatrixColumnDto> getColumns() {
        return columns;
    }

    public void setColumns(Set<PricePlanMatrixColumnDto> columns) {
        this.columns = columns;
    }

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

    public PriceVersionTypeEnum getPriceVersionType() {
        return priceVersionType;
    }

    public void setPriceVersionType(PriceVersionTypeEnum priceVersionType) {
        this.priceVersionType = priceVersionType;
    }
}

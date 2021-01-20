package org.meveo.api.dto.catalog;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.math.BigDecimal;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
public class PricePlanMatrixVersionDto extends BaseEntityDto {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1105680934764861643L;

	@NotNull
    private String pricePlanMatrixCode;

    private VersionStatusEnum statusEnum;
    @NotNull
    /** The currentVersion. */
    protected int version;
    private Date statusDate;
    private String label;
    protected DatePeriod validity = new DatePeriod();
    @NotNull
    private Boolean isMatrix;
    private BigDecimal priceWithoutTax;
    private BigDecimal amountWithoutTax;
    private BigDecimal amountWithTax;
    private String amountWithoutTaxEL;
    private String amountWithTaxEL;

    public PricePlanMatrixVersionDto() {
    }

    public PricePlanMatrixVersionDto(PricePlanMatrixVersion pricePlanMatrixVersion) {
        setLabel(pricePlanMatrixVersion.getLabel());
        setMatrix(pricePlanMatrixVersion.getMatrix());
        setPricePlanMatrixCode(pricePlanMatrixVersion.getPricePlanMatrix().getCode());
        setVersion(pricePlanMatrixVersion.getCurrentVersion());
        setStatusEnum(pricePlanMatrixVersion.getStatus());
        setStatusDate(pricePlanMatrixVersion.getStatusDate());
        setValidity(pricePlanMatrixVersion.getValidity());
        setAmountWithoutTax(pricePlanMatrixVersion.getAmountWithoutTax());
        setAmountWithTax(pricePlanMatrixVersion.getAmountWithTax());
        setAmountWithoutTaxEL(pricePlanMatrixVersion.getAmountWithoutTaxEL());
        setAmountWithTaxEL(pricePlanMatrixVersion.getAmountWithTaxEL());
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
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

    public BigDecimal getPriceWithoutTax() {
        return priceWithoutTax;
    }

    public void setPriceWithoutTax(BigDecimal priceWithoutTax) {
        this.priceWithoutTax = priceWithoutTax;
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
}

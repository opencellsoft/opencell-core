package org.meveo.model.quote;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.cpq.enums.OneShotTypeEnum;
import org.meveo.model.cpq.enums.PriceTypeEnum;

@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_quote_price", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_quote_price_seq"), })
@NamedQuery(name = "QuotePrice.findByCode", query = "select q from QuotePrice where q.code=:code")
public class QuotePrice extends BusinessEntity  {


    /**
     * quote item
     */
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "quote_item_id", nullable = false, referencedColumnName = "id")
	@NotNull
    private QuoteItem quoteItem;
    
    @Column(name = "charge_code", nullable = false, length = 20)
    @Size(max = 20)
    private String chargeCode;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "price_type", nullable = false)
    @NotNull
    private PriceTypeEnum priceType;

    @Column(name = "recurence_duration")
    private int recurenceDuration;

    @Column(name = "recurence_periodicity")
    private int recurencePeriodicity;

    @Column(name = "overcharge")
    private boolean overCharge;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "oneshot_type")
    private OneShotTypeEnum oneShotType;

    @Column(name = "param1", length = 50)
    @Size(max = 50)
    private String param1;

    @Column(name = "param2", length = 50)
    @Size(max = 50)
    private String param2;

    @Column(name = "param3", length = 50)
    @Size(max = 50)
    private String param3;

    @Column(name = "param4", length = 50)
    @Size(max = 50)
    private String param4;

    @Column(name = "price_matrix", nullable = false)
    private Boolean priceMatrix;

    @Column(name = "dim1_matrix", length = 50)
    @Size(max = 50)
    private String dim1Matrix;

    @Column(name = "dim2_matrix", length = 50)
    @Size(max = 50)
    private String dim2Matrix;

    @Column(name = "dim3_matrix", length = 50)
    @Size(max = 50)
    private String dim3Matrix;

    @Column(name = "usage_code", length = 20, nullable = false)
    @Size(max = 20)
    private String usageCode;
    

    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal quantity = BigDecimal.ONE;
    

    @Column(name = "unite_price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal unitePriceWithoutTax;

    @Column(name = "price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal priceWithoutTax;

    @Column(name = "tax_code", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal taxCode;

    @Column(name = "tax_rate")
    private int taxRate;

    @Column(name = "price_with_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal priceWithTax;

	/**
	 * @return the quoteItem
	 */
	public QuoteItem getQuoteItem() {
		return quoteItem;
	}

	/**
	 * @param quoteItem the quoteItem to set
	 */
	public void setQuoteItem(QuoteItem quoteItem) {
		this.quoteItem = quoteItem;
	}

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
	 * @return the priceType
	 */
	public PriceTypeEnum getPriceType() {
		return priceType;
	}

	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(PriceTypeEnum priceType) {
		this.priceType = priceType;
	}

	/**
	 * @return the recurenceDuration
	 */
	public int getRecurenceDuration() {
		return recurenceDuration;
	}

	/**
	 * @param recurenceDuration the recurenceDuration to set
	 */
	public void setRecurenceDuration(int recurenceDuration) {
		this.recurenceDuration = recurenceDuration;
	}

	/**
	 * @return the recurencePeriodicity
	 */
	public int getRecurencePeriodicity() {
		return recurencePeriodicity;
	}

	/**
	 * @param recurencePeriodicity the recurencePeriodicity to set
	 */
	public void setRecurencePeriodicity(int recurencePeriodicity) {
		this.recurencePeriodicity = recurencePeriodicity;
	}

	/**
	 * @return the overCharge
	 */
	public boolean isOverCharge() {
		return overCharge;
	}

	/**
	 * @param overCharge the overCharge to set
	 */
	public void setOverCharge(boolean overCharge) {
		this.overCharge = overCharge;
	}

	/**
	 * @return the oneShotType
	 */
	public OneShotTypeEnum getOneShotType() {
		return oneShotType;
	}

	/**
	 * @param oneShotType the oneShotType to set
	 */
	public void setOneShotType(OneShotTypeEnum oneShotType) {
		this.oneShotType = oneShotType;
	}

	/**
	 * @return the param1
	 */
	public String getParam1() {
		return param1;
	}

	/**
	 * @param param1 the param1 to set
	 */
	public void setParam1(String param1) {
		this.param1 = param1;
	}

	/**
	 * @return the param2
	 */
	public String getParam2() {
		return param2;
	}

	/**
	 * @param param2 the param2 to set
	 */
	public void setParam2(String param2) {
		this.param2 = param2;
	}

	/**
	 * @return the param3
	 */
	public String getParam3() {
		return param3;
	}

	/**
	 * @param param3 the param3 to set
	 */
	public void setParam3(String param3) {
		this.param3 = param3;
	}

	/**
	 * @return the param4
	 */
	public String getParam4() {
		return param4;
	}

	/**
	 * @param param4 the param4 to set
	 */
	public void setParam4(String param4) {
		this.param4 = param4;
	}

	/**
	 * @return the priceMatrix
	 */
	public Boolean getPriceMatrix() {
		return priceMatrix;
	}

	/**
	 * @param priceMatrix the priceMatrix to set
	 */
	public void setPriceMatrix(Boolean priceMatrix) {
		this.priceMatrix = priceMatrix;
	}

	/**
	 * @return the dim1Matrix
	 */
	public String getDim1Matrix() {
		return dim1Matrix;
	}

	/**
	 * @param dim1Matrix the dim1Matrix to set
	 */
	public void setDim1Matrix(String dim1Matrix) {
		this.dim1Matrix = dim1Matrix;
	}

	/**
	 * @return the dim2Matrix
	 */
	public String getDim2Matrix() {
		return dim2Matrix;
	}

	/**
	 * @param dim2Matrix the dim2Matrix to set
	 */
	public void setDim2Matrix(String dim2Matrix) {
		this.dim2Matrix = dim2Matrix;
	}

	/**
	 * @return the dim3Matrix
	 */
	public String getDim3Matrix() {
		return dim3Matrix;
	}

	/**
	 * @param dim3Matrix the dim3Matrix to set
	 */
	public void setDim3Matrix(String dim3Matrix) {
		this.dim3Matrix = dim3Matrix;
	}

	/**
	 * @return the usageCode
	 */
	public String getUsageCode() {
		return usageCode;
	}

	/**
	 * @param usageCode the usageCode to set
	 */
	public void setUsageCode(String usageCode) {
		this.usageCode = usageCode;
	}

	/**
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the unitePriceWithoutTax
	 */
	public BigDecimal getUnitePriceWithoutTax() {
		return unitePriceWithoutTax;
	}

	/**
	 * @param unitePriceWithoutTax the unitePriceWithoutTax to set
	 */
	public void setUnitePriceWithoutTax(BigDecimal unitePriceWithoutTax) {
		this.unitePriceWithoutTax = unitePriceWithoutTax;
	}

	/**
	 * @return the priceWithoutTax
	 */
	public BigDecimal getPriceWithoutTax() {
		return priceWithoutTax;
	}

	/**
	 * @param priceWithoutTax the priceWithoutTax to set
	 */
	public void setPriceWithoutTax(BigDecimal priceWithoutTax) {
		this.priceWithoutTax = priceWithoutTax;
	}

	/**
	 * @return the taxCode
	 */
	public BigDecimal getTaxCode() {
		return taxCode;
	}

	/**
	 * @param taxCode the taxCode to set
	 */
	public void setTaxCode(BigDecimal taxCode) {
		this.taxCode = taxCode;
	}

	/**
	 * @return the taxRate
	 */
	public int getTaxRate() {
		return taxRate;
	}

	/**
	 * @param taxRate the taxRate to set
	 */
	public void setTaxRate(int taxRate) {
		this.taxRate = taxRate;
	}

	/**
	 * @return the priceWithTax
	 */
	public BigDecimal getPriceWithTax() {
		return priceWithTax;
	}

	/**
	 * @param priceWithTax the priceWithTax to set
	 */
	public void setPriceWithTax(BigDecimal priceWithTax) {
		this.priceWithTax = priceWithTax;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(chargeCode, dim1Matrix, dim2Matrix, dim3Matrix, oneShotType, overCharge,
				param1, param2, param3, param4, priceMatrix, priceType, priceWithTax, priceWithoutTax, quantity,
				quoteItem, recurenceDuration, recurencePeriodicity, taxCode, taxRate, unitePriceWithoutTax, usageCode);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuotePrice other = (QuotePrice) obj;
		return Objects.equals(chargeCode, other.chargeCode) && Objects.equals(dim1Matrix, other.dim1Matrix)
				&& Objects.equals(dim2Matrix, other.dim2Matrix) && Objects.equals(dim3Matrix, other.dim3Matrix)
				&& oneShotType == other.oneShotType && overCharge == other.overCharge
				&& Objects.equals(param1, other.param1) && Objects.equals(param2, other.param2)
				&& Objects.equals(param3, other.param3) && Objects.equals(param4, other.param4)
				&& Objects.equals(priceMatrix, other.priceMatrix) && priceType == other.priceType
				&& Objects.equals(priceWithTax, other.priceWithTax)
				&& Objects.equals(priceWithoutTax, other.priceWithoutTax) && Objects.equals(quantity, other.quantity)
				&& Objects.equals(quoteItem, other.quoteItem) && recurenceDuration == other.recurenceDuration
				&& recurencePeriodicity == other.recurencePeriodicity && Objects.equals(taxCode, other.taxCode)
				&& taxRate == other.taxRate && Objects.equals(unitePriceWithoutTax, other.unitePriceWithoutTax)
				&& Objects.equals(usageCode, other.usageCode);
	}
}

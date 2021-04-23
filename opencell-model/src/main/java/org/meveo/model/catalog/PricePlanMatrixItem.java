package org.meveo.model.catalog;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * @author Khairi
 * @version 10.0
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cpq_price_plan_matrix_item")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_price_plan_matrix_item_seq"), })
public class PricePlanMatrixItem extends BaseEntity {

	/**
	 * value of dimension 1
	 */
	@Column(name = "dim1_value", nullable = false, length = 50)
	private String dimValue1;

	/**
	 * value of dimension 2
	 */
	@Column(name = "dim2_value", length = 50)
	private String dimValue2;

	/**
	 * value of dimension 3
	 */
	@Column(name = "dim3_value", length = 50)
	private String dimValue3;
	
	/**
	 * price without tax
	 */
	@Column(name = "price_without_tax", precision = NB_PRECISION, scale = NB_DECIMALS)
	private BigDecimal priceWithoutTax;

	/**
	 * @return the dimValue1
	 */
	public String getDimValue1() {
		return dimValue1;
	}

	/**
	 * @param dimValue1 the dimValue1 to set
	 */
	public void setDimValue1(String dimValue1) {
		this.dimValue1 = dimValue1;
	}

	/**
	 * @return the dimValue2
	 */
	public String getDimValue2() {
		return dimValue2;
	}

	/**
	 * @param dimValue2 the dimValue2 to set
	 */
	public void setDimValue2(String dimValue2) {
		this.dimValue2 = dimValue2;
	}

	/**
	 * @return the dimValue3
	 */
	public String getDimValue3() {
		return dimValue3;
	}

	/**
	 * @param dimValue3 the dimValue3 to set
	 */
	public void setDimValue3(String dimValue3) {
		this.dimValue3 = dimValue3;
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
	
}

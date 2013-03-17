/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.model.catalog;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "CAT_PRICE_CODE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_PRICE_CODE_SEQ")
public class PriceCode extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "CODE", length = 20, nullable = false)
	@Size(max = 20, min = 1)
	@NotNull
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHARGE_ID_IN")
	@NotNull
	private OneShotChargeTemplate chargeTemplateIn;

	@Column(name = "CHARGE_IN_PRICE_1", precision = 23, scale = 12)
	@Digits(integer = 23, fraction = 12)
	private BigDecimal chargeInPrice1;

	@Column(name = "CHARGE_IN_PRICE_2", precision = 23, scale = 12)
	@Digits(integer = 23, fraction = 12)
	private BigDecimal chargeInPrice2;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CHARGE_ID_OUT")
	private OneShotChargeTemplate chargeTemplateOut;

	@Column(name = "CHARGE_OUT_PRICE_1", precision = 23, scale = 12, nullable = true)
	@Digits(integer = 23, fraction = 12)
	private BigDecimal chargeOutPrice1;

	@Column(name = "CHARGE_OUT_PRICE_2", precision = 23, scale = 12, nullable = true)
	@Digits(integer = 23, fraction = 12)
	private BigDecimal chargeOutPrice2;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public OneShotChargeTemplate getChargeTemplateIn() {
		return chargeTemplateIn;
	}

	public void setChargeTemplateIn(OneShotChargeTemplate chargeTemplateIn) {
		this.chargeTemplateIn = chargeTemplateIn;
	}

	public BigDecimal getChargeInPrice1() {
		return chargeInPrice1;
	}

	public void setChargeInPrice1(BigDecimal chargeInPrice1) {
		this.chargeInPrice1 = chargeInPrice1;
	}

	public BigDecimal getChargeInPrice2() {
		return chargeInPrice2;
	}

	public void setChargeInPrice2(BigDecimal chargeInPrice2) {
		this.chargeInPrice2 = chargeInPrice2;
	}

	public OneShotChargeTemplate getChargeTemplateOut() {
		return chargeTemplateOut;
	}

	public void setChargeTemplateOut(OneShotChargeTemplate chargeTemplateOut) {
		this.chargeTemplateOut = chargeTemplateOut;
	}

	public BigDecimal getChargeOutPrice1() {
		return chargeOutPrice1;
	}

	public void setChargeOutPrice1(BigDecimal chargeOutPrice1) {
		this.chargeOutPrice1 = chargeOutPrice1;
	}

	public BigDecimal getChargeOutPrice2() {
		return chargeOutPrice2;
	}

	public void setChargeOutPrice2(BigDecimal chargeOutPrice2) {
		this.chargeOutPrice2 = chargeOutPrice2;
	}

}

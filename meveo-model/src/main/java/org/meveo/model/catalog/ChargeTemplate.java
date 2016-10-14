/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.OperationTypeEnum;
import org.meveo.model.finance.RevenueRecognitionRule;

@Entity
@ModuleItem
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "CHARGE")
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_CHARGE_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_CHARGE_TEMPLATE_SEQ")
@Inheritance(strategy = InheritanceType.JOINED)
public class ChargeTemplate extends BusinessCFEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "CREDIT_DEBIT_FLAG")
	private OperationTypeEnum type;

	@Column(name = "AMOUNT_EDITABLE")
	private Boolean amountEditable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INVOICE_SUB_CATEGORY", nullable = false)
	@NotNull
	private InvoiceSubCategory invoiceSubCategory;

	@OneToMany(mappedBy = "chargeTemplate", fetch = FetchType.LAZY)
	private List<ChargeInstance> chargeInstances = new ArrayList<ChargeInstance>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_CHRG_EDR", joinColumns = @JoinColumn(name = "CHARGE_TMPL_ID"), inverseJoinColumns = @JoinColumn(name = "TRIGG_EDR_ID"))
	private List<TriggeredEDRTemplate> edrTemplates = new ArrayList<TriggeredEDRTemplate>();

	@Column(name = "INPUT_UNIT_DESCRIPTION", length = 20)
    @Size(max = 20)
	private String inputUnitDescription;
	
	@Column(name = "RATING_UNIT_DESCRIPTION", length = 20)
    @Size(max = 20)
	private String ratingUnitDescription;
	
	@Column(name = "UNIT_MULTIPLICATOR", precision = BaseEntity.NB_PRECISION, scale = BaseEntity.NB_DECIMALS)
	private BigDecimal unitMultiplicator;
	
	@Column(name = "UNIT_NB_DECIMAL")
	private int unitNbDecimal = BaseEntity.NB_DECIMALS;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ROUNDING_MODE")
	private RoundingModeEnum roundingMode = RoundingModeEnum.NEAREST; 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REVENUE_RECOG_RULE_ID")
	private RevenueRecognitionRule revenueRecognitionRule;

	public OperationTypeEnum getType() {
		return type;
	}

	public void setType(OperationTypeEnum type) {
		this.type = type;
	}

	public Boolean getAmountEditable() {
		return amountEditable;
	}

	public void setAmountEditable(Boolean amountEditable) {
		this.amountEditable = amountEditable;
	}

	public InvoiceSubCategory getInvoiceSubCategory() {
		return invoiceSubCategory;
	}

	public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
		this.invoiceSubCategory = invoiceSubCategory;
	}

	public List<ChargeInstance> getChargeInstances() {
		return chargeInstances;
	}

	public void setChargeInstances(List<ChargeInstance> chargeInstances) {
		this.chargeInstances = chargeInstances;
	}

	public List<TriggeredEDRTemplate> getEdrTemplates() {
		return edrTemplates;
	}

	public void setEdrTemplates(List<TriggeredEDRTemplate> edrTemplates) {
		this.edrTemplates = edrTemplates;
	}

	public String getInputUnitDescription() {
		return inputUnitDescription;
	}

	public void setInputUnitDescription(String inputUnitDescription) {
		this.inputUnitDescription = inputUnitDescription;
	}

	public String getRatingUnitDescription() {
		return ratingUnitDescription;
	}

	public void setRatingUnitDescription(String ratingUnitDescription) {
		this.ratingUnitDescription = ratingUnitDescription;
	}

	public BigDecimal getUnitMultiplicator() {
		return unitMultiplicator;
	}

	public void setUnitMultiplicator(BigDecimal unitMultiplicator) {
		this.unitMultiplicator = unitMultiplicator;
	}

	public int getUnitNbDecimal() {
		return unitNbDecimal;
	}

	public void setUnitNbDecimal(int unitNbDecimal) {
		this.unitNbDecimal = unitNbDecimal;
	}

	/**
	 * @return the roundingMode
	 */
	public RoundingModeEnum getRoundingMode() {
		return roundingMode;
	}

	/**
	 * @param roundingMode the roundingMode to set
	 */
	public void setRoundingMode(RoundingModeEnum roundingMode) {
		this.roundingMode = roundingMode;
	}

	public RevenueRecognitionRule getRevenueRecognitionRule() {
		return revenueRecognitionRule;
	}

	public void setRevenueRecognitionRule(RevenueRecognitionRule revenueRecognitionRule) {
		this.revenueRecognitionRule = revenueRecognitionRule;
	}

    
}
/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.catalog;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Unit Of Measure
 *
 * @author Mounir Bahije
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "cat_unit_of_measure", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "cat_unit_of_measure_seq"), })
@NamedQueries({
		@NamedQuery(name = "unitOfMeasure.listBaseUnits", query = "from UnitOfMeasure UOM where UOM.parentUnitOfMeasure is null"),
		@NamedQuery(name = "unitOfMeasure.listChildUnits", query = "from UnitOfMeasure UOM where UOM.parentUnitOfMeasure=:parentUnitOfMeasure") })
public class UnitOfMeasure extends BusinessEntity {

	private static final long serialVersionUID = 1278336655583944747L;

	/**
	 * symbol
	 */
	@Column(name = "symbol", length = 100)
	@Size(max = 100)
	private String symbol;

	/**
	 * Translated descriptions in JSON format with language code as a key and
	 * translated description as a value
	 */
	@Type(type = "json")
	@Column(name = "description_i18n", columnDefinition = "text")
	private Map<String, String> descriptionI18n;

	/**
	 * multiplicator
	 */
	@Column(name = "multiplicator")
	private Long multiplicator = 1l;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private UnitOfMeasure parentUnitOfMeasure;

	public UnitOfMeasure() {

	}

	public UnitOfMeasure(String code, String symbol) {
		this.code = code;
		this.symbol = symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Map<String, String> getDescriptionI18n() {
		return descriptionI18n;
	}

	public void setDescriptionI18n(Map<String, String> descriptionI18n) {
		this.descriptionI18n = descriptionI18n;
	}

	public Long getMultiplicator() {
		return multiplicator;
	}

	public void setMultiplicator(Long multiplicator) {
		this.multiplicator = multiplicator;
	}

	public String getDescriptionNotNull() {
		return StringUtils.isBlank(super.getDescription()) ? getCode() : super.getDescription();
	}

	/**
	 * Instantiate descriptionI18n field if it is null. NOTE: do not use this method
	 * unless you have an intention to modify it's value, as entity will be marked
	 * dirty and record will be updated in DB
	 *
	 * @return descriptionI18n value or instantiated descriptionI18n field value
	 */
	public Map<String, String> getDescriptionI18nNullSafe() {
		if (descriptionI18n == null) {
			descriptionI18n = new HashMap<>();
		}
		return descriptionI18n;
	}

	public UnitOfMeasure getParentUnitOfMeasure() {
		return parentUnitOfMeasure;
	}

	public void setParentUnitOfMeasure(UnitOfMeasure parentUnitOfMeasure) {
		this.parentUnitOfMeasure = parentUnitOfMeasure;
	}

	public boolean isCompatibleWith(UnitOfMeasure ratingUnitOfMeasure) {
		if (ratingUnitOfMeasure != null) {
			UnitOfMeasure ratingParentUOM = ratingUnitOfMeasure.getParentUnitOfMeasure();
			UnitOfMeasure parentUOM = this.getParentUnitOfMeasure();
			if (ratingParentUOM != null && (ratingParentUOM.equals(this) || ratingParentUOM.equals(parentUOM))) {
				return true;
			} else {
				return parentUOM != null && parentUOM.equals(ratingUnitOfMeasure);
			}
		}
		return false;
	}

	public boolean isBaseUnit() {
		return this.parentUnitOfMeasure == null;
	}

}
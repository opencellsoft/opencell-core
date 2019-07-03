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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

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
public class UnitOfMeasure extends BusinessEntity {

    private static final long serialVersionUID = 1278336655583944747L;

    /**
     * symbol
     */
    @Column(name = "symbol", length = 100)
    @Size(max = 100)
    private String symbol;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    /**
     * multiplicator
     */
    @Column(name = "multiplicator")
    private Long multiplicator = 1l;


//    @OneToMany(mappedBy = "inputUnitOfMeasure", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
//    private List<ChargeTemplate> chargeTemplatesInput = new ArrayList<ChargeTemplate>();
//
//    @OneToMany(mappedBy = "ratingUnitOfMeasure", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
//    private List<ChargeTemplate> chargeTemplatesRating = new ArrayList<>();


    @SuppressWarnings("rawtypes")
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
     * Instantiate descriptionI18n field if it is null. NOTE: do not use this method unless you have an intention to modify it's value, as entity will be marked dirty and record
     * will be updated in DB
     *
     * @return descriptionI18n value or instantiated descriptionI18n field value
     */
    public Map<String, String> getDescriptionI18nNullSafe() {
        if (descriptionI18n == null) {
            descriptionI18n = new HashMap<>();
        }
        return descriptionI18n;
    }

//    public List<ChargeTemplate> getChargeTemplatesInput() {
//        return chargeTemplatesInput;
//    }
//
//    public void setChargeTemplatesInput(List<ChargeTemplate> chargeTemplatesInput) {
//        this.chargeTemplatesInput = chargeTemplatesInput;
//    }
//
//    public List<ChargeTemplate> getChargeTemplatesRating() {
//        return chargeTemplatesRating;
//    }
//
//    public void setChargeTemplatesRating(List<ChargeTemplate> chargeTemplatesRating) {
//        this.chargeTemplatesRating = chargeTemplatesRating;
//    }

    public UnitOfMeasure getParentUnitOfMeasure() {
        return parentUnitOfMeasure;
    }

    public void setParentUnitOfMeasure(UnitOfMeasure parentUnitOfMeasure) {
        this.parentUnitOfMeasure = parentUnitOfMeasure;
    }


}
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

package org.meveo.api.dto.tax;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.AuditableEntityDto;
import org.meveo.api.dto.IEntityDto;
import org.meveo.api.dto.IVersionedDto;
import org.meveo.model.tax.TaxMapping;

/**
 * DTO implementation of Tax mapping. Tax mapping
 * 
 * @author Andrius Karpavicius
 *
 */
@XmlRootElement(name = "TaxMapping")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaxMappingDto extends AuditableEntityDto implements Serializable, IVersionedDto, IEntityDto {
    private static final long serialVersionUID = 1L;

    /**
     * Identifier
     **/
    private Long id;

    /**
     * Account tax category
     **/
    private String accountTaxCategoryCode;
    /**
     * Charge tax class
     **/
    private String chargeTaxClassCode;
    /**
     * Tax mapping validity from.
     **/
    @XmlAttribute()
    protected Date validFrom;

    /**
     * Tax mapping validity to.
     **/
    @XmlAttribute()
    protected Date validTo;
    /**
     * Seller's country
     **/
    private String sellerCountryCode;
    /**
     * Buyer's country
     **/
    private String buyerCountryCode;
    /**
     * Filter expression
     **/
    @Size(max = 2000)
    private String filterEL;
    /**
     * Filter expression for Spark
     **/
    @Size(max = 2000)
    private String filterELSpark;
    /**
     * Tax to apply
     **/
    private String taxCode;
    /**
     * Tax expression
     **/
    @Size(max = 2000)
    private String taxEL;
    /**
     * Tax expression for Spark
     **/
    @Size(max = 2000)
    private String taxELSpark;
    /**
     * Script to determine tax
     **/
    private String taxScriptCode;

    /**
     * Priority. Higher value, higher the priority
     */
    private Integer priority;

    /**
     * Record's data source
     **/
    @Size(max = 2000)
    private String source;
    /**
     * Original record that this record overrides (identifier)
     **/
    private Long originId;

    /**
     * Default constructor
     */
    public TaxMappingDto() {
        super();
    }

    /**
     * Instantiates a new TaxMapping Dto.
     *
     * @param entity The Tax mapping entity
     */
    public TaxMappingDto(TaxMapping entity) {
        super(entity);

        this.id = entity.getId();

        if (entity.getAccountTaxCategory() != null) {
            this.accountTaxCategoryCode = entity.getAccountTaxCategory().getCode();
        }

        if (entity.getChargeTaxClass() != null) {
            this.chargeTaxClassCode = entity.getChargeTaxClass().getCode();
        }

        if (entity.getValid() != null) {
            this.validFrom = entity.getValid().getFrom();
            this.validTo = entity.getValid().getTo();
        }

        if (entity.getSellerCountry() != null) {
            this.sellerCountryCode = entity.getSellerCountry().getCode();
        }

        if (entity.getBuyerCountry() != null) {
            this.buyerCountryCode = entity.getBuyerCountry().getCode();
        }

        this.filterEL = entity.getFilterEL();

        this.filterELSpark = entity.getFilterELSpark();

        if (entity.getTax() != null) {
            this.taxCode = entity.getTax().getCode();
        }

        this.taxEL = entity.getTaxEL();

        this.taxELSpark = entity.getTaxELSpark();

        if (entity.getTaxScript() != null) {
            this.taxScriptCode = entity.getTaxScript().getCode();
        }

        this.priority = entity.getPriority();
        this.source = entity.getSource();

        this.originId = entity.getOriginId();
    }

    /**
     * @param id Identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Identifier
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param accountTaxCategory Account tax category
     */
    public void setAccountTaxCategoryCode(String accountTaxCategoryCode) {
        this.accountTaxCategoryCode = accountTaxCategoryCode;
    }

    /**
     * @return Account tax category
     */
    public String getAccountTaxCategoryCode() {
        return this.accountTaxCategoryCode;
    }

    /**
     * @param chargeTaxClass Charge tax class
     */
    public void setChargeTaxClassCode(String chargeTaxClassCode) {
        this.chargeTaxClassCode = chargeTaxClassCode;
    }

    /**
     * @return Charge tax class
     */
    public String getChargeTaxClassCode() {
        return this.chargeTaxClassCode;
    }

    @Override
    public Date getValidFrom() {
        return validFrom;
    }

    @Override
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    @Override
    public Date getValidTo() {
        return validTo;
    }

    @Override
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * @param sellerCountry Seller's country
     */
    public void setSellerCountryCode(String sellerCountryCode) {
        this.sellerCountryCode = sellerCountryCode;
    }

    /**
     * @return Seller's country
     */
    public String getSellerCountryCode() {
        return this.sellerCountryCode;
    }

    /**
     * @param buyerCountry Buyer's country
     */
    public void setBuyerCountryCode(String buyerCountryCode) {
        this.buyerCountryCode = buyerCountryCode;
    }

    /**
     * @return Buyer's country
     */
    public String getBuyerCountryCode() {
        return this.buyerCountryCode;
    }

    /**
     * @param filterEL Filter expression
     */
    public void setFilterEL(String filterEL) {
        this.filterEL = filterEL;
    }

    /**
     * @return Filter expression
     */
    public String getFilterEL() {
        return this.filterEL;
    }

    /**
     * @param filterELSpark Filter expression for Spark
     */
    public void setFilterELSpark(String filterELSpark) {
        this.filterELSpark = filterELSpark;
    }

    /**
     * @return Filter expression for Spark
     */
    public String getFilterELSpark() {
        return this.filterELSpark;
    }

    /**
     * @param tax Tax to apply
     */
    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    /**
     * @return Tax to apply
     */
    public String getTaxCode() {
        return this.taxCode;
    }

    /**
     * @param taxEL Tax expression
     */
    public void setTaxEL(String taxEL) {
        this.taxEL = taxEL;
    }

    /**
     * @return Tax expression
     */
    public String getTaxEL() {
        return this.taxEL;
    }

    /**
     * @param taxELSpark Tax expression for Spark
     */
    public void setTaxELSpark(String taxELSpark) {
        this.taxELSpark = taxELSpark;
    }

    /**
     * @return Tax expression for Spark
     */
    public String getTaxELSpark() {
        return this.taxELSpark;
    }

    /**
     * @param taxScript Script to determine tax
     */
    public void setTaxScriptCode(String taxScriptCode) {
        this.taxScriptCode = taxScriptCode;
    }

    /**
     * @return Script to determine tax
     */
    public String getTaxScriptCode() {
        return this.taxScriptCode;
    }

    /**
     * @return Priority. Higher value, higher the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority Priority. Higher value, higher the priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @param source Record's data source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return Record's data source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * @param originId Original record that this record overrides (identifier)
     */
    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    /**
     * @return Original record that this record overrides (identifier)
     */
    public Long getOriginId() {
        return this.originId;
    }
}
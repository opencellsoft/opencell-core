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
package org.meveo.model.crm;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Customer category
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Cacheable
@CustomFieldEntity
@ExportIdentifier({ "code" })
@Table(name = "crm_customer_category", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "crm_customer_category_seq"), })
public class CustomerCategory extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Is account exonerated from taxes
     */
    @Type(type = "numeric_boolean")
    @Column(name = "exonerated_from_taxes")
    private boolean exoneratedFromTaxes = false;

    /**
     * Expression to determine if account is exonerated from taxes
     */
    @Column(name = "exoneration_tax_el", length = 2000)
    @Size(max = 2000)
    private String exonerationTaxEl;

    /**
     * Expression to determine if account is exonerated from taxes - for Spark
     */
    @Column(name = "exoneration_tax_el_sp", length = 2000)
    @Size(max = 2000)
    private String exonerationTaxElSpark;

    /**
     * Exoneration reason
     */
    @Column(name = "exoneration_reason", length = 255)
    @Size(max = 255)
    private String exonerationReason;

    /**
     * @return True if account is exonerated from taxes
     */
    public boolean getExoneratedFromTaxes() {
        return exoneratedFromTaxes;
    }

    /**
     * @param exoneratedFromTaxes True if account is exonerated from taxes
     */
    public void setExoneratedFromTaxes(boolean exoneratedFromTaxes) {
        this.exoneratedFromTaxes = exoneratedFromTaxes;
    }

    /**
     * @return Expression to determine if account is exonerated from taxes
     */
    public String getExonerationTaxEl() {
        return exonerationTaxEl;
    }

    /**
     * @param exonerationTaxEl Expression to determine if account is exonerated from taxes
     */
    public void setExonerationTaxEl(String exonerationTaxEl) {
        this.exonerationTaxEl = exonerationTaxEl;
    }

    /**
     * @return Expression to determine if account is exonerated from taxes - for Spark
     */
    public String getExonerationTaxElSpark() {
        return exonerationTaxElSpark;
    }

    /**
     * @param exonerationTaxElSpark Expression to determine if account is exonerated from taxes - for Spark
     */
    public void setExonerationTaxElSpark(String exonerationTaxElSpark) {
        this.exonerationTaxElSpark = exonerationTaxElSpark;
    }

    /**
     * @return the exonerationReason
     */
    public String getExonerationReason() {
        return exonerationReason;
    }

    /**
     * @param exonerationReason the exonerationReason to set
     */
    public void setExonerationReason(String exonerationReason) {
        this.exonerationReason = exonerationReason;
    }
}
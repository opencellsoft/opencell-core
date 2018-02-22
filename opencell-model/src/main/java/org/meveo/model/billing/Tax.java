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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "TAX")
@ExportIdentifier({ "code" })
@Table(name = "billing_tax", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_tax_seq"), })
@NamedQueries({
        @NamedQuery(name = "tax.getNbTaxesNotAssociated", query = "select count(*) from Tax t where t.id not in (select l.tax.id from TaxLanguage l where l.tax.id is not null)"
                + " and t.id not in (select inv.tax.id from InvoiceSubcategoryCountry inv where inv.tax.id is not null)"),
        @NamedQuery(name = "tax.getTaxesNotAssociated", query = "from Tax t where t.id not in (select l.tax.id from TaxLanguage l where l.tax.id is not null ) "
                + " and t.id not in (select inv.tax.id from InvoiceSubcategoryCountry inv where inv.tax.id is not null)")

})
public class Tax extends BusinessCFEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "accounting_code", length = 255)
    @Size(max = 255)
    private String accountingCode;

    @Column(name = "tax_percentage", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal percent;

    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    public Tax() {

    }

    public Tax(Tax tax) {
        this.code = tax.getCode();
        this.description = tax.getDescription();
        this.setAuditable(tax.getAuditable());
        this.setDisabled(tax.isDisabled());
        this.accountingCode = tax.getAccountingCode();
        this.percent = tax.getPercent();
    }

    public String getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
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
}
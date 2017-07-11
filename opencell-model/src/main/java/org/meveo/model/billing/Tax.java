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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.MultilanguageEntity;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "TAX")
@MultilanguageEntity(key = "menu.taxes", group = "Tax")
@ExportIdentifier({ "code"})
@Table(name = "billing_tax", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "billing_tax_seq"), })
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
}
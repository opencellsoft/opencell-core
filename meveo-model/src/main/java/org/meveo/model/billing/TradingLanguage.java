/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@ExportIdentifier({ "language.languageCode", "provider" })
@Cacheable
@Table(name = "BILLING_TRADING_LANGUAGE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_TRADING_LANGUAGE_SEQ")
@NamedQueries({			
@NamedQuery(name = "tradingLanguage.getNbLanguageNotAssociated", 
	           query = "select count(*) from TradingLanguage tr where tr.id not in (select s.tradingLanguage.id from Seller s where s.tradingLanguage.id is not null) and tr.provider=:provider"),
	           
@NamedQuery(name = "tradingLanguage.getLanguagesNotAssociated", 
	           query = "from TradingLanguage tr where tr.id not in (select s.tradingLanguage.id from Seller s where s.tradingLanguage.id is not null) and tr.provider=:provider")	           	                  	         
	})

public class TradingLanguage extends AuditableEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LANGUAGE_ID")
	private Language language;

	@Column(name = "PR_DESCRIPTION", length = 100)
	private String prDescription;

	@Transient
	String languageCode;

	public String getPrDescription() {
		return prDescription;
	}

	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getLanguageCode() {
		return (language != null) ? language.getLanguageCode() : null;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

}

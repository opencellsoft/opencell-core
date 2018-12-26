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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

/**
 * Language enabled in application
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ObservableEntity
@ExportIdentifier({ "language.languageCode" })
@Cacheable
@Table(name = "billing_trading_language")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_trading_language_seq"), })
@NamedQueries({
        @NamedQuery(name = "TradingLanguage.getNbLanguageNotAssociated", query = "select count(*) from TradingLanguage tr where tr.id not in (select s.tradingLanguage.id from Seller s where s.tradingLanguage.id is not null)", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
        @NamedQuery(name = "TradingLanguage.getLanguagesNotAssociated", query = "from TradingLanguage tr where tr.id not in (select s.tradingLanguage.id from Seller s where s.tradingLanguage.id is not null) "),
        @NamedQuery(name = "TradingLanguage.getByCode", query = "from TradingLanguage tr where tr.language.languageCode = :tradingLanguageCode) ", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
        @NamedQuery(name = "TradingLanguage.languageCodes", query = "select distinct tr.language.languageCode from TradingLanguage tr order by tr.language.languageCode", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
public class TradingLanguage extends EnableEntity {
    private static final long serialVersionUID = 1L;

    /**
     * Language
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language language;

    /**
     * Description. Deprecated in 5.3. Use language.description instead.
     */
    @Deprecated
    @Column(name = "pr_description", length = 255)
    @Size(max = 255)
    private String prDescription;

    /**
     * Language code
     */
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

    @Override
    public String toString() {
        return String.format("TradingLanguage [language=%s, id=%s]", language, getId());
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof TradingLanguage)) {
            return false;
        }

        TradingLanguage other = (TradingLanguage) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;

        } else if (language.getId().equals(other.getLanguage().getId())) {
            return true;
        }
        return false;
    }
}
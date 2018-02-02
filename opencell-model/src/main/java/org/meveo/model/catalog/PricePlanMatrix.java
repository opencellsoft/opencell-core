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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.scripts.ScriptInstance;

@Entity
@ModuleItem
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "PRICEPLAN")
@ExportIdentifier({ "code" })
@Table(name = "cat_price_plan_matrix", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_price_plan_matrix_seq"), })
@NamedQueries({
        @NamedQuery(name = "PricePlanMatrix.getPricePlansForCache", query = "SELECT ppm from PricePlanMatrix ppm left join ppm.offerTemplate ot left join ppm.validityCalendar vc where ppm.disabled is false order by ppm.eventCode, ppm.priority ASC") })
public class PricePlanMatrix extends BusinessCFEntity implements Comparable<PricePlanMatrix> {
    private static final long serialVersionUID = 1L;

    @Column(name = "event_code", length = 255, nullable = false)
    @Size(min = 1, max = 255)
    @NotNull
    private String eventCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private OfferTemplate offerTemplate;

    @Column(name = "start_subscription_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startSubscriptionDate;

    @Column(name = "end_subscription_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endSubscriptionDate;

    @Column(name = "start_rating_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startRatingDate;

    @Column(name = "end_rating_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endRatingDate;

    @Column(name = "min_quantity")
    @Digits(integer = 23, fraction = 12)
    private BigDecimal minQuantity;

    @Column(name = "max_quantity")
    @Digits(integer = 23, fraction = 12)
    private BigDecimal maxQuantity;

    @Column(name = "min_subscr_age")
    private Long minSubscriptionAgeInMonth;

    @Column(name = "max_subscr_age")
    private Long maxSubscriptionAgeInMonth;

    @Column(name = "criteria_1", length = 255)
    @Size(max = 255)
    private String criteria1Value;

    @Column(name = "criteria_2", length = 255)
    @Size(max = 255)
    private String criteria2Value;

    @Column(name = "criteria_3", length = 255)
    @Size(max = 255)
    private String criteria3Value;

    @Column(name = "criteria_el", length = 2000)
    @Size(max = 2000)
    private String criteriaEL;

    @Column(name = "amount_without_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal amountWithoutTax;

    @Column(name = "amount_with_tax", precision = 23, scale = 12)
    @Digits(integer = 23, fraction = 12)
    private BigDecimal amountWithTax;

    @Column(name = "amount_without_tax_el", length = 2000)
    @Size(max = 2000)
    private String amountWithoutTaxEL;

    @Column(name = "amount_with_tax_el", length = 2000)
    @Size(max = 2000)
    private String amountWithTaxEL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_currency_id")
    private TradingCurrency tradingCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_country_id")
    private TradingCountry tradingCountry;

    @Column(name = "priority", columnDefinition = "int DEFAULT 1")
    private int priority = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Seller seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valid_cal_id")
    private Calendar validityCalendar;

    @Column(name = "sequence")
    private Long sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    @Column(name = "wo_description_el", length = 2000)
    @Size(max = 2000)
    private String woDescriptionEL;
    
    @Column(name = "rating_el", length = 2000)
    @Size(max = 2000)
    private String ratingEL;

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }

    public Date getStartSubscriptionDate() {
        return startSubscriptionDate;
    }

    public void setStartSubscriptionDate(Date startSubscriptionDate) {
        this.startSubscriptionDate = startSubscriptionDate;
    }

    public Date getEndSubscriptionDate() {
        return endSubscriptionDate;
    }

    public void setEndSubscriptionDate(Date endSubscriptionDate) {
        this.endSubscriptionDate = endSubscriptionDate;
    }

    public Date getStartRatingDate() {
        return startRatingDate;
    }

    public void setStartRatingDate(Date startRatingDate) {
        this.startRatingDate = startRatingDate;
    }

    public Date getEndRatingDate() {
        return endRatingDate;
    }

    public void setEndRatingDate(Date endRatingDate) {
        this.endRatingDate = endRatingDate;
    }

    public BigDecimal getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(BigDecimal minQuantity) {
        this.minQuantity = minQuantity;
    }

    public BigDecimal getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(BigDecimal maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public Long getMinSubscriptionAgeInMonth() {
        return minSubscriptionAgeInMonth;
    }

    public void setMinSubscriptionAgeInMonth(Long minSubscriptionAgeInMonth) {
        this.minSubscriptionAgeInMonth = minSubscriptionAgeInMonth;
    }

    public Long getMaxSubscriptionAgeInMonth() {
        return maxSubscriptionAgeInMonth;
    }

    public void setMaxSubscriptionAgeInMonth(Long maxSubscriptionAgeInMonth) {
        this.maxSubscriptionAgeInMonth = maxSubscriptionAgeInMonth;
    }

    public String getCriteria1Value() {
        return criteria1Value;
    }

    public void setCriteria1Value(String criteria1Value) {
        this.criteria1Value = criteria1Value;
    }

    public String getCriteria2Value() {
        return criteria2Value;
    }

    public void setCriteria2Value(String criteria2Value) {
        this.criteria2Value = criteria2Value;
    }

    public String getCriteria3Value() {
        return criteria3Value;
    }

    public void setCriteria3Value(String criteria3Value) {
        this.criteria3Value = criteria3Value;
    }

    public String getCriteriaEL() {
        return criteriaEL;
    }

    public void setCriteriaEL(String criteriaEL) {
        this.criteriaEL = criteriaEL;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public String getAmountWithoutTaxEL() {
        return amountWithoutTaxEL;
    }

    public void setAmountWithoutTaxEL(String amountWithoutTaxEL) {
        this.amountWithoutTaxEL = amountWithoutTaxEL;
    }

    public String getAmountWithTaxEL() {
        return amountWithTaxEL;
    }

    public void setAmountWithTaxEL(String amountWithTaxEL) {
        this.amountWithTaxEL = amountWithTaxEL;
    }

    public TradingCurrency getTradingCurrency() {
        return tradingCurrency;
    }

    public void setTradingCurrency(TradingCurrency tradingCurrency) {
        this.tradingCurrency = tradingCurrency;
    }

    public TradingCountry getTradingCountry() {
        return tradingCountry;
    }

    public void setTradingCountry(TradingCountry tradingCountry) {
        this.tradingCountry = tradingCountry;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Calendar getValidityCalendar() {
        return validityCalendar;
    }

    public void setValidityCalendar(Calendar validityCalendar) {
        this.validityCalendar = validityCalendar;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    @Override
    public String toString() {
        return String.format(
            "PricePlanMatrix [%s, eventCode=%s, offerTemplate=%s, startSubscriptionDate=%s, endSubscriptionDate=%s, startRatingDate=%s, endRatingDate=%s, minQuantity=%s, maxQuantity=%s, minSubscriptionAgeInMonth=%s, maxSubscriptionAgeInMonth=%s, criteria1Value=%s, criteria2Value=%s, criteria3Value=%s, criteriaEL=%s, amountWithoutTax=%s, amountWithTax=%s, tradingCurrency=%s, tradingCountry=%s, priority=%s, seller=%s, validityCalendar=%s]",
            super.toString(), eventCode, offerTemplate != null ? offerTemplate.getId() : null, startSubscriptionDate, endSubscriptionDate, startRatingDate, endRatingDate,
            minQuantity, maxQuantity, minSubscriptionAgeInMonth, maxSubscriptionAgeInMonth, criteria1Value, criteria2Value, criteria3Value, criteriaEL, amountWithoutTax,
            amountWithTax, tradingCurrency != null ? tradingCurrency.getId() : null, tradingCountry != null ? tradingCountry.getId() : null, priority,
            seller != null ? seller.getId() : null, validityCalendar != null ? validityCalendar.getId() : null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((criteria1Value == null) ? 0 : criteria1Value.hashCode());
        result = prime * result + ((criteria2Value == null) ? 0 : criteria2Value.hashCode());
        result = prime * result + ((criteria3Value == null) ? 0 : criteria3Value.hashCode());
        result = prime * result + ((endRatingDate == null) ? 0 : endRatingDate.hashCode());
        result = prime * result + ((endSubscriptionDate == null) ? 0 : endSubscriptionDate.hashCode());
        result = prime * result + ((eventCode == null) ? 0 : eventCode.hashCode());
        result = prime * result + ((maxSubscriptionAgeInMonth == null) ? 0 : maxSubscriptionAgeInMonth.hashCode());
        result = prime * result + ((minSubscriptionAgeInMonth == null) ? 0 : minSubscriptionAgeInMonth.hashCode());
        result = prime * result + ((startRatingDate == null) ? 0 : startRatingDate.hashCode());
        result = prime * result + ((startSubscriptionDate == null) ? 0 : startSubscriptionDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof PricePlanMatrix)) {
            return false;
        }

        PricePlanMatrix other = (PricePlanMatrix) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        if (criteria1Value == null) {
            if (other.criteria1Value != null)
                return false;
        } else if (!criteria1Value.equals(other.criteria1Value))
            return false;
        if (criteria2Value == null) {
            if (other.criteria2Value != null)
                return false;
        } else if (!criteria2Value.equals(other.criteria2Value))
            return false;
        if (criteria3Value == null) {
            if (other.criteria3Value != null)
                return false;
        } else if (!criteria3Value.equals(other.criteria3Value))
            return false;
        if (endRatingDate == null) {
            if (other.endRatingDate != null)
                return false;
        } else if (!endRatingDate.equals(other.endRatingDate))
            return false;
        if (endSubscriptionDate == null) {
            if (other.endSubscriptionDate != null)
                return false;
        } else if (!endSubscriptionDate.equals(other.endSubscriptionDate))
            return false;
        if (eventCode == null) {
            if (other.eventCode != null)
                return false;
        } else if (!eventCode.equals(other.eventCode))
            return false;
        if (maxSubscriptionAgeInMonth == null) {
            if (other.maxSubscriptionAgeInMonth != null)
                return false;
        } else if (!maxSubscriptionAgeInMonth.equals(other.maxSubscriptionAgeInMonth))
            return false;
        if (minSubscriptionAgeInMonth == null) {
            if (other.minSubscriptionAgeInMonth != null)
                return false;
        } else if (!minSubscriptionAgeInMonth.equals(other.minSubscriptionAgeInMonth))
            return false;
        if (startRatingDate == null) {
            if (other.startRatingDate != null)
                return false;
        } else if (!startRatingDate.equals(other.startRatingDate))
            return false;
        if (startSubscriptionDate == null) {
            if (other.startSubscriptionDate != null)
                return false;
        } else if (!startSubscriptionDate.equals(other.startSubscriptionDate))
            return false;
        if (seller == null) {
            if (other.seller != null) {
                return false;
            }
        } else if (other.seller == null || (seller.getId() != other.seller.getId())) {
            return false;
        }
        if (scriptInstance == null) {
            if (other.scriptInstance != null) {
                return false;
            }
        } else if (other.scriptInstance == null || (scriptInstance.getId() != other.scriptInstance.getId())) {
            return false;
        }
        if (priority != other.priority) {
            return false;
        }
        if (validityCalendar == null) {
            if (other.validityCalendar != null) {
                return false;
            }
        } else if (other.validityCalendar == null || (validityCalendar.getId() != other.getValidityCalendar().getId())) {
            return false;
        }

        return true;
    }

    @Override
    public int compareTo(PricePlanMatrix o) {
        return this.getPriority() - o.getPriority();
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

    public String getWoDescriptionEL() {
        return woDescriptionEL;
    }

    public void setWoDescriptionEL(String woDescriptionEL) {
        this.woDescriptionEL = woDescriptionEL;
    }

    public String getRatingEL() {
        return ratingEL;
    }

    public void setRatingEL(String ratingEL) {
        this.ratingEL = ratingEL;
    }
}
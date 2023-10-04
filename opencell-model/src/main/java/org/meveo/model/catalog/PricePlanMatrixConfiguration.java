package org.meveo.model.catalog;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ModuleItem;
import org.meveo.model.ObservableEntity;

@Entity
@ModuleItem
@ObservableEntity
@Table(name = "cat_price_plan_matrix_configuration")
@NamedQueries({
		@NamedQuery(name = "PricePlanMatrixConfiguration.getFirstPricePlanMatrixConfiguration", query = "from PricePlanMatrixConfiguration order by id asc ", 
				hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
public class PricePlanMatrixConfiguration extends AuditableEntity {

	

    public PricePlanMatrixConfiguration() {
		super();
	}

	@Type(type = "numeric_boolean")
    @Column(name = "use_event_code")
    private boolean useEventCode;

    @Type(type = "numeric_boolean")
    @Column(name = "use_seller_id")
    private boolean useSellerId;

    @Type(type = "numeric_boolean")
    @Column(name = "use_offer_id")
    private boolean useOfferId;

    @Type(type = "numeric_boolean")
    @Column(name = "use_trading_country_id")
    private boolean useTradingCountryId;

    @Type(type = "numeric_boolean")
    @Column(name = "use_trading_currency_id")
    private boolean useTradingCurrencyId;

    @Type(type = "numeric_boolean")
    @Column(name = "use_criteria_1")
    private boolean useCriteria1;

    @Type(type = "numeric_boolean")
    @Column(name = "use_criteria_2")
    private boolean useCriteria2;

    @Type(type = "numeric_boolean")
    @Column(name = "use_criteria_3")
    private boolean useCriteria3;

    @Type(type = "numeric_boolean")
    @Column(name = "use_start_subscription_date")
    private boolean useStartSubscriptionDate;

    @Type(type = "numeric_boolean")
    @Column(name = "use_end_subscription_date")
    private boolean useEndSubscriptionDate;

    @Type(type = "numeric_boolean")
    @Column(name = "use_min_subscr_age")
    private boolean useMinSubscrAge;

    @Type(type = "numeric_boolean")
    @Column(name = "use_max_subscr_age")
    private boolean useMaxSubscrAge;

    @Type(type = "numeric_boolean")
    @Column(name = "use_start_rating_date")
    private boolean useStartRatingDate;

    @Type(type = "numeric_boolean")
    @Column(name = "use_end_rating_date")
    private boolean useEndRatingDate;

    @Type(type = "numeric_boolean")
    @Column(name = "use_validity_from")
    private boolean useValidityFrom;

    @Type(type = "numeric_boolean")
    @Column(name = "use_validity_date")
    private boolean useValidityDate;

    @Type(type = "numeric_boolean")
    @Column(name = "use_max_quantity")
    private boolean useMaxQuantity;

    @Type(type = "numeric_boolean")
    @Column(name = "use_min_quantity")
    private boolean useMinQuantity;
    
    

	public PricePlanMatrixConfiguration(boolean useEventCode, boolean useSellerId, boolean useOfferId,
			boolean useTradingCountryId, boolean useTradingCurrencyId, boolean useCriteria1, boolean useCriteria2,
			boolean useCriteria3, boolean useStartSubscriptionDate, boolean useEndSubscriptionDate,
			boolean useMinSubscrAge, boolean useMaxSubscrAge, boolean useStartRatingDate, boolean useEndRatingDate,
			boolean useValidityFrom, boolean useValidityDate, boolean useMaxQuantity, boolean useMinQuantity) {
		super();
		this.useEventCode = useEventCode;
		this.useSellerId = useSellerId;
		this.useOfferId = useOfferId;
		this.useTradingCountryId = useTradingCountryId;
		this.useTradingCurrencyId = useTradingCurrencyId;
		this.useCriteria1 = useCriteria1;
		this.useCriteria2 = useCriteria2;
		this.useCriteria3 = useCriteria3;
		this.useStartSubscriptionDate = useStartSubscriptionDate;
		this.useEndSubscriptionDate = useEndSubscriptionDate;
		this.useMinSubscrAge = useMinSubscrAge;
		this.useMaxSubscrAge = useMaxSubscrAge;
		this.useStartRatingDate = useStartRatingDate;
		this.useEndRatingDate = useEndRatingDate;
		this.useValidityFrom = useValidityFrom;
		this.useValidityDate = useValidityDate;
		this.useMaxQuantity = useMaxQuantity;
		this.useMinQuantity = useMinQuantity;
	}

	public boolean isUseEventCode() {
		return useEventCode;
	}

	public void setUseEventCode(boolean useEventCode) {
		this.useEventCode = useEventCode;
	}

	public boolean isUseSellerId() {
		return useSellerId;
	}

	public void setUseSellerId(boolean useSellerId) {
		this.useSellerId = useSellerId;
	}

	public boolean isUseOfferId() {
		return useOfferId;
	}

	public void setUseOfferId(boolean useOfferId) {
		this.useOfferId = useOfferId;
	}

	public boolean isUseTradingCountryId() {
		return useTradingCountryId;
	}

	public void setUseTradingCountryId(boolean useTradingCountryId) {
		this.useTradingCountryId = useTradingCountryId;
	}

	public boolean isUseTradingCurrencyId() {
		return useTradingCurrencyId;
	}

	public void setUseTradingCurrencyId(boolean useTradingCurrencyId) {
		this.useTradingCurrencyId = useTradingCurrencyId;
	}

	public boolean isUseCriteria1() {
		return useCriteria1;
	}

	public void setUseCriteria1(boolean useCriteria1) {
		this.useCriteria1 = useCriteria1;
	}

	public boolean isUseCriteria2() {
		return useCriteria2;
	}

	public void setUseCriteria2(boolean useCriteria2) {
		this.useCriteria2 = useCriteria2;
	}

	public boolean isUseCriteria3() {
		return useCriteria3;
	}

	public void setUseCriteria3(boolean useCriteria3) {
		this.useCriteria3 = useCriteria3;
	}

	public boolean isUseStartSubscriptionDate() {
		return useStartSubscriptionDate;
	}

	public void setUseStartSubscriptionDate(boolean useStartSubscriptionDate) {
		this.useStartSubscriptionDate = useStartSubscriptionDate;
	}

	public boolean isUseEndSubscriptionDate() {
		return useEndSubscriptionDate;
	}

	public void setUseEndSubscriptionDate(boolean useEndSubscriptionDate) {
		this.useEndSubscriptionDate = useEndSubscriptionDate;
	}

	public boolean isUseMinSubscrAge() {
		return useMinSubscrAge;
	}

	public void setUseMinSubscrAge(boolean useMinSubscrAge) {
		this.useMinSubscrAge = useMinSubscrAge;
	}

	public boolean isUseMaxSubscrAge() {
		return useMaxSubscrAge;
	}

	public void setUseMaxSubscrAge(boolean useMaxSubscrAge) {
		this.useMaxSubscrAge = useMaxSubscrAge;
	}

	public boolean isUseStartRatingDate() {
		return useStartRatingDate;
	}

	public void setUseStartRatingDate(boolean useStartRatingDate) {
		this.useStartRatingDate = useStartRatingDate;
	}

	public boolean isUseEndRatingDate() {
		return useEndRatingDate;
	}

	public void setUseEndRatingDate(boolean useEndRatingDate) {
		this.useEndRatingDate = useEndRatingDate;
	}

	public boolean isUseValidityFrom() {
		return useValidityFrom;
	}

	public void setUseValidityFrom(boolean useValidityFrom) {
		this.useValidityFrom = useValidityFrom;
	}

	public boolean isUseValidityDate() {
		return useValidityDate;
	}

	public void setUseValidityDate(boolean useValidityDate) {
		this.useValidityDate = useValidityDate;
	}

	public boolean isUseMaxQuantity() {
		return useMaxQuantity;
	}

	public void setUseMaxQuantity(boolean useMaxQuantity) {
		this.useMaxQuantity = useMaxQuantity;
	}

	public boolean isUseMinQuantity() {
		return useMinQuantity;
	}

	public void setUseMinQuantity(boolean useMinQuantity) {
		this.useMinQuantity = useMinQuantity;
	}
   
}
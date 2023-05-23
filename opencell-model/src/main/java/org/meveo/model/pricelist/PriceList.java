package org.meveo.model.pricelist;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.Country;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.payments.CreditCategory;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.shared.Title;

@Table(name = "cat_price_list")
@Entity
@CustomFieldEntity(cftCodePrefix = "PriceList")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_price_list_seq"), })
@NamedQueries({
		@NamedQuery(name = "PriceList.getExpiredOpenPriceList", query = "SELECT pl FROM PriceList pl WHERE pl.validUntil <= :untilDate AND pl.status in :openStatus")
})
public class PriceList extends BusinessCFEntity {

    private static final long serialVersionUID = 3512021797431043307L;

	@Column(name = "valid_from", nullable = true)
    private Date validFrom;

    @Column(name = "valid_until", nullable = true)
    private Date validUntil;

    @Column(name = "application_start_date", nullable = true)
    private Date applicationStartDate;

    @Column(name = "application_end_date", nullable = true)
    private Date applicationEndDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private PriceListStatusEnum status;

	@OneToMany(mappedBy = "priceList", fetch = FetchType.LAZY)
	private Set<PriceListLine> lines;

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_price_list_customer_brand", joinColumns = @JoinColumn(name = "price_list_id"), inverseJoinColumns = @JoinColumn(name = "customer_brand_id"))
	private Set<CustomerBrand> brands = new HashSet<>();
	
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_price_list_customer_category", joinColumns = @JoinColumn(name = "price_list_id"), inverseJoinColumns = @JoinColumn(name = "customer_category_id"))
	@Column(name = "customer_category")
	private Set<CustomerCategory> customerCategories = new HashSet<>();
	
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_price_list_credit_category", joinColumns = @JoinColumn(name = "price_list_id"), inverseJoinColumns = @JoinColumn(name = "credit_category_id"))
	@Column(name = "credit_category")
	private Set<CreditCategory> creditCategories = new HashSet<>();
	
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_price_list_country", joinColumns = @JoinColumn(name = "price_list_id"), inverseJoinColumns = @JoinColumn(name = "country_id"))
	@Column(name = "country")
	private Set<Country> countries = new HashSet<>();
	
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_price_list_currency", joinColumns = @JoinColumn(name = "price_list_id"), inverseJoinColumns = @JoinColumn(name = "currency_id"))
	@Column(name = "currency")
	private Set<Currency> currencies = new HashSet<>();
	
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_price_list_legal_entity", joinColumns = @JoinColumn(name = "price_list_id"), inverseJoinColumns = @JoinColumn(name = "legal_entity_id"))
	@Column(name = "legal_entity")
	private Set<Title> legalEntities = new HashSet<>();
	
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ElementCollection(targetClass = PaymentMethodEnum.class)
    @CollectionTable(name = "cat_price_list_payment_method", joinColumns = @JoinColumn(name = "price_list_id"))
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
	private Set<PaymentMethodEnum> paymentMethods = new HashSet<>();
	
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "cat_price_list_seller", joinColumns = @JoinColumn(name = "price_list_id"), inverseJoinColumns = @JoinColumn(name = "seller_id"))
	@Column(name = "seller")
	private Set<Seller> sellers = new HashSet<Seller>();

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}

	public Date getApplicationStartDate() {
		return applicationStartDate;
	}

	public void setApplicationStartDate(Date applicationStartDate) {
		this.applicationStartDate = applicationStartDate;
	}

	public Date getApplicationEndDate() {
		return applicationEndDate;
	}

	public void setApplicationEndDate(Date applicationEndDate) {
		this.applicationEndDate = applicationEndDate;
	}

	public PriceListStatusEnum getStatus() {
		return status;
	}

	public void setStatus(PriceListStatusEnum status) {
		this.status = status;
	}

	public Set<PriceListLine> getLines() {
		return lines;
	}

	public void setLines(Set<PriceListLine> lines) {
		this.lines = lines;
	}

	public Set<CustomerBrand> getBrands() {
		return brands;
	}

	public void setBrands(Set<CustomerBrand> brands) {
		this.brands = brands;
	}

	public Set<CustomerCategory> getCustomerCategories() {
		return customerCategories;
	}

	public void setCustomerCategories(Set<CustomerCategory> customerCategories) {
		this.customerCategories = customerCategories;
	}

	public Set<CreditCategory> getCreditCategories() {
		return creditCategories;
	}

	public void setCreditCategories(Set<CreditCategory> creditCategories) {
		this.creditCategories = creditCategories;
	}

	public Set<Country> getCountries() {
		return countries;
	}

	public void setCountries(Set<Country> countries) {
		this.countries = countries;
	}

	public Set<Currency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Set<Currency> currencies) {
		this.currencies = currencies;
	}

	public Set<Title> getLegalEntities() {
		return legalEntities;
	}

	public void setLegalEntities(Set<Title> legalEntities) {
		this.legalEntities = legalEntities;
	}

	public Set<PaymentMethodEnum> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(Set<PaymentMethodEnum> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public Set<Seller> getSellers() {
		return sellers;
	}

	public void setSellers(Set<Seller> sellers) {
		this.sellers = sellers;
	}
}
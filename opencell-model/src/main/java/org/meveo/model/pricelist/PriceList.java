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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.securityDeposit.SecurityTemplateStatusEnum;

@Table(name = "price_list")
@Entity
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "price_list_seq"), })
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
    private SecurityTemplateStatusEnum status;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "price_list_brand", joinColumns = @JoinColumn(name = "price_list_id", referencedColumnName = "id"))
	@Column(name = "brand")
	private Set<String> brands = new HashSet<>();
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "price_list_client_category", joinColumns = @JoinColumn(name = "price_list_id", referencedColumnName = "id"))
	@Column(name = "client_category")
	private Set<String> clientCategories = new HashSet<>();
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "price_list_credit_category", joinColumns = @JoinColumn(name = "price_list_id", referencedColumnName = "id"))
	@Column(name = "credit_category")
	private Set<String> creditCategories = new HashSet<>();
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "price_list_country", joinColumns = @JoinColumn(name = "price_list_id", referencedColumnName = "id"))
	@Column(name = "country")
	private Set<String> countries = new HashSet<>();
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "price_list_currency", joinColumns = @JoinColumn(name = "price_list_id", referencedColumnName = "id"))
	@Column(name = "currency")
	private Set<String> currencies = new HashSet<>();
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "price_list_legal_entity", joinColumns = @JoinColumn(name = "price_list_id", referencedColumnName = "id"))
	@Column(name = "legal_entity")
	private Set<String> legalEntities = new HashSet<>();
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "price_list_payment_method", joinColumns = @JoinColumn(name = "price_list_id", referencedColumnName = "id"))
	@Column(name = "payment_method")
	private Set<String> paymentMethods = new HashSet<>();
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "price_list_seller", joinColumns = @JoinColumn(name = "price_list_id", referencedColumnName = "id"))
	@Column(name = "seller")
	private Set<String> sellers = new HashSet<>();

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

	public SecurityTemplateStatusEnum getStatus() {
		return status;
	}

	public void setStatus(SecurityTemplateStatusEnum status) {
		this.status = status;
	}

	public Set<String> getBrands() {
		return brands;
	}

	public void setBrands(Set<String> brands) {
		this.brands = brands;
	}

	public Set<String> getClientCategories() {
		return clientCategories;
	}

	public void setClientCategories(Set<String> clientCategories) {
		this.clientCategories = clientCategories;
	}

	public Set<String> getCreditCategories() {
		return creditCategories;
	}

	public void setCreditCategories(Set<String> creditCategories) {
		this.creditCategories = creditCategories;
	}

	public Set<String> getCountries() {
		return countries;
	}

	public void setCountries(Set<String> countries) {
		this.countries = countries;
	}

	public Set<String> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Set<String> currencies) {
		this.currencies = currencies;
	}

	public Set<String> getLegalEntities() {
		return legalEntities;
	}

	public void setLegalEntities(Set<String> legalEntities) {
		this.legalEntities = legalEntities;
	}

	public Set<String> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(Set<String> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	public Set<String> getSellers() {
		return sellers;
	}

	public void setSellers(Set<String> sellers) {
		this.sellers = sellers;
	}
}

package org.meveo.model.pricelist;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessCFEntity;

@Table(name = "cat_price_list")
@Entity
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_price_list_seq"), })
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
}

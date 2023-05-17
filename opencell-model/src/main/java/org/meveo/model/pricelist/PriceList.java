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

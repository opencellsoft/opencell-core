package org.meveo.model.cpq.offer;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.cpq.tags.Tag;

/**
 * 
 * @author Tarik FAKHOURI.
 * @author Mbarek-Ay.
 * @version 10.0
 *
 */
@Entity
@Table(name = "cpq_commercial_offer", uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_commercial_offer_seq"), })
public class CommercialOffer extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6924714603938903496L;
	

	/**
	 * seller associated to the entity
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;
	
	/**
	 * current status 
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "status", nullable = false)
	@NotNull
	private VersionStatusEnum status;
	
	/**
	 * status date : modified automatically when the status change
	 */
	@Column(name = "status_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date statusDate;
	
	/**
	 * date begin that the offer can be publish 
	 */
	@Column(name = "begin_date")
	@Temporal(TemporalType.DATE)
	private Date beginDate;
	
	/**
	 * date end of the offer if the status is publish
	 */
	@Column(name = "end_date")
	@Temporal(TemporalType.DATE)
	private Date endDate;

	/**
	 *  list of tag associated to offer commercial
	 */
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinTable(
			name = "cpq_commercial_offer_tags",
			joinColumns = @JoinColumn(name = "commercial_offer_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id")				
		)
	private Set<Tag> tagsList = new HashSet<>();

	/**
	 * @return the seller
	 */
	public Seller getSeller() {
		return seller;
	}

	/**
	 * @param seller the seller to set
	 */
	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	/**
	 * @return the status
	 */
	public VersionStatusEnum getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(VersionStatusEnum status) {
		this.status = status;
	}

	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the beginDate
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate the beginDate to set
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the tagsList
	 */
	public Set<Tag> getTagsList() {
		return tagsList;
	}

	/**
	 * @param tagsList the tagsList to set
	 */
	public void setTagsList(Set<Tag> tagsList) {
		this.tagsList = tagsList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(beginDate, endDate, seller, status, statusDate, tagsList);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommercialOffer other = (CommercialOffer) obj;
		return Objects.equals(beginDate, other.beginDate) && Objects.equals(endDate, other.endDate)
				&& Objects.equals(seller, other.seller) && status == other.status
				&& Objects.equals(statusDate, other.statusDate) && Objects.equals(tagsList, other.tagsList);
	}
	
	

}

package org.meveo.model.catalog;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

/**
 * @author Edward P. Legaspi
 */
@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_PRODUCT_OFFERING", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_PRODUCT_OFFERING_SEQ")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class ProductOffering extends BusinessEntity {

	private static final long serialVersionUID = 6877386866687396135L;

	@Column(name = "NAME", length = 100)
	private String name;

	@OneToMany(mappedBy = "productOffering")
	private List<OfferTemplateCategory> offerTemplateCategories;

	@Column(name = "MODEL_CODE", length = 60)
	private String modelCode;

	@Column(name = "VALID_FROM")
	@Temporal(TemporalType.TIMESTAMP)
	private Date validFrom;

	@Column(name = "VALID_TO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;

	@Column(name = "image")
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private Blob image;

	@OneToMany(mappedBy = "productOffering")
	private List<DigitalResource> attachments;

	@Enumerated(EnumType.STRING)
	@Column(name = "LIFE_CYCLE_STATUS")
	private LifeCycleStatusEnum lifeCycleStatus;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}
	
	public byte[] getImageAsByteArr() {
		if (image != null) {
			int blobLength;
			try {
				blobLength = (int) image.length();
				byte[] blobAsBytes = image.getBytes(1, blobLength);

				return blobAsBytes;
			} catch (SQLException e) {
				return null;
			}
		}

		return null;
	}

	public LifeCycleStatusEnum getLifeCycleStatus() {
		return lifeCycleStatus;
	}

	public void setLifeCycleStatus(LifeCycleStatusEnum lifeCycleStatus) {
		this.lifeCycleStatus = lifeCycleStatus;
	}

	public List<DigitalResource> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<DigitalResource> attachments) {
		this.attachments = attachments;
	}

	public List<OfferTemplateCategory> getOfferTemplateCategories() {
		return offerTemplateCategories;
	}

	public void setOfferTemplateCategories(List<OfferTemplateCategory> offerTemplateCategories) {
		this.offerTemplateCategories = offerTemplateCategories;
	}

}

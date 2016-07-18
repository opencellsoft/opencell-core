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

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "OFFER")
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_OFFER_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_TEMPLATE_SEQ")
@NamedQueries({ @NamedQuery(name = "OfferTemplate.countActive", query = "SELECT COUNT(*) FROM OfferTemplate WHERE disabled=false"),
		@NamedQuery(name = "OfferTemplate.countDisabled", query = "SELECT COUNT(*) FROM OfferTemplate WHERE disabled=true"),
		@NamedQuery(name = "OfferTemplate.countExpiring", query = "SELECT COUNT(*) FROM OfferTemplate WHERE :nowMinus1Day<validTo and validTo > NOW()") })
public class OfferTemplate extends ProductOffering {
	private static final long serialVersionUID = 1L;

	@Column(name = "NAME", length = 100)
	@Size(max = 100)
	private String name;

	@Column(name = "image")
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private Blob image;

	@ManyToOne
	@JoinColumn(name = "CAT_OFFER_TEMPLATE_CAT_ID")
	private OfferTemplateCategory offerTemplateCategory;

	@ManyToOne
	@JoinColumn(name = "BUSINESS_OFFER_MODEL_ID")
	private BusinessOfferModel businessOfferModel;

	@OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<OfferServiceTemplate>();

	@Column(name = "VALID_FROM")
	@Temporal(TemporalType.TIMESTAMP)
	private Date validFrom;

	@Column(name = "VALID_TO")
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;

	@Column(name = "IMAGE_CONTENT_TYPE", length = 50)
	@Size(max = 50)
	private String imageContentType;

	@OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<OfferProductTemplate> offerProductTemplates = new ArrayList<OfferProductTemplate>();

	public List<OfferServiceTemplate> getOfferServiceTemplates() {
		return offerServiceTemplates;
	}

	public void setOfferServiceTemplates(List<OfferServiceTemplate> offerServiceTemplates) {
		this.offerServiceTemplates = offerServiceTemplates;
	}

	@Override
	public ICustomFieldEntity[] getParentCFEntities() {
		return null;
	}

	public void addOfferServiceTemplate(OfferServiceTemplate serviceTemplate) {
		if (getOfferServiceTemplates() == null) {
			offerServiceTemplates = new ArrayList<OfferServiceTemplate>();
		}
		offerServiceTemplates.add(serviceTemplate);
	}

	public OfferTemplateCategory getOfferTemplateCategory() {
		return offerTemplateCategory;
	}

	public void setOfferTemplateCategory(OfferTemplateCategory offerTemplateCategory) {
		this.offerTemplateCategory = offerTemplateCategory;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	public BusinessOfferModel getBusinessOfferModel() {
		return businessOfferModel;
	}

	public void setBusinessOfferModel(BusinessOfferModel businessOfferModel) {
		this.businessOfferModel = businessOfferModel;
	}

	/**
	 * Check if offer contains a given service template
	 * 
	 * @param serviceTemplate
	 *            Service template to match
	 * @return True if offer contains a given service template
	 */
	public boolean containsServiceTemplate(ServiceTemplate serviceTemplate) {

		for (OfferServiceTemplate offerServiceTemplate : offerServiceTemplates) {
			if (offerServiceTemplate.getServiceTemplate().equals(serviceTemplate)) {
				return true;
			}
		}
		return false;
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

	public String getImageContentType() {
		return imageContentType;
	}

	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
	}

	public List<OfferProductTemplate> getOfferProductTemplates() {
		return offerProductTemplates;
	}

	public void setOfferProductTemplates(List<OfferProductTemplate> offerProductTemplates) {
		this.offerProductTemplates = offerProductTemplates;
	}

	public String getNameOrCode() {
		if (!StringUtils.isBlank(name)) {
			return name;
		} else {
			return code;
		}
	}

}
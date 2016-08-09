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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "OFFER")
// @ExportIdentifier({ "code", "provider" })
// @Table(name = "CAT_OFFER_TEMPLATE", uniqueConstraints =
// @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@DiscriminatorValue("OFFER")
// @SequenceGenerator(name = "ID_GENERATOR", sequenceName =
// "CAT_OFFER_TEMPLATE_SEQ")
@NamedQueries({ @NamedQuery(name = "OfferTemplate.countActive", query = "SELECT COUNT(*) FROM OfferTemplate WHERE disabled=false"),
		@NamedQuery(name = "OfferTemplate.countDisabled", query = "SELECT COUNT(*) FROM OfferTemplate WHERE disabled=true"),
		@NamedQuery(name = "OfferTemplate.countExpiring", query = "SELECT COUNT(*) FROM OfferTemplate WHERE :nowMinus1Day<validTo and validTo > NOW()") })
public class OfferTemplate extends ProductOffering {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "CAT_OFFER_TEMPLATE_CAT_ID")
	private OfferTemplateCategory offerTemplateCategory;

	@ManyToOne
	@JoinColumn(name = "BUSINESS_OFFER_MODEL_ID")
	private BusinessOfferModel businessOfferModel;

	@OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<OfferServiceTemplate> offerServiceTemplates = new ArrayList<OfferServiceTemplate>();

	@OneToMany(mappedBy = "offerTemplate", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private List<OfferProductTemplate> offerProductTemplates = new ArrayList<OfferProductTemplate>();

	@Size(max = 2000)
	@Column(name = "LONG_DESCRIPTION", columnDefinition = "TEXT")
	private String longDescription;

	@Transient
	public String prefix;

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

	public List<OfferProductTemplate> getOfferProductTemplates() {
		return offerProductTemplates;
	}

	public void setOfferProductTemplates(List<OfferProductTemplate> offerProductTemplates) {
		this.offerProductTemplates = offerProductTemplates;
	}

	public void addOfferProductTemplate(OfferProductTemplate offerProductTemplate) {
		if (getOfferProductTemplates() == null) {
			offerProductTemplates = new ArrayList<OfferProductTemplate>();
		}
		offerProductTemplates.add(offerProductTemplate);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

}
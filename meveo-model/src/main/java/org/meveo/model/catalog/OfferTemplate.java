/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.catalog;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.scripts.ScriptInstance;

@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "OFFER")
@ExportIdentifier({ "code", "provider" })
@Table(name = "CAT_OFFER_TEMPLATE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CAT_OFFER_TEMPLATE_SEQ")
public class OfferTemplate extends BusinessCFEntity {
	private static final long serialVersionUID = 1L;

	@Column(name = "NAME", nullable = false, length = 100)
	@Size(max = 100)
	private String name;

	@Version
	@Column(name = "ENTITY_VERSION")
	private Integer entityVersion;

	@Column(name = "image")
	@Lob
	@Basic(fetch = FetchType.LAZY)
	private Blob image;

	@ManyToOne
	@JoinColumn(name = "CAT_OFFER_TEMPLATE_CAT_ID")
	private OfferTemplateCategory offerTemplateCategory;

	@Column(name = "BOM_CODE", length = 60)
	private String bomCode;

	@NotNull
	@Enumerated(EnumType.STRING)
	private OfferTemplateStatusEnum status = OfferTemplateStatusEnum.PUBLISHED;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_OFFER_SERV_TEMPLATES", joinColumns = @JoinColumn(name = "OFFER_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "SERVICE_TEMPLATE_ID"))
	private List<ServiceTemplate> serviceTemplates;

	@ManyToOne
	@JoinColumn(name = "TERMINATION_SCRIPT_INSTANCE_ID")
	private ScriptInstance terminationScript;

	@ManyToOne
	@JoinColumn(name = "SUBSCRIPTION_SCRIPT_INSTANCE_ID")
	private ScriptInstance subscriptionScript;

	public List<ServiceTemplate> getServiceTemplates() {
		return serviceTemplates;
	}

	public void setServiceTemplates(List<ServiceTemplate> serviceTemplates) {
		this.serviceTemplates = serviceTemplates;
	}

	@Override
	public ICustomFieldEntity getParentCFEntity() {
		return null;
	}

	public String getBomCode() {
		return bomCode;
	}

	public void setBomCode(String bomCode) {
		this.bomCode = bomCode;
	}

	public ScriptInstance getSubscriptionScript() {
		return subscriptionScript;
	}

	public void setSubscriptionScript(ScriptInstance subscriptionScript) {
		this.subscriptionScript = subscriptionScript;
	}

	public ScriptInstance getTerminationScript() {
		return terminationScript;
	}

	public void setTerminationScript(ScriptInstance terminationScript) {
		this.terminationScript = terminationScript;
	}

	public void addServiceTemplate(ServiceTemplate serviceTemplate) {
		if (getServiceTemplates() == null) {
			serviceTemplates = new ArrayList<ServiceTemplate>();
		}

		serviceTemplates.add(serviceTemplate);
	}

	public OfferTemplateStatusEnum getStatus() {
		return status;
	}

	public void setStatus(OfferTemplateStatusEnum status) {
		this.status = status;
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

	public Integer getEntityVersion() {
		return entityVersion;
	}

	public void setEntityVersion(Integer entityVersion) {
		this.entityVersion = entityVersion;
	}
}
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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "CAT_OFFER_SERV_TEMPLATES", joinColumns = @JoinColumn(name = "OFFER_TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "SERVICE_TEMPLATE_ID"))
	private List<ServiceTemplate> serviceTemplates;

	@Column(name = "BOM_CODE", length = 60)
	private String bomCode;

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
}
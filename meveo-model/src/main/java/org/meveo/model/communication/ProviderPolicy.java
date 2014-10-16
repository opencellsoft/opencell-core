/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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
package org.meveo.model.communication;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "COM_PROVIDER_POLICY")
@AttributeOverride(name = "provider", column = @Column(name = "PROVIDER_ID", nullable = false, unique = true))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_PROV_POL_SEQ")
public class ProviderPolicy extends BaseEntity {

	private static final long serialVersionUID = -1L;

	@Embedded
	private CommunicationPolicy policy;

	public CommunicationPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(CommunicationPolicy policy) {
		this.policy = policy;
	}

}

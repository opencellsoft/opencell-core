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
package org.meveo.model.communication.contact;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.communication.MediaEnum;

@Entity
@ExportIdentifier({ "code"})
@Table(name = "com_contact_coords", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@DiscriminatorColumn(name = "media")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "com_contact_coords_seq"), })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ContactCoordinates extends BusinessEntity {
	private static final long serialVersionUID = 5212396734631312511L;

	@Enumerated(EnumType.STRING)
	@Column(name = "media", insertable = false, updatable = false)
	private MediaEnum media;

	public MediaEnum getMedia() {
		return media;
	}

	public void setMedia(MediaEnum media) {
		this.media = media;
	}

}

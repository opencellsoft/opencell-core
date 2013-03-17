/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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

import org.meveo.model.BusinessEntity;
import org.meveo.model.communication.MediaEnum;

@Entity
@Table(name = "COM_CONTACT_COORDS")
@DiscriminatorColumn(name = "MEDIA")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_CONTACT_COORDS_SEQ")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ContactCoordinates extends BusinessEntity {
	private static final long serialVersionUID = 5212396734631312511L;

	@Enumerated(EnumType.STRING)
	@Column(name="MEDIA",insertable=false,updatable=false)
	MediaEnum media;
	

	public MediaEnum getMedia() {
		return media;
	}

	public void setMedia(MediaEnum media) {
		this.media = media;
	}
	
}

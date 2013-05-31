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
package org.meveo.model.communication;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "COM_MESSAGE_TEMPLATE")
@DiscriminatorColumn(name = "MEDIA")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_MSG_TMPL_SEQ")
public abstract class MessageTemplate extends BusinessEntity {

	private static final long serialVersionUID = 5835960109145222442L;

	@Enumerated(EnumType.STRING)
	@Column(name = "MEDIA", insertable = false, updatable = false)
	MediaEnum media;

	@Column(name = "TAG_START")
	private String tagStartDelimiter = "#{";

	@Column(name = "TAG_END")
	private String tagEndDelimiter = "}";

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE")
	MessageTemplateTypeEnum type;

	public MediaEnum getMedia() {
		return media;
	}

	public void setMedia(MediaEnum media) {
		this.media = media;
	}

	public String getTagStartDelimiter() {
		return tagStartDelimiter;
	}

	public void setTagStartDelimiter(String tagStartDelimiter) {
		this.tagStartDelimiter = tagStartDelimiter;
	}

	public String getTagEndDelimiter() {
		return tagEndDelimiter;
	}

	public void setTagEndDelimiter(String tagEndDelimiter) {
		this.tagEndDelimiter = tagEndDelimiter;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public MessageTemplateTypeEnum getType() {
		return type;
	}

	public void setType(MessageTemplateTypeEnum type) {
		this.type = type;
	}

}

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
package org.meveo.model.communication;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

@Entity
@ObservableEntity
@ExportIdentifier({ "code"})
@Table(name = "com_message_template", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@DiscriminatorColumn(name = "media")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "com_msg_tmpl_seq"), })
public abstract class MessageTemplate extends BusinessEntity {

	private static final long serialVersionUID = 5835960109145222442L;

	@Enumerated(EnumType.STRING)
	@Column(name = "media", insertable = false, updatable = false)
	private MediaEnum media;

	@Column(name = "tag_start", length = 255)
    @Size(max = 255)
	private String tagStartDelimiter = "#{";

	@Column(name = "tag_end", length = 255)
    @Size(max = 255)
	private String tagEndDelimiter = "}";

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private MessageTemplateTypeEnum type;

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

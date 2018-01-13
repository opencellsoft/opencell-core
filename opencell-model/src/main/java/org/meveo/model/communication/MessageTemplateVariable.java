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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code"})
@Table(name = "com_msg_tmpl_variable", uniqueConstraints = @UniqueConstraint(columnNames = { "code"}))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "com_msg_tmpl_var_seq"), })
public class MessageTemplateVariable extends BusinessEntity {

	private static final long serialVersionUID = -8541728044647573746L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_id")
	private MessageTemplate messageTemplate;

	@Type(type="numeric_boolean")
    @Column(name = "mandatory")
	private boolean mandatory;

	public MessageTemplate getMessageTemplate() {
		return messageTemplate;
	}

	public void setMessageTemplate(MessageTemplate messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

}

/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.communication;

import java.util.Date;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

@Entity
@ObservableEntity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "com_message_template", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@DiscriminatorColumn(name = "media")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "com_msg_tmpl_seq"), })
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

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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "campaign" })
@Table(name = "com_campaign", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "com_campaign_seq") })
public class Campaign extends BusinessEntity {

    private static final long serialVersionUID = -5865150907978275819L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "schedule_date")
    private Date scheduleDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "thread_id")
    private Integer processingThreadId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Enumerated(value = EnumType.ORDINAL)
    @Column(name = "priority")
    private PriorityEnum priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "media")
    private MediaEnum media;

    @Column(name = "sub_media", length = 255)
    @Size(max = 255)
    private String subMedia;

    @Type(type = "numeric_boolean")
    @Column(name = "use_any_media")
    private Boolean useAnyMedia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CampaignStatusEnum status;

    @OneToMany(mappedBy = "campaign")
    private List<Message> messages;

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getProcessingThreadId() {
        return processingThreadId;
    }

    public void setProcessingThreadId(Integer processingThreadId) {
        this.processingThreadId = processingThreadId;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public PriorityEnum getPriority() {
        return priority;
    }

    public void setPriority(PriorityEnum priority) {
        this.priority = priority;
    }

    public MediaEnum getMedia() {
        return media;
    }

    public void setMedia(MediaEnum media) {
        this.media = media;
    }

    public String getSubMedia() {
        return subMedia;
    }

    public void setSubMedia(String subMedia) {
        this.subMedia = subMedia;
    }

    public Boolean isUseAnyMedia() {
        return useAnyMedia;
    }

    public void setUseAnyMedia(Boolean useAnyMedia) {
        this.useAnyMedia = useAnyMedia;
    }

    public CampaignStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CampaignStatusEnum status) {
        this.status = status;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

}

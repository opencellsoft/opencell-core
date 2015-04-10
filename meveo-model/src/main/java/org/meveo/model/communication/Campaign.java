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
package org.meveo.model.communication;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "COM_CAMPAIGN", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_CAMPAIGN_SEQ")
public class Campaign extends BusinessEntity {

	private static final long serialVersionUID = -5865150907978275819L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SCHEDULE_DATE")
	private Date scheduleDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;

	@Column(name = "THREAD_ID")
	private Integer processingThreadId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "PRIORITY")
	private PriorityEnum priority;

	@Enumerated(EnumType.STRING)
	@Column(name = "MEDIA")
	MediaEnum media;

	@Column(name = "SUB_MEDIA")
	String subMedia;

	@Column(name = "USE_ANY_MEDIA")
	private Boolean useAnyMedia;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
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

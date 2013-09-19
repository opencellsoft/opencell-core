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

import org.meveo.model.BusinessEntity;

@Entity
@Table(name = "COM_CAMPAIGN")
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

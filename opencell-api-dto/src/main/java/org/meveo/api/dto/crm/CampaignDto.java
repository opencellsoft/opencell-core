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

package org.meveo.api.dto.crm;

import java.util.Date;
import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.communication.Campaign;
import org.meveo.model.communication.CampaignStatusEnum;
import org.meveo.model.communication.MediaEnum;
import org.meveo.model.communication.PriorityEnum;

public class CampaignDto extends BusinessEntityDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3629760837848965128L;
	
	private Date scheduleDate;

	private Date startDate;
	
	private Integer processingThreadId;

	private Date endDate;

	private PriorityEnum priority;

	private MediaEnum media;
	
	private String subMedia;

	private Boolean useAnyMedia;

	private CampaignStatusEnum status;

//	private List<Message> messages;
	
	public CampaignDto () {
		
	}
	
	public CampaignDto(Campaign campaign) {
		scheduleDate = campaign.getScheduleDate();
		startDate = campaign.getStartDate();
		processingThreadId = campaign.getProcessingThreadId();
		endDate = campaign.getEndDate();
		priority = campaign.getPriority();
		media = campaign.getMedia();
		subMedia = campaign.getSubMedia();
		useAnyMedia = campaign.isUseAnyMedia();
		status = campaign.getStatus();
	}

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

	public Boolean getUseAnyMedia() {
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
}

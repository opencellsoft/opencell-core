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
	
//	private List<ContactGroup> contactGroups;

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

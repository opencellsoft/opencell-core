package org.meveo.api.dto.module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.catalog.CounterTemplateDto;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.notification.NotificationDto;
import org.meveo.api.dto.notification.WebhookNotificationDto;
import org.meveo.model.admin.MeveoModule;
import org.meveo.model.admin.ModuleLicenseEnum;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 *
 */
@XmlRootElement(name = "Module")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModuleDto extends BaseDataModelDto {

	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(required=true)
	private String code;
	@XmlAttribute(required=true)
	private ModuleLicenseEnum license;
	@XmlAttribute
	private String description;
	private String logoPicture;
	private byte[] logoPictureFile;
	
	private List<CustomEntityTemplateDto> cetDtos;
	private List<CustomFieldTemplateDto> cftDtos;
	private List<FilterDto> filterDtos;
	private List<JobInstanceDto> jobDtos;
	private LinkedList<JobInstanceDto> jobNextDtos;
	private List<ScriptInstanceDto> scriptDtos;
	private List<NotificationDto> notificationDtos;
	private List<TimerEntityDto> timerEntityDtos;
	private List<EmailNotificationDto> emailNotifDtos;
	private List<JobTriggerDto> jobTriggerDtos;
	private List<WebhookNotificationDto> webhookNotifDtos;
	private List<CounterTemplateDto> counterDtos;
	
	public ModuleDto(){}
	public ModuleDto(MeveoModule meveoModule){
		this.code=meveoModule.getCode();
		this.description=meveoModule.getDescription();
		this.license=meveoModule.getLicense();
		this.logoPicture=meveoModule.getLogoPicture();
		this.cetDtos=new ArrayList<CustomEntityTemplateDto>();
		this.cftDtos=new ArrayList<CustomFieldTemplateDto>();
		this.filterDtos=new ArrayList<FilterDto>();
		this.jobNextDtos=new LinkedList<JobInstanceDto>();
		this.jobDtos=new ArrayList<JobInstanceDto>();
		this.timerEntityDtos=new ArrayList<TimerEntityDto>();
		this.scriptDtos=new ArrayList<ScriptInstanceDto>();
		this.notificationDtos=new ArrayList<NotificationDto>();
		this.emailNotifDtos=new ArrayList<EmailNotificationDto>();
		this.jobTriggerDtos=new ArrayList<JobTriggerDto>();
		this.webhookNotifDtos=new ArrayList<WebhookNotificationDto>();
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public ModuleLicenseEnum getLicense() {
		return license;
	}
	public void setLicense(ModuleLicenseEnum license) {
		this.license = license;
	}
	
	public List<CustomEntityTemplateDto> getCetDtos() {
		return cetDtos;
	}
	public void setCetDtos(List<CustomEntityTemplateDto> cetDtos) {
		this.cetDtos = cetDtos;
	}
	public List<CustomFieldTemplateDto> getCftDtos() {
		return cftDtos;
	}
	public void setCftDtos(List<CustomFieldTemplateDto> cftDtos) {
		this.cftDtos = cftDtos;
	}
	public List<FilterDto> getFilterDtos() {
		return filterDtos;
	}
	public void setFilterDtos(List<FilterDto> filterDtos) {
		this.filterDtos = filterDtos;
	}
	public List<JobInstanceDto> getJobDtos() {
		return jobDtos;
	}
	public void setJobDtos(List<JobInstanceDto> jobDtos) {
		this.jobDtos = jobDtos;
	}
	public List<ScriptInstanceDto> getScriptDtos() {
		return scriptDtos;
	}
	public void setScriptDtos(List<ScriptInstanceDto> scriptDtos) {
		this.scriptDtos = scriptDtos;
	}
	public List<NotificationDto> getNotificationDtos() {
		return notificationDtos;
	}
	public void setNotificationDtos(List<NotificationDto> notificationDtos) {
		this.notificationDtos = notificationDtos;
	}
	public List<TimerEntityDto> getTimerEntityDtos() {
		return timerEntityDtos;
	}
	public void setTimerEntityDtos(List<TimerEntityDto> timerEntityDtos) {
		this.timerEntityDtos = timerEntityDtos;
	}
	public List<EmailNotificationDto> getEmailNotifDtos() {
		return emailNotifDtos;
	}
	public void setEmailNotifDtos(List<EmailNotificationDto> emailNotifDtos) {
		this.emailNotifDtos = emailNotifDtos;
	}
	public List<JobTriggerDto> getJobTriggerDtos() {
		return jobTriggerDtos;
	}
	public void setJobTriggerDtos(List<JobTriggerDto> jobTriggerDtos) {
		this.jobTriggerDtos = jobTriggerDtos;
	}
	public List<WebhookNotificationDto> getWebhookNotifDtos() {
		return webhookNotifDtos;
	}
	public void setWebhookNotifDtos(List<WebhookNotificationDto> webhookNotifDtos) {
		this.webhookNotifDtos = webhookNotifDtos;
	}
	public List<CounterTemplateDto> getCounterDtos() {
		return counterDtos;
	}
	public void setCounterDtos(List<CounterTemplateDto> counterDtos) {
		this.counterDtos = counterDtos;
	}
	
	public LinkedList<JobInstanceDto> getJobNextDtos() {
		return jobNextDtos;
	}
	public void setJobNextDtos(LinkedList<JobInstanceDto> jobNextDtos) {
		this.jobNextDtos = jobNextDtos;
	}
	
	public String getLogoPicture() {
		return logoPicture;
	}
	public void setLogoPicture(String logoPicture) {
		this.logoPicture = logoPicture;
	}
	public byte[] getLogoPictureFile() {
		return logoPictureFile;
	}
	public void setLogoPictureFile(byte[] logoPictureFile) {
		this.logoPictureFile = logoPictureFile;
	}
	@Override
	public String toString() {
		return "ModuleDto [code=" + code + ", license=" + license
				+ ", description=" + description + ", logoPicture=" + logoPicture
				+ ", cetDtos=" + cetDtos
				+ ", cftDtos=" + cftDtos + ", filterDtos=" + filterDtos
				+ ", jobDtos=" + jobDtos + ", scriptDtos=" + scriptDtos
				+ ", notificationDtos=" + notificationDtos
				+ ", timerEntityDtos=" + timerEntityDtos + ", emailNotifDtos="
				+ emailNotifDtos + ", jobTriggerDtos=" + jobTriggerDtos
				+ ", webhookNotifDtos=" + webhookNotifDtos + ", counterDtos="
				+ counterDtos + "]";
	}
	@Override
	public Serializable getId() {
		return this.code.hashCode();
	}
	@Override
	public void setId(Long id) {
		
	}
	@Override
	public boolean isTransient() {
		return true;
	}
}

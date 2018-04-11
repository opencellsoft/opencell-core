package org.meveo.api.dto.notification;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.notification.JobTrigger;
import org.meveo.model.notification.Notification;

/**
 * @author Tyshan Shi
 *
 **/
@XmlRootElement(name = "JobTrigger")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobTriggerDto extends NotificationDto {

    private static final long serialVersionUID = 1L;

    private Map<String, String> jobParams = new HashMap<String, String>();

    @XmlElement(required = true)
    private String jobInstance;

    public Map<String, String> getJobParams() {
        return jobParams;
    }

    public void setJobParams(Map<String, String> jobParams) {
        this.jobParams = jobParams;
    }

    public String getJobInstance() {
        return jobInstance;
    }

    public void setJobInstance(String jobInstance) {
        this.jobInstance = jobInstance;
    }

    public JobTriggerDto() {

    }

    public JobTriggerDto(JobTrigger jobTrigger) {
        super((Notification) jobTrigger);
        if (jobTrigger.getJobParams() != null) {
            jobParams.putAll(jobTrigger.getJobParams());
        }
        if (jobTrigger.getJobInstance() != null) {
            jobInstance = jobTrigger.getJobInstance().getCode();
        }
    }

    @Override
    public String toString() {
        return "JobTriggerDto [jobParams=" + jobParams + ", jobInstance=" + jobInstance + "]";
    }

}

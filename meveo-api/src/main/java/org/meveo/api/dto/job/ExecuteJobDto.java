package org.meveo.api.dto.job;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ExecuteJob")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecuteJobDto extends BaseDto {

	private static final long serialVersionUID = -7091372162470026030L;

	@XmlElement(required = true)
	private String jobCategory;

	@XmlElement(required = true)
	private String jobName;

	public String getJobCategory() {
		return jobCategory;
	}

	public void setJobCategory(String jobCategory) {
		this.jobCategory = jobCategory;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@Override
	public String toString() {
		return "ExecuteJobDto [jobCategory=" + jobCategory + ", jobName=" + jobName + ", toString()="
				+ super.toString() + "]";
	}

}

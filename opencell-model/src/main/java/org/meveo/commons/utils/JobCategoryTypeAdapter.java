package org.meveo.commons.utils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.meveo.model.jobs.JobCategoryEnum;

public final class JobCategoryTypeAdapter extends XmlAdapter<String, JobCategoryEnum> {
	public JobCategoryTypeAdapter() {
	}

	@Override
	public JobCategoryEnum unmarshal(String v) throws Exception {
		return (JobCategoryEnum) EnumBuilder.build(v, JobCategoryEnum.class);
	}

	@Override
	public String marshal(JobCategoryEnum v) throws Exception {
		return v.getName();
	}

}
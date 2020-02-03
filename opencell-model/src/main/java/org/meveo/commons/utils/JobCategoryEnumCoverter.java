package org.meveo.commons.utils;

import javax.persistence.AttributeConverter;

import org.meveo.model.jobs.JobCategoryEnum;

public class JobCategoryEnumCoverter implements AttributeConverter<JobCategoryEnum, String> {

    @Override
    public String convertToDatabaseColumn(JobCategoryEnum attribute) {
        return attribute.getName();
    }

    @Override
    public JobCategoryEnum convertToEntityAttribute(String dbData) {
    	return (JobCategoryEnum) EnumBuilder.build(dbData, JobCategoryEnum.class);
    }
}

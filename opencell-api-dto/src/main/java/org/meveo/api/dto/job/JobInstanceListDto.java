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

package org.meveo.api.dto.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The Class JobInstanceListDto.
 * 
 * @author Adnane Boubia
 * @lastModifiedVersion 7.0.0
 */
@XmlRootElement(name = "JobInstanceListDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceListDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8442798673237108841L;

	/** The list size. */
    private int listSize;

    /** The job instance. */
    private List<JobInstanceDto> jobInstance;

    /**
     * Gets the job instances.
     *
     * @return the job instance
     */
    public List<JobInstanceDto> getJobInstances() {
        if (jobInstance == null) {
        	jobInstance = new ArrayList<JobInstanceDto>();
        }

        return jobInstance;
    }

    /**
     * Sets the job instances.
     *
     * @param jobInstance the new job instances
     */
    public void setJobInstances(List<JobInstanceDto> jobInstance) {
        this.jobInstance = jobInstance;
    }

    /**
     * Gets the list size.
     *
     * @return the list size
     */
    public int getListSize() {
        return listSize;
    }

    /**
     * Sets the list size.
     *
     * @param listSize the new list size
     */
    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    @Override
    public String toString() {
        return "JobInstanceDto [JobInstance=" + jobInstance + "]";
    }


}
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
package org.meveo.service.bi.local;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import org.meveo.model.bi.Job;
import org.meveo.service.base.local.IPersistenceService;

/**
 * Job service interface.
 * 
 * @author Gediminas Ubartas
 * @created 20010.09.23
 */
@Local
public interface JobServiceLocal extends IPersistenceService<Job> {

    /**
     * This method returns all names of jobs
     * 
     * @return List of job entities names
     */
    public List<String> getJobNames();

    /**
     * Saves Job to Database
     * 
     * @param name
     *            Job name
     * @param nextExecutionDate
     *            Job next execution date
     * @param active
     *            If Job is active
     * 
     * @param jobFrequency
     *            Job execution Frequency
     * @return Map of scheduler values
     */
    public void createJob(String name, Date nextExecutionDate, boolean active, int jobFrequency);

    /**
     * Get Job information from repository
     * 
     * @param jobName
     *            Job name
     * @return Job
     */
    public Job getJobInfo(String jobName);

    /**
     * Get Job scheduler parameters
     * 
     * @param jobRepositoryId
     *            Job id in repository
     * @return Map of scheduler values
     */
    public Map<String, Integer> getJobSchedulerInfo(int jobId);
}
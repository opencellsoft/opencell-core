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
package org.meveo.model.jobs;

import java.io.Serializable;

import javax.inject.Named;

@Named
public class TimerInfo implements Serializable {

	private static final long serialVersionUID = 5572229725635504448L;
	private boolean active = true;
	private String jobName;
	private String parametres;
	private Long providerId;
	private Long followingTimerId;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getParametres() {
		return parametres;
	}

	public void setParametres(String parametres) {
		this.parametres = parametres;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public Long getFollowingTimerId() {
		return followingTimerId;
	}

	public void setFollowingTimerId(Long followingTimerId) {
		this.followingTimerId = followingTimerId;
	}

}

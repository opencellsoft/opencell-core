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
package org.meveo.model.mediation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.EnableEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;

/**
 * Access linked to Subscription and Zone.
 * 
 * @author seb
 * 
 */
@Entity
@Table(name = "MEDINA_ACCESS")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ACCESS_SEQ")
public class Access extends EnableEntity {
	
	private static final long serialVersionUID = 1L;

    //input

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;
	
	@Column(name = "DISCRIMINATOR_CODE")
	private String discriminatorCode;

	@Column(name = "ACCES_USER_ID")
	private String accessUserId;


	//output
	//access can be attached to a user account, a subscription or a specific service
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACCOUNT_ID")
    private UserAccount userAccount;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBSCRIPTION_ID")
    private Subscription subscription;
	 
    @JoinColumn(name = "SERVICE_CODE")
    private String serviceCode;
    
    @Column(name = "WALLET_NAME")
    private String walletName;
    
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDiscriminatorCode() {
		return discriminatorCode;
	}

	public void setDiscriminatorCode(String discriminatorCode) {
		this.discriminatorCode = discriminatorCode;
	}

	public String getAccessUserId() {
		return accessUserId;
	}

	public void setAccessUserId(String accessUserId) {
		this.accessUserId = accessUserId;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
		userAccount=subscription.getUserAccount();
		 
	}

 

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getWalletName() {
		return walletName;
	}

	public void setWalletName(String walletName) {
		this.walletName = walletName;
	}

}

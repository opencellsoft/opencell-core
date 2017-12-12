/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.model.crm.ProviderContact;
import org.meveo.model.listeners.AccountCodeGenerationListener;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.Name;

@Entity
@ObservableEntity
@Table(name = "account_entity", uniqueConstraints = @UniqueConstraint(columnNames = { "code", "account_type" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "account_entity_seq"), })
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "account_type") // Hibernate does not support of discriminator column with Joined strategy, so need to set it manually
@EntityListeners({ AccountCodeGenerationListener.class })
public abstract class AccountEntity extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "external_ref_1", length = 255)
    @Size(max = 255)
    protected String externalRef1;

    @Column(name = "external_ref_2", length = 255)
    @Size(max = 255)
    protected String externalRef2;

    @Embedded
    protected Name name;

    @Embedded
    protected Address address;

    @Type(type = "numeric_boolean")
    @Column(name = "default_level")
    protected Boolean defaultLevel = true;

    @Column(name = "provider_contact", length = 255)
    @Size(max = 255)
    protected String providerContact;

    @ManyToOne
    @JoinColumn(name = "primary_contact")
    protected ProviderContact primaryContact;

    @Column(name = "account_type", insertable = true, updatable = false, length = 10)
    @Size(max = 10)
    protected String accountType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bam_id")
    protected BusinessAccountModel businessAccountModel;
    
    @Column(name = "job_title", length = 255)
    private String jobTitle;

    public String getExternalRef1() {
        return externalRef1;
    }

    public void setExternalRef1(String externalRef1) {
        this.externalRef1 = externalRef1;
    }

    public String getExternalRef2() {
        return externalRef2;
    }

    public void setExternalRef2(String externalRef2) {
        this.externalRef2 = externalRef2;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Boolean getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(Boolean defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    public String getProviderContact() {
        return providerContact;
    }

    public void setProviderContact(String providerContact) {
        this.providerContact = providerContact;
    }

    public ProviderContact getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(ProviderContact primaryContact) {
        this.primaryContact = primaryContact;
    }

    public String getAccountType() {
        return accountType;
    }

    public BusinessAccountModel getBusinessAccountModel() {
        return businessAccountModel;
    }

    public void setBusinessAccountModel(BusinessAccountModel businessAccountModel) {
        this.businessAccountModel = businessAccountModel;
    }

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
}
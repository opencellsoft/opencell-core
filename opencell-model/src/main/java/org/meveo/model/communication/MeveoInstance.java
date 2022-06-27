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
package org.meveo.model.communication;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.commons.keystore.KeystoreManager;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.admin.User;
import org.meveo.model.billing.AuthenticationTypeEnum;
import org.meveo.model.crm.Customer;

/**
 * Another installation of application. Allows to:
 * <ul>
 * <li>export configuration and data via export to another application installation</li>
 * <li>notify another application installation about event occurred via notifications</li>
 * <li>create EDRs in another application instalation. EDRs are created via API</li>
 * </ul>
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "com_meveo_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "com_meveo_instance_seq"), })
public class MeveoInstance extends BusinessEntity {

    private static final long serialVersionUID = 1733186433208397850L;

    /**
     * Additional information - User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Additional information - Customer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * Additional information - Product name
     */
    @Column(name = "product_name", length = 255)
    @Size(max = 255)
    private String productName;

    /**
     * Additional information - Product version
     */
    @Column(name = "product_version", length = 255)
    @Size(max = 255)
    private String productVersion;

    /**
     * Additional information - Owner
     */
    @Column(name = "owner", length = 255)
    @Size(max = 255)
    private String owner;

    /**
     * Additional information - MD5
     */
    @Column(name = "md5", length = 255)
    @Size(max = 255)
    private String md5;

    /**
     * Relation with current instalation
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MeveoInstanceStatusEnum status;

    /**
     * Additional information - Creation date
     */
    @Column(name = "creation_date")
    private Date creationDate;

    /**
     * Additional information - Update date
     */
    @Column(name = "update_date")
    private Date updateDate;

    /**
     * Additional information - Enterprise key
     */
    @Column(name = "key_entreprise", length = 255)
    @Size(max = 255)
    private String keyEntreprise;

    /**
     * Additional information - MAC address
     */
    @Column(name = "mac_address", length = 255)
    @Size(max = 255)
    private String macAddress;

    /**
     * Additional information - Machine vendor
     */
    @Column(name = "machine_vendor", length = 255)
    @Size(max = 255)
    private String machineVendor;

    /**
     * Additional information - Instalation mode
     */
    @Column(name = "installation_mode", length = 255)
    @Size(max = 255)
    private String installationMode;

    /**
     * Additional information - Number of cores
     */
    @Column(name = "nb_cores", length = 255)
    @Size(max = 255)
    private String nbCores;

    /**
     * Additional information - Memory
     */
    @Column(name = "memory", length = 255)
    @Size(max = 255)
    private String memory;

    /**
     * Additional information - HD size
     */
    @Column(name = "hd_size", length = 255)
    @Size(max = 255)
    private String hdSize;

    /**
     * Additional information - OS name
     */
    @Column(name = "os_name", length = 255)
    @Size(max = 255)
    private String osName;

    /**
     * Additional information - OS version
     */
    @Column(name = "ps_version", length = 255)
    @Size(max = 255)
    private String osVersion;

    /**
     * Additional information - OS ARCH
     */
    @Column(name = "os_arch", length = 255)
    @Size(max = 255)
    private String osArch;

    /**
     * Additional information - JVM version
     */
    @Column(name = "java_vm_version", length = 255)
    @Size(max = 255)
    private String javaVmVersion;

    /**
     * Additional information - JVM name
     */
    @Column(name = "java_vm_name", length = 255)
    @Size(max = 255)
    private String javaVmName;

    /**
     * Additional information - JVM vendor
     */
    @Column(name = "java_vendor", length = 255)
    @Size(max = 255)
    private String javaVendor;

    /**
     * Additional information - Java version
     */
    @Column(name = "java_version", length = 255)
    @Size(max = 255)
    private String javaVersion;

    /**
     * Additional information - Application server vendor
     */
    @Column(name = "as_vendor", length = 255)
    @Size(max = 255)
    private String asVendor;

    /**
     * Additional information - Application server version
     */
    @Column(name = "as_version", length = 255)
    @Size(max = 255)
    private String asVersion;

    /**
     * URL of another instance
     */
    @Column(name = "url", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String url;

    /**
     * Authentication username to connect
     */
    @Column(name = "auth_username", length = 60)
    @Size(max = 60)
    private String authUsername;

    /**
     * Authentication password to connect
     */
    @Column(name = "auth_password", length = 60)
    @Size(max = 60)
    private String authPasswordDB;

    /**
     * transient authPassword
     */
    transient private String authPassword;

    /**
     * transient authPassword in Keystore
     */
    transient private String authPasswordKS;
    
    /**
     * Authentication client id
     */
    @Column(name = "client_id", length = 60)
    @Size(max = 60)
    private String clientId;

    /**
     * Authentication client secret
     */
    @Column(name = "client_secret", length = 60)
    @Size(max = 60)
    private String clientSecret;
    
	/**
     * 
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "authentication_type")
    private AuthenticationTypeEnum authenticationType = AuthenticationTypeEnum.BASIC_AUTHENTICATION;

	public MeveoInstance() {

    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return the productVersion
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * @param productVersion the productVersion to set
     */
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the md5
     */
    public String getMd5() {
        return md5;
    }

    /**
     * @param md5 the md5 to set
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate the creationDate to set
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the updateDate
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate the updateDate to set
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return the keyEntreprise
     */
    public String getKeyEntreprise() {
        return keyEntreprise;
    }

    /**
     * @param keyEntreprise the keyEntreprise to set
     */
    public void setKeyEntreprise(String keyEntreprise) {
        this.keyEntreprise = keyEntreprise;
    }

    /**
     * @return the macAddress
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * @param macAddress the macAddress to set
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * @return the machineVendor
     */
    public String getMachineVendor() {
        return machineVendor;
    }

    /**
     * @param machineVendor the machineVendor to set
     */
    public void setMachineVendor(String machineVendor) {
        this.machineVendor = machineVendor;
    }

    /**
     * @return the installationMode
     */
    public String getInstallationMode() {
        return installationMode;
    }

    /**
     * @param installationMode the installationMode to set
     */
    public void setInstallationMode(String installationMode) {
        this.installationMode = installationMode;
    }

    /**
     * @return the nbCores
     */
    public String getNbCores() {
        return nbCores;
    }

    /**
     * @param nbCores the nbCores to set
     */
    public void setNbCores(String nbCores) {
        this.nbCores = nbCores;
    }

    /**
     * @return the memory
     */
    public String getMemory() {
        return memory;
    }

    /**
     * @param memory the memory to set
     */
    public void setMemory(String memory) {
        this.memory = memory;
    }

    /**
     * @return the hdSize
     */
    public String getHdSize() {
        return hdSize;
    }

    /**
     * @param hdSize the hdSize to set
     */
    public void setHdSize(String hdSize) {
        this.hdSize = hdSize;
    }

    /**
     * @return the osName
     */
    public String getOsName() {
        return osName;
    }

    /**
     * @param osName the osName to set
     */
    public void setOsName(String osName) {
        this.osName = osName;
    }

    /**
     * @return the osVersion
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * @param osVersion the osVersion to set
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * @return the osArch
     */
    public String getOsArch() {
        return osArch;
    }

    /**
     * @param osArch the osArch to set
     */
    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    /**
     * @return the javaVmVersion
     */
    public String getJavaVmVersion() {
        return javaVmVersion;
    }

    /**
     * @param javaVmVersion the javaVmVersion to set
     */
    public void setJavaVmVersion(String javaVmVersion) {
        this.javaVmVersion = javaVmVersion;
    }

    /**
     * @return the javaVmName
     */
    public String getJavaVmName() {
        return javaVmName;
    }

    /**
     * @param javaVmName the javaVmName to set
     */
    public void setJavaVmName(String javaVmName) {
        this.javaVmName = javaVmName;
    }

    /**
     * @return the javaVendor
     */
    public String getJavaVendor() {
        return javaVendor;
    }

    /**
     * @param javaVendor the javaVendor to set
     */
    public void setJavaVendor(String javaVendor) {
        this.javaVendor = javaVendor;
    }

    /**
     * @return the javaVersion
     */
    public String getJavaVersion() {
        return javaVersion;
    }

    /**
     * @param javaVersion the javaVersion to set
     */
    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    /**
     * @return the asVendor
     */
    public String getAsVendor() {
        return asVendor;
    }

    /**
     * @param asVendor the asVendor to set
     */
    public void setAsVendor(String asVendor) {
        this.asVendor = asVendor;
    }

    /**
     * @return the asVersion
     */
    public String getAsVersion() {
        return asVersion;
    }

    /**
     * @param asVersion the asVersion to set
     */
    public void setAsVersion(String asVersion) {
        this.asVersion = asVersion;
    }

    /**
     * @return the status
     */
    public MeveoInstanceStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(MeveoInstanceStatusEnum status) {
        this.status = status;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    public String getAuthPasswordDB() {
        return authPasswordDB;
    }

    public void setAuthPasswordDB(String authPasswordDB) {
        this.authPasswordDB = authPasswordDB;
    }

    public String getAuthPassword() {
        if (KeystoreManager.existKeystore()) {
            return getAuthPasswordKS();
        }
        else {
            return getAuthPasswordDB();
        }
    }

    public void setAuthPassword(String password) {
        if (KeystoreManager.existKeystore()) {
            authPasswordDB = "";
            this.authPasswordKS = password;
            setAuthPasswordKS();
        }
        else {
            setAuthPasswordDB(password);
        }
    }

    public String getAuthPasswordKS() {
        if (KeystoreManager.existCredential(getClass().getSimpleName() + "." + getId())) {
            return KeystoreManager.retrieveCredential(getClass().getSimpleName() + "." + getId());
        }
        else {
            return "";
        }
    }

    @PostPersist
    public void setAuthPasswordKS() {
        if (this.authPasswordKS == null) {
            this.authPasswordKS = "";
        }

        if (getId() != null && KeystoreManager.existKeystore() &&! this.authPasswordKS.equals(getAuthPasswordKS())) {
            KeystoreManager.addCredential(getClass().getSimpleName() + "." + getId(), this.authPasswordKS);
        }
    }
    
    public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	

    public AuthenticationTypeEnum getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(AuthenticationTypeEnum authenticationType) {
		this.authenticationType = authenticationType;
	}
}
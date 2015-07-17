/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.communication;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;

@Entity
@Table(name = "COM_MEVEO_INSTANCE", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE"}))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "COM_MEVEO_INSTANCE_SEQ")
public class MeveoInstance extends BusinessEntity {
	
	@OneToOne
	@JoinColumn(name = "USER_ID")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "CUSTOMER_ID")
	private Customer customer;

	@Column(name = "PRODUCT_NAME")
	private String productName;

	@Column(name = "PRODUCT_VERSION")
	private String productVersion;
	
	@Column(name = "OWNER")
	private String owner;
	
	@Column(name = "MD5")
	private String md5;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private MeveoInstanceStatusEnum status;
	
	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	
	@Column(name = "KEY_ENTREPRISE")
	private String keyEntreprise;
	
	@Column(name = "MAC_ADDRESS")
	private String macAddress;
	
	@Column(name = "MACHINE_VENDOR")
	private String machineVendor;

	@Column(name = "INSTALLATION_MODE")
	private String installationMode;
	
	@Column(name = "NB_CORES")
	private  String nbCores;
	
	@Column(name = "MEMORY")
	private String memory;
	
	@Column(name = "HD_SIZE")
	private String hdSize;
	
	@Column(name = "OS_NAME")
	private String osName;
	
	@Column(name = "PS_VERSION")
	private String osVersion;
	
	@Column(name = "OS_ARCH")
	private String osArch;

	@Column(name = "JAVA_VM_VERSION")
	private String javaVmVersion;
	
	@Column(name = "JAVA_VM_NAME")
	private String javaVmName;
	
	@Column(name = "JAVA_VENDOR")
	private String javaVendor;
	
	@Column(name = "JAVA_VERSION")
	private String javaVersion;
	
	@Column(name = "AS_VENDOR")
	private String asVendor;
	
	@Column(name = "AS_VERSION")
	private String asVersion;
	
public MeveoInstance (){
	
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


}

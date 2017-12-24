package org.meveo.api.dto.communication;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.communication.MeveoInstanceStatusEnum;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 6:50:08 AM
 *
 */
@XmlRootElement(name = "MeveoInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoInstanceDto extends BusinessDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4747242987390520289L;
	
	private String productName;
	private String productVersion;
	private String owner;
	private String md5;
	private MeveoInstanceStatusEnum status;
	private Date creationDate;
	private Date updateDate;
	private String keyEntreprise;
	private String macAddress;
	private String machineVendor;
	private String installationMode;
	private String nbCores;
	private String memory;
	private String hdSize;
	private String osName;
	private String osVersion;
	private String osArch;
	private String javaVmVersion;
	private String javaVmName;
	private String javaVendor;
	private String javaVersion;
	private String asVendor;
	private String asVersion;
	@XmlElement(required=true)
	private String url;
	private String authUsername;
	private String authPassword;
	
	private String user;
	private String customer;
	public MeveoInstanceDto(){
		
	}
	public MeveoInstanceDto(MeveoInstance meveoInstance){
		super(meveoInstance);
		this.productName=meveoInstance.getProductName();
		this.productVersion=meveoInstance.getProductVersion();
		this.owner=meveoInstance.getOwner();
		this.md5=meveoInstance.getMd5();
		this.status=meveoInstance.getStatus();
		this.creationDate=meveoInstance.getCreationDate();
		this.updateDate=meveoInstance.getUpdateDate();
		this.keyEntreprise=meveoInstance.getKeyEntreprise();
		this.macAddress=meveoInstance.getMacAddress();
		this.machineVendor=meveoInstance.getMachineVendor();
		this.installationMode=meveoInstance.getInstallationMode();
		this.nbCores=meveoInstance.getNbCores();
		this.memory=meveoInstance.getMemory();
		this.hdSize=meveoInstance.getHdSize();
		this.osName=meveoInstance.getOsName();
		this.osVersion=meveoInstance.getOsVersion();
		this.osArch=meveoInstance.getOsArch();
		this.javaVmName=meveoInstance.getJavaVmName();
		this.javaVmVersion=meveoInstance.getJavaVmVersion();
		this.asVendor=meveoInstance.getAsVendor();
		this.asVersion=meveoInstance.getAsVersion();
		this.url=meveoInstance.getUrl();
		this.authUsername=meveoInstance.getAuthUsername();
		this.authPassword=meveoInstance.getAuthPassword();
		if(meveoInstance.getUser()!=null){
			this.user=meveoInstance.getUser().getUserName();
		}
		if(meveoInstance.getCustomer()!=null){
			this.customer=meveoInstance.getCustomer().getCode();
		}
	}

	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductVersion() {
		return productVersion;
	}
	public void setProductVersion(String productVersion) {
		this.productVersion = productVersion;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public MeveoInstanceStatusEnum getStatus() {
		return status;
	}
	public void setStatus(MeveoInstanceStatusEnum status) {
		this.status = status;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getKeyEntreprise() {
		return keyEntreprise;
	}
	public void setKeyEntreprise(String keyEntreprise) {
		this.keyEntreprise = keyEntreprise;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getMachineVendor() {
		return machineVendor;
	}
	public void setMachineVendor(String machineVendor) {
		this.machineVendor = machineVendor;
	}
	public String getInstallationMode() {
		return installationMode;
	}
	public void setInstallationMode(String installationMode) {
		this.installationMode = installationMode;
	}
	public String getNbCores() {
		return nbCores;
	}
	public void setNbCores(String nbCores) {
		this.nbCores = nbCores;
	}
	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	public String getHdSize() {
		return hdSize;
	}
	public void setHdSize(String hdSize) {
		this.hdSize = hdSize;
	}
	public String getOsName() {
		return osName;
	}
	public void setOsName(String osName) {
		this.osName = osName;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public String getOsArch() {
		return osArch;
	}
	public void setOsArch(String osArch) {
		this.osArch = osArch;
	}
	public String getJavaVmVersion() {
		return javaVmVersion;
	}
	public void setJavaVmVersion(String javaVmVersion) {
		this.javaVmVersion = javaVmVersion;
	}
	public String getJavaVmName() {
		return javaVmName;
	}
	public void setJavaVmName(String javaVmName) {
		this.javaVmName = javaVmName;
	}
	public String getJavaVendor() {
		return javaVendor;
	}
	public void setJavaVendor(String javaVendor) {
		this.javaVendor = javaVendor;
	}
	public String getJavaVersion() {
		return javaVersion;
	}
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
	public String getAsVendor() {
		return asVendor;
	}
	public void setAsVendor(String asVendor) {
		this.asVendor = asVendor;
	}
	public String getAsVersion() {
		return asVersion;
	}
	public void setAsVersion(String asVersion) {
		this.asVersion = asVersion;
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
	public String getAuthPassword() {
		return authPassword;
	}
	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	
}


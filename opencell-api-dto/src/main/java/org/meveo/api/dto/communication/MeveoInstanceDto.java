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
 * The Class MeveoInstanceDto.
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Jun 3, 2016 6:50:08 AM
 */
@XmlRootElement(name = "MeveoInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class MeveoInstanceDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4747242987390520289L;

    /** The product name. */
    private String productName;
    
    /** The product version. */
    private String productVersion;
    
    /** The owner. */
    private String owner;
    
    /** The md 5. */
    private String md5;
    
    /** The status. */
    private MeveoInstanceStatusEnum status;
    
    /** The creation date. */
    private Date creationDate;
    
    /** The update date. */
    private Date updateDate;
    
    /** The key entreprise. */
    private String keyEntreprise;
    
    /** The mac address. */
    private String macAddress;
    
    /** The machine vendor. */
    private String machineVendor;
    
    /** The installation mode. */
    private String installationMode;
    
    /** The nb cores. */
    private String nbCores;
    
    /** The memory. */
    private String memory;
    
    /** The hd size. */
    private String hdSize;
    
    /** The os name. */
    private String osName;
    
    /** The os version. */
    private String osVersion;
    
    /** The os arch. */
    private String osArch;
    
    /** The java vm version. */
    private String javaVmVersion;
    
    /** The java vm name. */
    private String javaVmName;
    
    /** The java vendor. */
    private String javaVendor;
    
    /** The java version. */
    private String javaVersion;
    
    /** The as vendor. */
    private String asVendor;
    
    /** The as version. */
    private String asVersion;
    
    /** The url. */
    @XmlElement(required = true)
    private String url;
    
    /** The auth username. */
    private String authUsername;
    
    /** The auth password. */
    private String authPassword;

    /** The user. */
    private String user;
    
    /** The customer. */
    private String customer;

    /**
     * Instantiates a new meveo instance dto.
     */
    public MeveoInstanceDto() {

    }

    /**
     * Instantiates a new meveo instance dto.
     *
     * @param meveoInstance the meveoInstance entity
     */
    public MeveoInstanceDto(MeveoInstance meveoInstance) {
        super(meveoInstance);
        this.productName = meveoInstance.getProductName();
        this.productVersion = meveoInstance.getProductVersion();
        this.owner = meveoInstance.getOwner();
        this.md5 = meveoInstance.getMd5();
        this.status = meveoInstance.getStatus();
        this.creationDate = meveoInstance.getCreationDate();
        this.updateDate = meveoInstance.getUpdateDate();
        this.keyEntreprise = meveoInstance.getKeyEntreprise();
        this.macAddress = meveoInstance.getMacAddress();
        this.machineVendor = meveoInstance.getMachineVendor();
        this.installationMode = meveoInstance.getInstallationMode();
        this.nbCores = meveoInstance.getNbCores();
        this.memory = meveoInstance.getMemory();
        this.hdSize = meveoInstance.getHdSize();
        this.osName = meveoInstance.getOsName();
        this.osVersion = meveoInstance.getOsVersion();
        this.osArch = meveoInstance.getOsArch();
        this.javaVmName = meveoInstance.getJavaVmName();
        this.javaVmVersion = meveoInstance.getJavaVmVersion();
        this.asVendor = meveoInstance.getAsVendor();
        this.asVersion = meveoInstance.getAsVersion();
        this.url = meveoInstance.getUrl();
        this.authUsername = meveoInstance.getAuthUsername();
        this.authPassword = meveoInstance.getAuthPassword();
        if (meveoInstance.getUser() != null) {
            this.user = meveoInstance.getUser().getUserName();
        }
        if (meveoInstance.getCustomer() != null) {
            this.customer = meveoInstance.getCustomer().getCode();
        }
    }

    /**
     * Gets the product name.
     *
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the product name.
     *
     * @param productName the new product name
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets the product version.
     *
     * @return the product version
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * Sets the product version.
     *
     * @param productVersion the new product version
     */
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner.
     *
     * @param owner the new owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets the md 5.
     *
     * @return the md 5
     */
    public String getMd5() {
        return md5;
    }

    /**
     * Sets the md 5.
     *
     * @param md5 the new md 5
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public MeveoInstanceStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(MeveoInstanceStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the creation date.
     *
     * @return the creation date
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date.
     *
     * @param creationDate the new creation date
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the update date.
     *
     * @return the update date
     */
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * Sets the update date.
     *
     * @param updateDate the new update date
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * Gets the key entreprise.
     *
     * @return the key entreprise
     */
    public String getKeyEntreprise() {
        return keyEntreprise;
    }

    /**
     * Sets the key entreprise.
     *
     * @param keyEntreprise the new key entreprise
     */
    public void setKeyEntreprise(String keyEntreprise) {
        this.keyEntreprise = keyEntreprise;
    }

    /**
     * Gets the mac address.
     *
     * @return the mac address
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets the mac address.
     *
     * @param macAddress the new mac address
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    /**
     * Gets the machine vendor.
     *
     * @return the machine vendor
     */
    public String getMachineVendor() {
        return machineVendor;
    }

    /**
     * Sets the machine vendor.
     *
     * @param machineVendor the new machine vendor
     */
    public void setMachineVendor(String machineVendor) {
        this.machineVendor = machineVendor;
    }

    /**
     * Gets the installation mode.
     *
     * @return the installation mode
     */
    public String getInstallationMode() {
        return installationMode;
    }

    /**
     * Sets the installation mode.
     *
     * @param installationMode the new installation mode
     */
    public void setInstallationMode(String installationMode) {
        this.installationMode = installationMode;
    }

    /**
     * Gets the nb cores.
     *
     * @return the nb cores
     */
    public String getNbCores() {
        return nbCores;
    }

    /**
     * Sets the nb cores.
     *
     * @param nbCores the new nb cores
     */
    public void setNbCores(String nbCores) {
        this.nbCores = nbCores;
    }

    /**
     * Gets the memory.
     *
     * @return the memory
     */
    public String getMemory() {
        return memory;
    }

    /**
     * Sets the memory.
     *
     * @param memory the new memory
     */
    public void setMemory(String memory) {
        this.memory = memory;
    }

    /**
     * Gets the hd size.
     *
     * @return the hd size
     */
    public String getHdSize() {
        return hdSize;
    }

    /**
     * Sets the hd size.
     *
     * @param hdSize the new hd size
     */
    public void setHdSize(String hdSize) {
        this.hdSize = hdSize;
    }

    /**
     * Gets the os name.
     *
     * @return the os name
     */
    public String getOsName() {
        return osName;
    }

    /**
     * Sets the os name.
     *
     * @param osName the new os name
     */
    public void setOsName(String osName) {
        this.osName = osName;
    }

    /**
     * Gets the os version.
     *
     * @return the os version
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * Sets the os version.
     *
     * @param osVersion the new os version
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * Gets the os arch.
     *
     * @return the os arch
     */
    public String getOsArch() {
        return osArch;
    }

    /**
     * Sets the os arch.
     *
     * @param osArch the new os arch
     */
    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    /**
     * Gets the java vm version.
     *
     * @return the java vm version
     */
    public String getJavaVmVersion() {
        return javaVmVersion;
    }

    /**
     * Sets the java vm version.
     *
     * @param javaVmVersion the new java vm version
     */
    public void setJavaVmVersion(String javaVmVersion) {
        this.javaVmVersion = javaVmVersion;
    }

    /**
     * Gets the java vm name.
     *
     * @return the java vm name
     */
    public String getJavaVmName() {
        return javaVmName;
    }

    /**
     * Sets the java vm name.
     *
     * @param javaVmName the new java vm name
     */
    public void setJavaVmName(String javaVmName) {
        this.javaVmName = javaVmName;
    }

    /**
     * Gets the java vendor.
     *
     * @return the java vendor
     */
    public String getJavaVendor() {
        return javaVendor;
    }

    /**
     * Sets the java vendor.
     *
     * @param javaVendor the new java vendor
     */
    public void setJavaVendor(String javaVendor) {
        this.javaVendor = javaVendor;
    }

    /**
     * Gets the java version.
     *
     * @return the java version
     */
    public String getJavaVersion() {
        return javaVersion;
    }

    /**
     * Sets the java version.
     *
     * @param javaVersion the new java version
     */
    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    /**
     * Gets the as vendor.
     *
     * @return the as vendor
     */
    public String getAsVendor() {
        return asVendor;
    }

    /**
     * Sets the as vendor.
     *
     * @param asVendor the new as vendor
     */
    public void setAsVendor(String asVendor) {
        this.asVendor = asVendor;
    }

    /**
     * Gets the as version.
     *
     * @return the as version
     */
    public String getAsVersion() {
        return asVersion;
    }

    /**
     * Sets the as version.
     *
     * @param asVersion the new as version
     */
    public void setAsVersion(String asVersion) {
        this.asVersion = asVersion;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url the new url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the auth username.
     *
     * @return the auth username
     */
    public String getAuthUsername() {
        return authUsername;
    }

    /**
     * Sets the auth username.
     *
     * @param authUsername the new auth username
     */
    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }

    /**
     * Gets the auth password.
     *
     * @return the auth password
     */
    public String getAuthPassword() {
        return authPassword;
    }

    /**
     * Sets the auth password.
     *
     * @param authPassword the new auth password
     */
    public void setAuthPassword(String authPassword) {
        this.authPassword = authPassword;
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user the new user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets the customer.
     *
     * @return the customer
     */
    public String getCustomer() {
        return customer;
    }

    /**
     * Sets the customer.
     *
     * @param customer the new customer
     */
    public void setCustomer(String customer) {
        this.customer = customer;
    }
}